-- Enterprise Inventory Management System Database Script
-- Database: inventory_management
-- Author: Senior Full Stack Java Developer

CREATE DATABASE IF NOT EXISTS inventory_management;
USE inventory_management;

-- 1. Admins Table
DROP TABLE IF EXISTS stock_transactions;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS admins;

CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Categories Table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Suppliers Table
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30),
    address TEXT,
    contact_person VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Products Table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    unit_price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    min_stock_level INT NOT NULL DEFAULT 10,
    category_id BIGINT NOT NULL,
    supplier_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Stock Transactions Table
CREATE TABLE stock_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12, 2),
    reference_number VARCHAR(50),
    notes TEXT,
    supplier_id BIGINT,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample Seed Data
-- Note: Default Admin password is admin123 (BCrypt hashed)
INSERT INTO admins (username, password, full_name, email, role, created_at)
VALUES ('admin', '$2a$10$wO4q6/s8379lK07y3u44b.mH0pB0z3uL9pB1O1oFw25O33rYp982e', 'System Administrator', 'admin@enterprise.com', 'ADMIN', NOW());

INSERT INTO categories (name, description, created_at) VALUES
('Computing Hardware', 'Processors, memory modules, and high speed storage units', NOW()),
('Networking Devices', 'Enterprise routers, switches, and patch accessories', NOW()),
('Industrial Tools', 'Precision workshop tools, diagnostic equipment, and meters', NOW()),
('Power Equipment', 'Voltage regulators, uninterruptible power supplies, and battery units', NOW()),
('Packaging Supplies', 'Reinforced boxes, protective static bags, and strapping', NOW());

INSERT INTO suppliers (name, email, phone, address, contact_person, created_at) VALUES
('Apex Industrial Supplies', 'contact@apexsupplies.com', '+1 555 0192', '742 Evergreen Road, Sector 4', 'Robert Vance', NOW()),
('Global Tech Logistics', 'sales@globaltechlogistics.com', '+1 555 0843', '1200 Innovation Parkway', 'Elena Rostova', NOW()),
('Prime Semiconductor Corp', 'support@primesemicorp.com', '+1 555 0471', '55 Silicon Boulevard', 'David Chen', NOW()),
('Vertex Packaging Solutions', 'orders@vertexpackaging.com', '+1 555 0329', '89 Harbor Way', 'Sarah Jenkins', NOW());

INSERT INTO products (name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id, created_at) VALUES
('Intel Core i7 Processor', 'SKU1001', '8 core high efficiency desktop processor with cache', 329.99, 45, 10, 1, 3, NOW()),
('DDR5 32GB RAM Module', 'SKU1002', 'High speed 5600MHz desktop memory stick', 119.50, 5, 15, 1, 3, NOW()),
('NVMe SSD 1TB High Speed', 'SKU1003', 'PCIe Gen 4 solid state storage unit', 89.90, 0, 10, 1, 2, NOW()),
('Gigabit 24 Port Managed Switch', 'SKU1004', 'Rackmount manageable network distribution switch', 249.00, 18, 5, 2, 2, NOW()),
('Cat6 Shielded Patch Cable 50m', 'SKU1005', 'Outdoor rated copper networking patch cable', 34.50, 8, 20, 2, 1, NOW()),
('Digital Multimeter Pro', 'SKU1006', 'True RMS electrical diagnostic handheld multimeter', 79.99, 30, 8, 3, 1, NOW()),
('Thermal Heat Sink Compound', 'SKU1007', 'Non conductive carbon thermal interface paste', 12.50, 0, 25, 1, 3, NOW()),
('Reinforced Storage Container', 'SKU1008', 'Heavy duty industrial composite modular container', 45.00, 62, 15, 5, 4, NOW()),
('Industrial Soldering Station', 'SKU1009', 'Adjustable temperature digital soldering unit', 145.00, 4, 6, 3, 1, NOW()),
('UPS 1500VA Battery Backup', 'SKU1010', 'Pure sine wave power protection unit', 399.00, 14, 4, 4, 2, NOW()),
('Laser Temperature Sensor', 'SKU1011', 'Non contact infrared digital thermometer', 55.00, 22, 5, 3, 1, NOW()),
('Optical Transceiver SFP Module', 'SKU1012', '10G single mode fiber optic pluggable module', 68.00, 3, 10, 2, 2, NOW());

INSERT INTO stock_transactions (product_id, transaction_type, quantity, unit_price, reference_number, notes, supplier_id, transaction_date, created_at) VALUES
(1, 'STOCK_IN', 50, 329.99, 'PO1001', 'Initial batch delivery from supplier', 3, NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 18 DAY),
(1, 'STOCK_OUT', 5, 329.99, 'SO2001', 'Fulfillment for order ref 8492', NULL, NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY),
(2, 'STOCK_IN', 20, 119.50, 'PO1002', 'Restock shipment from Prime Semiconductor', 3, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 15 DAY),
(2, 'STOCK_OUT', 15, 119.50, 'SO2002', 'Assembly requisition order', NULL, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY),
(4, 'STOCK_IN', 25, 249.00, 'PO1003', 'Warehouse transfer from regional hub', 2, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY),
(4, 'STOCK_OUT', 7, 249.00, 'SO2003', 'Infrastructure upgrade project dispatch', NULL, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY),
(6, 'STOCK_IN', 35, 79.99, 'PO1004', 'Standard tool replenishment', 1, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY),
(6, 'STOCK_OUT', 5, 79.99, 'SO2004', 'Testing lab equipment allocation', NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
(8, 'STOCK_IN', 70, 45.00, 'PO1005', 'Bulk packaging carton delivery', 4, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY),
(8, 'STOCK_OUT', 8, 45.00, 'SO2005', 'Outbound shipping pack allocation', NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY);
