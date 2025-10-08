package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.entity.SavingsRate;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;

public interface SavingsRateService {
    double getSavingsRateForDate(final Site site, final LocalDate date);
    SavingsRate setSavingsRateForDate(final Site site, final LocalDate date, final double ratePerKWH, final String userId);
}

