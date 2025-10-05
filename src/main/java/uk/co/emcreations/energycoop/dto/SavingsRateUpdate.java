package uk.co.emcreations.energycoop.dto;

import org.springframework.format.annotation.DateTimeFormat;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;

public record SavingsRateUpdate(Site site,
                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveDate,
                                double ratePerKWH) {
}
