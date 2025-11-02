package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.co.emcreations.energycoop.model.Site;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfoServiceImplTest {

    @InjectMocks
    private InfoServiceImpl infoService;

    @Nested
    @DisplayName("getSites() tests")
    class GetSites {
        @Test
        @DisplayName("Should return all available sites")
        void getSites_returnsAllSites() {
            // Given
            Site[] expectedSites = Site.values();

            // When
            Site[] actualSites = infoService.getSites();

            // Then
            assertNotNull(actualSites, "Sites array should not be null");
            assertArrayEquals(expectedSites, actualSites, "Should return all available sites");
        }

        @Test
        @DisplayName("Should return same array on multiple calls")
        void getSites_returnsSameArrayOnMultipleCalls() {
            // When
            Site[] firstCall = infoService.getSites();
            Site[] secondCall = infoService.getSites();

            // Then
            assertNotNull(firstCall, "First call sites array should not be null");
            assertNotNull(secondCall, "Second call sites array should not be null");
            assertArrayEquals(firstCall, secondCall, "Multiple calls should return the same sites");
        }
    }

    @Nested
    @DisplayName("getSitesWithUserOwnership() tests")
    class GetSitesWithUserOwnership {
        @Test
        @DisplayName("Should return sites with positive ownership values")
        void getSitesWithUserOwnership_returnsOwnedSites() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            Map<String, Long> ownerships = new HashMap<>();
            ownerships.put("gf-wattage", 1L);
            ownerships.put("kh-wattage", 0L);
            attributes.put("ownerships", ownerships);

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getTokenAttributes()).thenReturn(attributes);

            // When
            Site[] actualSites = infoService.getSitesWithUserOwnership(token);

            // Then
            assertNotNull(actualSites, "Sites array should not be null");
            assertArrayEquals(new Site[]{Site.GRAIG_FATHA}, actualSites, "Should only return sites with ownership > 0");
        }

        @Test
        @DisplayName("Should return empty array when user has no ownership")
        void getSitesWithUserOwnership_returnsEmptyArrayWhenNoOwnership() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            Map<String, Long> ownerships = new HashMap<>();
            ownerships.put("gf-wattage", 0L);
            ownerships.put("kh-wattage", 0L);
            attributes.put("ownerships", ownerships);

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getTokenAttributes()).thenReturn(attributes);

            // When
            Site[] actualSites = infoService.getSitesWithUserOwnership(token);

            // Then
            assertNotNull(actualSites, "Sites array should not be null");
            assertEquals(0, actualSites.length, "Should return empty array when no sites have positive ownership");
        }

        @Test
        @DisplayName("Should handle multiple sites with ownership")
        void getSitesWithUserOwnership_handlesMultipleSites() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            Map<String, Long> ownerships = new HashMap<>();
            ownerships.put("gf-wattage", 1L);
            ownerships.put("kh-wattage", 1L);
            attributes.put("ownerships", ownerships);

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getTokenAttributes()).thenReturn(attributes);

            // When
            Site[] actualSites = infoService.getSitesWithUserOwnership(token);

            // Then
            assertNotNull(actualSites, "Sites array should not be null");
            assertEquals(2, actualSites.length, "Should return all sites with positive ownership");
            assertTrue(Arrays.asList(actualSites).containsAll(Arrays.asList(Site.GRAIG_FATHA, Site.KIRK_HILL)),
                "Should contain both sites with positive ownership");
        }
    }
}
