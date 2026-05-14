package model;

/**
 * Représente un utilisateur du système.
 * 
 * Un utilisateur est une entité de base caractérisée par son identité (prénom, nom)
 * et son adresse e-mail. Cette classe est une classe parent pour les étudiants et admins.
 * 
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
 * @version 1.0
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructeur d'un utilisateur.
     * 
     * @param id Identifiant unique de l'utilisateur
     * @param firstName Prénom de l'utilisateur
     * @param lastName Nom de famille de l'utilisateur
     * @param email Adresse e-mail de l'utilisateur
     */
    public User(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Retourne l'identifiant de l'utilisateur.
     * @return L'identifiant unique de l'utilisateur
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le prénom de l'utilisateur.
     * @return Le prénom de l'utilisateur
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retourne le nom de famille de l'utilisateur.
     * @return Le nom de famille de l'utilisateur
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the email address of the user.
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the ID of the user.
     * @param id The ID of the user.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the first name of the user.
     * @param firstName The first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the user.
     * @param lastName The last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the email address of the user.
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}