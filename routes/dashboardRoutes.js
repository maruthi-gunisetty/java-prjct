// Dashboard Routes for Digital Book
const express = require('express');
const router = express.Router();
const DashboardController = require('../controllers/dashboardController');

router.get('/', (req, res) => res.redirect('/dashboard'));
router.get('/dashboard', DashboardController.getIndex);

module.exports = router;
