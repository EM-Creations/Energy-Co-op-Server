package uk.co.emcreations.energycoop.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.emcreations.energycoop.model.Site;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaxDocumentTest {

    @Nested
    @DisplayName("Basic document generation tests")
    class BasicDocumentTests {
        @Test
        @DisplayName("generateTaxDocument creates valid PDF bytes")
        void generateTaxDocument_success() throws IOException {
            // Given
            var site = Site.GRAIG_FATHA;
            var userId = "test-user";
            var from = LocalDate.of(2025, 1, 1);
            var to = LocalDate.of(2025, 12, 31);
            var totalSavingsAmount = 123.45;
            var currentShares = 100;
            var withdrawnShares = 0;
            var withdrawnCapital = 0;

            // When
            byte[] pdfBytes = TaxDocument.generateTaxDocument(site, userId, from, to,
                totalSavingsAmount, currentShares, withdrawnShares, withdrawnCapital);

            // Then
            assertNotNull(pdfBytes);
            assertTrue(pdfBytes.length > 0);

            // Verify PDF can be parsed
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                assertNotNull(document);
                assertEquals(1, document.getNumberOfPages());
            }
        }
    }

    @Nested
    @DisplayName("Number formatting tests")
    class NumberFormattingTests {
        @Test
        @DisplayName("generateTaxDocument formats currency with two decimal places")
        void generateTaxDocument_formatsCurrency() throws IOException {
            // Given
            var totalSavingsAmount = 1234.567;
            var withdrawnCapital = 567.891;

            // When
            byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                    LocalDate.now(), LocalDate.now(), totalSavingsAmount, 100, 0, withdrawnCapital);

            // Then
            String content = extractPdfContent(pdfBytes);
            assertTrue(content.contains("1,234.57")); // Total savings
            assertTrue(content.contains("567.89")); // Withdrawn capital
        }

        @Test
        @DisplayName("generateTaxDocument formats shares without decimals")
        void generateTaxDocument_formatsShares() throws IOException {
            // Given
            var currentShares = 12345;
            var withdrawnShares = 6789;

            // When
            byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                    LocalDate.now(), LocalDate.now(), 100.0, currentShares, withdrawnShares, 0.0);

            // Then
            String content = extractPdfContent(pdfBytes);
            assertTrue(content.contains("12,345")); // Current shares
            assertTrue(content.contains("6,789")); // Withdrawn shares
            assertTrue(content.contains("5,556")); // Carried shares (12345 - 6789)
        }

        @Test
        @DisplayName("generateTaxDocument handles zero values")
        void generateTaxDocument_handlesZeroValues() throws IOException {
            // When
            byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                    LocalDate.now(), LocalDate.now(), 0.0, 0.0, 0.0, 0.0);

            // Then
            String content = extractPdfContent(pdfBytes);
            assertTrue(content.contains("0.00")); // Total savings
            assertTrue(content.contains("0")); // Shares
        }

        @Test
        @DisplayName("generateTaxDocument handles large amounts with grouping")
        void generateTaxDocument_handlesLargeAmounts() throws IOException {
            // Given
            var largeAmount = 1_234_567.89;
            var largeShares = 9_876_543;

            // When
            byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                    LocalDate.now(), LocalDate.now(), largeAmount, largeShares, 0.0, 0.0);

            // Then
            String content = extractPdfContent(pdfBytes);
            assertTrue(content.contains("1,234,567.89")); // Large amount
            assertTrue(content.contains("9,876,543")); // Large shares
        }
    }

    @Nested
    @DisplayName("Content validation tests")
    class ContentValidationTests {
        @Test
        @DisplayName("generateTaxDocument includes all required sections")
        void generateTaxDocument_includesAllSections() throws IOException {
            // Given
            var site = Site.GRAIG_FATHA;
            var userId = "test-user";
            var from = LocalDate.of(2025, 1, 1);
            var to = LocalDate.of(2025, 12, 31);

            // When
            byte[] pdfBytes = TaxDocument.generateTaxDocument(site, userId, from, to,
                100.00, 1000, 200, 50.00);

            // Then
            String content = extractPdfContent(pdfBytes);

            // Verify headers
            assertTrue(content.contains("Energy Co-op (GRAIG_FATHA) Tax Document"));
            assertTrue(content.contains("Produced for user: test-user"));
            assertTrue(content.contains("Period: 2025-01-01 to 2025-12-31"));

            // Verify sections
            assertTrue(content.contains("Brought forward shares: 1,000"));
            assertTrue(content.contains("Shares withdrawn: 200"));
            assertTrue(content.contains("Carried forward shares: 800"));

            assertTrue(content.contains("Total savings: £100.00"));
            assertTrue(content.contains("Capital withdrawn: £50.00"));
            assertTrue(content.contains("Interest income: £50.00")); // 100 - 50

            // Verify disclaimer
            assertTrue(content.contains("This document is intended for informational purposes only"));
            assertTrue(content.contains("Please consult a tax professional"));
        }
    }

    // Helper method to extract text content from PDF bytes
    private String extractPdfContent(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
