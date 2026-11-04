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
    @DisplayName("Verify root / directly loads the dashboard without forced login")
    void testRootLoadsDashboardDirectly() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("totalProducts"));
    }

    @Test
    @DisplayName("Verify /dashboard directly accessible without forced login")
    void testDashboardDirectAccess() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("totalProducts"))
                .andExpect(model().attributeExists("totalStockQuantity"))
                .andExpect(model().attributeExists("recentProducts"));
    }

    @Test
    @DisplayName("Verify /products directly accessible without forced login")
    void testProductsDirectAccess() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @DisplayName("Verify /login page is accessible to everyone")
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("Verify successful login with admin credentials")
    void testSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Verify login with fake credentials also grants access in demonstration mode")
    void testLoginWithFakeCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "fakeUser99")
                        .param("password", "randomSecret456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Verify one click demo login route /login/demo")
    void testOneClickDemoLogin() throws Exception {
        mockMvc.perform(get("/login/demo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
