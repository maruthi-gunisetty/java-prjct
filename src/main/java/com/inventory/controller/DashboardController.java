package com.inventory.controller;

import com.inventory.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        Map<String, Object> data = dashboardService.getDashboardData();
        model.addAllAttributes(data);
        model.addAttribute("activePage", "dashboard");
        return "dashboard/index";
    }
}
