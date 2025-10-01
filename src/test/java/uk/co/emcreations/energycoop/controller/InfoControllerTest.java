package uk.co.emcreations.energycoop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.InfoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InfoController.class)
class InfoControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String baseURL = "/api/v1/info";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InfoService infoService;

    @Nested
    @DisplayName("GET /sites endpoint tests")
    class GetSitesTests {
        @Test
        @DisplayName("Returns 200 OK with sites array")
        void getSites_returnsSites() throws Exception {
            when(infoService.getSites()).thenReturn(Site.values());

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/sites")
                            .with(oidcLogin()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            Site[] actualSites = objectMapper.readValue(json, Site[].class);
            assertArrayEquals(Site.values(), actualSites);
            verify(infoService, times(1)).getSites();
        }

        @Test
        @DisplayName("Returns 200 OK with empty array")
        void getSites_returnsEmptyArray() throws Exception {
            var sites = new Site[]{};
            when(infoService.getSites()).thenReturn(sites);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/sites")
                            .with(oidcLogin()))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            Site[] actualSites = objectMapper.readValue(json, Site[].class);
            assertNotNull(actualSites);
            assertEquals(0, actualSites.length);
            verify(infoService, times(1)).getSites();
        }

        @Test
        @DisplayName("Returns 200 OK with null response")
        void getSites_returnsNull() throws Exception {
            when(infoService.getSites()).thenReturn(null);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/sites")
                            .with(oidcLogin()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
            verify(infoService, times(1)).getSites();
        }

        @Test
        @DisplayName("Returns 302 redirection without login")
        void getSites_unauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/sites"))
                    .andExpect(status().is3xxRedirection());

            verify(infoService, never()).getSites();
        }
    }
}
