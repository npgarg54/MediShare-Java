package com.medishare.service;

import com.medishare.model.DashboardSummary;
import com.medishare.model.InventoryBatch;
import com.medishare.model.StockSummary;

import java.time.LocalDate;
import java.util.List;

public class DashboardService {
    private final InventoryService inventoryService;
    private final AlertService alertService;

    public DashboardService(InventoryService inventoryService, AlertService alertService) {
        this.inventoryService = inventoryService;
        this.alertService = alertService;
    }

    public DashboardSummary getSummary(int facilityId) {
        List<InventoryBatch> batches = inventoryService.getInventory(facilityId, "");
        LocalDate today = LocalDate.now();
        int expired = alertService.getExpiredBatches(batches, today).size();
        int nearExpiry = alertService.getNearExpiryBatches(batches, today, AlertService.DEFAULT_WARNING_DAYS).size();
        int usableUnits = batches.stream()
                .filter(batch -> !InventoryRules.isExpired(batch.getExpiryDate(), today))
                .mapToInt(InventoryBatch::getQuantity)
                .sum();
        List<StockSummary> lowStock = inventoryService.getLowStock(facilityId);
        return new DashboardSummary(batches.size(), usableUnits, expired, nearExpiry, lowStock.size());
    }
}
