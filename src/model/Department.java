package model;

/**
 * This class represents a department in the system. It may contain information about the department's name, and other relevant details.
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Department {
    private int id;
    private String name;
    private String description;
    private String handleBy;

    /**
     * Constructs a new Department with the specified name.
     * @param id The unique identifier of the department.
     * @param name The name of the department.
     * @param description The description of the department.
     * @param handleBy The user who handles the department.
     */
    public Department(int id, String name, String description, String handleBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.handleBy = handleBy;
    }

    /**
     * Returns the ID of the department.
     * @return The ID of the department.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the department.
     * @return The name of the department.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the department.
     * @return The description of the department.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the user who handles the department.
     * @return The user who handles the department.
     */
    public String getHandleBy() {
        return handleBy;
    }

    /**
     * Sets the ID of the department.
     * @param id The ID of the department.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the name of the department.
     * @param name The name of the department.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description of the department.
     * @param description The description of the department.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the user who handles the department.
     * @param handleBy The user who handles the department.
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
