package service;

import java.util.ArrayList;
import model.*;

public class AppCache {

    private static AppCache instance;

    private ArrayList<Department> departments;
    private ArrayList<Specialization> specializations;
    private ArrayList<Session> sessions;
    private ArrayList<Campaign> campaigns;
    private ArrayList<Registration> registrations;

    private AppCache() {}

    public static AppCache getInstance() {
        if (instance == null) {
            instance = new AppCache();
        }
        return instance;
    }

    // ==========================
    // DEPARTMENTS
    // ==========================
    public ArrayList<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments = departments;
    }

    // ==========================
    // SPECIALIZATIONS
    // ==========================
    public ArrayList<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(ArrayList<Specialization> specializations) {
        this.specializations = specializations;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
    }
    
    public ArrayList<Registration> getRegistrations(){
        return registrations;
    }

    public void setRegistration(ArrayList<Registration> registrations){
        this.registrations = registrations;
    }
    // ==========================
    // RESET CACHE
    // ==========================
    public void clear() {
        departments = null;
        specializations = null;
        sessions = null;
        campaigns = null;
    }
}