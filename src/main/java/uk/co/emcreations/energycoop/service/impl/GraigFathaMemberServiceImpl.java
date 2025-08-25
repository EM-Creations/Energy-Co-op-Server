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
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;
import uk.co.emcreations.energycoop.util.EntityHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static uk.co.emcreations.energycoop.model.Site.GRAIG_FATHA;

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
    private final uk.co.emcreations.energycoop.service.SavingsRateService savingsRateService;

    @Value("${site.capacity.gf:100}")
    double totalCapacity;

    @Override
    public EnergySaving getTodaySavings(final double wattageOwnership) {
        log.info("getTodaySavings() called with wattageOwnership: {}", wattageOwnership);

        LocalDate today = LocalDate.now();
        Pair<LocalDateTime, LocalDateTime> todayStartAndEnd = getDayBounds(today);

        double todayGenerationSoFar = getGenerationBetweenTimes(todayStartAndEnd.getLeft(), todayStartAndEnd.getRight());
        log.info("Today's generation so far: {} kWh", todayGenerationSoFar);

        double savingsRate = savingsRateService.getSavingsRateForDate(GRAIG_FATHA, today);
        double totalSavingsForToday = getSavings(todayGenerationSoFar, savingsRate);
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
            double savingsRate = savingsRateService.getSavingsRateForDate(GRAIG_FATHA, current);
            double totalSavings = getSavings(generation, savingsRate);
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
                generationStatEntryRepository.findFirstBySiteAndTimestampBetweenOrderByTimestampDesc(GRAIG_FATHA, start, end);

        if (null != todayGenerationSoFar) { // If there's data for today, return it
            return todayGenerationSoFar.getKWhGenerated();
        } else { // if there's no data for today, fetch it and store it
            VensysMeanData energyYield = graigFathaStatsService.getMeanEnergyYield();

            GenerationStatEntry statEntry = EntityHelper.createGenerationStatEntry(energyYield, GRAIG_FATHA);
            entityManager.persist(statEntry);

            return statEntry.getKWhGenerated();
        }
    }

    private double getHistoricalGenerationBetweenTimes(final LocalDateTime start, final LocalDateTime end) {
        PerformanceStatEntry generationOnDay =
                performanceStatEntryRepository.findFirstBySiteAndForDateBetweenOrderByTimestampDesc(GRAIG_FATHA, start, end);

        if (null != generationOnDay) { // If there's data for this day, return it
            return generationOnDay.getKWhGenerated();
        } else { // if there's no data for this day, fetch it and store it
            VensysPerformanceData performanceData = graigFathaStatsService.getPerformance(start, end);

            PerformanceStatEntry statEntry = EntityHelper.createPerformanceStatEntry(performanceData, GRAIG_FATHA);
            entityManager.persist(statEntry);

            return statEntry.getKWhGenerated();
        }
    }

    private Pair<LocalDateTime, LocalDateTime> getDayBounds(final LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);
        return Pair.of(startOfDay, endOfDay);
    }

    private double getSavings(final double generationKWH, final double savingsRate) {
        return generationKWH * savingsRate;
    }

    private double getOwnershipPercentage(final double wattageOwnership) {
        return wattageOwnership / totalCapacity;
    }
}