package com.medishare.dao;

import com.medishare.model.InventoryBatch;
import com.medishare.model.StockSummary;

import java.time.LocalDate;
import java.util.List;

/** Persistence contract used by InventoryService; JDBC is one implementation. */
public interface InventoryRepository {
    List<InventoryBatch> findByFacility(int facilityId, String search);
    void addBatch(int facilityId, int medicineId, String batchNumber, int quantity,
                  LocalDate expiryDate, LocalDate receivedDate);
    boolean updateQuantity(int facilityId, int batchId, int quantity);
    boolean deleteBatch(int facilityId, int batchId);
    List<StockSummary> findLowStock(int facilityId, LocalDate today);
}
