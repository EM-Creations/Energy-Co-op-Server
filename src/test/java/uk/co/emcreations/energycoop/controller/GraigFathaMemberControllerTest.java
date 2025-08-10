package uk.co.emcreations.energycoop.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GraigFathaMemberController.class)
class GraigFathaMemberControllerTest {
    private final static String baseURL = "/api/v1/graigFatha/member";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    GraigFathaMemberService service;

    @Test
    @DisplayName("GET /todaySavings returns servlet exception when principal is not set")
    void testGetTodaySavings() {
        var expectedSavings = 100.0;
        var wattageOwnership = 100.0;

        when(service.getTodaySavings(wattageOwnership)).thenReturn(expectedSavings);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/todaySavings").with(oidcLogin()))
                    .andExpect(status().isOk())
                    .andReturn();
        });
    }
}