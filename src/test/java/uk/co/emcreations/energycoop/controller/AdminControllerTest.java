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
        private final double TEST_RATE = 5.67;

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 200 OK for Graig Fatha")
        void testSetSavingsRateGraigFatha() throws Exception {
            var expectedRate = SavingsRate.builder()
                    .site(Site.GRAIG_FATHA)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(TEST_RATE)
                    .build();

            when(savingsRateService.setSavingsRateForDate(eq(Site.GRAIG_FATHA), eq(TEST_DATE), eq(TEST_RATE)))
                    .thenReturn(expectedRate);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/GRAIG_FATHA/2024-01-01/5.67")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();

            assertTrue(json.contains(String.valueOf(expectedRate.getRatePerKWH())));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 200 OK for Kirk Hill")
        void testSetSavingsRateKirkHill() throws Exception {
            var expectedRate = SavingsRate.builder()
                    .site(Site.KIRK_HILL)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(TEST_RATE)
                    .build();

            when(savingsRateService.setSavingsRateForDate(eq(Site.KIRK_HILL), eq(TEST_DATE), eq(TEST_RATE)))
                    .thenReturn(expectedRate);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/KIRK_HILL/2024-01-01/5.67")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();

            assertTrue(json.contains(String.valueOf(expectedRate.getRatePerKWH())));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 200 OK for Derril Water")
        void testSetSavingsRateDerrilWater() throws Exception {
            var expectedRate = SavingsRate.builder()
                    .site(Site.DERRIL_WATER)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(TEST_RATE)
                    .build();

            when(savingsRateService.setSavingsRateForDate(eq(Site.DERRIL_WATER), eq(TEST_DATE), eq(TEST_RATE)))
                    .thenReturn(expectedRate);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/DERRIL_WATER/2024-01-01/5.67")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();

            assertTrue(json.contains(String.valueOf(expectedRate.getRatePerKWH())));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 400 BAD REQUEST for invalid site")
        void testSetSavingsRateInvalidSite() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/INVALID_SITE/2024-01-01/5.67")
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 400 BAD REQUEST for invalid date format")
        void testSetSavingsRateInvalidDate() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/GRAIG_FATHA/invalid-date/5.67")
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 400 BAD REQUEST for invalid rate")
        void testSetSavingsRateInvalidRate() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/GRAIG_FATHA/2024-01-01/invalid-rate")
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /savings-rate/{site}/{date}/{rate} returns 403 FORBIDDEN without login")
        void testSetSavingsRateUnauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post(BASE_URL + "/savings-rate/GRAIG_FATHA/2024-01-01/5.67"))
                    .andExpect(status().isForbidden());
        }
    }
}
