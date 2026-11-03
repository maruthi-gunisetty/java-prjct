package com.inventory.controller;

import com.inventory.entity.Category;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("activePage", "categories");
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Add New Category");
        model.addAttribute("activePage", "categories");
        return "categories/form";
    }

    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", category.getId() == null ? "Add New Category" : "Edit Category");
            model.addAttribute("activePage", "categories");
            return "categories/form";
        }

        try {
            if (category.getId() == null) {
                categoryService.save(category);
                redirectAttributes.addFlashAttribute("successMessage", "Category created successfully");
            } else {
                categoryService.update(category.getId(), category);
                redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
            }
        } catch (DuplicateResourceException e) {
            bindingResult.rejectValue("name", "error.category", e.getMessage());
            model.addAttribute("pageTitle", category.getId() == null ? "Add New Category" : "Edit Category");
            model.addAttribute("activePage", "categories");
            return "categories/form";
        }

        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Edit Category");
        model.addAttribute("activePage", "categories");
        return "categories/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete category due to associated records");
        }
        return "redirect:/categories";
    }

    @GetMapping("/view/{id}")
    public String viewCategoryProducts(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("products", category.getProducts());
        model.addAttribute("activePage", "categories");
        return "categories/view";
    }
}
