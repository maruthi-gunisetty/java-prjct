package com.inventory.repository;

import com.inventory.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByProductIdOrderByTransactionDateDesc(Long productId);

    List<StockTransaction> findTop5ByOrderByTransactionDateDesc();

    List<StockTransaction> findAllByOrderByTransactionDateDesc();

    @Query("SELECT t FROM StockTransaction t WHERE " +
           "(:productId IS NULL OR t.product.id = :productId) AND " +
           "(:transactionType IS NULL OR t.transactionType = :transactionType) AND " +
           "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC")
    List<StockTransaction> filterTransactions(@Param("productId") Long productId,
                                              @Param("transactionType") String transactionType,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM StockTransaction t WHERE t.transactionType = :type")
    long calculateTotalQuantityByType(@Param("type") String type);

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM StockTransaction t WHERE t.transactionType = :type AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    long calculateQuantityByTypeAndDateRange(@Param("type") String type,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
}

