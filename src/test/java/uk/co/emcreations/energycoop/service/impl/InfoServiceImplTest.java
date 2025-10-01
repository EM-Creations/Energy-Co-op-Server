package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.emcreations.energycoop.model.Site;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
