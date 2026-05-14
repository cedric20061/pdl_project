package model;

import java.time.LocalDate;

/**
 * Représente une campagne d'inscription du système.
 * 
 * Une campagne encadre une période d'inscription des étudiants à des sessions.
 * Elle est caractérisée par ses dates de début/fin, son statut et des limites
 * (nombre maximal de choix, promotion cible).
 * 
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
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


    /**
     * Constructeur vide d'une campagne.
     */
    public Campaign(){}
    
    /**
     * Constructeur d'une campagne.
     * 
     * @param id Identifiant unique de la campagne
     * @param status Statut de la campagne
     * @param startDate Date de début de la campagne (format ISO: YYYY-MM-DD)
     * @param endDate Date de fin de la campagne (format ISO: YYYY-MM-DD)
     * @param maxChoices Nombre maximal de choix autorisés pour la campagne
     * @param promotion Année de promotion visée par la campagne
     * @param createdBy Identifiant de l'utilisateur ayant créé la campagne
     * @param modifiedBy Identifiant de l'utilisateur ayant modifié la campagne
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
     * Retourne l'identifiant de la campagne.
     * @return L'identifiant unique de la campagne
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le statut de la campagne.
     * @return Le statut de la campagne
     */
    public String getStatus() {
        return status;
    }

    /**
     * Retourne la date de début de la campagne.
     * @return La date de début de la campagne
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Retourne la date de fin de la campagne.
     * @return La date de fin de la campagne
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Retourne le nombre maximal de choix pour la campagne.
     * @return Le nombre maximal de choix
     */
    public int getMaxChoices() {
        return maxChoices;
    }

    /**
     * Retourne l'année de promotion visée par la campagne.
     * @return L'année de promotion
     */
    public int getPromotion() {
        return promotion;
    }

    /**
     * Retourne l'identifiant de l'utilisateur ayant créé la campagne.
     * @return L'identifiant du créateur
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Retourne l'identifiant de l'utilisateur ayant modifié la campagne.
     * @return L'identifiant du dernier modificateur
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