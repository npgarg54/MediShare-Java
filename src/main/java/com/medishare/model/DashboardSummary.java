package com.medishare.model;

public class DashboardSummary {
    private final int totalBatches;
    private final int totalUsableUnits;
    private final int expiredBatches;
    private final int nearExpiryBatches;
    private final int lowStockMedicines;

    public DashboardSummary(int totalBatches, int totalUsableUnits, int expiredBatches,
                            int nearExpiryBatches, int lowStockMedicines) {
        this.totalBatches = totalBatches;
        this.totalUsableUnits = totalUsableUnits;
        this.expiredBatches = expiredBatches;
        this.nearExpiryBatches = nearExpiryBatches;
        this.lowStockMedicines = lowStockMedicines;
    }

    public int getTotalBatches() { return totalBatches; }
    public int getTotalUsableUnits() { return totalUsableUnits; }
    public int getExpiredBatches() { return expiredBatches; }
    public int getNearExpiryBatches() { return nearExpiryBatches; }
    public int getLowStockMedicines() { return lowStockMedicines; }
}
