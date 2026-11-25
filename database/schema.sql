-- MediShare Phase 1 database schema
-- Run this script in MySQL Workbench or with: mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS medishare
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE medishare;

CREATE TABLE IF NOT EXISTS facilities (
    facility_id INT PRIMARY KEY AUTO_INCREMENT,
    facility_code VARCHAR(20) NOT NULL UNIQUE,
    facility_name VARCHAR(120) NOT NULL,
    facility_type VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(80) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_facility_type CHECK (facility_type IN ('HOSPITAL', 'CLINIC', 'PHARMACY'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    facility_id INT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_facility FOREIGN KEY (facility_id)
        REFERENCES facilities(facility_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'FACILITY_MANAGER'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medicines (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    medicine_code VARCHAR(20) NOT NULL UNIQUE,
    generic_name VARCHAR(120) NOT NULL,
    strength VARCHAR(40) NOT NULL,
    dosage_form VARCHAR(40) NOT NULL,
    category VARCHAR(60) NOT NULL,
    reorder_level INT NOT NULL DEFAULT 20,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_medicine_reorder CHECK (reorder_level >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory_batches (
    batch_id INT PRIMARY KEY AUTO_INCREMENT,
    facility_id INT NOT NULL,
    medicine_id INT NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    received_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_batch_facility FOREIGN KEY (facility_id)
        REFERENCES facilities(facility_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_batch_medicine FOREIGN KEY (medicine_id)
        REFERENCES medicines(medicine_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_facility_medicine_batch UNIQUE (facility_id, medicine_id, batch_number),
    CONSTRAINT chk_batch_quantity CHECK (quantity >= 0),
    INDEX idx_batch_facility_expiry (facility_id, expiry_date),
    INDEX idx_batch_medicine (medicine_id)
) ENGINE=InnoDB;

-- Phase 2 tables (transfer_requests and stock_movements) will be added
-- in the Phase 2 migration after the Phase 1 prototype is evaluated.
