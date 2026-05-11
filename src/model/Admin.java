package model;

/**
 * Représente un administrateur du système.
 * 
 * Un administrateur hérite de la classe User et est autorisé à gérer
 * les ressources du système (campagnes, sessions, inscriptions, etc.).
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 * @see User
 */
public class Admin extends User {
    /**
     * Constructeur d'un administrateur.
     * 
     * @param id Identifiant unique de l'administrateur
     * @param firstName Prénom de l'administrateur
     * @param lastName Nom de famille de l'administrateur
     * @param email Adresse e-mail de l'administrateur
     */
    public Admin(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return super.toString() + " (Admin)";
    }
}