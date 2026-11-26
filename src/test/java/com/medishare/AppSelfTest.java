package com.medishare;

import com.medishare.exception.LogoutRequestedException;
import com.medishare.exception.OperationCancelledException;
import com.medishare.model.InventoryBatch;
import com.medishare.service.AlertService;
import com.medishare.service.InventoryRules;
import com.medishare.ui.InputHelper;
import com.medishare.util.MedicineInputRules;
import com.medishare.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/** Dependency-free checks for the core rules and console navigation. */
public class AppSelfTest {
    public static void main(String[] args) {
        int passed = 0;
        String encoded = PasswordUtil.hash("demo123");
        check(PasswordUtil.verify("demo123", encoded), "password hash verifies"); passed++;
        check(!PasswordUtil.verify("wrong", encoded), "incorrect password is rejected"); passed++;

        LocalDate today = LocalDate.of(2026, 11, 24);
        check(InventoryRules.isExpired(today.minusDays(1), today), "yesterday is expired"); passed++;
        check(!InventoryRules.isExpired(today, today), "today is not expired"); passed++;
        check(InventoryRules.isNearExpiry(today, today, 60), "today is inside warning window"); passed++;
        check(InventoryRules.isNearExpiry(today.plusDays(60), today, 60), "day 60 is inside warning window"); passed++;
        check(!InventoryRules.isNearExpiry(today.plusDays(61), today, 60), "day 61 is outside warning window"); passed++;

        InventoryRules.validateQuantity(0);
        boolean rejectedNegative = false;
        try { InventoryRules.validateQuantity(-1); } catch (RuntimeException ex) { rejectedNegative = true; }
        check(rejectedNegative, "negative quantity is rejected"); passed++;

        InventoryBatch later = batch(1, today.plusDays(40));
        InventoryBatch sooner = batch(2, today.plusDays(8));
        List<InventoryBatch> sorted = new AlertService().getNearExpiryBatches(List.of(later, sooner), today, 60);
        check(sorted.get(0).getBatchId() == 2, "expiry alerts sort earliest expiry first"); passed++;

        check("MED010".equals(MedicineInputRules.normalizeCode(" med 010 ")), "medicine codes normalize to uppercase and remove spaces"); passed++;
        check("Paracetamol".equals(MedicineInputRules.normalizeTitle("pArAcEtAmOl")), "medicine names normalize to title case"); passed++;
        check("Oral Rehydration Salts".equals(MedicineInputRules.normalizeTitle(" oral   rehydration salts ")), "multiple spaces collapse and each word is capitalized"); passed++;
        check("BATCH-2026A".equals(MedicineInputRules.normalizeBatchNumber(" batch-2026a ")), "batch numbers normalize to uppercase"); passed++;
        check("500 mg".equals(MedicineInputRules.normalizeStrength("500 MG")), "strength unit is standardized"); passed++;
        check("500 mg".equals(MedicineInputRules.normalizeStrength("500mg")), "strength spacing is standardized"); passed++;
        check("100 mg/5 mL".equals(MedicineInputRules.normalizeStrength("100 mg / 5 ml")), "compound strength formats normalize correctly"); passed++;
        check("2 mg/mL".equals(MedicineInputRules.normalizeStrength("2mg/ml")), "per-volume strength formats are accepted"); passed++;
        check("0.9%".equals(MedicineInputRules.normalizeStrength("0.9%")), "percentage strengths are accepted"); passed++;
        check("Not specified".equals(MedicineInputRules.normalizeStrength("not specified")), "non-numeric specifications use the controlled phrase"); passed++;
        boolean rejectedUnitless = false;
        try { MedicineInputRules.normalizeStrength("500"); } catch (RuntimeException ex) { rejectedUnitless = true; }
        check(rejectedUnitless, "unitless medicine strength is rejected"); passed++;
        check("Tablet".equals(MedicineInputRules.normalizeDosageForm("tablet")), "dosage forms normalize to a catalogue value"); passed++;
        check("Suppository".equals(MedicineInputRules.normalizeDosageForm("suppository")), "Other dosage forms can be entered with consistent casing"); passed++;

        boolean cancelled = false;
        try { new InputHelper(new Scanner(":cancel\n")).readRequired("test: "); }
        catch (OperationCancelledException ex) { cancelled = true; }
        check(cancelled, ":cancel exits a current input prompt"); passed++;
        boolean logout = false;
        try { new InputHelper(new Scanner(":logout\n")).readRequired("test: "); }
        catch (LogoutRequestedException ex) { logout = true; }
        check(logout, ":logout is recognized at an input prompt"); passed++;
        boolean cancelledNumber = false;
        try { new InputHelper(new Scanner(":cancel\n")).readInt("test: ", 1, 4); }
        catch (OperationCancelledException ex) { cancelledNumber = true; }
        check(cancelledNumber, ":cancel works during numeric prompts"); passed++;
        boolean logoutDate = false;
        try { new InputHelper(new Scanner(":logout\n")).readDate("test: "); }
        catch (LogoutRequestedException ex) { logoutDate = true; }
        check(logoutDate, ":logout works during date prompts"); passed++;

        System.out.println("MediShare Beta v0.2 self-test: " + passed + "/" + passed + " checks passed.");
    }

    private static InventoryBatch batch(int id, LocalDate expiry) {
        return new InventoryBatch(id, 1, 1, "MED001", "Paracetamol", "500 mg", "Tablet",
                "BATCH-" + id, 10, 20, expiry, LocalDate.of(2026, 1, 1));
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError("FAILED: " + description);
        System.out.println("[PASS] " + description);
    }
}
