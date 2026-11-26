package com.medishare.service;

import com.medishare.exception.ValidationException;
import java.time.LocalDate;

/** Pure business rules kept separate so they can be tested without a database. */
public final class InventoryRules {
    private InventoryRules() { }

    public static void validateQuantity(int quantity) {
        if (quantity < 0) throw new ValidationException("Quantity cannot be negative.");
    }

    public static void validateNewBatch(String batchNumber, int quantity,
                                        LocalDate receivedDate, LocalDate expiryDate) {
        if (batchNumber == null || batchNumber.isBlank()) {
            throw new ValidationException("Batch number is required.");
        }
        validateQuantity(quantity);
        if (receivedDate == null || expiryDate == null) {
            throw new ValidationException("Received date and expiry date are required.");
        }
        if (!expiryDate.isAfter(receivedDate)) {
            throw new ValidationException("Expiry date must be after the received date.");
        }
    }

    public static boolean isExpired(LocalDate expiryDate, LocalDate today) {
        return expiryDate.isBefore(today);
    }

    public static boolean isNearExpiry(LocalDate expiryDate, LocalDate today, int warningDays) {
        return !expiryDate.isBefore(today) && !expiryDate.isAfter(today.plusDays(warningDays));
    }
}
