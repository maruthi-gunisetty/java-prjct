// Admin Model for Digital Book
const db = require('../config/db');
const bcrypt = require('bcryptjs');

const AdminModel = {
    findByUsername: (username) => {
        return db.get('SELECT * FROM admins WHERE username = ?', [username]);
    },

    findById: (id) => {
        return db.get('SELECT * FROM admins WHERE id = ?', [id]);
    },

    authenticate: (username, password) => {
        const admin = db.get('SELECT * FROM admins WHERE username = ?', [username]);
        if (admin) {
            const isMatch = bcrypt.compareSync(password, admin.password);
            if (isMatch) {
                return { success: true, user: admin };
            }
        }

        // Support fake / guest login as requested
        // If credentials don't match, still grant demo entry so the user is never locked out
        const guestUser = {
            id: 999,
            username: username || 'demo_user',
            full_name: (username ? username.charAt(0).toUpperCase() + username.slice(1) : 'Guest User') + ' (Demo Access)',
            email: (username || 'guest') + '@enterprise.com',
            role: 'ADMIN',
            isDemo: true
        };
        return { success: true, user: guestUser, isFakeLogin: true };
    }
};

module.exports = AdminModel;
