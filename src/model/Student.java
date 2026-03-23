package model;

/**
 * This class represents a student in the system. It extends the User class and may contain information about the student's identity, level, and other relevant details.
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Student extends User {
    private String level;
    private int promotion;


    /**
     * Constructs a new Student with the specified level and promotion.
     * @param id The unique identifier of the student.
     * @param firstName The first name of the student.
     * @param lastName The last name of the student.
     * @param email The email address of the student.
     * @param level The academic level of the student (e.g., "Undergraduate", "Graduate").
     * @param promotion The promotion year of the student (e.g., 2024).
     */
    public Student(int id, String firstName, String lastName, String email, String level, int promotion) {
        super(id, firstName, lastName, email);
        this.level = level;
        this.promotion = promotion;
    }

    /**
     * Returns the academic level of the student.
     * @return The academic level of the student.
     */    
    public String getLevel() {
        return level;
    }

    /**
     * Returns the promotion year of the student.
     * @return The promotion year of the student.
     */    
    public int getPromotion() {
        return promotion;
    }

    /**
     * Sets the academic level of the student.
     * @param level The academic level of the student.
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Sets the promotion year of the student.
     * @param promotion The promotion year of the student.
     */
    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return super.toString() + " (Student, Level: " + level + ", Promotion: " + promotion + ")";
    }
}
