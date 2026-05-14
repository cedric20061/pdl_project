package model;

/**
 * Représente une inscription d'un étudiant à une session.
 * 
 * Une inscription fait le lien entre un étudiant et une session.
 * Elle inclut le rang de préférence de l'étudiant et le statut de l'inscription
 * (PENDING, CONFIRMED, VALIDATED, REJECTED).
 * 
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
 * @version 1.0
 */
public class Registration {
    private int sessionId;
    private int studentId;
    private String studentName;
    private String studentEmail;
    private int rank;
    private String status;

    /**
     * Constructs a new Registration with the specified session ID, student ID, rank, and status.
     * @param sessionId The ID of the session for which the registration is made.
     * @param studentId The ID of the student who is registering.
     * @param rank The rank of the registration (e.g., priority).
     * @param status The status of the registration (e.g., pending, confirmed).
     */
    public Registration(int sessionId, int studentId, String studentName, String studentEmail, int rank, String status) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.rank = rank;
        this.status = status;
    }

    /**
     * Returns the ID of the session for which the registration is made.
     * @return The ID of the session for which the registration is made.
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Returns the ID of the student who is registering.
     * @return The ID of the student who is registering.
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * Returns the rank of the registration.
     * @return The rank of the registration.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the status of the registration.
     * @return The status of the registration.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the name of the student who is registering.
     * @return The name of the student who is registering.
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Returns the email of the student who is registering.
     * @return The email of the student who is registering.
     */
    public String getStudentEmail() {
        return studentEmail;
    }

    /**
     * Sets the ID of the session for which the registration is made.
     * @param sessionId The ID of the session for which the registration is made.
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Sets the ID of the student who is registering.
     * @param studentId The ID of the student who is registering.
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * Sets the rank of the registration.
     * @param rank The rank of the registration.
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Sets the status of the registration.
     * @param status The status of the registration.
     */
    public void setStatus(String status) {
        this.status = status;
    }

        /**
        * Sets the name of the student who is registering.
        * @param studentName The name of the student who is registering.
        */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * Sets the email of the student who is registering.
     * @param studentEmail The email of the student who is registering.
     */ 
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    } 

    @Override
    public String toString() {
        return "Registration{" +
                "sessionId=" + sessionId +
                ", studentId=" + studentId +
                ", rank=" + rank +
                ", status='" + status + '\'' +
                '}';
    }

}
