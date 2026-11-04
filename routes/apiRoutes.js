// REST API Routes for Digital Book
const express = require('express');
const router = express.Router();
const ProductModel = require('../models/productModel');
const TransactionModel = require('../models/transactionModel');

router.get('/dashboard/charts', (req, res) => {
    try {
        const categoryData = ProductModel.getCategoryDistribution();
        const movements = TransactionModel.getMovementsSummary();

        res.json({
            categoryLabels: categoryData.map(c => c.name),
            categoryQuantities: categoryData.map(c => c.total_quantity),
            categoryValuations: categoryData.map(c => c.total_valuation),
            movements: {
                stockIn: movements.totalStockIn,
                stockOut: movements.totalStockOut
            }
        });
    } catch (error) {
        console.error('API Error:', error);
        res.status(500).json({ error: 'Failed to retrieve chart data' });
    }
});

module.exports = router;
