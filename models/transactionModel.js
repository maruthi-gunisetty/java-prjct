// Stock Transaction Model for Digital Book
const db = require('../config/db');

const TransactionModel = {
    getAll: (filters = {}) => {
        let sql = `
            SELECT t.*,
                   p.name as product_name,
                   p.sku as product_sku,
                   s.name as supplier_name
            FROM stock_transactions t
            LEFT JOIN products p ON t.product_id = p.id
            LEFT JOIN suppliers s ON t.supplier_id = s.id
            WHERE 1=1
        `;
        const params = [];

        if (filters.type && filters.type !== '') {
            sql += ' AND t.transaction_type = ?';
            params.push(filters.type);
        }

        if (filters.productId && filters.productId !== '') {
            sql += ' AND t.product_id = ?';
            params.push(filters.productId);
        }

        sql += ' ORDER BY t.transaction_date DESC, t.id DESC';

        if (filters.limit) {
            sql += ' LIMIT ?';
            params.push(parseInt(filters.limit, 10));
        }

        return db.query(sql, params);
    },

    getRecent: (limit = 6) => {
        return db.query(`
            SELECT t.*,
                   p.name as product_name,
                   p.sku as product_sku,
                   s.name as supplier_name
            FROM stock_transactions t
            LEFT JOIN products p ON t.product_id = p.id
            LEFT JOIN suppliers s ON t.supplier_id = s.id
            ORDER BY t.transaction_date DESC, t.id DESC
            LIMIT ?
        `, [limit]);
    },

    recordStockIn: ({ productId, quantity, unitPrice, referenceNumber, notes, supplierId }) => {
        const qty = parseInt(quantity, 10);
        if (qty <= 0) {
            throw new Error('Quantity must be greater than zero');
        }

        const product = db.get('SELECT * FROM products WHERE id = ?', [productId]);
        if (!product) {
            throw new Error('Product not found');
        }

        const price = unitPrice !== undefined && unitPrice !== null && unitPrice !== '' 
            ? parseFloat(unitPrice) 
            : product.unit_price;

        const effectiveSupplierId = supplierId || product.supplier_id || null;

        // Execute transaction
        db.run('UPDATE products SET quantity = quantity + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?', [qty, productId]);
        const res = db.run(`
            INSERT INTO stock_transactions (product_id, transaction_type, quantity, unit_price, reference_number, notes, supplier_id)
            VALUES (?, 'STOCK_IN', ?, ?, ?, ?, ?)
        `, [
            productId,
            qty,
            price,
            referenceNumber || 'IN' + Date.now().toString().slice(-6),
            notes || 'Standard Stock In entry',
            effectiveSupplierId
        ]);

        return res.lastInsertRowid;
    },

    recordStockOut: ({ productId, quantity, unitPrice, referenceNumber, notes }) => {
        const qty = parseInt(quantity, 10);
        if (qty <= 0) {
            throw new Error('Quantity must be greater than zero');
        }

        const product = db.get('SELECT * FROM products WHERE id = ?', [productId]);
        if (!product) {
            throw new Error('Product not found');
        }

        if (product.quantity < qty) {
            throw new Error(`Insufficient stock. Available quantity is ${product.quantity}, but requested ${qty}`);
        }

        const price = unitPrice !== undefined && unitPrice !== null && unitPrice !== ''
            ? parseFloat(unitPrice)
            : product.unit_price;

        // Execute transaction
        db.run('UPDATE products SET quantity = quantity - ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?', [qty, productId]);
        const res = db.run(`
            INSERT INTO stock_transactions (product_id, transaction_type, quantity, unit_price, reference_number, notes, supplier_id)
            VALUES (?, 'STOCK_OUT', ?, ?, ?, ?, NULL)
        `, [
            productId,
            qty,
            price,
            referenceNumber || 'OUT' + Date.now().toString().slice(-6),
            notes || 'Standard Stock Out dispatch'
        ]);

        return res.lastInsertRowid;
    },

    getMovementsSummary: () => {
        const totalStockIn = db.get(`
            SELECT COALESCE(SUM(quantity), 0) as total
            FROM stock_transactions
            WHERE transaction_type = 'STOCK_IN'
        `).total;

        const totalStockOut = db.get(`
            SELECT COALESCE(SUM(quantity), 0) as total
            FROM stock_transactions
            WHERE transaction_type = 'STOCK_OUT'
        `).total;

        return {
            totalStockIn,
            totalStockOut
        };
    }
};

module.exports = TransactionModel;
