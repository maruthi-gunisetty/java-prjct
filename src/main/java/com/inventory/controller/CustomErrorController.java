package com.inventory.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = 500;
        String title = "System Notification";
        String message = "An unexpected error occurred while processing your request.";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                title = "Page Not Found";
                message = "The requested resource could not be located on the server.";
                model.addAttribute("errorCode", 404);
                model.addAttribute("errorTitle", title);
                model.addAttribute("errorMessage", message);
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                title = "Access Restricted";
                message = "You do not have authorization to view this resource.";
            }
        }

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        return "error/500";
    }
}
