package com.medishare.model;

public class StockSummary {
    private final int medicineId;
    private final String medicineCode;
    private final String medicineName;
    private final String strength;
    private final int usableQuantity;
    private final int reorderLevel;

    public StockSummary(int medicineId, String medicineCode, String medicineName,
                        String strength, int usableQuantity, int reorderLevel) {
        this.medicineId = medicineId;
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.strength = strength;
        this.usableQuantity = usableQuantity;
        this.reorderLevel = reorderLevel;
    }

    public int getMedicineId() { return medicineId; }
    public String getMedicineCode() { return medicineCode; }
    public String getMedicineName() { return medicineName; }
    public String getStrength() { return strength; }
    public int getUsableQuantity() { return usableQuantity; }
    public int getReorderLevel() { return reorderLevel; }
}
