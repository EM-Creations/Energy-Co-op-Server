package uk.co.emcreations.energycoop.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import uk.co.emcreations.energycoop.model.Site;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TaxDocument {
    public static byte[] generateTaxDocument(final Site site, final String userId, final LocalDate from,
                                             final LocalDate to, final double totalSavingsAmount, final double currentShares,
                                             final double withdrawnShares, final double withdrawnCapital) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            // Formatting helpers
            String formattedTotalSavingsAmount = formatCurrency(totalSavingsAmount);
            String formattedCurrentShares = formatInteger(currentShares);
            String formattedWithdrawnShares = formatInteger(withdrawnShares);
            String formattedWithdrawnCapital = formatCurrency(withdrawnCapital);

            double carriedShares = currentShares - withdrawnShares;
            String formattedCarriedShares = formatInteger(carriedShares);

            double interestIncome = totalSavingsAmount - withdrawnCapital;
            String formattedInterestIncome = formatCurrency(interestIncome);

            // Create document and page
            var document = createDocument(site, from, to);
            var page = new PDPage();
            document.addPage(page);
            var cs = new PDPageContentStream(document, page);

            var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDRectangle mediaBox = page.getMediaBox();

            var margin = 75;
            var fontSize = 12;
            float leading = 1.5f * fontSize;
            float startX = mediaBox.getLowerLeftX() + margin;
            float startY = mediaBox.getUpperRightY() - margin;

            // Prepare the content lines in the same order as before
            List<String> bodyLines = Arrays.asList(
                    "Generation time: " + LocalDateTime.now(),
                    "Produced for user: " + userId,
                    "Period: " + from + " to " + to,
                    "",
                    "Brought forward shares: " + formattedCurrentShares,
                    "Shares withdrawn: " + formattedWithdrawnShares,
                    "Carried forward shares: " + formattedCarriedShares,
                    "",
                    "Total savings: £" + formattedTotalSavingsAmount,
                    "Capital withdrawn: £" + formattedWithdrawnCapital,
                    "Interest income: £" + formattedInterestIncome,
                    "",
                    "This document is intended for informational purposes only.",
                    "Please consult a tax professional for advice regarding your specific situation."
            );

            var title = "Energy Co-op (" + site + ") Tax Document";

            // title font size was constant; compute it inside helper to reduce cognitive complexity
            writeDocumentContent(cs, font, startX, startY, fontSize, leading, title, bodyLines);

            document.save(os);
            document.close();

            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper: format currency with grouping and two decimal places
    private static String formatCurrency(double value) {
        return String.format("%,.2f", value);
    }

    // Helper: format integer-like values with grouping and no decimals
    private static String formatInteger(double value) {
        return String.format("%,.0f", value);
    }

    // Helper: create a PDDocument and set metadata
    private static PDDocument createDocument(Site site, LocalDate from, LocalDate to) {
        var document = new PDDocument();
        PDDocumentInformation info = document.getDocumentInformation();
        info.setAuthor("Energy Co-op");
        info.setCreator("Energy Co-op");
        info.setTitle("Tax Document (" + site + ") " + from + " to " + to);
        info.setSubject("Tax Document for " + site + ".");
        return document;
    }

    // Helper: write the title and body lines to the content stream and close the stream
    private static void writeDocumentContent(final PDPageContentStream cs, final PDType1Font font,
                                             float startX, float startY, float bodyFontSize,
                                             float leading, final String title, final List<String> bodyLines) throws IOException {
        var titleFontSize = 16f; // fixed title size

        cs.beginText();
        cs.setFont(font, titleFontSize);
        cs.newLineAtOffset(startX, startY);
        cs.showText(title);
        cs.newLineAtOffset(0, -leading);
        cs.newLineAtOffset(0, -leading);

        cs.setFont(font, bodyFontSize);
        for (String line : bodyLines) {
            cs.showText(line);
            cs.newLineAtOffset(0, -leading);
        }

        cs.endText();
        cs.close();
    }
}
