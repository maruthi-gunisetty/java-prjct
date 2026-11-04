// Category Routes for Digital Book
const express = require('express');
const router = express.Router();
const CategoryController = require('../controllers/categoryController');

router.get('/categories', CategoryController.list);
router.get('/categories/new', CategoryController.getNew);
router.post('/categories', CategoryController.postCreate);
router.get('/categories/:id/edit', CategoryController.getEdit);
router.post('/categories/:id/edit', CategoryController.postEdit);
router.post('/categories/:id/delete', CategoryController.postDelete);

module.exports = router;
