package service;

import model.User;

public class AppSession {

    private static AppSession instance;
    private User currentUser;
    private boolean isAdmin;

    // Constructeur privé (singleton)
    private AppSession() {}

    public static AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }

    // ==========================
    // USER
    // ==========================
    public void setUser(User user) {
        this.currentUser = user;
    }

    public User getUser() {
        return currentUser;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public boolean isLogged() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }
}