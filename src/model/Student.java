package model;

/**
 * Représente un étudiant du système.
 * 
 * Un étudiant hérite de la classe User et ajoute des informations spécifiques
 * comme le niveau académique et l'année de promotion.
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 * @see User
 */
public class Student extends User {
    private String level;
    private int promotion;


    /**
     * Constructeur d'un étudiant.
     * 
     * @param id Identifiant unique de l'étudiant
     * @param firstName Prénom de l'étudiant
     * @param lastName Nom de famille de l'étudiant
     * @param email Adresse e-mail de l'étudiant
     * @param level Niveau académique de l'étudiant (ex: "Licence", "Master")
     * @param promotion Année de promotion de l'étudiant (ex: 2024)
     */
    public Student(int id, String firstName, String lastName, String email, String level, int promotion) {
        super(id, firstName, lastName, email);
        this.level = level;
        this.promotion = promotion;
    }

    /**
     * Retourne le niveau académique de l'étudiant.
     * @return Le niveau académique de l'étudiant
     */
    public String getLevel() {
        return level;
    }

    /**
     * Retourne l'année de promotion de l'étudiant.
     * @return L'année de promotion de l'étudiant
     */
    public int getPromotion() {
        return promotion;
    }

    /**
     * Définit le niveau académique de l'étudiant.
     * @param level Le niveau académique de l'étudiant
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Définit l'année de promotion de l'étudiant.
     * @param promotion L'année de promotion de l'étudiant
     */
    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return super.toString() + " (Student, Level: " + level + ", Promotion: " + promotion + ")";
    }
}
