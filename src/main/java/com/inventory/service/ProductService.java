package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Product> findAllSorted(String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction != null ? direction : "ASC"), 
                            sortBy != null ? sortBy : "name");
        return productRepository.findAll(sort);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID " + id));
    }

    public Product save(Product product) {
        String cleanSku = product.getSku() != null ? product.getSku().trim().toUpperCase() : "";
        product.setSku(cleanSku);

        if (product.getId() == null) {
            if (productRepository.existsBySku(cleanSku)) {
                throw new DuplicateResourceException("Product with SKU " + cleanSku + " already exists");
            }
        } else {
            if (productRepository.existsBySkuAndIdNot(cleanSku, product.getId())) {
                throw new DuplicateResourceException("Another product with SKU " + cleanSku + " already exists");
            }
        }

        if (product.getQuantity() == null) {
            product.setQuantity(0);
        }
        if (product.getMinStockLevel() == null) {
            product.setMinStockLevel(10);
        }

        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = findById(id);
        String cleanSku = updated.getSku() != null ? updated.getSku().trim().toUpperCase() : "";

        if (productRepository.existsBySkuAndIdNot(cleanSku, id)) {
            throw new DuplicateResourceException("Another product with SKU " + cleanSku + " already exists");
        }

        existing.setName(updated.getName().trim());
        existing.setSku(cleanSku);
        existing.setDescription(updated.getDescription());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setQuantity(updated.getQuantity());
        existing.setMinStockLevel(updated.getMinStockLevel());
        existing.setCategory(updated.getCategory());
        existing.setSupplier(updated.getSupplier());

        return productRepository.save(existing);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public List<Product> search(String keyword, Long categoryId) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return productRepository.searchProducts(cleanKeyword, categoryId);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    public List<Product> getRecentProducts() {
        return productRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public long getTotalStockQuantity() {
        return productRepository.calculateTotalStockQuantity();
    }

    public BigDecimal getTotalInventoryValue() {
        BigDecimal val = productRepository.calculateTotalInventoryValue();
        return val != null ? val : BigDecimal.ZERO;
    }

    public long countLowStock() {
        return productRepository.countLowStockProducts();
    }

    public long countOutOfStock() {
        return productRepository.countOutOfStockProducts();
    }

    public long countTotal() {
        return productRepository.count();
    }

    public void updateStock(Product product, int deltaQuantity) {
        int newQuantity = product.getQuantity() + deltaQuantity;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot become negative");
        }
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }
}
