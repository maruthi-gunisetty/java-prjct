// Database Configuration and Initialization for Digital Book
const Database = require('better-sqlite3');
const path = require('path');
const fs = require('fs');
const bcrypt = require('bcryptjs');

const dbDir = path.join(__dirname, '..', 'data');
if (!fs.existsSync(dbDir)) {
    fs.mkdirSync(dbDir, { recursive: true });
}

const dbPath = path.join(dbDir, 'inventory.db');
const db = new Database(dbPath);

// Enable Foreign Key constraints
db.pragma('foreign_keys = ON');

function initDatabase() {
    // 1. Admins Table
    db.exec(`
        CREATE TABLE IF NOT EXISTS admins (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            full_name TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            role TEXT NOT NULL DEFAULT 'ADMIN',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    `);

    // 2. Categories Table
    db.exec(`
        CREATE TABLE IF NOT EXISTS categories (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE,
            description TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    `);

    // 3. Suppliers Table
    db.exec(`
        CREATE TABLE IF NOT EXISTS suppliers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT,
            phone TEXT,
            address TEXT,
            contact_person TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    `);

    // 4. Products Table
    db.exec(`
        CREATE TABLE IF NOT EXISTS products (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            sku TEXT NOT NULL UNIQUE,
            description TEXT,
            unit_price REAL NOT NULL,
            quantity INTEGER NOT NULL DEFAULT 0,
            min_stock_level INTEGER NOT NULL DEFAULT 10,
            category_id INTEGER NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
            supplier_id INTEGER REFERENCES suppliers(id) ON DELETE SET NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    `);

    // 5. Stock Transactions Table
    db.exec(`
        CREATE TABLE IF NOT EXISTS stock_transactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
            transaction_type TEXT NOT NULL,
            quantity INTEGER NOT NULL,
            unit_price REAL,
            reference_number TEXT,
            notes TEXT,
            supplier_id INTEGER REFERENCES suppliers(id) ON DELETE SET NULL,
            transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    `);

    // Seed data if empty
    const adminCount = db.prepare('SELECT COUNT(*) as count FROM admins').get().count;
    if (adminCount === 0) {
        const hashedPassword = bcrypt.hashSync('admin123', 10);
        db.prepare(`
            INSERT INTO admins (username, password, full_name, email, role)
            VALUES (?, ?, ?, ?, ?)
        `).run('admin', hashedPassword, 'System Administrator', 'admin@enterprise.com', 'ADMIN');
    }

    const catCount = db.prepare('SELECT COUNT(*) as count FROM categories').get().count;
    if (catCount === 0) {
        const insertCategory = db.prepare('INSERT INTO categories (name, description) VALUES (?, ?)');
        const categories = [
            ['Computing Hardware', 'Processors, memory modules, and high speed storage units'],
            ['Networking Devices', 'Enterprise routers, switches, and patch accessories'],
            ['Industrial Tools', 'Precision workshop tools, diagnostic equipment, and meters'],
            ['Power Equipment', 'Voltage regulators, uninterruptible power supplies, and battery units'],
            ['Packaging Supplies', 'Reinforced boxes, protective static bags, and strapping']
        ];
        for (const cat of categories) {
            insertCategory.run(cat[0], cat[1]);
        }
    }

    const suppCount = db.prepare('SELECT COUNT(*) as count FROM suppliers').get().count;
    if (suppCount === 0) {
        const insertSupplier = db.prepare('INSERT INTO suppliers (name, email, phone, address, contact_person) VALUES (?, ?, ?, ?, ?)');
        const suppliers = [
            ['Apex Industrial Supplies', 'contact@apexsupplies.com', '+1 555 0192', '742 Evergreen Road, Sector 4', 'Robert Vance'],
            ['Global Tech Logistics', 'sales@globaltechlogistics.com', '+1 555 0843', '1200 Innovation Parkway', 'Elena Rostova'],
            ['Prime Semiconductor Corp', 'support@primesemicorp.com', '+1 555 0471', '55 Silicon Boulevard', 'David Chen'],
            ['Vertex Packaging Solutions', 'orders@vertexpackaging.com', '+1 555 0329', '89 Harbor Way', 'Sarah Jenkins']
        ];
        for (const supp of suppliers) {
            insertSupplier.run(supp[0], supp[1], supp[2], supp[3], supp[4]);
        }
    }

    const prodCount = db.prepare('SELECT COUNT(*) as count FROM products').get().count;
    if (prodCount === 0) {
        const insertProduct = db.prepare(`
            INSERT INTO products (name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        `);
        const products = [
            ['Intel Core i7 Processor', 'SKU1001', '8 core high efficiency desktop processor with cache', 329.99, 45, 10, 1, 3],
            ['DDR5 32GB RAM Module', 'SKU1002', 'High speed 5600MHz desktop memory stick', 119.50, 5, 15, 1, 3],
            ['NVMe SSD 1TB High Speed', 'SKU1003', 'PCIe Gen 4 solid state storage unit', 89.90, 0, 10, 1, 2],
            ['Gigabit 24 Port Managed Switch', 'SKU1004', 'Rackmount manageable network distribution switch', 249.00, 18, 5, 2, 2],
            ['Cat6 Shielded Patch Cable 50m', 'SKU1005', 'Outdoor rated copper networking patch cable', 34.50, 8, 20, 2, 1],
            ['Digital Multimeter Pro', 'SKU1006', 'True RMS electrical diagnostic handheld multimeter', 79.99, 30, 8, 3, 1],
            ['Thermal Heat Sink Compound', 'SKU1007', 'Non conductive carbon thermal interface paste', 12.50, 0, 25, 1, 3],
            ['Reinforced Storage Container', 'SKU1008', 'Heavy duty industrial composite modular container', 45.00, 62, 15, 5, 4],
            ['Industrial Soldering Station', 'SKU1009', 'Adjustable temperature digital soldering unit', 145.00, 4, 6, 3, 1],
            ['UPS 1500VA Battery Backup', 'SKU1010', 'Pure sine wave power protection unit', 399.00, 14, 4, 4, 2],
            ['Laser Temperature Sensor', 'SKU1011', 'Non contact infrared digital thermometer', 55.00, 22, 5, 3, 1],
            ['Optical Transceiver SFP Module', 'SKU1012', '10G single mode fiber optic pluggable module', 68.00, 3, 10, 2, 2]
        ];
        for (const prod of products) {
            insertProduct.run(...prod);
        }
    }

    const txCount = db.prepare('SELECT COUNT(*) as count FROM stock_transactions').get().count;
    if (txCount === 0) {
        const insertTx = db.prepare(`
            INSERT INTO stock_transactions (product_id, transaction_type, quantity, unit_price, reference_number, notes, supplier_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        `);
        const transactions = [
            [1, 'STOCK_IN', 50, 329.99, 'PO1001', 'Initial batch delivery from supplier', 3],
            [1, 'STOCK_OUT', 5, 329.99, 'SO2001', 'Fulfillment for order ref 8492', null],
            [2, 'STOCK_IN', 20, 119.50, 'PO1002', 'Restock shipment from Prime Semiconductor', 3],
            [2, 'STOCK_OUT', 15, 119.50, 'SO2002', 'Assembly requisition order', null],
            [4, 'STOCK_IN', 25, 249.00, 'PO1003', 'Warehouse transfer from regional hub', 2],
            [4, 'STOCK_OUT', 7, 249.00, 'SO2003', 'Infrastructure upgrade project dispatch', null],
            [6, 'STOCK_IN', 35, 79.99, 'PO1004', 'Standard tool replenishment', 1],
            [6, 'STOCK_OUT', 5, 79.99, 'SO2004', 'Testing lab equipment allocation', null],
            [8, 'STOCK_IN', 70, 45.00, 'PO1005', 'Bulk packaging carton delivery', 4],
            [8, 'STOCK_OUT', 8, 45.00, 'SO2005', 'Outbound shipping pack allocation', null]
        ];
        for (const tx of transactions) {
            insertTx.run(...tx);
        }
    }
}

// Initialize tables and seed records immediately
initDatabase();

module.exports = {
    db,
    query: (sql, params = []) => db.prepare(sql).all(params),
    get: (sql, params = []) => db.prepare(sql).get(params),
    run: (sql, params = []) => db.prepare(sql).run(params)
};
