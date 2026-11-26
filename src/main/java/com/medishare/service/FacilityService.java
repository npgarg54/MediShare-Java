package com.medishare.service;

import com.medishare.dao.FacilityDAO;
import com.medishare.model.Facility;

import java.util.List;

public class FacilityService {
    private final FacilityDAO facilityDAO;

    public FacilityService(FacilityDAO facilityDAO) {
        this.facilityDAO = facilityDAO;
    }

    public List<Facility> getActiveFacilities() {
        return facilityDAO.findAllActive();
    }

    public List<Facility> search(String query) {
        return facilityDAO.search(query);
    }
}
