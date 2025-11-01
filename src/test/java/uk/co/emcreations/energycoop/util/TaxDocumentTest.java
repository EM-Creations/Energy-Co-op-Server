package uk.co.emcreations.energycoop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.emcreations.energycoop.model.Site;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaxDocumentTest {

    @Test
    @DisplayName("generateTaxDocument creates PDF bytes")
    void generateTaxDocument_success() throws IOException {
        // Given
        Site site = Site.GRAIG_FATHA;
        String userId = "test-user";
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);
        double totalSavingsAmount = 123.45;

        // When
        byte[] pdfBytes = TaxDocument.generateTaxDocument(site, userId, from, to, totalSavingsAmount);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("generateTaxDocument handles formatted amounts")
    void generateTaxDocument_formatsDecimalPlaces() throws IOException {
        // Given
        double totalSavingsAmount = 123.4567;

        // When
        byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                LocalDate.now(), LocalDate.now(), totalSavingsAmount);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("generateTaxDocument handles zero savings amount")
    void generateTaxDocument_handlesZeroAmount() throws IOException {
        // When
        byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                LocalDate.now(), LocalDate.now(), 0.0);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("generateTaxDocument handles large savings amounts")
    void generateTaxDocument_handlesLargeAmount() throws IOException {
        // Given
        double largeAmount = 1_000_000.99;

        // When
        byte[] pdfBytes = TaxDocument.generateTaxDocument(Site.GRAIG_FATHA, "test-user",
                LocalDate.now(), LocalDate.now(), largeAmount);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}
