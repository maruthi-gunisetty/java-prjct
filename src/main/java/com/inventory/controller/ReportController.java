package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.entity.StockTransaction;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.ReportService;
import com.inventory.service.StockTransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StockTransactionService stockTransactionService;
    private final ReportService reportService;

    public ReportController(ProductService productService,
                            CategoryService categoryService,
                            StockTransactionService stockTransactionService,
                            ReportService reportService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.stockTransactionService = stockTransactionService;
        this.reportService = reportService;
    }

    @GetMapping
    public String showReports(@RequestParam(value = "reportType", required = false, defaultValue = "ALL_PRODUCTS") String reportType,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(value = "startDate", required = false) 
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam(value = "endDate", required = false) 
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              Model model) {
        List<Product> products = productService.search(null, categoryId);

        if ("LOW_STOCK".equalsIgnoreCase(reportType)) {
            products = products.stream().filter(Product::isLowStock).collect(Collectors.toList());
        } else if ("OUT_OF_STOCK".equalsIgnoreCase(reportType)) {
            products = products.stream().filter(Product::isOutOfStock).collect(Collectors.toList());
        } else if ("IN_STOCK".equalsIgnoreCase(reportType)) {
            products = products.stream().filter(p -> !p.isOutOfStock() && !p.isLowStock()).collect(Collectors.toList());
        }

        BigDecimal totalValuation = products.stream()
                .map(Product::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalUnits = products.stream()
                .mapToLong(p -> p.getQuantity() != null ? p.getQuantity() : 0)
                .sum();

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        List<StockTransaction> transactions = stockTransactionService.filterTransactions(null, null, startDateTime, endDateTime);

        model.addAttribute("reportType", reportType);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("products", products);
        model.addAttribute("transactions", transactions);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("totalValuation", totalValuation);
        model.addAttribute("totalUnits", totalUnits);
        model.addAttribute("activePage", "reports");

        return "reports/index";
    }

    @GetMapping("/export/csv")
    public void exportCsv(@RequestParam(value = "reportType", required = false, defaultValue = "ALL_PRODUCTS") String reportType,
                          @RequestParam(value = "categoryId", required = false) Long categoryId,
                          HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");

        if ("TRANSACTIONS".equalsIgnoreCase(reportType)) {
            response.setHeader("Content-Disposition", "attachment; filename=\"inventory_transactions_report.csv\"");
            List<StockTransaction> transactions = stockTransactionService.findAll();
            String csv = reportService.generateStockTransactionsCsv(transactions);
            response.getWriter().write(csv);
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=\"inventory_products_report.csv\"");
            List<Product> products = productService.search(null, categoryId);
            if ("LOW_STOCK".equalsIgnoreCase(reportType)) {
                products = products.stream().filter(Product::isLowStock).collect(Collectors.toList());
            } else if ("OUT_OF_STOCK".equalsIgnoreCase(reportType)) {
                products = products.stream().filter(Product::isOutOfStock).collect(Collectors.toList());
            }
            String csv = reportService.generateProductInventoryCsv(products);
            response.getWriter().write(csv);
        }
    }
}
