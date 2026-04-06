package gui.frontend.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.Campaign;
import model.Session;
import model.Student;
import dao.CampaignDAO;
import dao.SessionDAO;
import dao.RegistrationDAO;
import model.Registration;

/**
 * Service for filtering and suggesting sessions to students.
 * Provides:
 * - Search by specialization, date, time
 * - Filter by capacity, campaign status
 * - Get alternative suggestions when preferred session is full
 * - Sort by various criteria (date, capacity, preference)
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class SessionFilterService {

    private SessionDAO sessionDAO;
    private CampaignDAO campaignDAO;
    private RegistrationDAO registrationDAO;

    public SessionFilterService() {
        this.sessionDAO = new SessionDAO();
        this.campaignDAO = new CampaignDAO();
        this.registrationDAO = new RegistrationDAO();
    }

    // ===============================
    // GET AVAILABLE SESSIONS
    // ===============================
    
    /**
     * Get all active sessions for the student's promotion.
     * Only returns sessions from campaigns that are currently ONGOING.
     * 
     * @param student The student requesting sessions
     * @return List of available sessions for the student
     */
    public ArrayList<Session> getAvailableSessionsForStudent(Student student) {
        ArrayList<Session> allSessions = sessionDAO.getList();
        ArrayList<Campaign> activeCampaigns = campaignDAO.getActiveCampaigns();

        List<Integer> activeCampaignIds = activeCampaigns.stream()
                .filter(c -> c.getPromotion() == student.getPromotion())
                .map(Campaign::getId)
                .collect(Collectors.toList());

        return allSessions.stream()
                .filter(s -> activeCampaignIds.contains(s.getCampaignId()))
                .sorted(Comparator.comparing(Session::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ===============================
    // SEARCH AND FILTER
    // ===============================

    /**
     * Search sessions by specialization name.
     * 
     * @param sessions The sessions to search in
     * @param specializationName The specialization to search for (partial match)
     * @return Filtered list of sessions
     */
    public ArrayList<Session> searchBySpecialization(ArrayList<Session> sessions, String specializationName) {
        if (specializationName == null || specializationName.isEmpty()) {
            return sessions;
        }
        
        return sessions.stream()
                .filter(s -> s.getSpecializationName() != null && 
                           s.getSpecializationName().toLowerCase().contains(specializationName.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Search sessions by campaign name.
     * 
     * @param sessions The sessions to search in
     * @param campaignName The campaign to search for (partial match)
     * @return Filtered list of sessions
     */
    public ArrayList<Session> searchByCampaign(ArrayList<Session> sessions, String campaignName) {
        if (campaignName == null || campaignName.isEmpty()) {
            return sessions;
        }

        return sessions.stream()
                .filter(s -> s.getCampaignName() != null && 
                           s.getCampaignName().toLowerCase().contains(campaignName.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Filter sessions by date range.
     * 
     * @param sessions The sessions to filter
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return Filtered list of sessions
     */
    public ArrayList<Session> filterByDateRange(ArrayList<Session> sessions, LocalDate startDate, LocalDate endDate) {
        return sessions.stream()
                .filter(s -> !s.getDate().isBefore(startDate) && !s.getDate().isAfter(endDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Filter sessions that still have available capacity.
     * 
     * @param sessions The sessions to filter
     * @return Sessions with remaining capacity > 0
     */
    public ArrayList<Session> filterByAvailableCapacity(ArrayList<Session> sessions) {
        return sessions.stream()
                .filter(s -> s.getRemainingCapacity() > 0)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Filter sessions that are full or nearly full.
     * 
     * @param sessions The sessions to filter
     * @return Sessions with no remaining capacity
     */
    public ArrayList<Session> filterByFullCapacity(ArrayList<Session> sessions) {
        return sessions.stream()
                .filter(s -> s.getRemainingCapacity() == 0)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Filter sessions that are almost full (less than 25% remaining capacity).
     * 
     * @param sessions The sessions to filter
     * @return Sessions with low remaining capacity
     */
    public ArrayList<Session> filterByAlmostFull(ArrayList<Session> sessions) {
        return sessions.stream()
                .filter(s -> {
                    double percentRemaining = (double) s.getRemainingCapacity() / s.getMaxCapacity();
                    return percentRemaining > 0 && percentRemaining <= 0.25;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ===============================
    // SUGGESTIONS
    // ===============================

    /**
     * Get alternative sessions when a preferred one is full.
     * Returns similar sessions (same specialization, nearby dates) with available capacity.
     * 
     * @param student The student requesting suggestions
     * @param fullSession The session that is full
     * @return List of alternative sessions, sorted by similarity
     */
    public ArrayList<Session> getSuggestionsForFullSession(Student student, Session fullSession) {
        ArrayList<Session> allAvailable = getAvailableSessionsForStudent(student);
        LocalDate searchDate = fullSession.getDate();
        
        return allAvailable.stream()
                .filter(s -> s.getId() != fullSession.getId())
                .filter(s -> s.getSpecializationId() == fullSession.getSpecializationId())
                .filter(s -> s.getRemainingCapacity() > 0)
                .filter(s -> Math.abs(java.time.temporal.ChronoUnit.DAYS.between(searchDate, s.getDate())) <= 7)
                .sorted(Comparator.comparingInt(Session::getRemainingCapacity).reversed()
                                 .thenComparingLong(s -> Math.abs(java.time.temporal.ChronoUnit.DAYS.between(searchDate, s.getDate()))))
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get sessions similar to one the student is interested in.
     * Used for "You might also like" suggestions.
     * 
     * @param referenceSession The reference session
     * @param allAvailableSessions All available sessions
     * @param maxResults Maximum number of suggestions to return
     * @return List of similar sessions
     */
    public ArrayList<Session> getSimilarSessions(Session referenceSession, ArrayList<Session> allAvailableSessions, int maxResults) {
        return allAvailableSessions.stream()
                .filter(s -> s.getId() != referenceSession.getId())
                .filter(s -> s.getSpecializationId() == referenceSession.getSpecializationId() || 
                           s.getCampaignId() == referenceSession.getCampaignId())
                .filter(s -> s.getRemainingCapacity() > 0)
                .limit(maxResults)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ===============================
    // SORTING
    // ===============================

    /**
     * Sort sessions by date (earliest first).
     * 
     * @param sessions The sessions to sort
     * @return Sorted list
     */
    public ArrayList<Session> sortByDate(ArrayList<Session> sessions) {
        return sessions.stream()
                .sorted(Comparator.comparing(Session::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sort sessions by available capacity (most capacity first).
     * 
     * @param sessions The sessions to sort
     * @return Sorted list
     */
    public ArrayList<Session> sortByCapacity(ArrayList<Session> sessions) {
        return sessions.stream()
                .sorted(Comparator.comparingInt(Session::getRemainingCapacity).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sort sessions by date and capacity combination.
     * 
     * @param sessions The sessions to sort
     * @return Sorted list with date as primary sort, capacity as secondary
     */
    public ArrayList<Session> sortByDateAndCapacity(ArrayList<Session> sessions) {
        return sessions.stream()
                .sorted(Comparator.comparing(Session::getDate)
                               .thenComparingInt(Session::getRemainingCapacity).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ===============================
    // STUDENT REGISTRATIONS
    // ===============================

    /**
     * Get all registrations for a student, sorted by preference rank.
     * 
     * @param studentId The ID of the student
     * @return List of registrations sorted by rank
     */
    public ArrayList<Registration> getStudentRegistrationsByRank(int studentId) {
        ArrayList<Registration> registrations = registrationDAO.getByStudent(studentId);
        registrations.sort(Comparator.comparingInt(Registration::getRank));
        return registrations;
    }

    /**
     * Check if a student is already registered for a session.
     * 
     * @param studentId The student ID
     * @param sessionId The session ID
     * @return true if student is registered, false otherwise
     */
    public boolean isStudentRegistered(int studentId, int sessionId) {
        ArrayList<Registration> registrations = registrationDAO.getByStudent(studentId);
        return registrations.stream().anyMatch(r -> r.getSessionId() == sessionId);
    }
}
