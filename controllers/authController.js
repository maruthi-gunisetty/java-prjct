// Auth Controller for Digital Book
const AdminModel = require('../models/adminModel');

const AuthController = {
    getLogin: (req, res) => {
        if (req.session.user) {
            return res.redirect('/dashboard');
        }
        res.render('auth/login', {
            title: 'Digital Book | Administrator Access',
            activeNav: 'login'
        });
    },

    postLogin: (req, res) => {
        const { username, password } = req.body;
        const result = AdminModel.authenticate(username, password);

        if (result.success) {
            req.session.user = result.user;
            if (result.isFakeLogin) {
                req.flash('info', 'Authenticated in Guest Demo Mode with provided credentials.');
            } else {
                req.flash('success', 'Welcome back, ' + result.user.full_name);
            }
            return res.redirect('/dashboard');
        } else {
            req.flash('error', 'Authentication failed. Please verify credentials.');
            return res.redirect('/login');
        }
    },

    demoLogin: (req, res) => {
        req.session.user = {
            id: 1,
            username: 'admin',
            full_name: 'Demo Administrator',
            email: 'admin@enterprise.com',
            role: 'ADMIN',
            isDemo: true
        };
        req.flash('success', 'Logged in as Demo Administrator with full administrative access.');
        res.redirect('/dashboard');
    },

    logout: (req, res) => {
        req.session.destroy(() => {
            res.redirect('/dashboard');
        });
    }
};

module.exports = AuthController;
