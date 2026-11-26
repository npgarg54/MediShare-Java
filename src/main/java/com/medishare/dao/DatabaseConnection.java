package com.medishare.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/medishare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final Properties FILE_PROPERTIES = loadProperties();

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        String url = setting("MEDISHARE_DB_URL", "db.url", DEFAULT_URL);
        String username = setting("MEDISHARE_DB_USERNAME", "db.username", "root");
        String password = setting("MEDISHARE_DB_PASSWORD", "db.password", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL Connector/J was not found. Build the project with Maven so the JDBC driver is available.", ex);
        }
        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String environmentKey, String propertyKey, String fallback) {
        String env = System.getenv(environmentKey);
        if (env != null && !env.isBlank()) return env;
        String property = FILE_PROPERTIES.getProperty(propertyKey);
        return property == null ? fallback : property.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        Path path = Path.of("config", "db.properties");
        if (Files.isRegularFile(path)) {
            try (InputStream input = Files.newInputStream(path)) {
                properties.load(input);
            } catch (IOException ex) {
                System.err.println("Warning: could not read config/db.properties: " + ex.getMessage());
            }
        }
        return properties;
    }
}
