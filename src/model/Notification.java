package model;

import java.time.LocalDate;

/**
 * Représente une notification du système.
 * 
 * Une notification est un message destiné à un étudiant ou un administrateur
 * pour l'informer d'un changement dans le système (inscription, confirmation, etc.).
 * 
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
 * @version 1.0
 */
public class Notification {
    private int id;
    private String content;
    private String type;
    private LocalDate creationDate;
    private int studentId;
    private int adminId;
    private boolean isRead;

    /**
     * Constructs a new Notification with the specified parameters.
     * @param id The unique identifier of the notification.
     * @param content The content of the notification.
     * @param type The type of the notification (e.g., info, warning).
     * @param creationDate The date when the notification was created.
     * @param studentId The ID of the student who is the recipient of the notification.
     * @param adminId The ID of the admin who is the recipient of the notification.
     * @param isRead Indicates whether the notification has been read by the recipient.
     */
    public Notification(int id, String content, String type, LocalDate creationDate, int studentId, int adminId, boolean isRead) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.creationDate = creationDate;
        this.studentId = studentId;
        this.adminId = adminId;
        this.isRead = isRead;
    }

    /**
     * Returns the unique identifier of the notification.
     * @return The unique identifier of the notification.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the content of the notification.
     * @return The content of the notification.
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the type of the notification.
     * @return The type of the notification.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the date when the notification was created.
     * @return The date when the notification was created.
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Returns the ID of the student who is the recipient of the notification.
     * @return The ID of the student who is the recipient of the notification.
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * Returns the ID of the admin who is the recipient of the notification.
     * @return The ID of the admin who is the recipient of the notification.
     */
    public int getAdminId() {
        return adminId;
    }

    /**
     * Returns whether the notification has been read by the recipient.
     * @return true if the notification has been read by the recipient, false otherwise.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the ID of the notification.
     * @param id The ID of the notification.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the content of the notification.
     * @param content The content of the notification.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets the type of the notification.
     * @param type The type of the notification.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the creation date of the notification.
     * @param creationDate The creation date of the notification.
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Sets the ID of the student who is the recipient of the notification.
     * @param studentId The ID of the student who is the recipient of the notification.
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * Sets the ID of the admin who is the recipient of the notification.
     * @param adminId The ID of the admin who is the recipient of the notification.
     */
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    /**
     * Sets whether the notification has been read by the recipient.
     * @param isRead true if the notification has been read, false otherwise.
     */
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", creationDate=" + creationDate +
                ", studentId=" + studentId +
                ", adminId=" + adminId +
                ", isRead=" + isRead +
                '}';
    }

}
