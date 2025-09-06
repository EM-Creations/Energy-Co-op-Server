package uk.co.emcreations.energycoop.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.dto.EnergySaving;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;
import uk.co.emcreations.energycoop.util.PrincipalHelper;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GraigFathaMemberController.class)
class GraigFathaMemberControllerTest {
    private final static String baseURL = "/api/v1/graigFatha/member";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    GraigFathaMemberService service;

    private MockedStatic<PrincipalHelper> principalHelperMock;

    @BeforeEach
    void setUp() {
        // This method can be used to set up common test data or mocks if needed
        principalHelperMock = mockStatic(PrincipalHelper.class);

        EnumMap<Site, Double> ownerships = new EnumMap<>(Site.class);
        ownerships.put(Site.GRAIG_FATHA, 100.0);
        principalHelperMock.when(() -> PrincipalHelper.extractOwnershipsFromPrincipal(any(Principal.class)))
                .thenReturn(ownerships);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        principalHelperMock.close();
    }

    @Test
    @DisplayName("GET /todaySavings returns servlet exception when principal is not set")
    void testGetTodaySavings() {
        var expectedSavings = new EnergySaving(
                100.0,
                "GBP", // Assuming GBP as the currency, can be parameterized if needed
                1.0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        var wattageOwnership = 100.0;

        when(service.getTodaySavings(wattageOwnership)).thenReturn(expectedSavings);
        principalHelperMock.when(() -> PrincipalHelper.extractOwnershipsFromPrincipal(any(Principal.class)))
                .thenReturn(null);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/todaySavings").with(oidcLogin()))
                    .andExpect(status().isOk())
                    .andReturn();
        });
    }

    @Test
    @DisplayName("GET /todaySavings returns 200 OK with valid principal")
    void testGetTodaySavings_success() throws Exception {
        Principal principal = mock(Principal.class);

        var expectedSavings = new EnergySaving(100.0, "GBP", 1.0, LocalDateTime.now(), LocalDateTime.now());
        when(service.getTodaySavings(100.0)).thenReturn(expectedSavings);
        mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/todaySavings").with(oidcLogin()).principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /savings/{from}/{to} returns 200 OK with valid principal and dates")
    void testGetSavings_success() throws Exception {
        Principal principal = mock(Principal.class);
        EnumMap<Site, Double> ownerships = new EnumMap<>(Site.class);
        ownerships.put(Site.GRAIG_FATHA, 50.0);
        Set<EnergySaving> expectedSet = Set.of(new EnergySaving(50.0, "GBP", 1.0, LocalDateTime.now(), LocalDateTime.now()));
        when(service.getSavings(any(LocalDate.class), any(LocalDate.class), eq(50.0), anyString())).thenReturn(expectedSet);
        mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/savings/2023-01-01/2023-01-02").with(oidcLogin()).principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /savings/{from}/{to} returns 400 Bad Request for invalid date format")
    void testGetSavings_invalidDate() throws Exception {
        Principal principal = mock(Principal.class);
        mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/savings/invalid/2023-01-02").with(oidcLogin()).principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /todaySavings returns 500 if PrincipalHelper throws")
    void testGetTodaySavings_principalHelperThrows() {
        Principal principal = mock(Principal.class);

        principalHelperMock.when(() -> PrincipalHelper.extractOwnershipsFromPrincipal(any(Principal.class)))
                .thenThrow(new RuntimeException("Principal extraction failed"));

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/todaySavings").with(oidcLogin()))
                    .andExpect(status().is5xxServerError())
                    .andReturn();
        });
    }
}