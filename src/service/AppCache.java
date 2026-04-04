package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.*;

public class AppCache {

    private static AppCache instance;

    private ArrayList<Department> departments;
    private ArrayList<Specialization> specializations;
    private ArrayList<Session> sessions;
    private ArrayList<Campaign> campaigns;
    private ArrayList<Registration> registrations;
    
    private ArrayList<Student> students;
    private Map<Integer, ArrayList<Registration>> registrationsByStudent;  // studentId -> registrations
    private Map<Integer, Student> studentCache;  // studentId -> Student (for quick lookup)

    private AppCache() {}

    public static AppCache getInstance() {
        if (instance == null) {
            instance = new AppCache();
        }
        return instance;
    }

    // ==========================
    // DEPARTMENTS
    // ==========================
    public ArrayList<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments = departments;
    }

    // ==========================
    // SPECIALIZATIONS
    // ==========================
    public ArrayList<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(ArrayList<Specialization> specializations) {
        this.specializations = specializations;
    }

    // ==========================
    // SESSIONS
    // ==========================
    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    // ==========================
    // CAMPAIGNS
    // ==========================
    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
    }
    
    // ==========================
    // REGISTRATIONS
    // ==========================
    public ArrayList<Registration> getRegistrations(){
        return registrations;
    }

    public void setRegistration(ArrayList<Registration> registrations){
        this.registrations = registrations;
    }
    
    // ==========================
    // STUDENTS
    // ==========================
    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
        // Also build the studentCache for quick lookup by ID
        if (students != null) {
            if (studentCache == null) {
                studentCache = new HashMap<>();
            }
            studentCache.clear();
            for (Student s : students) {
                studentCache.put(s.getId(), s);
            }
        }
    }

    public Student getStudentById(int studentId) {
        if (studentCache != null) {
            return studentCache.get(studentId);
        }
        return null;
    }

    public void addStudentToCache(Student student) {
        if (studentCache == null) {
            studentCache = new HashMap<>();
        }
        studentCache.put(student.getId(), student);
        if (students != null && !students.contains(student)) {
            students.add(student);
        }
    }

    // ==========================
    // REGISTRATIONS BY STUDENT
    // ==========================
    public ArrayList<Registration> getRegistrationsByStudent(int studentId) {
        if (registrationsByStudent == null) {
            registrationsByStudent = new HashMap<>();
        }
        return registrationsByStudent.get(studentId);
    }

    public void setRegistrationsByStudent(int studentId, ArrayList<Registration> registrations) {
        if (registrationsByStudent == null) {
            registrationsByStudent = new HashMap<>();
        }
        registrationsByStudent.put(studentId, registrations);
    }

    public void addRegistrationToStudentCache(int studentId, Registration registration) {
        if (registrationsByStudent == null) {
            registrationsByStudent = new HashMap<>();
        }
        ArrayList<Registration> regs = registrationsByStudent.get(studentId);
        if (regs != null && !regs.contains(registration)) {
            regs.add(registration);
        }
    }

    public void removeRegistrationFromStudentCache(int studentId, int sessionId) {
        if (registrationsByStudent == null) return;
        ArrayList<Registration> regs = registrationsByStudent.get(studentId);
        if (regs != null) {
            regs.removeIf(r -> r.getSessionId() == sessionId);
        }
    }

    // ==========================
    // RESET CACHE
    // ==========================
    public void clear() {
        departments = null;
        specializations = null;
        sessions = null;
        campaigns = null;
        registrations = null;
        students = null;
        registrationsByStudent = null;
        if (studentCache != null) {
            studentCache.clear();
        }
    }

    public void invalidateStudentCache() {
        registrationsByStudent = null;
    }

    public void invalidateSessionCache() {
        sessions = null;
    }

    public void invalidateCampaignCache() {
        campaigns = null;
    }
}