package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;

public interface GraigFathaStatsService {
    VensysMeanData getMeanEnergyYield();
    VensysPerformanceData getYesterdayPerformance();
}