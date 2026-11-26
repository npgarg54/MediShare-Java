package com.medishare.ui;

import com.medishare.exception.DataAccessException;
import com.medishare.exception.LogoutRequestedException;
import com.medishare.exception.OperationCancelledException;
import com.medishare.exception.ValidationException;
import com.medishare.model.Admin;
import com.medishare.model.Medicine;
import com.medishare.service.FacilityService;
import com.medishare.service.MedicineService;
import com.medishare.util.MedicineInputRules;

import java.util.List;

public class AdminMenu {
    private final FacilityService facilityService;
    private final MedicineService medicineService;
    private final InputHelper input;

    public AdminMenu(FacilityService facilityService, MedicineService medicineService, InputHelper input) {
        this.facilityService = facilityService;
        this.medicineService = medicineService;
        this.input = input;
    }

    public void show(Admin admin) {
        boolean active = true;
        while (active) {
            header(admin.getDashboardTitle(), admin.getFullName());
            System.out.println("1. View registered facilities");
            System.out.println("2. Search facilities");
            System.out.println("3. View active medicine catalogue");
            System.out.println("4. Search medicine catalogue");
            System.out.println("5. Add medicine to catalogue");
            System.out.println("6. Deactivate medicine");
            System.out.println("7. View / restore inactive medicines");
            System.out.println("8. Log out");
            try {
                int choice = input.readInt("Choose an option: ", 1, 8);
                switch (choice) {
                    case 1 -> TablePrinter.printFacilities(facilityService.getActiveFacilities());
                    case 2 -> searchFacilities();
                    case 3 -> TablePrinter.printMedicines(medicineService.search(""));
                    case 4 -> searchMedicines();
                    case 5 -> addMedicine();
                    case 6 -> deactivateMedicine();
                    case 7 -> restoreMedicine();
                    case 8 -> active = false;
                    default -> { }
                }
                if (active) pause();
            } catch (OperationCancelledException ex) {
                System.out.println("\nNo active form to cancel. You are on the Admin Dashboard.");
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

    private void searchFacilities() {
        String term = input.readRequired("Search facility (code, name, type, address or city): ");
        TablePrinter.printFacilities(facilityService.search(term));
    }

    private void searchMedicines() {
        String term = input.readRequired("Search medicine (code, name, strength, form or category): ");
        TablePrinter.printMedicines(medicineService.search(term));
    }

    private void addMedicine() {
        System.out.println("\nADD MEDICINE TO MASTER CATALOGUE");
        System.out.println("Enter :cancel at any prompt to abandon this form; enter :logout to log out.");

        String code;
        while (true) {
            String entered = input.readRequired("Medicine code: ");
            try {
                code = MedicineInputRules.normalizeCode(entered);
                if (medicineService.existsByCode(code)) {
                    System.out.println("[ERROR] Medicine code " + code + " already exists. Enter another code.");
                    continue;
                }
                System.out.println("[OK] Medicine code is available.");
                break;
            } catch (ValidationException | IllegalArgumentException ex) {
                System.out.println("[ERROR] " + ex.getMessage());
            }
        }

        String name = input.readRequired("Generic name: ");
        String strength;
        while (true) {
            String entered = input.readRequired("Strength / specification (e.g. 500 mg, 100 mg/5 mL, 0.9%, or Not specified): ");
            try {
                strength = MedicineInputRules.normalizeStrength(entered);
                break;
            } catch (ValidationException | IllegalArgumentException ex) {
                System.out.println("[ERROR] " + ex.getMessage());
            }
        }

        System.out.println("\nSELECT DOSAGE FORM");
        List<String> forms = MedicineInputRules.dosageForms();
        for (int i = 0; i < forms.size(); i++) System.out.println((i + 1) + ". " + forms.get(i));
        int formChoice = input.readInt("Choose dosage form: ", 1, forms.size());
        String form = forms.get(formChoice - 1);
        if (form.equals("Other")) form = input.readRequired("Enter dosage form: ");

        String category = input.readRequired("Category: ");
        int reorder = input.readInt("Default reorder level (0 or more): ", 0, 1_000_000);
        medicineService.addMedicine(code, name, strength, form, category, reorder);
        System.out.println("Medicine added successfully. Text fields were normalized before saving.");
    }

    private void deactivateMedicine() {
        List<Medicine> medicines = medicineService.search("");
        TablePrinter.printMedicines(medicines);
        if (medicines.isEmpty()) return;
        int medicineId = input.readInt("Enter medicine ID to deactivate: ", 1, Integer.MAX_VALUE);
        Medicine medicine = medicineService.findById(medicineId);
        if (medicine == null) {
            System.out.println("No active medicine found with that ID.");
            return;
        }
        System.out.println("\nMEDICINE TO DEACTIVATE");
        printMedicineDetails(medicine);
        System.out.println("Deactivation hides it from new inventory entries but preserves existing batches and alerts.");
        if (input.readInt("1. Confirm deactivation   2. Cancel: ", 1, 2) == 2) {
            System.out.println("Deactivation cancelled.");
            return;
        }
        if (medicineService.deactivate(medicineId)) System.out.println("Medicine deactivated. Existing inventory records are preserved.");
        else System.out.println("Medicine status was not changed. Refresh the catalogue and try again.");
    }

    private void restoreMedicine() {
        List<Medicine> inactive = medicineService.searchInactive("");
        System.out.println("\nINACTIVE MEDICINES");
        TablePrinter.printMedicines(inactive);
        if (inactive.isEmpty()) return;
        int medicineId = input.readInt("Enter medicine ID to restore: ", 1, Integer.MAX_VALUE);
        Medicine medicine = medicineService.findInactiveById(medicineId);
        if (medicine == null) {
            System.out.println("No inactive medicine found with that ID.");
            return;
        }
        printMedicineDetails(medicine);
        if (input.readInt("1. Confirm restore   2. Cancel: ", 1, 2) == 2) {
            System.out.println("Restore cancelled.");
            return;
        }
        if (medicineService.restore(medicineId)) System.out.println("Medicine restored to the active catalogue.");
        else System.out.println("Medicine status was not changed. Refresh the catalogue and try again.");
    }

    private void printMedicineDetails(Medicine medicine) {
        System.out.println("Code      : " + medicine.getMedicineCode());
        System.out.println("Name      : " + medicine.getGenericName());
        System.out.println("Strength  : " + medicine.getStrength());
        System.out.println("Form      : " + medicine.getDosageForm());
        System.out.println("Category  : " + medicine.getCategory());
    }

    private void header(String title, String name) {
        System.out.println("\n" + "=".repeat(64));
        System.out.println("  " + title);
        System.out.println("  Signed in as: " + name);
        System.out.println("=".repeat(64));
        System.out.println("Navigation: :cancel = cancel current form | :logout = return to login");
    }

    private void pause() { input.readOptional("\nPress Enter to continue..."); }
}
