package com.medishare.dao;

import com.medishare.enums.FacilityType;
import com.medishare.exception.DataAccessException;
import com.medishare.model.Facility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacilityDAO {
    public List<Facility> findAllActive() {
        return search("");
    }

    /**
     * Partial, case-insensitive search across facility identity and location
     * fields.
     */
    public List<Facility> search(String query) {
        String sql = "SELECT facility_id, facility_code, facility_name, facility_type, address, city "
                + "FROM facilities WHERE is_active = TRUE AND (? = '' OR "
                + "LOWER(facility_code) LIKE LOWER(?) OR LOWER(facility_name) LIKE LOWER(?) "
                + "OR LOWER(facility_type) LIKE LOWER(?) OR LOWER(address) LIKE LOWER(?) OR LOWER(city) LIKE LOWER(?)) "
                + "ORDER BY facility_name";
        List<Facility> facilities = new ArrayList<>();
        String term = query == null ? "" : query.trim();
        String pattern = "%" + term + "%";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, term);
            for (int i = 2; i <= 6; i++)
                statement.setString(i, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next())
                    facilities.add(map(resultSet));
            }
            return facilities;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to search registered facilities.", ex);
        }
    }

    private Facility map(ResultSet rs) throws SQLException {
        return new Facility(rs.getInt("facility_id"), rs.getString("facility_code"),
                rs.getString("facility_name"), FacilityType.fromInput(rs.getString("facility_type")),
                rs.getString("address"), rs.getString("city"));
    }
}
