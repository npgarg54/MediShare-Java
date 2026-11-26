package com.medishare.service;

import com.medishare.dao.MedicineDAO;
import com.medishare.exception.ValidationException;
import com.medishare.model.Medicine;
import com.medishare.util.MedicineInputRules;

import java.util.List;

public class MedicineService {
    private final MedicineDAO medicineDAO;

    public MedicineService(MedicineDAO medicineDAO) {
        this.medicineDAO = medicineDAO;
    }

    public List<Medicine> search(String query) {
        return medicineDAO.search(query);
    }

    public List<Medicine> searchInactive(String query) {
        return medicineDAO.searchInactive(query);
    }

    public boolean existsByCode(String code) {
        return medicineDAO.existsByCode(MedicineInputRules.normalizeCode(code));
    }

    public Medicine findById(int medicineId) {
        return medicineDAO.findById(medicineId);
    }

    public Medicine findInactiveById(int medicineId) {
        return medicineDAO.findInactiveById(medicineId);
    }

    public boolean deactivate(int medicineId) {
        if (medicineId <= 0) throw new ValidationException("Enter a valid medicine ID.");
        return medicineDAO.setActive(medicineId, false);
    }

    public boolean restore(int medicineId) {
        if (medicineId <= 0) throw new ValidationException("Enter a valid medicine ID.");
        return medicineDAO.setActive(medicineId, true);
    }

    public void addMedicine(String code, String name, String strength, String dosageForm,
                            String category, int reorderLevel) {
        if (reorderLevel < 0) throw new ValidationException("Reorder level cannot be negative.");
        String normalizedCode = MedicineInputRules.normalizeCode(code);
        String normalizedName = MedicineInputRules.normalizeTitle(name);
        String normalizedStrength = MedicineInputRules.normalizeStrength(strength);
        String normalizedForm = MedicineInputRules.normalizeDosageForm(dosageForm);
        String normalizedCategory = MedicineInputRules.normalizeTitle(category);
        if (normalizedName.isBlank() || normalizedCategory.isBlank()) {
            throw new ValidationException("Generic name and category are required.");
        }
        if (medicineDAO.existsByCode(normalizedCode)) {
            throw new ValidationException("Medicine code already exists. Enter a different code.");
        }
        medicineDAO.add(new Medicine(0, normalizedCode, normalizedName, normalizedStrength,
                normalizedForm, normalizedCategory, reorderLevel));
    }
}
