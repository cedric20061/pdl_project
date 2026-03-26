package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This class represents a session in the system. It may contain information about the session's name, description, and other relevant details.
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class Session {

    //TODO  ajouter le nombre de places restantes
    private int id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxCapacity;
    private int remainingCapacity;
    private String room;
    private int specializationId;
    private int campaignId;
    private String specializationName; // for display purposes only
    private String campaignName;
    private String createdBy;
    private String modifiedBy;

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
    public Session(int id, String date, String startTime, String endTime, int maxCapacity, int remainingCapacity, String room, int specializationId, String specializationName, int campaignId, String campaignName, String createdBy, String modifiedBy) {
        this.id = id;
        this.date = LocalDate.parse(date);
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
        this.maxCapacity = maxCapacity;
        this.remainingCapacity = remainingCapacity;
        this.room = room;
        this.specializationId = specializationId;
        this.specializationName = specializationName;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
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
     * Returns the remaining capacity of the session.
     * @return The remaining capacity of the session.
     */
    public int getRemainingCapacity() {
        return remainingCapacity;
    }

    /**
     * Returns the room of the session.
     * @return The room of the session.
     */
    public String getRoom() {
        return room;
    }

    /**
     * Returns the ID of the specialization associated with the session.
     * @return The ID of the specialization associated with the session.
     */

    public int getSpecializationId() {
        return specializationId;
    }

    /**
     * Returns the name of the specialization associated with the session.
     * @return The name of the specialization associated with the session.
     */
    public String getSpecializationName() {
        return specializationName;
    }

    /**
     * Returns the name of the campaign associated with the session.
     * @return The name of the campaign associated with the session.
     */
    public String getCampaignName() {
        return campaignName;
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
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the ID of the user who last modified the session.
     * @return The ID of the user who last modified the session.
     */
    public String getModifiedBy() {
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
    * Sets the remaining capacity of the session.
    * @param remainingCapacity The remaining capacity of the session.
    */
    public void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    /**
     * Sets the room of the session.
     * @param room The room of the session.
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Sets the ID of the specialization associated with the session.
     * @param specializationId The ID of the specialization associated with the session.
     */
    public void setSpecializationId(int specializationId) {
        this.specializationId = specializationId;
    }

    /**
     * Sets the name of the specialization associated with the session.
     * @param specializationName The name of the specialization associated with the session.
     */
    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    /**
     * Sets the ID of the campaign associated with the session.
     * @param campaignId The ID of the campaign associated with the session.
     */
    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * Sets the name of the campaign associated with the session.
     * @param campaignName The name of the campaign associated with the session.
     */
    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    /**
     * Sets the ID of the user who created the session.
     * @param createdBy The ID of the user who created the session.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Sets the ID of the user who last modified the session.
     * @param modifiedBy The ID of the user who last modified the session.
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "Session - " + id + " - " + date + " - " + startTime + " to " + endTime;
    }
}