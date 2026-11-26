package com.medishare.model;

import com.medishare.enums.UserRole;

public abstract class User {
    private final int userId;
    private final String username;
    private final String fullName;
    private final UserRole role;

    protected User(int userId, String username, String fullName, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public UserRole getRole() { return role; }
    public abstract String getDashboardTitle();
}
