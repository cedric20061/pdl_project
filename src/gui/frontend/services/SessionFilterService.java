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
     * Filter sessions by time range (on any day).
     * 
     * @param sessions The sessions to filter
     * @param startTimeStr The start time (format: "HH:mm")
     * @param endTimeStr The end time (format: "HH:mm")
     * @return Filtered list of sessions within the time range
     */
    public ArrayList<Session> filterByTimeRange(ArrayList<Session> sessions, String startTimeStr, String endTimeStr) {
        if (startTimeStr == null || startTimeStr.isEmpty() || endTimeStr == null || endTimeStr.isEmpty()) {
            return sessions;
        }

        java.time.LocalTime startTime = java.time.LocalTime.parse(startTimeStr);
        java.time.LocalTime endTime = java.time.LocalTime.parse(endTimeStr);

        return sessions.stream()
                .filter(s -> {
                    // startTime and endTime are already LocalTime objects
                    java.time.LocalTime sessionStart = s.getStartTime();
                    java.time.LocalTime sessionEnd = s.getEndTime();
                    
                    if (sessionStart == null || sessionEnd == null) return false;
                    
                    // Session is within the requested time range
                    return !sessionStart.isBefore(startTime) && !sessionEnd.isAfter(endTime);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Filter sessions that start at a specific time.
     * 
     * @param sessions The sessions to filter
     * @param startTimeStr The start time to match (format: "HH:mm")
     * @return Filtered list of sessions starting at that time
     */
    public ArrayList<Session> filterByStartTime(ArrayList<Session> sessions, String startTimeStr) {
        if (startTimeStr == null || startTimeStr.isEmpty()) {
            return sessions;
        }

        java.time.LocalTime targetTime = java.time.LocalTime.parse(startTimeStr);
        
        return sessions.stream()
                .filter(s -> {
                    
                    return s.getStartTime() != null && s.getStartTime().equals(targetTime); 
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Check if a new session conflicts with existing student registrations.
     * A conflict occurs when:
     * - The new session is on the same day as an existing registration
     * - The time slots overlap
     * 
     * @param studentId The student ID
     * @param newSession The session the student wants to register for
     * @return true if there's a conflict, false otherwise
     */
    public boolean checkScheduleConflict(int studentId, Session newSession) {
        ArrayList<Registration> studentRegistrations = registrationDAO.getByStudent(studentId);
        SessionDAO sessionDAO = new SessionDAO();

        if (newSession.getStartTime() == null || newSession.getEndTime() == null || newSession.getDate() == null) {
            return false;
        }

        java.time.LocalTime newStart = newSession.getStartTime();
        java.time.LocalTime newEnd = newSession.getEndTime();
        LocalDate newDate = newSession.getDate();

        for (Registration reg : studentRegistrations) {
            Session existingSession = sessionDAO.get(reg.getSessionId());
            if (existingSession == null) continue;

            // Check if same day
            if (!existingSession.getDate().equals(newDate)) {
                continue;  // Different day, no conflict
            }

            // Check if time overlaps
            java.time.LocalTime existStart = existingSession.getStartTime();
            java.time.LocalTime existEnd = existingSession.getEndTime();

            if (existStart == null || existEnd == null) continue;

            // Overlap occurs when: newStart < existEnd AND newEnd > existStart
            if (newStart.isBefore(existEnd) && newEnd.isAfter(existStart)) {
                return true;  // Conflict found!
            }
        }

        return false;  // No conflicts
    }

    /**
     * Get a user-friendly message for a schedule conflict.
     * 
     * @param studentId The student ID
     * @param newSession The session with conflict
     * @return Descriptive error message
     */
    public String getConflictMessage(int studentId, Session newSession) {
        ArrayList<Registration> studentRegistrations = registrationDAO.getByStudent(studentId);
        SessionDAO sessionDAO = new SessionDAO();

        if (newSession.getStartTime() == null || newSession.getEndTime() == null || newSession.getDate() == null) {
            return "";
        }

        java.time.LocalTime newStart = newSession.getStartTime();
        java.time.LocalTime newEnd = newSession.getEndTime();
        LocalDate newDate = newSession.getDate();

        for (Registration reg : studentRegistrations) {
            Session existingSession = sessionDAO.get(reg.getSessionId());
            if (existingSession == null) continue;

            if (!existingSession.getDate().equals(newDate)) {
                continue;
            }

            java.time.LocalTime existStart = existingSession.getStartTime();
            java.time.LocalTime existEnd = existingSession.getEndTime();

            if (existStart == null || existEnd == null) continue;

            if (newStart.isBefore(existEnd) && newEnd.isAfter(existStart)) {
                return "Conflit d'horaire détecté !\n" +
                       "Vous avez déjà une session inscrite le " + newDate + "\n" +
                       "de " + existingSession.getStartTime() + " à " + existingSession.getEndTime() + "\n" +
                       "qui chevauche cette session (" + newSession.getStartTime() + " à " + newSession.getEndTime() + ")";
            }
        }

        return "";
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
