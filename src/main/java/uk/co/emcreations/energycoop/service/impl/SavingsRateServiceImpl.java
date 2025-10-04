package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.entity.SavingsRate;
import uk.co.emcreations.energycoop.entity.SavingsRateRepository;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.SavingsRateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsRateServiceImpl implements SavingsRateService {
    private final SavingsRateRepository savingsRateRepository;

    @Value("${site.rates.gf:1.0}")
    private double defaultSavingsRatePerWattGraigFatha;

    @Value("${site.rates.kh:1.0}")
    private double defaultSavingsRatePerWattKirkHill;

    @Value("${site.rates.dw:1.0}")
    private double defaultSavingsRatePerWattDerrilWater;

    @Override
    public double getSavingsRateForDate(final Site site, final LocalDate date) {
        double savingsRate = savingsRateRepository
                .findTopBySiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(site, date)
                .map(SavingsRate::getRatePerKWH)
                .orElseGet(() -> switch (site) {
                    case GRAIG_FATHA -> defaultSavingsRatePerWattGraigFatha;
                    case KIRK_HILL -> defaultSavingsRatePerWattKirkHill;
                    case DERRIL_WATER -> defaultSavingsRatePerWattDerrilWater;
                });

        log.debug("Retrieved savings rate for site {} on date {}: {} per kWh", site, date, savingsRate);

        return savingsRate;
    }

    @Override
    public SavingsRate setSavingsRateForDate(final Site site, final LocalDate date, final double ratePerKWH) {
        Optional<SavingsRate> currentRateOpt = savingsRateRepository.findBySiteAndEffectiveDate(site, date);

        SavingsRate rate;
        if (currentRateOpt.isPresent()) {
            rate = currentRateOpt.get();

            log.warn("Savings rate for site {} on date {} already exists: {} per kWh, updating..",
                    site, date, rate.getRatePerKWH());

            rate.setCreatedAt(LocalDateTime.now());
            rate.setRatePerKWH(ratePerKWH);
        } else {
            rate = SavingsRate.builder()
                    .site(site)
                    .effectiveDate(date)
                    .ratePerKWH(ratePerKWH)
                    .build();
        }

        return savingsRateRepository.save(rate);
    }
}
