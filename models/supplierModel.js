// Supplier Model for Digital Book
const db = require('../config/db');

const SupplierModel = {
    getAll: () => {
        return db.query(`
            SELECT s.*, COUNT(p.id) as product_count
            FROM suppliers s
            LEFT JOIN products p ON p.supplier_id = s.id
            GROUP BY s.id
            ORDER BY s.name ASC
        `);
    },

    getById: (id) => {
        return db.get(`
            SELECT s.*, COUNT(p.id) as product_count
            FROM suppliers s
            LEFT JOIN products p ON p.supplier_id = s.id
            WHERE s.id = ?
            GROUP BY s.id
        `, [id]);
    },

    create: ({ name, email, phone, address, contact_person }) => {
        return db.run(`
            INSERT INTO suppliers (name, email, phone, address, contact_person)
            VALUES (?, ?, ?, ?, ?)
        `, [name, email, phone, address, contact_person]);
    },

    update: (id, { name, email, phone, address, contact_person }) => {
        return db.run(`
            UPDATE suppliers
            SET name = ?, email = ?, phone = ?, address = ?, contact_person = ?
            WHERE id = ?
        `, [name, email, phone, address, contact_person, id]);
    },

    delete: (id) => {
        // Unlink products and transactions first
        db.run('UPDATE products SET supplier_id = NULL WHERE supplier_id = ?', [id]);
        db.run('UPDATE stock_transactions SET supplier_id = NULL WHERE supplier_id = ?', [id]);
        return db.run('DELETE FROM suppliers WHERE id = ?', [id]);
    }
};

module.exports = SupplierModel;
