package com.medishare.model;

import com.medishare.enums.UserRole;

public class Admin extends User {
    public Admin(int userId, String username, String fullName) {
        super(userId, username, fullName, UserRole.ADMIN);
    }

    @Override
    public String getDashboardTitle() {
        return "SYSTEM ADMIN CONSOLE";
    }
}
