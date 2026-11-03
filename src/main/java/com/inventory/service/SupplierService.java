package com.inventory.service;

import com.inventory.entity.Supplier;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
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
        if (supplier.getProducts() != null && !supplier.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete supplier because it is associated with " 
                    + supplier.getProducts().size() + " products");
        }
        supplierRepository.delete(supplier);
    }

    public long count() {
        return supplierRepository.count();
    }
}
