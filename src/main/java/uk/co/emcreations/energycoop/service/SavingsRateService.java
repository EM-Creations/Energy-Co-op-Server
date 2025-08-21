package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;

public interface SavingsRateService {
    double getSavingsRateForDate(final Site site, final LocalDate date);
}

