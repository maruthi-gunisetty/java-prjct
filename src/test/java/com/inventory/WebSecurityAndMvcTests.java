package com.inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityAndMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify unauthorized request to /dashboard redirects to /login")
    void testUnauthorizedRedirect() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Verify unauthorized request to /products redirects to /login")
    void testUnauthorizedProductsRedirect() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Verify /login page is accessible to everyone")
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("Verify successful login sets session and redirects to /dashboard")
    void testSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Verify authorized user with active session can access /dashboard")
    void testAuthorizedDashboardAccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentAdmin", "admin");

        mockMvc.perform(get("/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("totalProducts"))
                .andExpect(model().attributeExists("totalStockQuantity"))
                .andExpect(model().attributeExists("recentProducts"));
    }
}

