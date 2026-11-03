package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.StockTransaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ProductService productService;
    private final StockTransactionService stockTransactionService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public ReportService(ProductService productService, StockTransactionService stockTransactionService) {
        this.productService = productService;
        this.stockTransactionService = stockTransactionService;
    }

    public String generateProductInventoryCsv(List<Product> products) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Header (CRITICAL: zero hyphens)
        pw.println("Product ID,SKU,Product Name,Category,Supplier,Unit Price,Quantity,Min Stock,Status,Total Value,Date Added");

        for (Product p : products) {
            String categoryName = p.getCategory() != null ? p.getCategory().getName() : "None";
            String supplierName = p.getSupplier() != null ? p.getSupplier().getName() : "None";
            String dateAdded = p.getCreatedAt() != null ? p.getCreatedAt().format(DATE_FMT) : "None";

            pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%d,%d,\"%s\",%.2f,\"%s\"%n",
                    p.getId(),
                    escapeCsv(p.getSku()),
                    escapeCsv(p.getName()),
                    escapeCsv(categoryName),
                    escapeCsv(supplierName),
                    p.getUnitPrice(),
                    p.getQuantity(),
                    p.getMinStockLevel(),
                    escapeCsv(p.getStockStatus()),
                    p.getTotalValue(),
                    dateAdded
            );
        }

        pw.flush();
        return sw.toString();
    }

    public String generateStockTransactionsCsv(List<StockTransaction> transactions) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Header (CRITICAL: zero hyphens)
        pw.println("Transaction ID,Date,Type,Product SKU,Product Name,Quantity,Unit Price,Total Amount,Reference,Supplier,Notes");

        for (StockTransaction t : transactions) {
            String sku = t.getProduct() != null ? t.getProduct().getSku() : "None";
            String prodName = t.getProduct() != null ? t.getProduct().getName() : "None";
            String supplierName = t.getSupplier() != null ? t.getSupplier().getName() : "None";
            String date = t.getTransactionDate() != null ? t.getTransactionDate().format(DATE_FMT) : "None";
            String ref = t.getReferenceNumber() != null ? t.getReferenceNumber() : "None";
            String notes = t.getNotes() != null ? t.getNotes() : "None";

            pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%d,%.2f,%.2f,\"%s\",\"%s\",\"%s\"%n",
                    t.getId(),
                    date,
                    t.getTypeDisplayName(),
                    escapeCsv(sku),
                    escapeCsv(prodName),
                    t.getQuantity(),
                    t.getUnitPrice() != null ? t.getUnitPrice() : BigDecimal.ZERO,
                    t.getTotalAmount(),
                    escapeCsv(ref),
                    escapeCsv(supplierName),
                    escapeCsv(notes)
            );
        }

        pw.flush();
        return sw.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) {
            return "";
        }
        return val.replace("\"", "\"\"");
    }
}
