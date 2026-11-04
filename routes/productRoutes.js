// Product Routes for Digital Book
const express = require('express');
const router = express.Router();
const ProductController = require('../controllers/productController');

router.get('/products', ProductController.list);
router.get('/products/new', ProductController.getNew);
router.post('/products', ProductController.postCreate);
router.get('/products/:id', ProductController.getView);
router.get('/products/:id/edit', ProductController.getEdit);
router.post('/products/:id/edit', ProductController.postEdit);
router.post('/products/:id/delete', ProductController.postDelete);

module.exports = router;
