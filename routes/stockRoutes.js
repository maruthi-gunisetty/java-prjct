// Stock Routes for Digital Book
const express = require('express');
const router = express.Router();
const StockController = require('../controllers/stockController');

router.get('/stock/in', StockController.getStockIn);
router.post('/stock/in', StockController.postStockIn);
router.get('/stock/out', StockController.getStockOut);
router.post('/stock/out', StockController.postStockOut);
router.get('/stock/history', StockController.getHistory);
router.get('/stock/low', StockController.getLowStock);

module.exports = router;
