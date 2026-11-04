// Category Model for Digital Book
const db = require('../config/db');

const CategoryModel = {
    getAll: () => {
        return db.query(`
            SELECT c.*, COUNT(p.id) as product_count
            FROM categories c
            LEFT JOIN products p ON p.category_id = c.id
            GROUP BY c.id
            ORDER BY c.name ASC
        `);
    },

    getById: (id) => {
        return db.get(`
            SELECT c.*, COUNT(p.id) as product_count
            FROM categories c
            LEFT JOIN products p ON p.category_id = c.id
            WHERE c.id = ?
            GROUP BY c.id
        `, [id]);
    },

    getByName: (name, excludeId = null) => {
        if (excludeId) {
            return db.get('SELECT * FROM categories WHERE LOWER(name) = LOWER(?) AND id != ?', [name, excludeId]);
        }
        return db.get('SELECT * FROM categories WHERE LOWER(name) = LOWER(?)', [name]);
    },

    create: ({ name, description }) => {
        return db.run('INSERT INTO categories (name, description) VALUES (?, ?)', [name, description]);
    },

    update: (id, { name, description }) => {
        return db.run('UPDATE categories SET name = ?, description = ? WHERE id = ?', [name, description, id]);
    },

    countProducts: (id) => {
        const row = db.get('SELECT COUNT(*) as count FROM products WHERE category_id = ?', [id]);
        return row ? row.count : 0;
    },

    delete: (id) => {
        return db.run('DELETE FROM categories WHERE id = ?', [id]);
    }
};

module.exports = CategoryModel;
