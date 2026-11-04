// Category Controller for Digital Book
const CategoryModel = require('../models/categoryModel');

const CategoryController = {
    list: (req, res) => {
        try {
            const categories = CategoryModel.getAll();
            res.render('categories/list', {
                title: 'Digital Book | Category Management',
                activeNav: 'categories',
                categories
            });
        } catch (error) {
            console.error('Error fetching categories:', error);
            req.flash('error', 'Unable to retrieve category records');
            res.redirect('/dashboard');
        }
    },

    getNew: (req, res) => {
        res.render('categories/form', {
            title: 'Digital Book | New Product Category',
            activeNav: 'categories',
            isEdit: false,
            category: {}
        });
    },

    postCreate: (req, res) => {
        try {
            const { name, description } = req.body;
            if (!name || name.trim() === '') {
                req.flash('error', 'Category name is required');
                return res.redirect('/categories/new');
            }

            const existing = CategoryModel.getByName(name.trim());
            if (existing) {
                req.flash('error', 'Category name already exists');
                return res.redirect('/categories/new');
            }

            CategoryModel.create({
                name: name.trim(),
                description: description ? description.trim() : ''
            });

            req.flash('success', 'Category created successfully: ' + name);
            res.redirect('/categories');
        } catch (error) {
            console.error('Error creating category:', error);
            req.flash('error', 'Error creating category: ' + error.message);
            res.redirect('/categories/new');
        }
    },

    getEdit: (req, res) => {
        try {
            const category = CategoryModel.getById(req.params.id);
            if (!category) {
                req.flash('error', 'Category not found');
                return res.redirect('/categories');
            }
            res.render('categories/form', {
                title: 'Digital Book | Edit Category ' + category.name,
                activeNav: 'categories',
                isEdit: true,
                category
            });
        } catch (error) {
            console.error('Error loading category:', error);
            req.flash('error', 'Unable to load category details');
            res.redirect('/categories');
        }
    },

    postEdit: (req, res) => {
        try {
            const id = req.params.id;
            const { name, description } = req.body;

            if (!name || name.trim() === '') {
                req.flash('error', 'Category name is required');
                return res.redirect('/categories/' + id + '/edit');
            }

            const existing = CategoryModel.getByName(name.trim(), id);
            if (existing) {
                req.flash('error', 'Another category already exists with that name');
                return res.redirect('/categories/' + id + '/edit');
            }

            CategoryModel.update(id, {
                name: name.trim(),
                description: description ? description.trim() : ''
            });

            req.flash('success', 'Category updated successfully');
            res.redirect('/categories');
        } catch (error) {
            console.error('Error updating category:', error);
            req.flash('error', 'Failed to update category: ' + error.message);
            res.redirect('/categories/' + req.params.id + '/edit');
        }
    },

    postDelete: (req, res) => {
        try {
            const id = req.params.id;
            const productCount = CategoryModel.countProducts(id);

            if (productCount > 0) {
                req.flash('error', 'Cannot remove category with ' + productCount + ' active products. Please reassign or delete products first.');
                return res.redirect('/categories');
            }

            CategoryModel.delete(id);
            req.flash('success', 'Category deleted successfully');
            res.redirect('/categories');
        } catch (error) {
            console.error('Error deleting category:', error);
            req.flash('error', 'Unable to remove category: ' + error.message);
            res.redirect('/categories');
        }
    }
};

module.exports = CategoryController;
