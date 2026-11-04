// Supplier Controller for Digital Book
const SupplierModel = require('../models/supplierModel');

const SupplierController = {
    list: (req, res) => {
        try {
            const suppliers = SupplierModel.getAll();
            res.render('suppliers/list', {
                title: 'Digital Book | Supplier Management',
                activeNav: 'suppliers',
                suppliers
            });
        } catch (error) {
            console.error('Error fetching suppliers:', error);
            req.flash('error', 'Unable to retrieve supplier records');
            res.redirect('/dashboard');
        }
    },

    getNew: (req, res) => {
        res.render('suppliers/form', {
            title: 'Digital Book | Register New Supplier',
            activeNav: 'suppliers',
            isEdit: false,
            supplier: {}
        });
    },

    postCreate: (req, res) => {
        try {
            const { name, email, phone, address, contact_person } = req.body;
            if (!name || name.trim() === '') {
                req.flash('error', 'Supplier name is required');
                return res.redirect('/suppliers/new');
            }

            SupplierModel.create({
                name: name.trim(),
                email: email ? email.trim() : '',
                phone: phone ? phone.trim() : '',
                address: address ? address.trim() : '',
                contact_person: contact_person ? contact_person.trim() : ''
            });

            req.flash('success', 'Supplier registered successfully: ' + name);
            res.redirect('/suppliers');
        } catch (error) {
            console.error('Error creating supplier:', error);
            req.flash('error', 'Error creating supplier: ' + error.message);
            res.redirect('/suppliers/new');
        }
    },

    getEdit: (req, res) => {
        try {
            const supplier = SupplierModel.getById(req.params.id);
            if (!supplier) {
                req.flash('error', 'Supplier not found');
                return res.redirect('/suppliers');
            }
            res.render('suppliers/form', {
                title: 'Digital Book | Edit Supplier ' + supplier.name,
                activeNav: 'suppliers',
                isEdit: true,
                supplier
            });
        } catch (error) {
            console.error('Error loading supplier:', error);
            req.flash('error', 'Unable to load supplier details');
            res.redirect('/suppliers');
        }
    },

    postEdit: (req, res) => {
        try {
            const id = req.params.id;
            const { name, email, phone, address, contact_person } = req.body;

            if (!name || name.trim() === '') {
                req.flash('error', 'Supplier name is required');
                return res.redirect('/suppliers/' + id + '/edit');
            }

            SupplierModel.update(id, {
                name: name.trim(),
                email: email ? email.trim() : '',
                phone: phone ? phone.trim() : '',
                address: address ? address.trim() : '',
                contact_person: contact_person ? contact_person.trim() : ''
            });

            req.flash('success', 'Supplier updated successfully');
            res.redirect('/suppliers');
        } catch (error) {
            console.error('Error updating supplier:', error);
            req.flash('error', 'Failed to update supplier: ' + error.message);
            res.redirect('/suppliers/' + req.params.id + '/edit');
        }
    },

    postDelete: (req, res) => {
        try {
            const id = req.params.id;
            SupplierModel.delete(id);
            req.flash('success', 'Supplier removed successfully');
            res.redirect('/suppliers');
        } catch (error) {
            console.error('Error deleting supplier:', error);
            req.flash('error', 'Unable to delete supplier: ' + error.message);
            res.redirect('/suppliers');
        }
    }
};

module.exports = SupplierController;
