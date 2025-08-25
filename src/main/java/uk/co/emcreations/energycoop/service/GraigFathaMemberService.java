package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.dto.EnergySaving;

import java.time.LocalDate;
import java.util.Set;

public interface GraigFathaMemberService {
    EnergySaving getTodaySavings(final double wattageOwnership);
    Set<EnergySaving> getSavings(final LocalDate from, final LocalDate to, final double wattageOwnership, final String userId);
}