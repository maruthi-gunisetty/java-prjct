package com.inventory.controller;

import com.inventory.entity.Admin;
import com.inventory.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final AdminService adminService;

    public AuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session != null && session.getAttribute("currentAdmin") != null) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        Optional<Admin> adminOpt = adminService.authenticate(username, password);

        if (adminOpt.isPresent()) {
            HttpSession session = request.getSession(true);
            Admin admin = adminOpt.get();
            session.setAttribute("currentAdmin", admin);
            session.setAttribute("adminName", admin.getFullName());
            session.setAttribute("adminRole", admin.getRole());
            session.setAttribute("adminUsername", admin.getUsername());

            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + admin.getFullName());
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("infoMessage", "You have been logged out successfully");
        return "redirect:/login";
    }
}
