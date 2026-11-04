// Auth Routes for Digital Book
const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/authController');

router.get('/login', AuthController.getLogin);
router.post('/login', AuthController.postLogin);
router.get('/login/demo', AuthController.demoLogin);
router.get('/logout', AuthController.logout);

module.exports = router;
