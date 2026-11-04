// Digital Book Inventory Management Platform Server
// Full Stack Node.js + Express + EJS + SQLite
require('dotenv').config();
const express = require('express');
const session = require('express-session');
const flash = require('connect-flash');
const path = require('path');

// Ensure database tables and initial seed data are primed
require('./config/db');

// Route Handlers
const dashboardRoutes = require('./routes/dashboardRoutes');
const authRoutes = require('./routes/authRoutes');
const productRoutes = require('./routes/productRoutes');
const categoryRoutes = require('./routes/categoryRoutes');
const supplierRoutes = require('./routes/supplierRoutes');
const stockRoutes = require('./routes/stockRoutes');
const reportRoutes = require('./routes/reportRoutes');
const apiRoutes = require('./routes/apiRoutes');

const app = express();
const PORT = process.env.PORT || 8080;

// View Engine Setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// Body Parsers & Static Files
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Session & Flash Messages
app.use(session({
    secret: process.env.SESSION_SECRET || 'digital-book-enterprise-secret-key-2026',
    resave: false,
    saveUninitialized: false,
    cookie: {
        maxAge: 1000 * 60 * 60 * 24 // 24 hours
    }
}));
app.use(flash());

// Global template variables middleware
app.use((req, res, next) => {
    res.locals.user = req.session.user || null;
    res.locals.messages = {
        success: req.flash('success'),
        error: req.flash('error'),
        info: req.flash('info')
    };
    next();
});

// Mount Routes
app.use('/', dashboardRoutes);
app.use('/', authRoutes);
app.use('/', productRoutes);
app.use('/', categoryRoutes);
app.use('/', supplierRoutes);
app.use('/', stockRoutes);
app.use('/', reportRoutes);
app.use('/api', apiRoutes);

// 404 Handler
app.use((req, res) => {
    res.status(404).render('error/404', {
        title: 'Digital Book | 404 Not Found',
        activeNav: ''
    });
});

// 500 Error Handler
app.use((err, req, res, next) => {
    console.error('Unhandled Application Exception:', err);
    res.status(500).render('error/500', {
        title: 'Digital Book | System Exception',
        activeNav: '',
        error: err.message || 'An unexpected server error occurred'
    });
});

// Launch Server
if (require.main === module) {
    app.listen(PORT, () => {
        console.log(`=======================================================`);
        console.log(`Digital Book Management System running at http://localhost:${PORT}`);
        console.log(`Enterprise Dashboard directly accessible on port ${PORT}`);
        console.log(`Zero emojis, Zero hyphens, Roboto typography active`);
        console.log(`=======================================================`);
    });
}

module.exports = app;
