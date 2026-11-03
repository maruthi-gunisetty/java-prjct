package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.StockTransaction;
import com.inventory.entity.Supplier;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    public StockTransactionService(StockTransactionRepository transactionRepository,
                                   ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    public StockTransaction recordStockIn(Long productId, Integer quantity, Supplier supplier,
                                          String referenceNumber, String notes, LocalDateTime transactionDate) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Stock In quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID " + productId));

        // Update product quantity automatically
        product.setQuantity(product.getQuantity() + quantity);
        if (supplier != null && product.getSupplier() == null) {
            product.setSupplier(supplier);
        }
        productRepository.save(product);

        // Record stock in transaction
        StockTransaction transaction = new StockTransaction(
                product,
                "STOCK_IN",
                quantity,
                product.getUnitPrice(),
                referenceNumber,
                notes,
                supplier != null ? supplier : product.getSupplier(),
                transactionDate != null ? transactionDate : LocalDateTime.now()
        );

        return transactionRepository.save(transaction);
    }

    public StockTransaction recordStockOut(Long productId, Integer quantity,
                                           String referenceNumber, String notes, LocalDateTime transactionDate) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Stock Out quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID " + productId));

        // Validate available stock
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + product.getName() + ". Available stock is " 
                    + product.getQuantity() + " units, requested is " + quantity + " units"
            );
        }

        // Reduce product quantity automatically
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // Record stock out transaction
        StockTransaction transaction = new StockTransaction(
                product,
                "STOCK_OUT",
                quantity,
                product.getUnitPrice(),
                referenceNumber,
                notes,
                null,
                transactionDate != null ? transactionDate : LocalDateTime.now()
        );

        return transactionRepository.save(transaction);
    }

    public List<StockTransaction> findAll() {
        return transactionRepository.findAllByOrderByTransactionDateDesc();
    }

    public List<StockTransaction> getRecentTransactions() {
        return transactionRepository.findTop5ByOrderByTransactionDateDesc();
    }

    public List<StockTransaction> filterTransactions(Long productId, String transactionType,
                                                    LocalDateTime startDate, LocalDateTime endDate) {
        String cleanType = (transactionType != null && !transactionType.trim().isEmpty() && !transactionType.equalsIgnoreCase("ALL")) 
                ? transactionType.trim() : null;
        return transactionRepository.filterTransactions(productId, cleanType, startDate, endDate);
    }

    public long calculateTotalStockIn() {
        return transactionRepository.calculateTotalQuantityByType("STOCK_IN");
    }

    public long calculateTotalStockOut() {
        return transactionRepository.calculateTotalQuantityByType("STOCK_OUT");
    }
}
