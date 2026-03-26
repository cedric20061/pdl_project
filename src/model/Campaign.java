package model;

import java.time.LocalDate;

/**
 * This class represents a campaign in the system.
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Campaign {
    private int id;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private int maxChoices;
    private int promotion;
    private String createdBy;
    private String modifiedBy;


    public Campaign(){}
    /**
     * Constructs a new Campaign with the specified ID and status.
     * @param id The unique identifier of the campaign.
     * @param status The status of the campaign.
     * @param startDate The start date of the campaign.
     * @param endDate The end date of the campaign.
     * @param maxChoices The maximum number of choices for the campaign.
     * @param promotion The promotion value for the campaign.
     * @param createdBy The ID of the user who created the campaign.
     * @param modifiedBy The ID of the user who last modified the campaign.
     */
    public Campaign(int id, String status, String startDate, String endDate, int maxChoices, int promotion, String createdBy, String modifiedBy) {
        this.id = id;
        this.status = status;
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
        this.maxChoices = maxChoices;
        this.promotion = promotion;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    /**
     * Returns the ID of the campaign.
     * @return The ID of the campaign.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the status of the campaign.
     * @return The status of the campaign.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the start date of the campaign.
     * @return The start date of the campaign.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     *  Returns the end date of the campaign.
     * @return The end date of the campaign.
    */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the maximum number of choices for the campaign.
     * @return The maximum number of choices for the campaign.
     */
    public int getMaxChoices() {
        return maxChoices;
    }

    /**
     * Returns the promotion value for the campaign.
     * @return The promotion value for the campaign.
     */
    public int getPromotion() {
        return promotion;
    }

    /**
     * Returns the ID of the user who created the campaign.
     * @return The ID of the user who created the campaign.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the ID of the user who last modified the campaign.
     * @return The ID of the user who last modified the campaign.
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the ID of the campaign.
     * @param id The ID of the campaign.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the status of the campaign.
     * @param status The status of the campaign.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the start date of the campaign.
     * @param startDate The start date of the campaign.
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the end date of the campaign.
     * @param endDate The end date of the campaign.
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Sets the maximum number of choices for the campaign.
     * @param maxChoices The maximum number of choices for the campaign.
     */
    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    /**
     * Sets the promotion value for the campaign.
     * @param promotion The promotion value for the campaign.
     */
    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    /**
     * Sets the ID of the user who created the campaign.
     * @param createdBy The ID of the user who created the campaign.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Sets the ID of the user who last modified the campaign.
     * @param modifiedBy The ID of the user who last modified the campaign.
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }


    @Override
    public String toString() {
        return "Campagne - " + id + " - " + startDate + " à " + endDate;
    }
}