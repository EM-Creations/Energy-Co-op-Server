package uk.co.emcreations.energycoop.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.EnergySaving;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.entity.GenerationStatEntryRepository;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntry;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntryRepository;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;
import uk.co.emcreations.energycoop.util.EntityHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GraigFathaMemberServiceImpl implements GraigFathaMemberService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final GenerationStatEntryRepository generationStatEntryRepository;
    private final PerformanceStatEntryRepository performanceStatEntryRepository;
    private final GraigFathaStatsService graigFathaStatsService;

    @Value("${site.rates.gf:1.0}")
    double savingsRatePerWatt;

    @Value("${site.capacity.gf:100}")
    double totalCapacity;

    @Override
    public EnergySaving getTodaySavings(final double wattageOwnership) {
        log.info("getTodaySavings() called with wattageOwnership: {}", wattageOwnership);

        Pair<LocalDateTime, LocalDateTime> todayStartAndEnd = getDayBounds(LocalDate.now());

        double todayGenerationSoFar = getGenerationBetweenTimes(todayStartAndEnd.getLeft(), todayStartAndEnd.getRight());
        log.info("Today's generation so far: {} watts", todayGenerationSoFar);

        double totalSavingsForToday = getSavings(todayGenerationSoFar);
        log.info("Calculated savings for the wind farm today: {}", totalSavingsForToday);

        double memberOwnershipPct = getOwnershipPercentage(wattageOwnership);

        double memberSavings = totalSavingsForToday * memberOwnershipPct;
        log.info("Calculated savings for this member today: {}", memberSavings);

        return new EnergySaving(
                memberSavings,
                "GBP", // Assuming GBP as the currency, can be parameterized if needed
                todayStartAndEnd.getLeft(),
                todayStartAndEnd.getRight()
        );
    }

    @Override
    public Set<EnergySaving> getSavings(final LocalDate from, final LocalDate to, final double wattageOwnership) {
        log.info("getSavings() called with from: {}, to: {} and wattageOwnership: {}", from, to, wattageOwnership);

        double memberOwnershipPct = getOwnershipPercentage(wattageOwnership);

        Set<EnergySaving> savingsSet = new HashSet<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            Pair<LocalDateTime, LocalDateTime> todayStartAndEnd = getDayBounds(current);

            double generation = getHistoricalGenerationBetweenTimes(todayStartAndEnd.getLeft(), todayStartAndEnd.getRight());
            double totalSavings = getSavings(generation);
            double memberSavings = totalSavings * memberOwnershipPct;

            savingsSet.add(new EnergySaving(
                    memberSavings,
                    "GBP",
                    todayStartAndEnd.getLeft(),
                    todayStartAndEnd.getRight()
            ));

            current = current.plusDays(1);
        }

        double totalSavings = savingsSet.stream()
                .mapToDouble(EnergySaving::amount)
                .sum();

        log.info("Calculated savings for this member between: {} and {} = {}", from, to, totalSavings);

        return savingsSet;
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

    private double getHistoricalGenerationBetweenTimes(final LocalDateTime start, final LocalDateTime end) {
        PerformanceStatEntry generationOnDay =
                performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(Site.GRAIG_FATHA, start, end);

        if (null != generationOnDay) { // If there's data for this day, return it
            return generationOnDay.getWattsGenerated();
        } else { // if there's no data for this day, fetch it and store it
            VensysPerformanceData performanceData = graigFathaStatsService.getPerformance(start, end);

            PerformanceStatEntry statEntry = EntityHelper.createPerformanceStatEntry(performanceData, Site.GRAIG_FATHA);
            entityManager.persist(statEntry);

            return statEntry.getWattsGenerated();
        }
    }

    private Pair<LocalDateTime, LocalDateTime> getDayBounds(final LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);
        return Pair.of(startOfDay, endOfDay);
    }

    private double getSavings(double generationWattage) {
        return generationWattage * savingsRatePerWatt;
    }

    private double getOwnershipPercentage(double wattageOwnership) {
        return wattageOwnership / totalCapacity;
    }
}