// Product Model for Digital Book
const db = require('../config/db');

const ProductModel = {
    getAll: (filters = {}) => {
        let sql = `
            SELECT p.*,
                   c.name as category_name,
                   s.name as supplier_name,
                   (p.quantity * p.unit_price) as total_value,
                   CASE
                       WHEN p.quantity = 0 THEN 'Out of Stock'
                       WHEN p.quantity <= p.min_stock_level THEN 'Low Stock'
                       ELSE 'In Stock'
                   END as stock_status
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN suppliers s ON p.supplier_id = s.id
            WHERE 1=1
        `;
        const params = [];

        if (filters.search && filters.search.trim() !== '') {
            const term = `%${filters.search.trim()}%`;
            sql += ` AND (p.name LIKE ? OR p.sku LIKE ? OR p.description LIKE ?)`;
            params.push(term, term, term);
        }

        if (filters.categoryId && filters.categoryId !== '') {
            sql += ` AND p.category_id = ?`;
            params.push(filters.categoryId);
        }

        if (filters.status && filters.status !== '') {
            if (filters.status === 'OUT_OF_STOCK') {
                sql += ` AND p.quantity = 0`;
            } else if (filters.status === 'LOW_STOCK') {
                sql += ` AND p.quantity > 0 AND p.quantity <= p.min_stock_level`;
            } else if (filters.status === 'IN_STOCK') {
                sql += ` AND p.quantity > p.min_stock_level`;
            }
        }

        const validSortFields = ['name', 'sku', 'unit_price', 'quantity', 'category_name', 'created_at'];
        let sortBy = 'p.created_at';
        if (filters.sortBy && validSortFields.includes(filters.sortBy)) {
            sortBy = filters.sortBy === 'category_name' ? 'c.name' : `p.${filters.sortBy}`;
        }

        const order = (filters.order && filters.order.toUpperCase() === 'ASC') ? 'ASC' : 'DESC';
        sql += ` ORDER BY ${sortBy} ${order}`;

        return db.query(sql, params);
    },

    getById: (id) => {
        return db.get(`
            SELECT p.*,
                   c.name as category_name,
                   s.name as supplier_name,
                   (p.quantity * p.unit_price) as total_value,
                   CASE
                       WHEN p.quantity = 0 THEN 'Out of Stock'
                       WHEN p.quantity <= p.min_stock_level THEN 'Low Stock'
                       ELSE 'In Stock'
                   END as stock_status
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN suppliers s ON p.supplier_id = s.id
            WHERE p.id = ?
        `, [id]);
    },

    getBySku: (sku, excludeId = null) => {
        if (excludeId) {
            return db.get('SELECT * FROM products WHERE UPPER(sku) = UPPER(?) AND id != ?', [sku, excludeId]);
        }
        return db.get('SELECT * FROM products WHERE UPPER(sku) = UPPER(?)', [sku]);
    },

    create: ({ name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id }) => {
        const stmt = db.run(`
            INSERT INTO products (name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        `, [
            name,
            sku.toUpperCase(),
            description || '',
            parseFloat(unit_price) || 0,
            parseInt(quantity, 10) || 0,
            parseInt(min_stock_level, 10) || 10,
            category_id,
            supplier_id || null
        ]);
        return stmt.lastInsertRowid;
    },

    update: (id, { name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id }) => {
        return db.run(`
            UPDATE products
            SET name = ?,
                sku = ?,
                description = ?,
                unit_price = ?,
                quantity = ?,
                min_stock_level = ?,
                category_id = ?,
                supplier_id = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        `, [
            name,
            sku.toUpperCase(),
            description || '',
            parseFloat(unit_price) || 0,
            parseInt(quantity, 10) || 0,
            parseInt(min_stock_level, 10) || 10,
            category_id,
            supplier_id || null,
            id
        ]);
    },

    delete: (id) => {
        // Explicitly remove child transactions first so that foreign key deletion NEVER fails
        db.run('DELETE FROM stock_transactions WHERE product_id = ?', [id]);
        return db.run('DELETE FROM products WHERE id = ?', [id]);
    },

    getLowStock: () => {
        return db.query(`
            SELECT p.*,
                   c.name as category_name,
                   s.name as supplier_name,
                   CASE
                       WHEN p.quantity = 0 THEN 'Out of Stock'
                       ELSE 'Low Stock'
                   END as stock_status
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN suppliers s ON p.supplier_id = s.id
            WHERE p.quantity <= p.min_stock_level
            ORDER BY p.quantity ASC, p.name ASC
        `);
    },

    getDashboardStats: () => {
        const totalProducts = db.get('SELECT COUNT(*) as count FROM products').count;
        const totalStock = db.get('SELECT COALESCE(SUM(quantity), 0) as total FROM products').total;
        const lowStock = db.get('SELECT COUNT(*) as count FROM products WHERE quantity > 0 AND quantity <= min_stock_level').count;
        const outOfStock = db.get('SELECT COUNT(*) as count FROM products WHERE quantity = 0').count;
        const totalValue = db.get('SELECT COALESCE(SUM(quantity * unit_price), 0) as value FROM products').value;

        return {
            totalProducts,
            totalStock,
            lowStock,
            outOfStock,
            totalValue: parseFloat(totalValue.toFixed(2))
        };
    },

    getCategoryDistribution: () => {
        return db.query(`
            SELECT c.name,
                   COUNT(p.id) as product_count,
                   COALESCE(SUM(p.quantity), 0) as total_quantity,
                   COALESCE(SUM(p.quantity * p.unit_price), 0) as total_valuation
            FROM categories c
            LEFT JOIN products p ON p.category_id = c.id
            GROUP BY c.id
            HAVING COUNT(p.id) > 0
            ORDER BY total_quantity DESC
        `);
    }
};

module.exports = ProductModel;
