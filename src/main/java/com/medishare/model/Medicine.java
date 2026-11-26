package com.medishare.model;

public class Medicine {
    private final int medicineId;
    private final String medicineCode;
    private final String genericName;
    private final String strength;
    private final String dosageForm;
    private final String category;
    private final int reorderLevel;

    public Medicine(int medicineId, String medicineCode, String genericName, String strength,
                    String dosageForm, String category, int reorderLevel) {
        this.medicineId = medicineId;
        this.medicineCode = medicineCode;
        this.genericName = genericName;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.category = category;
        this.reorderLevel = reorderLevel;
    }

    public int getMedicineId() { return medicineId; }
    public String getMedicineCode() { return medicineCode; }
    public String getGenericName() { return genericName; }
    public String getStrength() { return strength; }
    public String getDosageForm() { return dosageForm; }
    public String getCategory() { return category; }
    public int getReorderLevel() { return reorderLevel; }

    public String getDisplayName() {
        return genericName + " " + strength + " (" + dosageForm + ")";
    }
}
