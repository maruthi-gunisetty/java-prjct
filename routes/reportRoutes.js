// Report Routes for Digital Book
const express = require('express');
const router = express.Router();
const ReportController = require('../controllers/reportController');

router.get('/reports', ReportController.getIndex);
router.get('/reports/export/csv', ReportController.exportCsv);

module.exports = router;
