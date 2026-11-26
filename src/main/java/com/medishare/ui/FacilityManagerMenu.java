package com.medishare.ui;

import com.medishare.exception.DataAccessException;
import com.medishare.exception.LogoutRequestedException;
import com.medishare.exception.OperationCancelledException;
import com.medishare.exception.ValidationException;
import com.medishare.model.DashboardSummary;
import com.medishare.model.FacilityManager;
import com.medishare.model.InventoryBatch;
import com.medishare.model.Medicine;
import com.medishare.service.AlertService;
import com.medishare.service.DashboardService;
import com.medishare.service.InventoryService;
import com.medishare.service.MedicineService;
import com.medishare.util.DateUtil;

import java.time.LocalDate;
import java.util.List;

public class FacilityManagerMenu {
    private final InventoryService inventoryService;
    private final MedicineService medicineService;
    private final DashboardService dashboardService;
    private final AlertService alertService;
    private final InputHelper input;

    public FacilityManagerMenu(InventoryService inventoryService, MedicineService medicineService,
                               DashboardService dashboardService, AlertService alertService, InputHelper input) {
        this.inventoryService = inventoryService;
        this.medicineService = medicineService;
        this.dashboardService = dashboardService;
        this.alertService = alertService;
        this.input = input;
    }

    public void show(FacilityManager manager) {
        boolean active = true;
        int facilityId = manager.getFacility().getFacilityId();
        while (active) {
            header(manager);
            System.out.println("1. View inventory dashboard");
            System.out.println("2. View all inventory batches");
            System.out.println("3. Search inventory");
            System.out.println("4. Add inventory batch");
            System.out.println("5. Adjust batch quantity");
            System.out.println("6. Delete inventory batch");
            System.out.println("7. View expiry alerts");
            System.out.println("8. View low-stock alerts");
            System.out.println("9. Search medicine catalogue");
            System.out.println("10. Log out");
            try {
                int choice = input.readInt("Choose an option: ", 1, 10);
                switch (choice) {
                    case 1 -> showDashboard(facilityId);
                    case 2 -> TablePrinter.printInventory(inventoryService.getInventory(facilityId, ""));
                    case 3 -> searchInventory(facilityId);
                    case 4 -> addBatch(facilityId);
                    case 5 -> adjustQuantity(facilityId);
                    case 6 -> deleteBatch(facilityId);
                    case 7 -> showExpiryAlerts(facilityId);
                    case 8 -> TablePrinter.printLowStock(inventoryService.getLowStock(facilityId));
                    case 9 -> searchCatalogue();
                    case 10 -> active = false;
                    default -> { }
                }
                if (active) input.readOptional("\nPress Enter to continue...");
            } catch (OperationCancelledException ex) {
                System.out.println("\nNo active form to cancel. You are on the Facility Dashboard.");
            } catch (LogoutRequestedException ex) {
                System.out.println("\nLogging out...");
                active = false;
            } catch (ValidationException | IllegalArgumentException ex) {
                System.out.println("\n[VALIDATION] " + ex.getMessage());
            } catch (DataAccessException ex) {
                System.out.println("\n[DATABASE] " + ex.getMessage());
            }
        }
    }

    private void showDashboard(int facilityId) {
        DashboardSummary s = dashboardService.getSummary(facilityId);
        System.out.println("\nINVENTORY DASHBOARD");
        System.out.println("-".repeat(52));
        System.out.printf("Total inventory batches       : %d%n", s.getTotalBatches());
        System.out.printf("Usable stock units             : %d%n", s.getTotalUsableUnits());
        System.out.printf("Expired batches                : %d%n", s.getExpiredBatches());
        System.out.printf("Batches expiring within 60 days: %d%n", s.getNearExpiryBatches());
        System.out.printf("Medicines below reorder level  : %d%n", s.getLowStockMedicines());
        System.out.println("-".repeat(52));
        System.out.println("Note: usable stock excludes expired batches.");
    }

    private void searchInventory(int facilityId) {
        String term = input.readRequired("Search by medicine code, name or batch number (partial text works): ");
        TablePrinter.printInventory(inventoryService.getInventory(facilityId, term));
    }

