package uk.co.emcreations.energycoop.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.EnergySaving;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.entity.GenerationStatEntryRepository;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;
import uk.co.emcreations.energycoop.util.EntityHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GraigFathaMemberServiceImpl implements GraigFathaMemberService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final GenerationStatEntryRepository generationStatEntryRepository;
    private final GraigFathaStatsService graigFathaStatsService;

    @Value("${site.rates.gf:1.0}")
    double savingsRatePerWatt;

    @Value("${site.capacity.gf:100}")
    double totalCapacity;

    @Override
    public EnergySaving getTodaySavings(final double wattageOwnership) {
        log.info("getTodaySavings() called with wattageOwnership: {}", wattageOwnership);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        double todayGenerationSoFar = getGenerationBetweenTimes(startOfDay, endOfDay);
        log.info("Today's generation so far: {} watts", todayGenerationSoFar);

        double totalSavingsForToday = getSavings(todayGenerationSoFar);
        log.info("Calculated savings for the wind farm today: {}", totalSavingsForToday);

        double memberOwnershipPct = getOwnershipPercentage(wattageOwnership);

        double memberSavings = totalSavingsForToday * memberOwnershipPct;
        log.info("Calculated savings for this member today: {}", memberSavings);

        return new EnergySaving(
                memberSavings,
                "GBP", // Assuming GBP as the currency, can be parameterized if needed
                startOfDay,
                endOfDay
        );
    }

    private double getGenerationBetweenTimes(final LocalDateTime start, final LocalDateTime end) {
        GenerationStatEntry todayGenerationSoFar =
                generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(Site.GRAIG_FATHA, start, end);

        if (null != todayGenerationSoFar) { // If there's data for today, return it
            return todayGenerationSoFar.getWattsGenerated();
        } else { // if there's no data for today, fetch it and store it
            VensysMeanData energyYield = graigFathaStatsService.getMeanEnergyYield();

            GenerationStatEntry statEntry = EntityHelper.createGenerationStatEntry(energyYield, Site.GRAIG_FATHA);
            entityManager.persist(statEntry);

            return statEntry.getWattsGenerated();
        }
    }

    private double getSavings(double generationWattage) {
        return generationWattage * savingsRatePerWatt;
    }

    private double getOwnershipPercentage(double wattageOwnership) {
        return wattageOwnership / totalCapacity;
    }
}