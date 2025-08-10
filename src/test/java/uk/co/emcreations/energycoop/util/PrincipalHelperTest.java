package uk.co.emcreations.energycoop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.co.emcreations.energycoop.model.Site;

import java.security.Principal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrincipalHelperTest {

    @Test
    @DisplayName("Given valid ownerships, when extractOwnershipsFromPrincipal is called, then it returns correct ownerships")
    void extractOwnershipsFromPrincipal_validOwnerships() {
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Long> ownerships = new HashMap<>();
        for (Site site : Site.values()) {
            ownerships.put(site.getOwnershipKey(), 42L);
        }
        attributes.put("ownerships", ownerships);

        JwtAuthenticationToken token = Mockito.mock(JwtAuthenticationToken.class);
        Mockito.when(token.getTokenAttributes()).thenReturn(attributes);

        EnumMap<Site, Double> result = PrincipalHelper.extractOwnershipsFromPrincipal(token);

        for (Site site : Site.values()) {
            assertEquals(42.0, result.get(site));
        }
    }

    @Test
    @DisplayName("Given no ownerships, when extractOwnershipsFromPrincipal is called, then it returns zero for all sites")
    void extractOwnershipsFromPrincipal_missingOwnerships() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("ownerships", new HashMap<String, Long>());

        JwtAuthenticationToken token = Mockito.mock(JwtAuthenticationToken.class);
        Mockito.when(token.getTokenAttributes()).thenReturn(attributes);

        EnumMap<Site, Double> result = PrincipalHelper.extractOwnershipsFromPrincipal(token);

        for (Site site : Site.values()) {
            assertEquals(0.0, result.get(site));
        }
    }

    @Test
    @DisplayName("Given an invalid principal, when extractOwnershipsFromPrincipal is called, then it throws IllegalArgumentException")
    void extractOwnershipsFromPrincipal_invalidPrincipalType() {
        Principal principal = Mockito.mock(Principal.class);
        assertThrows(IllegalArgumentException.class, () -> PrincipalHelper.extractOwnershipsFromPrincipal(principal));
    }

    @Test
    @DisplayName("Given non number ownerships, when extractOwnershipsFromPrincipal is called, then it returns zero for all sites")
    void extractOwnershipsFromPrincipal_nonNumberOwnerships() {
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> ownerships = new HashMap<>();
        for (Site site : Site.values()) {
            ownerships.put(site.getOwnershipKey(), "notANumber");
        }
        attributes.put("ownerships", ownerships);

        JwtAuthenticationToken token = Mockito.mock(JwtAuthenticationToken.class);
        Mockito.when(token.getTokenAttributes()).thenReturn(attributes);

        EnumMap<Site, Double> result = PrincipalHelper.extractOwnershipsFromPrincipal(token);

        for (Site site : Site.values()) {
            assertEquals(0.0, result.get(site));
        }
    }
}