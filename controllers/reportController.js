// Report Controller for Digital Book
const ProductModel = require('../models/productModel');
const CategoryModel = require('../models/categoryModel');

const ReportController = {
    getIndex: (req, res) => {
        try {
            const { status, categoryId } = req.query;
            const products = ProductModel.getAll({ status, categoryId, sortBy: 'name', order: 'ASC' });
            const categories = CategoryModel.getAll();

            const totalItems = products.length;
            const totalQuantity = products.reduce((sum, p) => sum + p.quantity, 0);
            const totalValuation = products.reduce((sum, p) => sum + (p.quantity * p.unit_price), 0);

            res.render('reports/index', {
                title: 'Digital Book | Inventory Valuation and Stock Reports',
                activeNav: 'reports',
                products,
                categories,
                selectedStatus: status || '',
                selectedCategoryId: categoryId || '',
                totalItems,
                totalQuantity,
                totalValuation: parseFloat(totalValuation.toFixed(2))
            });
        } catch (error) {
            console.error('Error generating report:', error);
            req.flash('error', 'Unable to generate inventory report');
            res.redirect('/dashboard');
        }
    },

    exportCsv: (req, res) => {
        try {
            const { status, categoryId } = req.query;
            const products = ProductModel.getAll({ status, categoryId, sortBy: 'name', order: 'ASC' });

            const headers = [
                'Product ID',
                'Product Name',
                'SKU',
                'Category',
                'Supplier',
                'Unit Price (USD)',
                'Quantity',
                'Total Valuation (USD)',
                'Stock Status',
                'Registration Date'
            ];

            const rows = products.map(p => {
                const escapeCsv = (val) => {
                    if (val === null || val === undefined) return '""';
                    const str = String(val).replace(/"/g, '""');
                    return `"${str}"`;
                };

                const cleanDate = p.created_at ? p.created_at.toString().replace(/-/g, '/') : '';

                return [
                    p.id,
                    escapeCsv(p.name),
                    escapeCsv(p.sku),
                    escapeCsv(p.category_name || 'Unassigned'),
                    escapeCsv(p.supplier_name || 'None'),
                    p.unit_price.toFixed(2),
                    p.quantity,
                    (p.quantity * p.unit_price).toFixed(2),
                    escapeCsv(p.stock_status),
                    escapeCsv(cleanDate)
                ].join(',');
            });

            const csvContent = [headers.join(','), ...rows].join('\r\n');

            res.setHeader('Content-Type', 'text/csv; charset=utf-8');
            res.setHeader('Content-Disposition', 'attachment; filename="digital_book_inventory_report.csv"');
            res.send(csvContent);
        } catch (error) {
            console.error('Error exporting CSV:', error);
            req.flash('error', 'Unable to export CSV report');
            res.redirect('/reports');
        }
    }
};

module.exports = ReportController;
