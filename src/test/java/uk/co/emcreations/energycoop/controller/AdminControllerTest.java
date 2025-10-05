package uk.co.emcreations.energycoop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.entity.SavingsRate;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.impl.SavingsRateServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "/api/v1/admin";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SavingsRateServiceImpl savingsRateService;

    @Nested
    @DisplayName("setSavingsRate tests")
    class SetSavingsRateTests {
        private final LocalDate TEST_DATE = LocalDate.of(2024, 1, 1);
        private final String TEST_DATE_STR = "2024-01-01";
        private final double TEST_RATE = 5.67;

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate returns 200 OK for Graig Fatha")
        void testSetSavingsRateGraigFatha() throws Exception {
            var expectedRate = SavingsRate.builder()
                    .site(Site.GRAIG_FATHA)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(TEST_RATE)
                    .build();

            when(savingsRateService.setSavingsRateForDate(eq(Site.GRAIG_FATHA), eq(TEST_DATE), eq(TEST_RATE)))
                    .thenReturn(expectedRate);

            var requestJson = """
                    {
                        "site": "GRAIG_FATHA",
                        "effectiveDate": "%s",
                        "ratePerKWH": %s
                    }""".formatted(TEST_DATE_STR, TEST_RATE);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            assertTrue(json.contains(String.valueOf(expectedRate.getRatePerKWH())));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate returns 200 OK for Kirk Hill")
        void testSetSavingsRateKirkHill() throws Exception {
            var expectedRate = SavingsRate.builder()
                    .site(Site.KIRK_HILL)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(TEST_RATE)
                    .build();

            when(savingsRateService.setSavingsRateForDate(eq(Site.KIRK_HILL), eq(TEST_DATE), eq(TEST_RATE)))
                    .thenReturn(expectedRate);

            var requestJson = """
                    {
                        "site": "KIRK_HILL",
                        "effectiveDate": "%s",
                        "ratePerKWH": %s
                    }""".formatted(TEST_DATE_STR, TEST_RATE);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            assertTrue(json.contains(String.valueOf(expectedRate.getRatePerKWH())));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate returns 200 OK for Derril Water")
        void testSetSavingsRateDerrilWater() throws Exception {
            var expectedRate = SavingsRate.builder()
                    .site(Site.DERRIL_WATER)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(TEST_RATE)
                    .build();

            when(savingsRateService.setSavingsRateForDate(eq(Site.DERRIL_WATER), eq(TEST_DATE), eq(TEST_RATE)))
                    .thenReturn(expectedRate);

            var requestJson = """
                    {
                        "site": "DERRIL_WATER",
                        "effectiveDate": "%s",
                        "ratePerKWH": %s
                    }""".formatted(TEST_DATE_STR, TEST_RATE);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            assertTrue(json.contains(String.valueOf(expectedRate.getRatePerKWH())));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate returns 400 BAD REQUEST for invalid site")
        void testSetSavingsRateInvalidSite() throws Exception {
            var invalidJson = """
                    {
                        "site": "INVALID_SITE",
                        "effectiveDate": "2024-01-01",
                        "ratePerKWH": 5.67
                    }""";

            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(invalidJson)
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate returns 400 BAD REQUEST for invalid date format")
        void testSetSavingsRateInvalidDate() throws Exception {
            var invalidJson = """
                    {
                        "site": "GRAIG_FATHA",
                        "effectiveDate": "invalid-date",
                        "ratePerKWH": 5.67
                    }""";

            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(invalidJson)
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate returns 400 BAD REQUEST for invalid rate")
        void testSetSavingsRateInvalidRate() throws Exception {
            var invalidJson = """
                    {
                        "site": "GRAIG_FATHA",
                        "effectiveDate": "2024-01-01",
                        "ratePerKWH": "invalid-rate"
                    }""";

            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(invalidJson)
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /savings-rate returns 403 FORBIDDEN without login")
        void testSetSavingsRateUnauthorized() throws Exception {
            var requestJson = """
                    {
                        "site": "GRAIG_FATHA",
                        "effectiveDate": "%s",
                        "ratePerKWH": %s
                    }""".formatted(TEST_DATE_STR, TEST_RATE);

            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate")
                    .contentType(APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isForbidden());
        }
    }
}
