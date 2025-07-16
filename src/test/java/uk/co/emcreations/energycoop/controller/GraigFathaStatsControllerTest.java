package uk.co.emcreations.energycoop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.service.GraigFathaStatsServiceImpl;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GraigFathaStatsController.class)
class GraigFathaStatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    GraigFathaStatsServiceImpl service;

    @Test
    @DisplayName("GET /energyYield returns 200 OK")
    void testGetEnergyYield() throws Exception {
        var vensysEnergyYield = VensysMeanData.builder().value(100).build();

        when(service.getMeanEnergyYield()).thenReturn(vensysEnergyYield);

        mockMvc.perform(MockMvcRequestBuilders.get("/energyYield").with(oidcLogin()))
                .andExpect(status().isOk());
    }
}