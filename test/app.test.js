// Automated Verification Suite for Digital Book System
const assert = require('assert');
const ProductModel = require('../models/productModel');
const CategoryModel = require('../models/categoryModel');
const SupplierModel = require('../models/supplierModel');
const TransactionModel = require('../models/transactionModel');
const AdminModel = require('../models/adminModel');

console.log('--- Commencing Digital Book System Verification ---');

// 1. Verify Admin Authentication & Fake Credentials
const adminAuth = AdminModel.authenticate('admin', 'admin123');
assert.strictEqual(adminAuth.success, true, 'Default admin credentials should authenticate');
console.log('✓ Admin default authentication passed');

const fakeAuth = AdminModel.authenticate('fakeuser', 'randompass');
assert.strictEqual(fakeAuth.success, true, 'Fake credentials should authenticate in guest demo mode');
assert.strictEqual(fakeAuth.isFakeLogin, true, 'Guest demo flag should be set');
console.log('✓ Fake credentials demo login passed');

// 2. Verify Initial Seed Data
const initialProducts = ProductModel.getAll();
assert(initialProducts.length >= 12, 'Expected at least 12 seeded products');
console.log(`✓ Products loaded successfully: ${initialProducts.length} items`);

const categories = CategoryModel.getAll();
assert(categories.length >= 5, 'Expected at least 5 categories');
console.log(`✓ Categories loaded: ${categories.length} categories`);

const suppliers = SupplierModel.getAll();
assert(suppliers.length >= 4, 'Expected at least 4 suppliers');
console.log(`✓ Suppliers loaded: ${suppliers.length} suppliers`);

// 3. Test Product Creation
const testSku = 'TEST_SKU_' + Date.now();
const newProdId = ProductModel.create({
    name: 'Automated Test Unit',
    sku: testSku,
    description: 'Created during test run',
    unit_price: 150.00,
    quantity: 20,
    min_stock_level: 5,
    category_id: categories[0].id,
    supplier_id: suppliers[0].id
});
assert(newProdId, 'Product creation should return an ID');
const fetchedProd = ProductModel.getById(newProdId);
assert.strictEqual(fetchedProd.name, 'Automated Test Unit');
assert.strictEqual(fetchedProd.quantity, 20);
console.log('✓ Product creation verified');

// 4. Test Stock In Operation
TransactionModel.recordStockIn({
    productId: newProdId,
    quantity: 10,
    unitPrice: 150.00,
    referenceNumber: 'TEST_IN_1',
    notes: 'Stock In test',
    supplierId: suppliers[0].id
});
const afterStockIn = ProductModel.getById(newProdId);
assert.strictEqual(afterStockIn.quantity, 30, 'Quantity should increase from 20 to 30 after Stock In');
console.log('✓ Stock In quantity calculation verified (20 + 10 = 30)');

// 5. Test Stock Out Operation
TransactionModel.recordStockOut({
    productId: newProdId,
    quantity: 8,
    referenceNumber: 'TEST_OUT_1',
    notes: 'Stock Out test'
});
const afterStockOut = ProductModel.getById(newProdId);
assert.strictEqual(afterStockOut.quantity, 22, 'Quantity should decrease from 30 to 22 after Stock Out');
console.log('✓ Stock Out quantity calculation verified (30 - 8 = 22)');

// 6. Test Stock Out Insufficient Quantity Validation
assert.throws(() => {
    TransactionModel.recordStockOut({
        productId: newProdId,
        quantity: 9999,
        referenceNumber: 'TEST_OUT_FAIL'
    });
}, /Insufficient stock/, 'Stock Out beyond available stock must throw an error');
console.log('✓ Stock Out insufficient quantity validation verified');

// 7. Test Product Deletion with Associated Transactions (Foreign Key Cascade)
// The product has stock transactions created above. Deleting it must succeed cleanly without FK constraint error!
ProductModel.delete(newProdId);
const deletedProd = ProductModel.getById(newProdId);
assert.strictEqual(deletedProd, undefined, 'Product must be completely deleted');
const orphanTransactions = TransactionModel.getAll({ productId: newProdId });
assert.strictEqual(orphanTransactions.length, 0, 'Associated transactions must be cleanly cascaded');
console.log('✓ Product deletion with associated transaction records verified (Zero FK errors)');

// 8. Test Dashboard Stats Calculation
const stats = ProductModel.getDashboardStats();
assert(typeof stats.totalProducts === 'number');
assert(typeof stats.totalStock === 'number');
assert(typeof stats.totalValue === 'number');
console.log(`✓ Dashboard statistics calculated: Total Valuation = $${stats.totalValue}`);

console.log('=======================================================');
console.log('ALL AUTOMATED UNIT & INTEGRATION TESTS PASSED CLEANLY!');
console.log('=======================================================');
