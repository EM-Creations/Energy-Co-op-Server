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

public class TaxDocument {
    public static byte[] generateTaxDocument(final Site site, final String userId, final LocalDate from,
                                             final LocalDate to, final double totalSavingsAmount) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String formattedTotalSavingsAmount = String.format("%.2f", totalSavingsAmount);

            var document = new PDDocument();
            PDDocumentInformation info = document.getDocumentInformation();
            info.setAuthor("Energy Co-op");
            info.setCreator("Energy Co-op");
            info.setTitle("Tax Document (" + site + ") " + from + " to " + to);
            info.setSubject("Tax Document for " + site + ".");

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

            cs.beginText();
            cs.setFont(font, 16);
            cs.newLineAtOffset(startX, startY);
            cs.showText("Energy Co-op (" + site + ") Tax Document");
            cs.newLineAtOffset(0, -leading);

            cs.newLineAtOffset(0, -leading);
            cs.setFont(font, fontSize);
            cs.showText("Generation time: " + LocalDateTime.now());
            cs.newLineAtOffset(0, -leading);

            cs.newLineAtOffset(0, -leading);
            cs.showText("Produced for user: " + userId);

            cs.newLineAtOffset(0, -leading);
            cs.showText("Period: " + from + " to " + to);

            cs.newLineAtOffset(0, -leading);
            cs.showText("Total savings: £" + formattedTotalSavingsAmount);
            cs.newLineAtOffset(0, -leading);

            // Disclaimer
            cs.newLineAtOffset(0, -leading);
            cs.showText("This document is intended for informational purposes only.");
            cs.newLineAtOffset(0, -leading);
            cs.showText("Please consult a tax professional for advice regarding your specific situation.");

            cs.endText();
            cs.close();

            document.save(os);
            document.close();

            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
