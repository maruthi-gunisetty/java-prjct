package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public SupplierService(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID " + id));
    }

    public Supplier save(Supplier supplier) {
        supplier.setName(supplier.getName().trim());
        return supplierRepository.save(supplier);
    }

    public Supplier update(Long id, Supplier updated) {
        Supplier existing = findById(id);
        existing.setName(updated.getName().trim());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setContactPerson(updated.getContactPerson());
        return supplierRepository.save(existing);
    }

    public void delete(Long id) {
        Supplier supplier = findById(id);
        // Unlink products associated with this supplier
        List<Product> products = productRepository.findBySupplierId(id);
        if (products != null) {
            for (Product p : products) {
                p.setSupplier(null);
                productRepository.save(p);
            }
        }
        supplierRepository.delete(supplier);
    }

    public long count() {
        return supplierRepository.count();
    }
}
