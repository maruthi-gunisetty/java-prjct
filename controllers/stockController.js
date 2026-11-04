// Stock Controller for Digital Book
const ProductModel = require('../models/productModel');
const SupplierModel = require('../models/supplierModel');
const TransactionModel = require('../models/transactionModel');

const StockController = {
    getStockIn: (req, res) => {
        try {
            const products = ProductModel.getAll({ sortBy: 'name', order: 'ASC' });
            const suppliers = SupplierModel.getAll();
            const preselectedProductId = req.query.productId ? parseInt(req.query.productId, 10) : null;

            res.render('stock/stock_in', {
                title: 'Digital Book | Stock In Entry',
                activeNav: 'stock_in',
                products,
                suppliers,
                preselectedProductId
            });
        } catch (error) {
            console.error('Error opening Stock In form:', error);
            req.flash('error', 'Unable to load Stock In form');
            res.redirect('/dashboard');
        }
    },

    postStockIn: (req, res) => {
        try {
            const { product_id, quantity, unit_price, reference_number, notes, supplier_id } = req.body;

            if (!product_id || !quantity) {
                req.flash('error', 'Product and quantity are required for Stock In');
                return res.redirect('/stock/in');
            }

            const qty = parseInt(quantity, 10);
            if (isNaN(qty) || qty <= 0) {
                req.flash('error', 'Stock In quantity must be a positive number');
                return res.redirect('/stock/in');
            }

            TransactionModel.recordStockIn({
                productId: parseInt(product_id, 10),
                quantity: qty,
                unitPrice: unit_price ? parseFloat(unit_price) : null,
                referenceNumber: reference_number ? reference_number.trim() : null,
                notes: notes ? notes.trim() : null,
                supplierId: supplier_id ? parseInt(supplier_id, 10) : null
            });

            req.flash('success', 'Stock In recorded successfully. Product inventory updated.');
            res.redirect('/stock/history');
        } catch (error) {
            console.error('Error recording Stock In:', error);
            req.flash('error', error.message || 'Error processing Stock In transaction');
            res.redirect('/stock/in');
        }
    },

    getStockOut: (req, res) => {
        try {
            const products = ProductModel.getAll({ sortBy: 'name', order: 'ASC' });
            const preselectedProductId = req.query.productId ? parseInt(req.query.productId, 10) : null;

            res.render('stock/stock_out', {
                title: 'Digital Book | Stock Out Dispatch',
                activeNav: 'stock_out',
                products,
                preselectedProductId
            });
        } catch (error) {
            console.error('Error opening Stock Out form:', error);
            req.flash('error', 'Unable to load Stock Out form');
            res.redirect('/dashboard');
        }
    },

    postStockOut: (req, res) => {
        try {
            const { product_id, quantity, reference_number, notes } = req.body;

            if (!product_id || !quantity) {
                req.flash('error', 'Product and quantity are required for Stock Out');
                return res.redirect('/stock/out');
            }

            const qty = parseInt(quantity, 10);
            if (isNaN(qty) || qty <= 0) {
                req.flash('error', 'Stock Out quantity must be a positive number');
                return res.redirect('/stock/out');
            }

            TransactionModel.recordStockOut({
                productId: parseInt(product_id, 10),
                quantity: qty,
                referenceNumber: reference_number ? reference_number.trim() : null,
                notes: notes ? notes.trim() : null
            });

            req.flash('success', 'Stock Out recorded successfully. Product inventory decreased.');
            res.redirect('/stock/history');
        } catch (error) {
            console.error('Error recording Stock Out:', error);
            req.flash('error', error.message || 'Error processing Stock Out transaction');
            res.redirect('/stock/out');
        }
    },

    getHistory: (req, res) => {
        try {
            const { type, productId } = req.query;
            const transactions = TransactionModel.getAll({ type, productId });
            const products = ProductModel.getAll({ sortBy: 'name', order: 'ASC' });

            res.render('stock/history', {
                title: 'Digital Book | Stock Transaction History',
                activeNav: 'history',
                transactions,
                products,
                selectedType: type || '',
                selectedProductId: productId || ''
            });
        } catch (error) {
            console.error('Error loading history:', error);
            req.flash('error', 'Unable to load transaction records');
            res.redirect('/dashboard');
        }
    },

    getLowStock: (req, res) => {
        try {
            const lowStockProducts = ProductModel.getLowStock();
            res.render('stock/low_stock', {
                title: 'Digital Book | Low Stock and Depleted Inventory Monitor',
                activeNav: 'low_stock',
                products: lowStockProducts
            });
        } catch (error) {
            console.error('Error loading low stock products:', error);
            req.flash('error', 'Unable to load low stock alerts');
            res.redirect('/dashboard');
        }
    }
};

module.exports = StockController;
