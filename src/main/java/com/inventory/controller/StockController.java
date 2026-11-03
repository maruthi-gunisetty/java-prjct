package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.entity.StockTransaction;
import com.inventory.entity.Supplier;
import com.inventory.exception.InsufficientStockException;
import com.inventory.service.ProductService;
import com.inventory.service.StockTransactionService;
import com.inventory.service.SupplierService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final StockTransactionService stockTransactionService;

    public StockController(ProductService productService,
                           SupplierService supplierService,
                           StockTransactionService stockTransactionService) {
        this.productService = productService;
        this.supplierService = supplierService;
        this.stockTransactionService = stockTransactionService;
    }

    @GetMapping("/in")
    public String showStockInForm(@RequestParam(value = "productId", required = false) Long productId,
                                  Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("selectedProductId", productId);
        model.addAttribute("activePage", "stock_in");
        return "stock/stock_in";
    }

    @PostMapping("/in")
    public String processStockIn(@RequestParam("productId") Long productId,
                                 @RequestParam("quantity") Integer quantity,
                                 @RequestParam(value = "supplierId", required = false) Long supplierId,
                                 @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
                                 @RequestParam(value = "notes", required = false) String notes,
                                 RedirectAttributes redirectAttributes) {
        if (quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stock In quantity must be at least 1");
            return "redirect:/stock/in" + (productId != null ? "?productId=" + productId : "");
        }

        try {
            Supplier supplier = (supplierId != null && supplierId > 0) ? supplierService.findById(supplierId) : null;
            StockTransaction txn = stockTransactionService.recordStockIn(
                    productId, quantity, supplier, referenceNumber, notes, LocalDateTime.now()
            );
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully recorded Stock In of " + quantity + " units for " + txn.getProduct().getName());
            return "redirect:/stock/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/in" + (productId != null ? "?productId=" + productId : "");
        }
    }

    @GetMapping("/out")
    public String showStockOutForm(@RequestParam(value = "productId", required = false) Long productId,
                                   Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("selectedProductId", productId);
        model.addAttribute("activePage", "stock_out");
        return "stock/stock_out";
    }

    @PostMapping("/out")
    public String processStockOut(@RequestParam("productId") Long productId,
                                  @RequestParam("quantity") Integer quantity,
                                  @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
                                  @RequestParam(value = "notes", required = false) String notes,
                                  RedirectAttributes redirectAttributes) {
        if (quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stock Out quantity must be at least 1");
            return "redirect:/stock/out" + (productId != null ? "?productId=" + productId : "");
        }

        try {
            StockTransaction txn = stockTransactionService.recordStockOut(
                    productId, quantity, referenceNumber, notes, LocalDateTime.now()
            );
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully recorded Stock Out of " + quantity + " units for " + txn.getProduct().getName());
            return "redirect:/stock/history";
        } catch (InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/out" + (productId != null ? "?productId=" + productId : "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/out" + (productId != null ? "?productId=" + productId : "");
        }
    }

    @GetMapping("/history")
    public String viewHistory(@RequestParam(value = "productId", required = false) Long productId,
                              @RequestParam(value = "transactionType", required = false) String transactionType,
                              @RequestParam(value = "startDate", required = false) 
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam(value = "endDate", required = false) 
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              Model model) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        List<StockTransaction> transactions = stockTransactionService.filterTransactions(
                productId, transactionType, startDateTime, endDateTime
        );

        model.addAttribute("transactions", transactions);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("selectedProductId", productId);
        model.addAttribute("selectedType", transactionType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activePage", "stock_history");
        return "stock/history";
    }

    @GetMapping("/low-stock")
    public String viewLowStockAlerts(Model model) {
        model.addAttribute("lowStockProducts", productService.getLowStockProducts());
        model.addAttribute("outOfStockProducts", productService.getOutOfStockProducts());
        model.addAttribute("activePage", "low_stock");
        return "stock/low_stock";
    }
}

