package model;

/**
 * Représente une spécialisation (dominante) du système.
 * 
 * Une spécialisation est une discipline académique offerte par un département.
 * Elle est caractérisée par son nom, sa description, un acronyme et un responsable.
 * 
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
 * @version 1.0
 */
public class Specialization {
    private int id;
    private String name;
    private String description;
    private String acronym;
    private String handleBy;
    private String departmentName;
    private int departmentId;

    /**
     * Constructs a new Specialization with the specified name, description, acronym, and handler.
     * @param id The unique identifier of the specialization.
     * @param name The name of the specialization.
     * @param description The description of the specialization.
     * @param acronym The acronym of the specialization.
     * @param handleBy The user who handles the specialization.
     * @param departmentName The name of the department to which the specialization belongs.
     * @param departmentId The ID of the department to which the specialization belongs.
     */
    public Specialization(int id, String name, String description, String acronym, String handleBy, String departmentName, int departmentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.acronym = acronym;
        this.handleBy = handleBy;
        this.departmentName = departmentName;
        this.departmentId = departmentId;
    }

    /**
     * Returns the ID of the specialization.
     * @return The ID of the specialization.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the specialization.
     * @return The name of the specialization.
     */

    public String getName() {
        return name;
    }

    /**
     * Returns the description of the specialization.
     * @return The description of the specialization.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the acronym of the specialization.
     * @return The acronym of the specialization.
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * Returns the user who handles the specialization.
     * @return The user who handles the specialization.
     */
    public String getHandleBy() {
        return handleBy;
    }

    /**
     * Returns the name of the department to which the specialization belongs.
     * @return The name of the department to which the specialization belongs.
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Returns the ID of the department to which the specialization belongs.
     * @return The ID of the department to which the specialization belongs.
     */
    public int getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the ID of the specialization.
     * @param id The ID of the specialization.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the name of the specialization.
     * @param name The name of the specialization.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description of the specialization.
     * @param description The description of the specialization.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the acronym of the specialization.
     * @param acronym The acronym of the specialization.
     */
    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    /**
     * Sets the user who handles the specialization.
     * @param handleBy The user who handles the specialization.
     */
    public void setHandleBy(String handleBy) {
        this.handleBy = handleBy;
    }

    /**
     * Sets the name of the department to which the specialization belongs.
     * @param departmentName The name of the department to which the specialization belongs.
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Sets the ID of the department to which the specialization belongs.
     * @param departmentId The ID of the department to which the specialization belongs.
     */
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
    
    @Override
    public String toString() {
        return "Specialization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", acronym='" + acronym + '\'' +
                ", handleBy='" + handleBy + '\'' +
                '}';
    }
}