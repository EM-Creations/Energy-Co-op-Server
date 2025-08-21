package uk.co.emcreations.energycoop.service.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.emcreations.energycoop.dto.EnergySaving;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.entity.GenerationStatEntryRepository;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntry;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntryRepository;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;
import uk.co.emcreations.energycoop.service.SavingsRateService;
import uk.co.emcreations.energycoop.util.EntityHelper;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraigFathaMemberServiceImplTest {
    @Mock EntityManager entityManager;
    @Mock GenerationStatEntryRepository generationStatEntryRepository;
    @Mock PerformanceStatEntryRepository performanceStatEntryRepository;
    @Mock GraigFathaStatsService graigFathaStatsService;
    @Mock SavingsRateService savingsRateService;
    private MockedStatic<EntityHelper> entityHelperMock;

    @InjectMocks GraigFathaMemberServiceImpl service;

    @BeforeEach
    void setUp() {
        service.totalCapacity = 100.0;
        entityHelperMock = mockStatic(EntityHelper.class);
        when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
    }

    @AfterEach
    public void tearDown() {
        // Closing the entityHelperMock after each test
        entityHelperMock.close();
    }

    @Test
    @DisplayName("getTodaySavings returns correct savings when repository has data")
    void testGetTodaySavings_withRepoData() {
        GenerationStatEntry entry = mock(GenerationStatEntry.class);
        when(entry.getWattsGenerated()).thenReturn(50.0);
        when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
        double wattageOwnership = 10.0;
        EnergySaving saving = service.getTodaySavings(wattageOwnership);
        assertEquals(5.0, saving.amount());
        assertEquals("GBP", saving.currency());
    }

    @Test
    @DisplayName("getTodaySavings persists and returns correct savings when repository has no data")
    void testGetTodaySavings_withNoRepoData() {
        when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(null);
        VensysMeanData meanData = VensysMeanData.builder().value(60.0).build();
        when(graigFathaStatsService.getMeanEnergyYield()).thenReturn(meanData);
        GenerationStatEntry statEntry = mock(GenerationStatEntry.class);
        when(statEntry.getWattsGenerated()).thenReturn(60.0);
        entityHelperMock.when(() -> EntityHelper.createGenerationStatEntry(any(), any())).thenReturn(statEntry);
        double wattageOwnership = 20.0;
        EnergySaving saving = service.getTodaySavings(wattageOwnership);
        assertEquals(12.0, saving.amount());
        verify(entityManager).persist(statEntry);
    }

    @Test
    @DisplayName("getSavings returns correct savings for multiple days with repo data")
    void testGetSavings_withRepoData() {
        PerformanceStatEntry entry = mock(PerformanceStatEntry.class);
        when(entry.getWattsGenerated()).thenReturn(100.0);
        when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
        double wattageOwnership = 10.0;
        LocalDate from = LocalDate.now().minusDays(2);
        LocalDate to = LocalDate.now();
        Set<EnergySaving> savings = service.getSavings(from, to, wattageOwnership);
        assertEquals(3, savings.size());
        for (EnergySaving saving : savings) {
            assertEquals(10.0, saving.amount());
            assertEquals("GBP", saving.currency());
        }
    }

    @Test
    @DisplayName("getSavings persists and returns correct savings when repo has no data")
    void testGetSavings_withNoRepoData() {
        when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(null);
        VensysPerformanceData perfData = VensysPerformanceData.builder().powerAvg(200.0).build();
        when(graigFathaStatsService.getPerformance(any(), any())).thenReturn(perfData);
        PerformanceStatEntry statEntry = mock(PerformanceStatEntry.class);
        when(statEntry.getWattsGenerated()).thenReturn(200.0);
        entityHelperMock.when(() -> EntityHelper.createPerformanceStatEntry(any(), any())).thenReturn(statEntry);
        double wattageOwnership = 50.0;
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now();
        Set<EnergySaving> savings = service.getSavings(from, to, wattageOwnership);
        assertEquals(1, savings.size());
        EnergySaving saving = savings.iterator().next();
        assertEquals(100.0, saving.amount());
        verify(entityManager).persist(statEntry);
    }

    @Test
    @DisplayName("getTodaySavings returns zero for zero ownership")
    void testGetTodaySavings_zeroOwnership() {
        GenerationStatEntry entry = mock(GenerationStatEntry.class);
        when(entry.getWattsGenerated()).thenReturn(100.0);
        when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
        double wattageOwnership = 0.0;
        EnergySaving saving = service.getTodaySavings(wattageOwnership);
        assertEquals(0.0, saving.amount());
    }

    @Test
    @DisplayName("getTodaySavings handles negative ownership")
    void testGetTodaySavings_negativeOwnership() {
        GenerationStatEntry entry = mock(GenerationStatEntry.class);
        when(entry.getWattsGenerated()).thenReturn(100.0);
        when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
        double wattageOwnership = -10.0;
        EnergySaving saving = service.getTodaySavings(wattageOwnership);
        assertEquals(-10.0, saving.amount());
    }
}
