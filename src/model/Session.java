package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This class represents a session in the system. It may contain information about the session's name, description, and other relevant details.
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Session {
    private int id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxCapacity;
    private int specializationId;
    private int campaignId;
    private int createdBy;
    private int modifiedBy;

    /**
     * Constructs a new Session with the specified parameters.
     * @param id The unique identifier of the session.
     * @param date The date of the session.
     * @param startTime The start time of the session.
     * @param endTime The end time of the session.
     * @param maxCapacity The maximum capacity of the session.
     * @param specializationId The ID of the specialization associated with the session.
     * @param campaignId The ID of the campaign associated with the session.
     * @param createdBy The ID of the user who created the session.
     * @param modifiedBy The ID of the user who last modified the session.
     */
    public Session(int id, LocalDate date, LocalTime startTime, LocalTime endTime, int maxCapacity, int specializationId, int campaignId, int createdBy, int modifiedBy) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.specializationId = specializationId;
        this.campaignId = campaignId;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    /**
     * Returns the unique identifier of the session.
     * @return The unique identifier of the session.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the date of the session.
     * @return The date of the session.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the start time of the session.
     * @return The start time of the session.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the session.
     * @return The end time of the session.
     */

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Returns the maximum capacity of the session.
     * @return The maximum capacity of the session.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Returns the ID of the specialization associated with the session.
     * @return The ID of the specialization associated with the session.
     */

    public int getSpecializationId() {
        return specializationId;
    }

    /**
     * Returns the ID of the campaign associated with the session.
     * @return The ID of the campaign associated with the session.
     */
    public int getCampaignId() {
        return campaignId;
    }

    /**
     * Returns the ID of the user who created the session.
     * @return The ID of the user who created the session.
     */
    public int getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the ID of the user who last modified the session.
     * @return The ID of the user who last modified the session.
     */
    public int getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the ID of the session.
     * @param id The ID of the session.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the date of the session.
     * @param date The date of the session.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Sets the start time of the session.
     * @param startTime The start time of the session.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time of the session.
     * @param endTime The end time of the session.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the maximum capacity of the session.
     * @param maxCapacity The maximum capacity of the session.
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Sets the ID of the specialization associated with the session.
     * @param specializationId The ID of the specialization associated with the session.
     */
    public void setSpecializationId(int specializationId) {
        this.specializationId = specializationId;
    }

    /**
     * Sets the ID of the campaign associated with the session.
     * @param campaignId The ID of the campaign associated with the session.
     */
    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * Sets the ID of the user who created the session.
     * @param createdBy The ID of the user who created the session.
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Sets the ID of the user who last modified the session.
     * @param modifiedBy The ID of the user who last modified the session.
     */
    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", maxCapacity=" + maxCapacity +
                ", specializationId=" + specializationId +
                ", campaignId=" + campaignId +
                ", createdBy=" + createdBy +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}