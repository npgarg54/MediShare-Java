package com.medishare.dao;

import com.medishare.enums.FacilityType;
import com.medishare.enums.UserRole;
import com.medishare.exception.DataAccessException;
import com.medishare.model.Admin;
import com.medishare.model.Facility;
import com.medishare.model.FacilityManager;
import com.medishare.model.User;
import com.medishare.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User authenticate(String username, String password) {
        String sql = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.role, "
                + "f.facility_id, f.facility_code, f.facility_name, f.facility_type, f.address, f.city "
                + "FROM users u LEFT JOIN facilities f ON u.facility_id = f.facility_id "
                + "WHERE u.username = ? AND u.is_active = TRUE";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next() || !PasswordUtil.verify(password, rs.getString("password_hash"))) return null;
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                UserRole role = UserRole.fromDatabase(rs.getString("role"));
                if (role == UserRole.ADMIN) return new Admin(userId, rs.getString("username"), fullName);
                int facilityId = rs.getInt("facility_id");
                if (rs.wasNull()) return null;
                Facility facility = new Facility(facilityId, rs.getString("facility_code"),
                        rs.getString("facility_name"), FacilityType.fromInput(rs.getString("facility_type")),
                        rs.getString("address"), rs.getString("city"));
                return new FacilityManager(userId, rs.getString("username"), fullName, facility);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Login could not be completed. Check the database connection.", ex);
        }
    }
}
