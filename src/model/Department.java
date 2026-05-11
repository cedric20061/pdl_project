package model;

/**
 * Représente un département du système.
 * 
 * Un département est une unité organisationnelle qui peut contenir plusieurs spécialisations.
 * Il est caractérisé par son nom, une description et un responsable.
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Department {
    private int id;                // Identifiant unique du département
    private String name;           // Nom du département
    private String description;    // Description du département
    private String handleBy;       // Responsable du département

    /**
     * Constructeur d'un département.
     * 
     * @param id Identifiant unique du département
     * @param name Nom du département
     * @param description Description du département
     * @param handleBy Responsable du département
     */
    public Department(int id, String name, String description, String handleBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.handleBy = handleBy;
    }

    /**
     * Retourne l'identifiant du département.
     * @return L'identifiant unique du département
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le nom du département.
     * @return Le nom du département
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne la description du département.
     * @return La description du département
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne le responsable du département.
     * @return L'utilisateur responsable du département
     */
    public String getHandleBy() {
        return handleBy;
    }

    /**
     * Définit l'identifiant du département.
     * @param id L'identifiant unique du département
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Définit le nom du département.
     * @param name Le nom du département
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Définit la description du département.
     * @param description La description du département
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Définit le responsable du département.
     * @param handleBy L'utilisateur responsable du département
     */
    public void setHandleBy(String handleBy) {
        this.handleBy = handleBy;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", handleBy='" + handleBy + '\'' +
                '}';
    }
}
