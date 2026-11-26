package com.medishare.service;

import com.medishare.model.InventoryBatch;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class AlertService {
    public static final int DEFAULT_WARNING_DAYS = 60;

    public List<InventoryBatch> getExpiredBatches(List<InventoryBatch> batches, LocalDate today) {
        return batches.stream()
                .filter(batch -> InventoryRules.isExpired(batch.getExpiryDate(), today))
                .sorted(Comparator.comparing(InventoryBatch::getExpiryDate))
                .toList();
    }

    public List<InventoryBatch> getNearExpiryBatches(List<InventoryBatch> batches, LocalDate today, int warningDays) {
        return batches.stream()
                .filter(batch -> InventoryRules.isNearExpiry(batch.getExpiryDate(), today, warningDays))
                .sorted(Comparator.comparing(InventoryBatch::getExpiryDate))
                .toList();
    }
}
