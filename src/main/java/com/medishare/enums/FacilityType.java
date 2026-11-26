package com.medishare.enums;

public enum FacilityType {
    HOSPITAL,
    CLINIC,
    PHARMACY;

    public static FacilityType fromInput(String value) {
        return FacilityType.valueOf(value.trim().toUpperCase());
    }
}