    private void addBatch(int facilityId) {
        System.out.println("\nACTIVE MEDICINE CATALOGUE");
        System.out.println("Enter :cancel at any prompt to abandon this form; enter :logout to log out.");
        List<Medicine> medicines = medicineService.search("");
        TablePrinter.printMedicines(medicines);
        if (medicines.isEmpty()) {
            System.out.println("No active medicines are available. Contact the System Admin.");
            return;
        }
        int medicineId = input.readInt("Enter medicine ID from the catalogue: ", 1, Integer.MAX_VALUE);
        String batchNumber = input.readRequired("Batch number: ");
        int quantity = input.readInt("Quantity: ", 0, 1_000_000_000);
        LocalDate receivedDate = LocalDate.now();
        System.out.println("Received date will be recorded as today: " + DateUtil.display(receivedDate));
        LocalDate expiryDate = input.readDate("Expiry date (yyyy-MM-dd): ");
        inventoryService.addBatch(facilityId, medicineId, batchNumber, quantity, receivedDate, expiryDate);
        System.out.println("Inventory batch added successfully.");
    }

    private void adjustQuantity(int facilityId) {
        TablePrinter.printInventory(inventoryService.getInventory(facilityId, ""));
        int batchId = input.readInt("Enter batch ID to adjust: ", 1, Integer.MAX_VALUE);
        int quantity = input.readInt("Enter the new current quantity (0 or more): ", 0, 1_000_000_000);
        inventoryService.adjustQuantity(facilityId, batchId, quantity);
        System.out.println("Batch quantity updated successfully.");
        System.out.println("The detailed stock movement audit is planned for Phase 2.");
    }

    private void deleteBatch(int facilityId) {
        List<InventoryBatch> batches = inventoryService.getInventory(facilityId, "");
        TablePrinter.printInventory(batches);
        if (batches.isEmpty()) return;
        int batchId = input.readInt("Enter batch ID to delete: ", 1, Integer.MAX_VALUE);
        InventoryBatch selected = batches.stream().filter(batch -> batch.getBatchId() == batchId).findFirst().orElse(null);
        if (selected == null) {
            System.out.println("Batch not found in your facility. Nothing was deleted.");
            return;
        }

        System.out.println("\nBATCH TO DELETE");
        System.out.println("Medicine : " + selected.getMedicineDisplayName());
        System.out.println("Batch no.: " + selected.getBatchNumber());
        System.out.println("Quantity : " + selected.getQuantity());
        System.out.println("Expiry   : " + DateUtil.display(selected.getExpiryDate()));
        System.out.println("\n1. Confirm deletion");
        System.out.println("2. Cancel");
        if (input.readInt("Choose an option: ", 1, 2) == 2) {
            System.out.println("Deletion cancelled. No changes were made.");
            return;
        }
        inventoryService.deleteBatch(facilityId, batchId);
        System.out.println("Inventory batch deleted successfully.");
    }

    private void showExpiryAlerts(int facilityId) {
        List<InventoryBatch> batches = inventoryService.getInventory(facilityId, "");
        TablePrinter.printExpirySection("EXPIRED BATCHES - DO NOT INCLUDE IN USABLE STOCK",
                alertService.getExpiredBatches(batches, LocalDate.now()));
        TablePrinter.printExpirySection("EXPIRING WITHIN THE NEXT " + AlertService.DEFAULT_WARNING_DAYS + " DAYS",
                alertService.getNearExpiryBatches(batches, LocalDate.now(), AlertService.DEFAULT_WARNING_DAYS));
    }

    private void searchCatalogue() {
        String term = input.readRequired("Search medicine catalogue (partial code, name, strength or category): ");
        TablePrinter.printMedicines(medicineService.search(term));
    }

    private void header(FacilityManager manager) {
        System.out.println("\n" + "=".repeat(64));
        System.out.println("  " + manager.getDashboardTitle());
        System.out.println("  Signed in as: " + manager.getFullName());
        System.out.println("  Facility code: " + manager.getFacility().getFacilityCode());
        System.out.println("=".repeat(64));
        System.out.println("Navigation: :cancel = cancel current form | :logout = return to login");
    }
}
