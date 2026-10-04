package com.petcare.service;

import com.petcare.dao.UserDAO;
import com.petcare.exception.AuthenticationException;
import com.petcare.exception.DatabaseException;
import com.petcare.model.User;
import com.petcare.model.UserRole;

/**
 * Authentication and Session Management Service
 */
public class AuthService {

    private static AuthService instance;
    private final UserDAO userDAO;
    private User currentUser;

    private AuthService() {
        this.userDAO = new UserDAO();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User login(String username, String password) throws DatabaseException, AuthenticationException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Please enter both username and password.");
        }
        this.currentUser = userDAO.authenticate(username.trim(), password.trim());
        return this.currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
}
