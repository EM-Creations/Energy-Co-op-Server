package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.dto.EnergySaving;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Set;

public interface GraigFathaMemberService {
    EnergySaving getTodaySavings(final double wattageOwnership);
    Set<EnergySaving> getSavings(final LocalDate from, final LocalDate to, final double wattageOwnership, final String userId);
    byte[] generateTaxDocument(final LocalDate from, final LocalDate to, final double suppliedOwnershipWattage,
                                   final String userId) throws URISyntaxException, IOException;
}