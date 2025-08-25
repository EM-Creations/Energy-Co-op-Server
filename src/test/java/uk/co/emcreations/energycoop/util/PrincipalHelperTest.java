package uk.co.emcreations.energycoop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.co.emcreations.energycoop.model.Site;

import java.security.Principal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrincipalHelperTest {

    @Nested
    @DisplayName("extractOwnershipsFromPrincipal tests")
    class ExtractOwnershipsFromPrincipalTests {
        @Test
        @DisplayName("Given valid ownerships, when extractOwnershipsFromPrincipal is called, then it returns correct ownerships")
        void extractOwnershipsFromPrincipal_validOwnerships() {
            Map<String, Object> attributes = new HashMap<>();
            Map<String, Long> ownerships = new HashMap<>();
            for (Site site : Site.values()) {
                ownerships.put(site.getOwnershipKey(), 42L);
            }
            attributes.put("ownerships", ownerships);

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getTokenAttributes()).thenReturn(attributes);

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

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getTokenAttributes()).thenReturn(attributes);

            EnumMap<Site, Double> result = PrincipalHelper.extractOwnershipsFromPrincipal(token);

            for (Site site : Site.values()) {
                assertEquals(0.0, result.get(site));
            }
        }

        @Test
        @DisplayName("Given an invalid principal, when extractOwnershipsFromPrincipal is called, then it throws IllegalArgumentException")
        void extractOwnershipsFromPrincipal_invalidPrincipalType() {
            Principal principal = mock(Principal.class);
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

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getTokenAttributes()).thenReturn(attributes);

            EnumMap<Site, Double> result = PrincipalHelper.extractOwnershipsFromPrincipal(token);

            for (Site site : Site.values()) {
                assertEquals(0.0, result.get(site));
            }
        }
    }

    @Nested
    @DisplayName("extractUserFromPrincipal tests")
    class ExtractUserFromPrincipalTests {
        @Test
        @DisplayName("Given principal with getName, extractUserFromPrincipal returns getName")
        void extractUserFromPrincipal_withName() {
            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getName()).thenReturn("user123");
            assertEquals("user123", PrincipalHelper.extractUserFromPrincipal(token));
        }

        @Test
        @DisplayName("Given principal with empty getName and sub attribute, extractUserFromPrincipal returns sub")
        void extractUserFromPrincipal_withSubAttribute() {
            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getName()).thenReturn("");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", "subUser");
            when(token.getTokenAttributes()).thenReturn(attributes);
            assertEquals("subUser", PrincipalHelper.extractUserFromPrincipal(token));
        }

        @Test
        @DisplayName("Given principal with empty getName and missing sub, extractUserFromPrincipal throws")
        void extractUserFromPrincipal_missingUserId() {
            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getName()).thenReturn("");
            Map<String, Object> attributes = new HashMap<>();
            when(token.getTokenAttributes()).thenReturn(attributes);
            assertThrows(IllegalArgumentException.class, () -> PrincipalHelper.extractUserFromPrincipal(token));
        }

        @Test
        @DisplayName("Given principal not JwtAuthenticationToken, extractUserFromPrincipal throws")
        void extractUserFromPrincipal_invalidPrincipalType() {
            Principal principal = mock(Principal.class);
            assertThrows(IllegalArgumentException.class, () -> PrincipalHelper.extractUserFromPrincipal(principal));
        }

        @Test
        @DisplayName("Given principal with non-string sub, extractUserFromPrincipal throws")
        void extractUserFromPrincipal_nonStringSub() {
            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getName()).thenReturn("");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", 12345);
            when(token.getTokenAttributes()).thenReturn(attributes);
            assertThrows(IllegalArgumentException.class, () -> PrincipalHelper.extractUserFromPrincipal(token));
        }
    }
}