package model;

/**
 * This class represents an administrator in the system. It extends the User class and may contain additional information or functionality specific to administrators.
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Admin extends User {
    /**
     * Constructs a new Admin with the specified name and email.
     * 
     * @param id The unique identifier of the admin.
     * @param firstName The first name of the admin.
     * @param lastName The last name of the admin.
     * @param email The email address of the admin.
     */
    public Admin(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return super.toString() + " (Admin)";
    }
}