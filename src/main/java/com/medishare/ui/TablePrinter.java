package com.medishare.ui;

import com.medishare.model.Facility;
import com.medishare.model.InventoryBatch;
import com.medishare.model.Medicine;
import com.medishare.model.StockSummary;
import com.medishare.util.DateUtil;

import java.util.List;

public final class TablePrinter {
    private TablePrinter() { }

    public static void printFacilities(List<Facility> facilities) {
        line();
        System.out.printf("%-8s %-30s %-12s %-22s%n", "CODE", "FACILITY", "TYPE", "CITY");
        line();
        if (facilities.isEmpty()) System.out.println("No facilities found.");
        for (Facility f : facilities) {
            System.out.printf("%-8s %-30s %-12s %-22s%n", f.getFacilityCode(), crop(f.getFacilityName(), 29),
                    f.getFacilityType(), crop(f.getCity(), 21));
        }
        line();
    }

    public static void printMedicines(List<Medicine> medicines) {
        line();
        System.out.printf("%-8s %-9s %-25s %-12s %-15s %8s%n", "ID", "CODE", "MEDICINE", "STRENGTH", "FORM", "REORDER");
        line();
        if (medicines.isEmpty()) System.out.println("No medicines found.");
        for (Medicine m : medicines) {
            System.out.printf("%-8d %-9s %-25s %-12s %-15s %8d%n", m.getMedicineId(), m.getMedicineCode(),
                    crop(m.getGenericName(), 24), crop(m.getStrength(), 11), crop(m.getDosageForm(), 14), m.getReorderLevel());
        }
        line();
    }

    public static void printInventory(List<InventoryBatch> batches) {
        line();
        System.out.printf("%-5s %-8s %-24s %-14s %8s %-13s %-13s%n", "ID", "CODE", "MEDICINE", "BATCH", "QTY", "EXPIRY", "STATUS");
        line();
        if (batches.isEmpty()) System.out.println("No inventory batches found.");
        for (InventoryBatch b : batches) {
            String status = b.getExpiryDate().isBefore(java.time.LocalDate.now()) ? "EXPIRED"
                    : InventoryBatchStatus.isNear(b.getExpiryDate()) ? "NEAR EXPIRY" : "NORMAL";
            System.out.printf("%-5d %-8s %-24s %-14s %8d %-13s %-13s%n", b.getBatchId(), b.getMedicineCode(),
                    crop(b.getMedicineName(), 23), crop(b.getBatchNumber(), 13), b.getQuantity(),
                    DateUtil.display(b.getExpiryDate()), status);
        }
        line();
    }

    public static void printLowStock(List<StockSummary> summaries) {
        line();
        System.out.printf("%-8s %-10s %-26s %12s %12s%n", "ID", "CODE", "MEDICINE", "USABLE QTY", "REORDER AT");
        line();
        if (summaries.isEmpty()) System.out.println("No low-stock medicines. Inventory is above configured reorder levels.");
        for (StockSummary s : summaries) {
            System.out.printf("%-8d %-10s %-26s %12d %12d%n", s.getMedicineId(), s.getMedicineCode(),
                    crop(s.getMedicineName() + " " + s.getStrength(), 25), s.getUsableQuantity(), s.getReorderLevel());
        }
        line();
    }

    public static void printExpirySection(String title, List<InventoryBatch> batches) {
        System.out.println("\n" + title);
        line();
        if (batches.isEmpty()) {
            System.out.println("No batches in this category.");
            return;
        }
        System.out.printf("%-5s %-9s %-24s %-14s %8s %-13s%n", "ID", "CODE", "MEDICINE", "BATCH", "QTY", "EXPIRY");
        line();
        for (InventoryBatch b : batches) {
            System.out.printf("%-5d %-9s %-24s %-14s %8d %-13s%n", b.getBatchId(), b.getMedicineCode(),
                    crop(b.getMedicineName(), 23), crop(b.getBatchNumber(), 13), b.getQuantity(), DateUtil.display(b.getExpiryDate()));
        }
        line();
    }

    private static String crop(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 1) + "~";
    }

    private static void line() { System.out.println("-".repeat(104)); }

    private static class InventoryBatchStatus {
        private static boolean isNear(java.time.LocalDate expiry) {
            java.time.LocalDate today = java.time.LocalDate.now();
            return !expiry.isBefore(today) && !expiry.isAfter(today.plusDays(60));
        }
    }
}
