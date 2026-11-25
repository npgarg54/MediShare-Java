package com.medishare.service;

import com.medishare.dao.UserDAO;
import com.medishare.model.User;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return null;
        return userDAO.authenticate(username, password);
    }
}
