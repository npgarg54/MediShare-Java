package com.medishare.dao;

import com.medishare.exception.DataAccessException;
import com.medishare.model.InventoryBatch;
import com.medishare.model.StockSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO implements InventoryRepository {
    private static final String SELECT_BATCHES = "SELECT b.batch_id, b.facility_id, b.medicine_id, "
            + "m.medicine_code, m.generic_name, m.strength, m.dosage_form, m.reorder_level, "
            + "b.batch_number, b.quantity, b.expiry_date, b.received_date "
            + "FROM inventory_batches b JOIN medicines m ON b.medicine_id = m.medicine_id ";

    @Override
    public List<InventoryBatch> findByFacility(int facilityId, String search) {
        String sql = SELECT_BATCHES + "WHERE b.facility_id = ? AND "
                + "(? = '' OR LOWER(m.medicine_code) LIKE LOWER(?) OR LOWER(m.generic_name) LIKE LOWER(?) "
                + "OR LOWER(b.batch_number) LIKE LOWER(?)) ORDER BY b.expiry_date, m.generic_name";
        List<InventoryBatch> batches = new ArrayList<>();
        String term = search == null ? "" : search.trim();
        String pattern = "%" + term + "%";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, facilityId);
            statement.setString(2, term);
            statement.setString(3, pattern);
            statement.setString(4, pattern);
            statement.setString(5, pattern);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next())
                    batches.add(mapBatch(rs));
            }
            return batches;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to retrieve facility inventory.", ex);
        }
    }

    @Override
    public void addBatch(int facilityId, int medicineId, String batchNumber, int quantity,
            LocalDate expiryDate, LocalDate receivedDate) {
        String sql = "INSERT INTO inventory_batches (facility_id, medicine_id, batch_number, quantity, expiry_date, received_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, facilityId);
            statement.setInt(2, medicineId);
            statement.setString(3, batchNumber.trim());
            statement.setInt(4, quantity);
            statement.setDate(5, java.sql.Date.valueOf(expiryDate));
            statement.setDate(6, java.sql.Date.valueOf(receivedDate));
            statement.executeUpdate();
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) {
                throw new IllegalArgumentException(
                        "This batch number already exists for the selected medicine at your facility, or a referenced record is invalid.");
            }
            throw new DataAccessException("Unable to add inventory batch.", ex);
        }
    }

    @Override
    public boolean updateQuantity(int facilityId, int batchId, int quantity) {
        String sql = "UPDATE inventory_batches SET quantity = ? WHERE batch_id = ? AND facility_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, batchId);
            statement.setInt(3, facilityId);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update batch quantity.", ex);
        }
    }

    @Override
    public boolean deleteBatch(int facilityId, int batchId) {
        String sql = "DELETE FROM inventory_batches WHERE batch_id = ? AND facility_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, batchId);
            statement.setInt(2, facilityId);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to delete inventory batch.", ex);
        }
    }

    @Override
    public List<StockSummary> findLowStock(int facilityId, LocalDate today) {
        String sql = "SELECT m.medicine_id, m.medicine_code, m.generic_name, m.strength, m.reorder_level, "
                + "COALESCE(SUM(CASE WHEN b.expiry_date >= ? THEN b.quantity ELSE 0 END), 0) AS usable_quantity "
                + "FROM medicines m LEFT JOIN inventory_batches b ON b.medicine_id = m.medicine_id "
                + "AND b.facility_id = ? GROUP BY m.medicine_id, m.medicine_code, m.generic_name, m.strength, m.reorder_level "
                + "HAVING COUNT(b.batch_id) > 0 AND usable_quantity < m.reorder_level ORDER BY usable_quantity, m.generic_name";
        List<StockSummary> summaries = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(today));
            statement.setInt(2, facilityId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    summaries.add(new StockSummary(rs.getInt("medicine_id"), rs.getString("medicine_code"),
                            rs.getString("generic_name"), rs.getString("strength"),
                            rs.getInt("usable_quantity"), rs.getInt("reorder_level")));
                }
            }
            return summaries;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to calculate low-stock medicines.", ex);
        }
    }

    private InventoryBatch mapBatch(ResultSet rs) throws SQLException {
        return new InventoryBatch(rs.getInt("batch_id"), rs.getInt("facility_id"), rs.getInt("medicine_id"),
                rs.getString("medicine_code"), rs.getString("generic_name"), rs.getString("strength"),
                rs.getString("dosage_form"), rs.getString("batch_number"), rs.getInt("quantity"),
                rs.getInt("reorder_level"), rs.getDate("expiry_date").toLocalDate(),
                rs.getDate("received_date").toLocalDate());
    }
}
