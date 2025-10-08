package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.emcreations.energycoop.entity.SavingsRate;
import uk.co.emcreations.energycoop.entity.SavingsRateRepository;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsRateServiceTest {
    @Mock
    SavingsRateRepository savingsRateRepository;

    @InjectMocks
    SavingsRateServiceImpl savingsRateService;

    @BeforeEach
    void setUp() throws Exception {
        // Set default rates via reflection (since @Value is not processed in unit tests)
        var gfField = SavingsRateServiceImpl.class.getDeclaredField("defaultSavingsRatePerWattGraigFatha");
        gfField.setAccessible(true);
        gfField.set(savingsRateService, 1.23);
        var khField = SavingsRateServiceImpl.class.getDeclaredField("defaultSavingsRatePerWattKirkHill");
        khField.setAccessible(true);
        khField.set(savingsRateService, 2.34);
        var dwField = SavingsRateServiceImpl.class.getDeclaredField("defaultSavingsRatePerWattDerrilWater");
        dwField.setAccessible(true);
        dwField.set(savingsRateService, 3.45);
    }

    @Nested
    @DisplayName("getSavingsRateForDate tests")
    class GetSavingsRateForDateTests {
        private final LocalDate TEST_DATE = LocalDate.of(2024, 1, 1);

        @Test
        @DisplayName("Returns repository rate if present for Graig Fatha")
        void returnsRepositoryRateIfPresent() {
            SavingsRate rate = mock(SavingsRate.class);
            when(rate.getRatePerKWH()).thenReturn(9.99);
            when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.GRAIG_FATHA, TEST_DATE))
                    .thenReturn(Optional.of(rate));

            double result = savingsRateService.getSavingsRateForDate(Site.GRAIG_FATHA, TEST_DATE);
            assertEquals(9.99, result);
        }

        @Test
        @DisplayName("Returns default for Graig Fatha if not present")
        void returnsDefaultForGraigFathaIfNotPresent() {
            when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.GRAIG_FATHA, TEST_DATE))
                    .thenReturn(Optional.empty());

            double result = savingsRateService.getSavingsRateForDate(Site.GRAIG_FATHA, TEST_DATE);
            assertEquals(1.23, result);
        }

        @Test
        @DisplayName("Returns default for Kirk Hill if not present")
        void returnsDefaultForKirkHillIfNotPresent() {
            when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.KIRK_HILL, TEST_DATE))
                    .thenReturn(Optional.empty());

            double result = savingsRateService.getSavingsRateForDate(Site.KIRK_HILL, TEST_DATE);
            assertEquals(2.34, result);
        }

        @Test
        @DisplayName("Returns default for Derril Water if not present")
        void returnsDefaultForDerrilWaterIfNotPresent() {
            when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.DERRIL_WATER, TEST_DATE))
                    .thenReturn(Optional.empty());

            double result = savingsRateService.getSavingsRateForDate(Site.DERRIL_WATER, TEST_DATE);
            assertEquals(3.45, result);
        }

        @Test
        @DisplayName("Uses most recent rate before given date")
        void usesMostRecentRateBeforeGivenDate() {
            var testDate = LocalDate.of(2024, 1, 15);
            SavingsRate rate = mock(SavingsRate.class);
            when(rate.getRatePerKWH()).thenReturn(8.88);
            when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.GRAIG_FATHA, testDate))
                    .thenReturn(Optional.of(rate));

            double result = savingsRateService.getSavingsRateForDate(Site.GRAIG_FATHA, testDate);
            assertEquals(8.88, result);
            verify(savingsRateRepository).findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.GRAIG_FATHA, testDate);
        }
    }

    @Nested
    @DisplayName("setSavingsRateForDate tests")
    class SetSavingsRateForDateTests {
        private final LocalDate TEST_DATE = LocalDate.of(2024, 1, 1);
        private final double TEST_RATE = 5.67;
        private final String TEST_USER_ID = "test-user";

        @Test
        @DisplayName("Successfully saves new rate for Graig Fatha")
        void successfullySavesNewRateForGraigFatha() {
            when(savingsRateRepository.findBySiteAndEffectiveDate(Site.GRAIG_FATHA, TEST_DATE))
                    .thenReturn(Optional.empty());

            when(savingsRateRepository.save(any(SavingsRate.class))).thenAnswer(invocation -> {
                SavingsRate savedRate = invocation.getArgument(0);
                assertEquals(Site.GRAIG_FATHA, savedRate.getSite());
                assertEquals(TEST_DATE, savedRate.getEffectiveDate());
                assertEquals(TEST_RATE, savedRate.getRatePerKWH());
                assertEquals(TEST_USER_ID, savedRate.getLastUpdatedByUser());
                return savedRate;
            });

            savingsRateService.setSavingsRateForDate(Site.GRAIG_FATHA, TEST_DATE, TEST_RATE, TEST_USER_ID);
            verify(savingsRateRepository).save(any(SavingsRate.class));
        }

        @Test
        @DisplayName("Successfully saves new rate for Kirk Hill")
        void successfullySavesNewRateForKirkHill() {
            when(savingsRateRepository.findBySiteAndEffectiveDate(Site.KIRK_HILL, TEST_DATE))
                    .thenReturn(Optional.empty());

            when(savingsRateRepository.save(any(SavingsRate.class))).thenAnswer(invocation -> {
                SavingsRate savedRate = invocation.getArgument(0);
                assertEquals(Site.KIRK_HILL, savedRate.getSite());
                assertEquals(TEST_DATE, savedRate.getEffectiveDate());
                assertEquals(TEST_RATE, savedRate.getRatePerKWH());
                return savedRate;
            });

            savingsRateService.setSavingsRateForDate(Site.KIRK_HILL, TEST_DATE, TEST_RATE, TEST_USER_ID);
            verify(savingsRateRepository).save(any(SavingsRate.class));
        }

        @Test
        @DisplayName("Successfully saves new rate for Derril Water")
        void successfullySavesNewRateForDerrilWater() {
            when(savingsRateRepository.findBySiteAndEffectiveDate(Site.DERRIL_WATER, TEST_DATE))
                    .thenReturn(Optional.empty());

            when(savingsRateRepository.save(any(SavingsRate.class))).thenAnswer(invocation -> {
                SavingsRate savedRate = invocation.getArgument(0);
                assertEquals(Site.DERRIL_WATER, savedRate.getSite());
                assertEquals(TEST_DATE, savedRate.getEffectiveDate());
                assertEquals(TEST_RATE, savedRate.getRatePerKWH());
                return savedRate;
            });

            savingsRateService.setSavingsRateForDate(Site.DERRIL_WATER, TEST_DATE, TEST_RATE, TEST_USER_ID);
            verify(savingsRateRepository).save(any(SavingsRate.class));
        }

        @Test
        @DisplayName("Updates existing rate when one exists for the date")
        void updatesExistingRateWhenOneExistsForTheDate() {
            var existingRate = SavingsRate.builder()
                    .site(Site.GRAIG_FATHA)
                    .effectiveDate(TEST_DATE)
                    .ratePerKWH(1.0)
                    .lastUpdatedByUser("old-user")
                    .build();

            when(savingsRateRepository.findBySiteAndEffectiveDate(Site.GRAIG_FATHA, TEST_DATE))
                    .thenReturn(Optional.of(existingRate));

            when(savingsRateRepository.save(any(SavingsRate.class))).thenAnswer(invocation -> {
                SavingsRate savedRate = invocation.getArgument(0);
                assertEquals(Site.GRAIG_FATHA, savedRate.getSite());
                assertEquals(TEST_DATE, savedRate.getEffectiveDate());
                assertEquals(TEST_RATE, savedRate.getRatePerKWH());
                assertEquals(TEST_USER_ID, savedRate.getLastUpdatedByUser());
                assertNotNull(savedRate.getCreatedAt());
                return savedRate;
            });

            SavingsRate result = savingsRateService.setSavingsRateForDate(Site.GRAIG_FATHA, TEST_DATE, TEST_RATE, TEST_USER_ID);

            verify(savingsRateRepository).save(any(SavingsRate.class));
            assertEquals(TEST_RATE, result.getRatePerKWH());
            assertEquals(TEST_USER_ID, result.getLastUpdatedByUser());
        }
    }
}
