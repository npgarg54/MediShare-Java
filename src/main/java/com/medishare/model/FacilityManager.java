package com.medishare.model;

import com.medishare.enums.UserRole;

public class FacilityManager extends User {
    private final Facility facility;

    public FacilityManager(int userId, String username, String fullName, Facility facility) {
        super(userId, username, fullName, UserRole.FACILITY_MANAGER);
        this.facility = facility;
    }

    public Facility getFacility() { return facility; }

    @Override
    public String getDashboardTitle() {
        return facility.getFacilityName() + " - FACILITY MANAGER";
    }
}
