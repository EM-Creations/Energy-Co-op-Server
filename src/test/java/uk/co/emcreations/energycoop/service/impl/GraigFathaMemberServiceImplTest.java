package uk.co.emcreations.energycoop.service.impl;

import jakarta.persistence.EntityManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;
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
import uk.co.emcreations.energycoop.service.MemberOwnershipService;
import uk.co.emcreations.energycoop.service.SavingsRateService;
import uk.co.emcreations.energycoop.util.EntityHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraigFathaMemberServiceImplTest {
    @Mock EntityManager entityManager;
    @Mock GenerationStatEntryRepository generationStatEntryRepository;
    @Mock PerformanceStatEntryRepository performanceStatEntryRepository;
    @Mock GraigFathaStatsService graigFathaStatsService;
    @Mock SavingsRateService savingsRateService;
    @Mock MemberOwnershipService memberOwnershipService;
    private MockedStatic<EntityHelper> entityHelperMock;

    @InjectMocks GraigFathaMemberServiceImpl service;

    @BeforeEach
    void setUp() {
        service.totalCapacity = 100.0;
        entityHelperMock = mockStatic(EntityHelper.class);
    }

    @AfterEach
    public void tearDown() {
        // Closing the entityHelperMock after each test
        entityHelperMock.close();
    }

    @Nested
    @DisplayName("getTodaySavings Tests")
    class GetTodaySavings {
        @Test
        @DisplayName("getTodaySavings returns correct savings when repository has data")
        void testGetTodaySavings_withRepoData() {
            GenerationStatEntry entry = mock(GenerationStatEntry.class);
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(entry.getKWhGenerated()).thenReturn(50.0);
            when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
            var wattageOwnership = 10.0;
            EnergySaving saving = service.getTodaySavings(wattageOwnership);
            assertEquals(5.0, saving.amount());
            assertEquals("GBP", saving.currency());
        }

        @Test
        @DisplayName("getTodaySavings persists and returns correct savings when repository has no data")
        void testGetTodaySavings_withNoRepoData() {
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(null);
            VensysMeanData meanData = VensysMeanData.builder().value(60.0).build();
            when(graigFathaStatsService.getMeanEnergyYield()).thenReturn(meanData);
            GenerationStatEntry statEntry = mock(GenerationStatEntry.class);
            when(statEntry.getKWhGenerated()).thenReturn(60.0);
            entityHelperMock.when(() -> EntityHelper.createGenerationStatEntry(any(), any())).thenReturn(statEntry);
            var wattageOwnership = 20.0;
            EnergySaving saving = service.getTodaySavings(wattageOwnership);
            assertEquals(12.0, saving.amount());
            verify(entityManager).persist(statEntry);
        }

        @Test
        @DisplayName("getTodaySavings returns zero for zero ownership")
        void testGetTodaySavings_zeroOwnership() {
            GenerationStatEntry entry = mock(GenerationStatEntry.class);
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(entry.getKWhGenerated()).thenReturn(100.0);
            when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
            var wattageOwnership = 0.0;
            EnergySaving saving = service.getTodaySavings(wattageOwnership);
            assertEquals(0.0, saving.amount());
        }

        @Test
        @DisplayName("getTodaySavings handles negative ownership")
        void testGetTodaySavings_negativeOwnership() {
            GenerationStatEntry entry = mock(GenerationStatEntry.class);
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(entry.getKWhGenerated()).thenReturn(100.0);
            when(generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
            double wattageOwnership = -10.0;
            EnergySaving saving = service.getTodaySavings(wattageOwnership);
            assertEquals(-10.0, saving.amount());
        }
    }

    @Nested
    @DisplayName("getSavings Tests")
    class GetSavings {
        private static final String userId = "testUser";

        @Test
        @DisplayName("getSavings returns correct savings for multiple days with repo data")
        void testGetSavings_withRepoData() {
            PerformanceStatEntry entry = mock(PerformanceStatEntry.class);
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(entry.getKWhGenerated()).thenReturn(100.0);
            when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(entry);
            when(memberOwnershipService.getMemberOwnershipForSite(any(), any(), anyString(), anyDouble()))
                    .thenAnswer(invocation -> invocation.getArgument(3));

            var wattageOwnership = 10.0;
            var from = LocalDate.now().minusDays(2);
            var to = LocalDate.now();
            Set<EnergySaving> savings = service.getSavings(from, to, wattageOwnership, userId);
            assertEquals(3, savings.size());
            for (EnergySaving saving : savings) {
                assertEquals(10.0, saving.amount());
                assertEquals("GBP", saving.currency());
            }
        }

        @Test
        @DisplayName("getSavings persists and returns correct savings when repo has no data")
        void testGetSavings_withNoRepoData() {
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any())).thenReturn(null);
            when(memberOwnershipService.getMemberOwnershipForSite(any(), any(), anyString(), anyDouble()))
                    .thenAnswer(invocation -> invocation.getArgument(3));

            var perfData = VensysPerformanceData.builder().powerAvg(200.0).build();
            when(graigFathaStatsService.getPerformance(any(), any())).thenReturn(perfData);
            PerformanceStatEntry statEntry = mock(PerformanceStatEntry.class);
            when(statEntry.getKWhGenerated()).thenReturn(200.0);
            entityHelperMock.when(() -> EntityHelper.createPerformanceStatEntry(any(), any())).thenReturn(statEntry);
            var wattageOwnership = 50.0;
            var from = LocalDate.now();
            var to = LocalDate.now();
            Set<EnergySaving> savings = service.getSavings(from, to, wattageOwnership, userId);
            assertEquals(1, savings.size());
            EnergySaving saving = savings.iterator().next();
            assertEquals(100.0, saving.amount());
            verify(entityManager).persist(statEntry);
        }
    }

    @Nested
    @DisplayName("generateTaxDocument Tests")
    class GenerateTaxDocument {
        private static final String userId = "testUser";
        private static final LocalDate from = LocalDate.of(2025, 1, 1);
        private static final LocalDate to = LocalDate.of(2025, 12, 31);
        private static final double wattageOwnership = 10.0;

        @Test
        @DisplayName("generateTaxDocument returns PDF with properly formatted numbers")
        void testGenerateTaxDocument_numberFormatting() throws IOException {
            // Given
            double dailyGeneration = 1234.567; // Should format as 1,234.57
            double effectiveOwnership = wattageOwnership * 100; // Simulated ownership percentage

            PerformanceStatEntry entry = mock(PerformanceStatEntry.class);
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(entry.getKWhGenerated()).thenReturn(dailyGeneration);
            when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any()))
                    .thenReturn(entry);
            when(memberOwnershipService.getMemberOwnershipForSite(any(), any(), eq(userId), eq(wattageOwnership)))
                    .thenReturn(effectiveOwnership);

            // When
            byte[] result = service.generateTaxDocument(from, to, wattageOwnership, userId);

            // Then
            assertNotNull(result);
            assertTrue(result.length > 0);

            // Extract and verify formatted content
            String content = extractPdfContent(result);

            // Annual savings should be dailyGeneration * 365 * effectiveOwnership
            double expectedAnnualSavings = 4_506_169.55;
            String formattedExpectedSavings = String.format("%,.2f", expectedAnnualSavings);
            assertTrue(content.contains(formattedExpectedSavings),
                "Expected to find formatted savings amount: " + formattedExpectedSavings);
        }

        @Test
        @DisplayName("generateTaxDocument handles large monetary amounts")
        void testGenerateTaxDocument_largeAmounts() throws IOException {
            // Given
            double dailyGeneration = 12345.678; // Will accumulate to over 1M for the year
            double effectiveOwnership = wattageOwnership * 150; // Higher ownership percentage

            PerformanceStatEntry entry = mock(PerformanceStatEntry.class);
            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(2.0); // Higher rate to get larger numbers
            when(entry.getKWhGenerated()).thenReturn(dailyGeneration);
            when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any()))
                    .thenReturn(entry);
            when(memberOwnershipService.getMemberOwnershipForSite(any(), any(), eq(userId), eq(wattageOwnership)))
                    .thenReturn(effectiveOwnership);

            // When
            byte[] result = service.generateTaxDocument(from, to, wattageOwnership, userId);

            // Then
            String content = extractPdfContent(result);

            // Verify large monetary amounts are properly formatted
            double expectedAnnualSavings = 135_185_174.10;
            String formattedExpectedSavings = String.format("%,.2f", expectedAnnualSavings);
            assertTrue(content.contains(formattedExpectedSavings),
                "Expected to find formatted large amount: " + formattedExpectedSavings);
        }

        @Test
        @DisplayName("generateTaxDocument handles missing performance data")
        void testGenerateTaxDocument_withMissingData() throws IOException {
            // Given
            double effectiveOwnership = wattageOwnership * 50; // Base ownership percentage

            when(savingsRateService.getSavingsRateForDate(any(), any())).thenReturn(1.0);
            when(performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(any(), any(), any()))
                    .thenReturn(null);
            var perfData = VensysPerformanceData.builder().powerAvg(200.0).build();
            when(graigFathaStatsService.getPerformance(any(), any())).thenReturn(perfData);
            PerformanceStatEntry statEntry = mock(PerformanceStatEntry.class);
            when(statEntry.getKWhGenerated()).thenReturn(200.0);
            entityHelperMock.when(() -> EntityHelper.createPerformanceStatEntry(any(), any())).thenReturn(statEntry);
            when(memberOwnershipService.getMemberOwnershipForSite(any(), any(), eq(userId), eq(wattageOwnership)))
                    .thenReturn(effectiveOwnership);

            // When
            byte[] result = service.generateTaxDocument(from, to, wattageOwnership, userId);

            // Then
            assertNotNull(result);
            assertTrue(result.length > 0);
            verify(entityManager, atLeastOnce()).persist(any(PerformanceStatEntry.class));
        }

        @Test
        @DisplayName("generateTaxDocument handles null dates")
        void testGenerateTaxDocument_nullDates() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () ->
                    service.generateTaxDocument(null, to, wattageOwnership, userId)
                ),
                () -> assertThrows(NullPointerException.class, () ->
                    service.generateTaxDocument(from, null, wattageOwnership, userId)
                )
            );
        }

        @Test
        @DisplayName("generateTaxDocument handles null userId")
        void testGenerateTaxDocument_nullUserId() {
            assertThrows(NullPointerException.class, () ->
                service.generateTaxDocument(from, to, wattageOwnership, null)
            );
        }
    }

    // Helper method to extract text content from PDF bytes
    private String extractPdfContent(byte[] pdfBytes) throws IOException {
        try (var document = Loader.loadPDF(pdfBytes)) {
            var stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
