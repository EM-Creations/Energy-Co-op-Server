package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("Returns repository rate if present for Graig Fatha")
    void returnsRepositoryRateIfPresent() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        SavingsRate rate = mock(SavingsRate.class);
        when(rate.getRatePerW()).thenReturn(9.99);
        when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.GRAIG_FATHA, date))
                .thenReturn(Optional.of(rate));
        double result = savingsRateService.getSavingsRateForDate(Site.GRAIG_FATHA, date);
        assertEquals(9.99, result);
    }

    @Test
    @DisplayName("Returns default for Graig Fatha if not present")
    void returnsDefaultForGraigFathaIfNotPresent() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.GRAIG_FATHA, date))
                .thenReturn(Optional.empty());
        double result = savingsRateService.getSavingsRateForDate(Site.GRAIG_FATHA, date);
        assertEquals(1.23, result);
    }

    @Test
    @DisplayName("Returns default for Kirk Hill if not present")
    void returnsDefaultForKirkHillIfNotPresent() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.KIRK_HILL, date))
                .thenReturn(Optional.empty());
        double result = savingsRateService.getSavingsRateForDate(Site.KIRK_HILL, date);
        assertEquals(2.34, result);
    }

    @Test
    @DisplayName("Returns default for Derril Water if not present")
    void returnsDefaultForDerrilWaterIfNotPresent() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(savingsRateRepository.findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Site.DERRIL_WATER, date))
                .thenReturn(Optional.empty());
        double result = savingsRateService.getSavingsRateForDate(Site.DERRIL_WATER, date);
        assertEquals(3.45, result);
    }
}
