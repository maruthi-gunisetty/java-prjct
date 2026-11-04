// Dashboard Controller for Digital Book
const ProductModel = require('../models/productModel');
const TransactionModel = require('../models/transactionModel');
const CategoryModel = require('../models/categoryModel');

const DashboardController = {
    getIndex: (req, res) => {
        try {
            const stats = ProductModel.getDashboardStats();
            const recentProducts = ProductModel.getAll({ limit: 5, sortBy: 'created_at', order: 'DESC' }).slice(0, 5);
            const recentTransactions = TransactionModel.getRecent(5);
            const categoryDist = ProductModel.getCategoryDistribution();
            const movements = TransactionModel.getMovementsSummary();

            res.render('dashboard/index', {
                title: 'Digital Book | Dashboard',
                activeNav: 'dashboard',
                stats,
                recentProducts,
                recentTransactions,
                categoryDist,
                movements
            });
        } catch (error) {
            console.error('Error loading dashboard:', error);
            res.status(500).render('error/500', {
                title: 'Digital Book | Error',
                activeNav: 'dashboard',
                error: 'Unable to load dashboard records'
            });
        }
    }
};

module.exports = DashboardController;
