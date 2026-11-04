// Product Controller for Digital Book
const ProductModel = require('../models/productModel');
const CategoryModel = require('../models/categoryModel');
const SupplierModel = require('../models/supplierModel');
const TransactionModel = require('../models/transactionModel');

const ProductController = {
    list: (req, res) => {
        try {
            const { search, categoryId, sortBy, order } = req.query;
            const products = ProductModel.getAll({ search, categoryId, sortBy, order });
            const categories = CategoryModel.getAll();

            res.render('products/list', {
                title: 'Digital Book | Product Catalog',
                activeNav: 'products',
                products,
                categories,
                search: search || '',
                selectedCategoryId: categoryId || '',
                sortBy: sortBy || 'created_at',
                order: order || 'DESC'
            });
        } catch (error) {
            console.error('Error fetching products:', error);
            req.flash('error', 'Unable to retrieve product records');
            res.redirect('/dashboard');
        }
    },

    getNew: (req, res) => {
        const categories = CategoryModel.getAll();
        const suppliers = SupplierModel.getAll();
        res.render('products/form', {
            title: 'Digital Book | New Product Registration',
            activeNav: 'products',
            isEdit: false,
            product: {},
            categories,
            suppliers
        });
    },

    postCreate: (req, res) => {
        try {
            const { name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id } = req.body;

            if (!name || !sku || !unit_price || !category_id) {
                req.flash('error', 'Please fill in all mandatory product fields');
                return res.redirect('/products/new');
            }

            const existingSku = ProductModel.getBySku(sku);
            if (existingSku) {
                req.flash('error', 'Product SKU already exists. Each product SKU must be unique.');
                return res.redirect('/products/new');
            }

            const newId = ProductModel.create({
                name: name.trim(),
                sku: sku.trim(),
                description: description ? description.trim() : '',
                unit_price: parseFloat(unit_price),
                quantity: parseInt(quantity, 10) || 0,
                min_stock_level: parseInt(min_stock_level, 10) || 10,
                category_id: parseInt(category_id, 10),
                supplier_id: supplier_id ? parseInt(supplier_id, 10) : null
            });

            // If initial stock quantity was entered, record an opening stock transaction
            const initQty = parseInt(quantity, 10) || 0;
            if (initQty > 0) {
                TransactionModel.recordStockIn({
                    productId: newId,
                    quantity: initQty,
                    unitPrice: parseFloat(unit_price),
                    referenceNumber: 'INIT' + newId,
                    notes: 'Initial inventory registration stock',
                    supplierId: supplier_id ? parseInt(supplier_id, 10) : null
                });
                // Since recordStockIn adds to quantity, we adjust so total stays initQty
                ProductModel.update(newId, {
                    name: name.trim(),
                    sku: sku.trim(),
                    description: description ? description.trim() : '',
                    unit_price: parseFloat(unit_price),
                    quantity: initQty,
                    min_stock_level: parseInt(min_stock_level, 10) || 10,
                    category_id: parseInt(category_id, 10),
                    supplier_id: supplier_id ? parseInt(supplier_id, 10) : null
                });
            }

            req.flash('success', 'Product registered successfully: ' + name);
            res.redirect('/products');
        } catch (error) {
            console.error('Error creating product:', error);
            req.flash('error', 'Error creating product: ' + error.message);
            res.redirect('/products/new');
        }
    },

    getView: (req, res) => {
        try {
            const product = ProductModel.getById(req.params.id);
            if (!product) {
                req.flash('error', 'Product record not found');
                return res.redirect('/products');
            }
            const transactions = TransactionModel.getAll({ productId: product.id, limit: 10 });

            res.render('products/view', {
                title: 'Digital Book | ' + product.name,
                activeNav: 'products',
                product,
                transactions
            });
        } catch (error) {
            console.error('Error viewing product:', error);
            req.flash('error', 'Unable to retrieve product details');
            res.redirect('/products');
        }
    },

    getEdit: (req, res) => {
        try {
            const product = ProductModel.getById(req.params.id);
            if (!product) {
                req.flash('error', 'Product record not found');
                return res.redirect('/products');
            }
            const categories = CategoryModel.getAll();
            const suppliers = SupplierModel.getAll();

            res.render('products/form', {
                title: 'Digital Book | Edit Product ' + product.sku,
                activeNav: 'products',
                isEdit: true,
                product,
                categories,
                suppliers
            });
        } catch (error) {
            console.error('Error loading edit form:', error);
            req.flash('error', 'Unable to load edit form');
            res.redirect('/products');
        }
    },

    postEdit: (req, res) => {
        try {
            const id = req.params.id;
            const { name, sku, description, unit_price, quantity, min_stock_level, category_id, supplier_id } = req.body;

            const existingSku = ProductModel.getBySku(sku, id);
            if (existingSku) {
                req.flash('error', 'Product SKU is already in use by another product.');
                return res.redirect('/products/' + id + '/edit');
            }

            ProductModel.update(id, {
                name: name.trim(),
                sku: sku.trim(),
                description: description ? description.trim() : '',
                unit_price: parseFloat(unit_price),
                quantity: parseInt(quantity, 10) || 0,
                min_stock_level: parseInt(min_stock_level, 10) || 10,
                category_id: parseInt(category_id, 10),
                supplier_id: supplier_id ? parseInt(supplier_id, 10) : null
            });

            req.flash('success', 'Product information updated successfully.');
            res.redirect('/products/' + id);
        } catch (error) {
            console.error('Error updating product:', error);
            req.flash('error', 'Failed to update product: ' + error.message);
            res.redirect('/products/' + req.params.id + '/edit');
        }
    },

    postDelete: (req, res) => {
        try {
            const id = req.params.id;
            const product = ProductModel.getById(id);
            const name = product ? product.name : 'Product';

            // Delete product and its child records cleanly
            ProductModel.delete(id);
            req.flash('success', 'Product and related transaction records removed successfully: ' + name);
            res.redirect('/products');
        } catch (error) {
            console.error('Error deleting product:', error);
            req.flash('error', 'Unable to delete product: ' + error.message);
            res.redirect('/products');
        }
    }
};

module.exports = ProductController;
