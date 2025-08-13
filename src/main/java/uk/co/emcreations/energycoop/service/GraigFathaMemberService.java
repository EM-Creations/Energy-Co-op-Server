package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.dto.EnergySaving;

public interface GraigFathaMemberService {
    EnergySaving getTodaySavings(final double wattageOwnership);
}