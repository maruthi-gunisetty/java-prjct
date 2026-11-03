package com.inventory.service;

import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.StockTransaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StockTransactionService stockTransactionService;

    public DashboardService(ProductService productService,
                            CategoryService categoryService,
                            StockTransactionService stockTransactionService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.stockTransactionService = stockTransactionService;
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        long totalProducts = productService.countTotal();
        long totalStockQuantity = productService.getTotalStockQuantity();
        long lowStockCount = productService.countLowStock();
        long outOfStockCount = productService.countOutOfStock();
        BigDecimal totalInventoryValue = productService.getTotalInventoryValue();

        List<Product> recentProducts = productService.getRecentProducts();
        List<StockTransaction> recentTransactions = stockTransactionService.getRecentTransactions();

        data.put("totalProducts", totalProducts);
        data.put("totalStockQuantity", totalStockQuantity);
        data.put("lowStockCount", lowStockCount);
        data.put("outOfStockCount", outOfStockCount);
        data.put("totalInventoryValue", totalInventoryValue);
        data.put("recentProducts", recentProducts);
        data.put("recentTransactions", recentTransactions);

        // Chart 1: Stock Distribution by Category
        List<Category> categories = categoryService.findAll();
        List<String> categoryLabels = new ArrayList<>();
        List<Long> categoryStockCounts = new ArrayList<>();

        for (Category cat : categories) {
            categoryLabels.add(cat.getName());
            long sum = cat.getProducts() != null 
                    ? cat.getProducts().stream().mapToLong(p -> p.getQuantity() != null ? p.getQuantity() : 0).sum()
                    : 0;
            categoryStockCounts.add(sum);
        }
        data.put("categoryLabels", categoryLabels);
        data.put("categoryStockCounts", categoryStockCounts);

        // Chart 2: Product Stock vs Minimum Stock Overview (Top 6 products)
        List<Product> allProducts = productService.findAll();
        List<String> productNames = new ArrayList<>();
        List<Integer> currentStocks = new ArrayList<>();
        List<Integer> minStocks = new ArrayList<>();

        int limit = Math.min(allProducts.size(), 7);
        for (int i = 0; i < limit; i++) {
            Product p = allProducts.get(i);
            productNames.add(p.getName());
            currentStocks.add(p.getQuantity());
            minStocks.add(p.getMinStockLevel());
        }
        data.put("overviewProductNames", productNames);
        data.put("overviewCurrentStocks", currentStocks);
        data.put("overviewMinStocks", minStocks);

        // Chart 3: Stock Movement Overview (Stock In vs Stock Out)
        long totalStockIn = stockTransactionService.calculateTotalStockIn();
        long totalStockOut = stockTransactionService.calculateTotalStockOut();
        data.put("totalStockIn", totalStockIn);
        data.put("totalStockOut", totalStockOut);

        return data;
    }
}
