package com.medishare.enums;

public enum UserRole {
    ADMIN,
    FACILITY_MANAGER;

    public static UserRole fromDatabase(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }
}
