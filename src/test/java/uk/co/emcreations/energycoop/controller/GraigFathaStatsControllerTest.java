package uk.co.emcreations.energycoop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.dto.VensysDataResponse;
import uk.co.emcreations.energycoop.dto.VensysEnergyYield;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GraigFathaStatsController.class)
class GraigFathaStatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    VensysGraigFathaClient client;

    @Test
    @DisplayName("GET /energyYield returns 200 OK")
    void testGetEnergyYield() throws Exception {
        var vensysEnergyYield = VensysEnergyYield.builder().value(100).build();
        var response = VensysDataResponse.builder().data(vensysEnergyYield).build();

        when(client.getEnergyYield()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/energyYield"))
                .andExpect(status().isOk());
    }
}