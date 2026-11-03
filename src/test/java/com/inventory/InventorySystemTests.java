package com.inventory;

import com.inventory.entity.Admin;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.InsufficientStockException;
import com.inventory.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InventorySystemTests {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private StockTransactionService stockTransactionService;

    @Autowired
    private DashboardService dashboardService;

    @Test
    @DisplayName("Test Admin Authentication with BCrypt")
    void testAdminAuthentication() {
        Optional<Admin> validAuth = adminService.authenticate("admin", "admin123");
        assertTrue(validAuth.isPresent());
        assertEquals("admin", validAuth.get().getUsername());

        Optional<Admin> invalidAuth = adminService.authenticate("admin", "wrongpassword");
        assertFalse(invalidAuth.isPresent());
    }

    @Test
    @DisplayName("Test Product Duplicate SKU Prevention")
    void testProductDuplicateSku() {
        Category cat = categoryService.findAll().get(0);
        Product duplicate = new Product("Duplicate Test", "SKU1001", "Testing duplicate SKU", 
                new BigDecimal("99.99"), 10, 5, cat, null);

        assertThrows(DuplicateResourceException.class, () -> {
            productService.save(duplicate);
        });
    }

    @Test
    @DisplayName("Test Stock In Auto Increment")
    void testStockInAutoIncrement() {
        List<Product> products = productService.findAll();
        Product product = products.get(0);
        int initialQuantity = product.getQuantity();

        stockTransactionService.recordStockIn(product.getId(), 25, null, "TESTPO1", "Test Notes", LocalDateTime.now());

        Product updated = productService.findById(product.getId());
        assertEquals(initialQuantity + 25, updated.getQuantity());
    }

    @Test
    @DisplayName("Test Stock Out Auto Decrement")
    void testStockOutAutoDecrement() {
        List<Product> products = productService.findAll();
        // Pick product with available stock
        Product product = products.stream().filter(p -> p.getQuantity() >= 10).findFirst().orElseThrow();
        int initialQuantity = product.getQuantity();

        stockTransactionService.recordStockOut(product.getId(), 5, "TESTSO1", "Test Out Notes", LocalDateTime.now());

        Product updated = productService.findById(product.getId());
        assertEquals(initialQuantity - 5, updated.getQuantity());
    }

    @Test
    @DisplayName("Test Insufficient Stock Validation on Stock Out")
    void testInsufficientStockValidation() {
        List<Product> products = productService.findAll();
        Product product = products.stream().filter(p -> p.getQuantity() > 0).findFirst().orElseThrow();
        int excessQuantity = product.getQuantity() + 999;

        assertThrows(InsufficientStockException.class, () -> {
            stockTransactionService.recordStockOut(product.getId(), excessQuantity, "ERR_SO", "Overdraw test", LocalDateTime.now());
        });
    }

    @Test
    @DisplayName("Test Category Deletion Prevention When Products Exist")
    void testCategoryDeleteValidation() {
        Category catWithProducts = categoryService.findAll().stream()
                .filter(c -> c.getProductCount() > 0)
                .findFirst()
                .orElseThrow();

        assertThrows(IllegalStateException.class, () -> {
            categoryService.delete(catWithProducts.getId());
        });
    }

    @Test
    @DisplayName("Test Dashboard Metrics Aggregation")
    void testDashboardAggregation() {
        var data = dashboardService.getDashboardData();
        assertNotNull(data);
        assertTrue((Long) data.get("totalProducts") > 0);
        assertTrue((Long) data.get("totalStockQuantity") > 0);
        assertNotNull(data.get("totalInventoryValue"));
    }
}

