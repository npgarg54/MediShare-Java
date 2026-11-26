package com.medishare.service;

import com.medishare.dao.InventoryRepository;
import com.medishare.dao.MedicineDAO;
import com.medishare.exception.ValidationException;
import com.medishare.model.InventoryBatch;
import com.medishare.model.Medicine;
import com.medishare.model.StockSummary;
import com.medishare.util.MedicineInputRules;

import java.time.LocalDate;
import java.util.List;

public class InventoryService {
    private final InventoryRepository inventoryDAO;
    private final MedicineDAO medicineDAO;

    public InventoryService(InventoryRepository inventoryDAO, MedicineDAO medicineDAO) {
        this.inventoryDAO = inventoryDAO;
        this.medicineDAO = medicineDAO;
    }

    public List<InventoryBatch> getInventory(int facilityId, String search) {
        return inventoryDAO.findByFacility(facilityId, search);
    }

    public void addBatch(int facilityId, int medicineId, String batchNumber, int quantity,
                         LocalDate receivedDate, LocalDate expiryDate) {
        InventoryRules.validateNewBatch(batchNumber, quantity, receivedDate, expiryDate);
        if (receivedDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Received date cannot be in the future.");
        }
        if (!expiryDate.isAfter(LocalDate.now())) {
            throw new ValidationException("An expired batch cannot be added as new inventory.");
        }
        Medicine medicine = medicineDAO.findById(medicineId);
        if (medicine == null) throw new ValidationException("Medicine was not found in the active catalogue.");
        inventoryDAO.addBatch(facilityId, medicineId, MedicineInputRules.normalizeBatchNumber(batchNumber),
                quantity, expiryDate, receivedDate);
    }

    public void adjustQuantity(int facilityId, int batchId, int newQuantity) {
        InventoryRules.validateQuantity(newQuantity);
        if (batchId <= 0) throw new ValidationException("Enter a valid batch ID.");
        boolean updated = inventoryDAO.updateQuantity(facilityId, batchId, newQuantity);
        if (!updated) throw new ValidationException("Batch not found in your facility. No quantity was changed.");
    }

    public void deleteBatch(int facilityId, int batchId) {
        if (batchId <= 0) throw new ValidationException("Enter a valid batch ID.");
        boolean deleted = inventoryDAO.deleteBatch(facilityId, batchId);
        if (!deleted) throw new ValidationException("Batch not found in your facility. Nothing was deleted.");
    }

    public List<StockSummary> getLowStock(int facilityId) {
        return inventoryDAO.findLowStock(facilityId, LocalDate.now());
    }
}
