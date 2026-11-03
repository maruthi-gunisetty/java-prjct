package com.inventory.controller;

import com.inventory.entity.Supplier;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("activePage", "suppliers");
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("pageTitle", "Add New Supplier");
        model.addAttribute("activePage", "suppliers");
        return "suppliers/form";
    }

    @PostMapping("/save")
    public String saveSupplier(@Valid @ModelAttribute("supplier") Supplier supplier,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", supplier.getId() == null ? "Add New Supplier" : "Edit Supplier");
            model.addAttribute("activePage", "suppliers");
            return "suppliers/form";
        }

        if (supplier.getId() == null) {
            supplierService.save(supplier);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier created successfully");
        } else {
            supplierService.update(supplier.getId(), supplier);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully");
        }

        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Supplier supplier = supplierService.findById(id);
        model.addAttribute("supplier", supplier);
        model.addAttribute("pageTitle", "Edit Supplier");
        model.addAttribute("activePage", "suppliers");
        return "suppliers/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier removed successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete supplier due to associated records");
        }
        return "redirect:/suppliers";
    }
}

