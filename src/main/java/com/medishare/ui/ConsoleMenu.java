package com.medishare.ui;

import com.medishare.dao.FacilityDAO;
import com.medishare.dao.InventoryDAO;
import com.medishare.dao.MedicineDAO;
import com.medishare.dao.UserDAO;
import com.medishare.exception.DataAccessException;
import com.medishare.exception.LogoutRequestedException;
import com.medishare.exception.OperationCancelledException;
import com.medishare.model.Admin;
import com.medishare.model.FacilityManager;
import com.medishare.model.User;
import com.medishare.service.AlertService;
import com.medishare.service.AuthService;
import com.medishare.service.DashboardService;
import com.medishare.service.FacilityService;
import com.medishare.service.InventoryService;
import com.medishare.service.MedicineService;

import java.util.Scanner;

public class ConsoleMenu {
    private final InputHelper input;
    private final AuthService authService;
    private final AdminMenu adminMenu;
    private final FacilityManagerMenu facilityManagerMenu;

    public ConsoleMenu(Scanner scanner) {
        this.input = new InputHelper(scanner);
        MedicineDAO medicineDAO = new MedicineDAO();
        InventoryService inventoryService = new InventoryService(new InventoryDAO(), medicineDAO);
        AlertService alertService = new AlertService();
        this.authService = new AuthService(new UserDAO());
        this.adminMenu = new AdminMenu(new FacilityService(new FacilityDAO()), new MedicineService(medicineDAO), input);
        this.facilityManagerMenu = new FacilityManagerMenu(inventoryService, new MedicineService(medicineDAO),
                new DashboardService(inventoryService, alertService), alertService, input);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printWelcome();
            System.out.println("1. Login");
            System.out.println("2. Exit");
            try {
                int choice = input.readInt("Choose an option: ", 1, 2);
                if (choice == 2) {
                    running = false;
                    continue;
                }
                try {
                    login();
                } catch (DataAccessException ex) {
                    System.out.println("\n[DATABASE ERROR] " + ex.getMessage());
                    System.out.println("Check that MySQL is running, database 'medishare' exists, and config/db.properties is correct.");
                }
            } catch (OperationCancelledException ex) {
                System.out.println("Nothing to cancel on the welcome screen.");
            } catch (LogoutRequestedException ex) {
                System.out.println("You are already at the login screen.");
            }
        }
        System.out.println("Thank you for using MediShare. Goodbye!");
    }

    private void login() {
        System.out.println("\nLOGIN");
        System.out.println("Enter :cancel to return to the welcome screen.");
        String username = input.readRequired("Username: ");
        String password = input.readRequired("Password: ");
        User user = authService.login(username, password);
        if (user == null) {
            System.out.println("Invalid username/password or inactive account.");
            return;
        }
        System.out.println("\nLogin successful. Welcome, " + user.getFullName() + "!");
        if (user instanceof Admin admin) {
            adminMenu.show(admin);
        } else if (user instanceof FacilityManager manager) {
            facilityManagerMenu.show(manager);
        }
    }

    private void printWelcome() {
        System.out.println("\n" + "=".repeat(64));
        System.out.println("                         MEDISHARE");
        System.out.println("                    INVENTORY SYSTEM");
        System.out.println("                       BETA v0.2");
        System.out.println("=".repeat(64));
        System.out.println("Coordinate stock visibility. Reduce avoidable expiry and shortage.");
        System.out.println("Navigation: :cancel = cancel current form | :logout = return to login");
        System.out.println("-".repeat(64));
    }
}
