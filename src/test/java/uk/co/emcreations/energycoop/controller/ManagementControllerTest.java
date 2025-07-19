package uk.co.emcreations.energycoop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.service.AuthService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagementController.class)
class ManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("GET /hello returns 200 OK")
    void testGetStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello").with(oidcLogin()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /threadCheck returns 200 OK")
    void testThreadCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/threadCheck").with(oidcLogin()))
                .andExpect(status().isOk());
    }
}