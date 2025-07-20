package uk.co.emcreations.energycoop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.emcreations.energycoop.model.User;
import uk.co.emcreations.energycoop.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /currentUser returns 200 OK")
    void testGetCurrentUser() throws Exception {
        var expectedUser = User.builder()
                .userId("auth0|user1")
                .email("me@me.com")
                .imageURL("https://image.com/img.png")
                .name("User 1")
                .build();

        when(userService.getUserFromPrincipal(any(OidcUser.class))).thenReturn(expectedUser);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/currentUser").with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        User actualUser = objectMapper.readValue(json, User.class);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    @DisplayName("GET /currentUser/ownership returns 200 OK")
    void testGetCurrentUserOwnership() throws Exception {
        var expectedOwnership = "ownership details";

        when(userService.getOwnership(any(OidcUser.class))).thenReturn(expectedOwnership);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/currentUser/ownership").with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertEquals(expectedOwnership, json);
    }
}