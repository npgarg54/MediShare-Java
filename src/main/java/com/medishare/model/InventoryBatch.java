package com.medishare.model;

import java.time.LocalDate;

public class InventoryBatch {
    private final int batchId;
    private final int facilityId;
    private final int medicineId;
    private final String medicineCode;
    private final String medicineName;
    private final String strength;
    private final String dosageForm;
    private final String batchNumber;
    private final int quantity;
    private final int reorderLevel;
    private final LocalDate expiryDate;
    private final LocalDate receivedDate;

    public InventoryBatch(int batchId, int facilityId, int medicineId, String medicineCode,
                          String medicineName, String strength, String dosageForm,
                          String batchNumber, int quantity, int reorderLevel,
                          LocalDate expiryDate, LocalDate receivedDate) {
        this.batchId = batchId;
        this.facilityId = facilityId;
        this.medicineId = medicineId;
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
    }

    public int getBatchId() { return batchId; }
    public int getFacilityId() { return facilityId; }
    public int getMedicineId() { return medicineId; }
    public String getMedicineCode() { return medicineCode; }
    public String getMedicineName() { return medicineName; }
    public String getStrength() { return strength; }
    public String getDosageForm() { return dosageForm; }
    public String getBatchNumber() { return batchNumber; }
    public int getQuantity() { return quantity; }
    public int getReorderLevel() { return reorderLevel; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public LocalDate getReceivedDate() { return receivedDate; }

    public String getMedicineDisplayName() {
        return medicineName + " " + strength + " (" + dosageForm + ")";
    }
}
