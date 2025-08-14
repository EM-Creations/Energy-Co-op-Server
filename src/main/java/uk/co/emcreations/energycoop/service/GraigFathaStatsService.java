package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;

import java.time.LocalDateTime;

public interface GraigFathaStatsService {
    VensysMeanData getMeanEnergyYield();
    VensysPerformanceData getYesterdayPerformance();
    VensysPerformanceData getPerformance(final LocalDateTime from, final LocalDateTime to);
}