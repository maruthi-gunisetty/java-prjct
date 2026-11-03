package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findBySupplierId(Long supplierId);

    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND p.quantity <= p.minStockLevel ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.quantity = 0 ORDER BY p.name ASC")
    List<Product> findOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0 AND p.quantity <= p.minStockLevel")
    long countLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity = 0")
    long countOutOfStockProducts();

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p")
    long calculateTotalStockQuantity();

    @Query("SELECT COALESCE(SUM(p.quantity * p.unitPrice), 0.0) FROM Product p")
    BigDecimal calculateTotalInventoryValue();

    List<Product> findTop5ByOrderByCreatedAtDesc();

    List<Product> findAllByOrderByCreatedAtDesc();
}
