package com.medishare.model;

import com.medishare.enums.FacilityType;

public class Facility {
    private final int facilityId;
    private final String facilityCode;
    private final String facilityName;
    private final FacilityType facilityType;
    private final String address;
    private final String city;

    public Facility(int facilityId, String facilityCode, String facilityName,
                    FacilityType facilityType, String address, String city) {
        this.facilityId = facilityId;
        this.facilityCode = facilityCode;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.city = city;
    }

    public int getFacilityId() { return facilityId; }
    public String getFacilityCode() { return facilityCode; }
    public String getFacilityName() { return facilityName; }
    public FacilityType getFacilityType() { return facilityType; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
}
