package com.medishare;

import com.medishare.dao.DatabaseConnection;
import com.medishare.service.DataSeeder;
import com.medishare.ui.ConsoleMenu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting MediShare Beta v0.2...");
        try (Connection connection = DatabaseConnection.getConnection()) {
            new DataSeeder().seedIfNeeded(connection);
            System.out.println("Database connected. Demo data is ready.\n");
        } catch (SQLException | RuntimeException ex) {
            System.err.println("\nUnable to start MediShare because the database setup failed.");
            System.err.println("Reason: " + ex.getMessage());
            System.err.println("Setup steps:");
            System.err.println("1. Start MySQL Server.");
            System.err.println("2. Execute database/schema.sql in MySQL Workbench.");
            System.err.println("3. Copy config/db.properties.example to config/db.properties and set your MySQL password.");
            System.err.println("4. Run: mvn clean compile exec:java");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleMenu(scanner).start();
        }
    }
}
