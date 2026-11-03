package com.inventory.service;

import com.inventory.entity.Category;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID " + id));
    }

    public Category save(Category category) {
        if (category.getId() == null && categoryRepository.existsByName(category.getName().trim())) {
            throw new DuplicateResourceException("Category name already exists");
        }
        category.setName(category.getName().trim());
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category updatedCategory) {
        Category existing = findById(id);
        if (!existing.getName().equalsIgnoreCase(updatedCategory.getName().trim()) 
                && categoryRepository.existsByName(updatedCategory.getName().trim())) {
            throw new DuplicateResourceException("Category name already exists");
        }
        existing.setName(updatedCategory.getName().trim());
        existing.setDescription(updatedCategory.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        Category category = findById(id);
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category because it currently contains " 
                    + category.getProducts().size() + " products");
        }
        categoryRepository.delete(category);
    }

    public long count() {
        return categoryRepository.count();
    }
}
