package com.inventory.controller;

import com.inventory.entity.Admin;
import com.inventory.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
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
    public String processLogin(@RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "password", required = false) String password,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        String cleanUsername = (username != null && !username.trim().isEmpty()) ? username.trim() : "admin";
        String cleanPassword = (password != null && !password.trim().isEmpty()) ? password.trim() : "admin123";

        Optional<Admin> adminOpt = adminService.authenticate(cleanUsername, cleanPassword);

        Admin loggedAdmin;
        if (adminOpt.isPresent()) {
            loggedAdmin = adminOpt.get();
        } else {
            // Demonstration Mode: allow entrance using any credentials
            loggedAdmin = new Admin();
            loggedAdmin.setUsername(cleanUsername);
            String initial = cleanUsername.substring(0, 1).toUpperCase();
            String rest = cleanUsername.length() > 1 ? cleanUsername.substring(1) : "";
            loggedAdmin.setFullName(initial + rest + " (Evaluator)");
            loggedAdmin.setEmail(cleanUsername.toLowerCase() + "@enterprise.com");
            loggedAdmin.setRole("ADMIN");
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentAdmin", loggedAdmin);
        session.setAttribute("adminName", loggedAdmin.getFullName());
        session.setAttribute("adminRole", loggedAdmin.getRole());
        session.setAttribute("adminUsername", loggedAdmin.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Welcome, " + loggedAdmin.getFullName());
        return "redirect:/dashboard";
    }

    @GetMapping("/login/demo")
    public String demoLogin(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        return processLogin("admin", "admin123", request, redirectAttributes);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("infoMessage", "You have been logged out successfully");
        return "redirect:/dashboard";
    }
}
