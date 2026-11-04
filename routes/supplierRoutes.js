// Supplier Routes for Digital Book
const express = require('express');
const router = express.Router();
const SupplierController = require('../controllers/supplierController');

router.get('/suppliers', SupplierController.list);
router.get('/suppliers/new', SupplierController.getNew);
router.post('/suppliers', SupplierController.postCreate);
router.get('/suppliers/:id/edit', SupplierController.getEdit);
router.post('/suppliers/:id/edit', SupplierController.postEdit);
router.post('/suppliers/:id/delete', SupplierController.postDelete);

module.exports = router;
