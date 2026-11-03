package com.inventory.controller;

import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listProducts(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                               @RequestParam(value = "sortBy", required = false, defaultValue = "name") String sortBy,
                               @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction,
                               Model model) {
        List<Product> products = productService.search(keyword, categoryId);

        // In-memory sort based on selected parameter
        Comparator<Product> comparator;
        switch (sortBy.toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(Product::getUnitPrice);
                break;
            case "quantity":
                comparator = Comparator.comparing(Product::getQuantity);
                break;
            case "sku":
                comparator = Comparator.comparing(Product::getSku);
                break;
            case "date":
                comparator = Comparator.comparing(Product::getCreatedAt);
                break;
            case "name":
            default:
                comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        products.sort(comparator);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("activePage", "products");
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("pageTitle", "Add New Product");
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("pageTitle", product.getId() == null ? "Add New Product" : "Edit Product");
            model.addAttribute("activePage", "products");
            return "products/form";
        }

        try {
            if (product.getId() == null) {
                productService.save(product);
                redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
            } else {
                productService.update(product.getId(), product);
                redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
            }
        } catch (DuplicateResourceException e) {
            bindingResult.rejectValue("sku", "error.product", e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("pageTitle", product.getId() == null ? "Add New Product" : "Edit Product");
            model.addAttribute("activePage", "products");
            return "products/form";
        }

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("pageTitle", "Edit Product");
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product removed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to remove product due to associated records");
        }
        return "redirect:/products";
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("activePage", "products");
        return "products/view";
    }
}
