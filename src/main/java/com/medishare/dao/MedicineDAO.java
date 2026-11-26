package com.medishare.dao;

import com.medishare.exception.DataAccessException;
import com.medishare.model.Medicine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {
    public List<Medicine> findAllActive() {
        return search("");
    }

    public List<Medicine> search(String query) {
        return searchByStatus(query, true);
    }

    public List<Medicine> searchInactive(String query) {
        return searchByStatus(query, false);
    }

    private List<Medicine> searchByStatus(String query, boolean active) {
        String sql = "SELECT medicine_id, medicine_code, generic_name, strength, dosage_form, category, reorder_level "
                + "FROM medicines WHERE is_active = ? AND (? = '' OR LOWER(medicine_code) LIKE LOWER(?) "
                + "OR LOWER(generic_name) LIKE LOWER(?) OR LOWER(category) LIKE LOWER(?) "
                + "OR LOWER(strength) LIKE LOWER(?) OR LOWER(dosage_form) LIKE LOWER(?)) "
                + "ORDER BY generic_name, strength";
        List<Medicine> medicines = new ArrayList<>();
        String term = query == null ? "" : query.trim();
        String pattern = "%" + term + "%";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setString(2, term);
            for (int i = 3; i <= 7; i++) statement.setString(i, pattern);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) medicines.add(map(rs));
            }
            return medicines;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to search the medicine catalogue.", ex);
        }
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT 1 FROM medicines WHERE UPPER(medicine_code) = UPPER(?) LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code.trim());
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to validate the medicine code.", ex);
        }
    }

    public Medicine findById(int medicineId) {
        return findByIdAndStatus(medicineId, true);
    }

    public Medicine findInactiveById(int medicineId) {
        return findByIdAndStatus(medicineId, false);
    }

    private Medicine findByIdAndStatus(int medicineId, boolean active) {
        String sql = "SELECT medicine_id, medicine_code, generic_name, strength, dosage_form, category, reorder_level "
                + "FROM medicines WHERE medicine_id = ? AND is_active = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, medicineId);
            statement.setBoolean(2, active);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to retrieve medicine.", ex);
        }
    }

    public void add(Medicine medicine) {
        String sql = "INSERT INTO medicines (medicine_code, generic_name, strength, dosage_form, category, reorder_level) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, medicine.getMedicineCode());
            statement.setString(2, medicine.getGenericName());
            statement.setString(3, medicine.getStrength());
            statement.setString(4, medicine.getDosageForm());
            statement.setString(5, medicine.getCategory());
            statement.setInt(6, medicine.getReorderLevel());
            statement.executeUpdate();
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) {
                throw new IllegalArgumentException("Medicine code already exists. Use a unique code.");
            }
            throw new DataAccessException("Unable to add medicine.", ex);
        }
    }

    public boolean setActive(int medicineId, boolean active) {
        String sql = "UPDATE medicines SET is_active = ? WHERE medicine_id = ? AND is_active = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setInt(2, medicineId);
            statement.setBoolean(3, !active);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update medicine catalogue status.", ex);
        }
    }

    private Medicine map(ResultSet rs) throws SQLException {
        return new Medicine(rs.getInt("medicine_id"), rs.getString("medicine_code"),
                rs.getString("generic_name"), rs.getString("strength"), rs.getString("dosage_form"),
                rs.getString("category"), rs.getInt("reorder_level"));
    }
}
