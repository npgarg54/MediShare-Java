package com.medishare.service;

import com.medishare.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/** Inserts repeatable fictional demonstration records without duplicating existing rows. */
public class DataSeeder {
    public void seedIfNeeded(Connection connection) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            int cityCare = ensureFacility(connection, "FC001", "City Care Clinic", "CLINIC", "Model Town", "Ludhiana");
            int greenLife = ensureFacility(connection, "FC002", "GreenLife Pharmacy", "PHARMACY", "Ferozepur Road", "Ludhiana");
            int sunrise = ensureFacility(connection, "FC003", "Sunrise Community Hospital", "HOSPITAL", "Civil Lines", "Ludhiana");

            ensureUser(connection, null, "admin", "admin123", "System Administrator", "ADMIN");
            ensureUser(connection, cityCare, "manager_citycare", "demo123", "City Care Manager", "FACILITY_MANAGER");
            ensureUser(connection, greenLife, "manager_greenlife", "demo123", "GreenLife Manager", "FACILITY_MANAGER");
            ensureUser(connection, sunrise, "manager_sunrise", "demo123", "Sunrise Manager", "FACILITY_MANAGER");

            int paracetamol = ensureMedicine(connection, "MED001", "Paracetamol", "500 mg", "Tablet", "Analgesic", 50);
            int cetirizine = ensureMedicine(connection, "MED002", "Cetirizine", "10 mg", "Tablet", "Antihistamine", 25);
            int ors = ensureMedicine(connection, "MED003", "Oral Rehydration Salts", "Not specified", "Sachet", "Rehydration", 30);
            int metformin = ensureMedicine(connection, "MED004", "Metformin", "500 mg", "Tablet", "Antidiabetic", 40);
            int antiseptic = ensureMedicine(connection, "MED005", "Antiseptic Solution", "100 ml", "Solution", "Antiseptic", 15);
            int zinc = ensureMedicine(connection, "MED006", "Zinc", "20 mg", "Tablet", "Mineral", 20);

            LocalDate today = LocalDate.now();
            ensureBatch(connection, cityCare, paracetamol, "CC-PAR-101", 100, today.plusDays(30), today.minusDays(10));
            ensureBatch(connection, cityCare, paracetamol, "CC-PAR-102", 50, today.plusDays(240), today.minusDays(5));
            ensureBatch(connection, cityCare, cetirizine, "CC-CET-201", 12, today.plusDays(180), today.minusDays(20));
            ensureBatch(connection, cityCare, ors, "CC-ORS-301", 80, today.plusDays(300), today.minusDays(15));
            ensureBatch(connection, cityCare, ors, "CC-ORS-302", 5, today.minusDays(10), today.minusDays(50));

            ensureBatch(connection, greenLife, paracetamol, "GL-PAR-101", 65, today.plusDays(200), today.minusDays(5));
            ensureBatch(connection, greenLife, ors, "GL-ORS-301", 100, today.plusDays(150), today.minusDays(4));
            ensureBatch(connection, greenLife, metformin, "GL-MET-401", 55, today.plusDays(270), today.minusDays(8));
            ensureBatch(connection, greenLife, zinc, "GL-ZNC-601", 25, today.plusDays(45), today.minusDays(3));

            ensureBatch(connection, sunrise, cetirizine, "SH-CET-201", 60, today.plusDays(210), today.minusDays(8));
            ensureBatch(connection, sunrise, antiseptic, "SH-ANT-501", 15, today.plusDays(10), today.minusDays(30));
            ensureBatch(connection, sunrise, metformin, "SH-MET-401", 90, today.plusDays(240), today.minusDays(12));

            connection.commit();
        } catch (SQLException | RuntimeException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private int ensureFacility(Connection c, String code, String name, String type, String address, String city) throws SQLException {
        Integer existing = findId(c, "SELECT facility_id FROM facilities WHERE facility_code = ?", code);
        if (existing != null) return existing;
        String sql = "INSERT INTO facilities (facility_code, facility_name, facility_type, address, city) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code); ps.setString(2, name); ps.setString(3, type); ps.setString(4, address); ps.setString(5, city);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
    }

    private void ensureUser(Connection c, Integer facilityId, String username, String password,
                            String fullName, String role) throws SQLException {
        if (findId(c, "SELECT user_id FROM users WHERE username = ?", username) != null) return;
        String sql = "INSERT INTO users (facility_id, username, password_hash, full_name, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (facilityId == null) ps.setNull(1, java.sql.Types.INTEGER); else ps.setInt(1, facilityId);
            ps.setString(2, username); ps.setString(3, PasswordUtil.hash(password)); ps.setString(4, fullName); ps.setString(5, role);
            ps.executeUpdate();
        }
    }

    private int ensureMedicine(Connection c, String code, String name, String strength, String form,
                               String category, int reorderLevel) throws SQLException {
        Integer existing = findId(c, "SELECT medicine_id FROM medicines WHERE medicine_code = ?", code);
        if (existing != null) {
            if ("MED003".equals(code)) {
                try (PreparedStatement update = c.prepareStatement(
                        "UPDATE medicines SET strength = 'Not specified' WHERE medicine_code = 'MED003' AND strength = 'Standard'")) {
                    update.executeUpdate();
                }
            }
            return existing;
        }
        String sql = "INSERT INTO medicines (medicine_code, generic_name, strength, dosage_form, category, reorder_level) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code); ps.setString(2, name); ps.setString(3, strength); ps.setString(4, form);
            ps.setString(5, category); ps.setInt(6, reorderLevel); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
    }

    private void ensureBatch(Connection c, int facilityId, int medicineId, String batchNumber,
                             int quantity, LocalDate expiryDate, LocalDate receivedDate) throws SQLException {
        String find = "SELECT batch_id FROM inventory_batches WHERE facility_id = ? AND medicine_id = ? AND batch_number = ?";
        try (PreparedStatement ps = c.prepareStatement(find)) {
            ps.setInt(1, facilityId); ps.setInt(2, medicineId); ps.setString(3, batchNumber);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return; }
        }
        String sql = "INSERT INTO inventory_batches (facility_id, medicine_id, batch_number, quantity, expiry_date, received_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, facilityId); ps.setInt(2, medicineId); ps.setString(3, batchNumber);
            ps.setInt(4, quantity); ps.setDate(5, java.sql.Date.valueOf(expiryDate)); ps.setDate(6, java.sql.Date.valueOf(receivedDate));
            ps.executeUpdate();
        }
    }

    private Integer findId(Connection c, String sql, String value) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : null; }
        }
    }
}
