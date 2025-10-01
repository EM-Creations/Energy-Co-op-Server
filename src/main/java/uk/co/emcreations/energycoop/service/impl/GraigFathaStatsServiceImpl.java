package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysMeanDataResponse;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraigFathaStatsServiceImpl implements GraigFathaStatsService {
    @Autowired
    private final VensysGraigFathaClient client;

    @Override
    public VensysMeanData getMeanEnergyYield() {
        log.info("getEnergyYield() called");

        VensysMeanDataResponse meanDataResponse = client.getMeanEnergyYield();
        Optional<VensysMeanData> meanDataOptional = Optional.ofNullable(meanDataResponse.data());

        return meanDataOptional.orElseGet(() -> {
            log.warn("No mean energy yield data available from client, falling back to current performance data.");
            VensysPerformanceData currentPerformance = getCurrentPerformance();
            return VensysMeanData.builder().value(currentPerformance.energyYield()).build();
        });
    }

    @Override
    public VensysPerformanceData getYesterdayPerformance() {
        log.info("getYesterdayPerformance() called");

        var from = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
        var to = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);

        return getPerformance(from, to);
    }

    @Override
    public VensysPerformanceData getPerformance(final LocalDateTime from, final LocalDateTime to) {
        log.info("getPerformance() called from: {}, to: {}", from, to);

        var fromTimestamp = from.toEpochSecond(ZoneOffset.UTC);
        var toTimestamp = to.toEpochSecond(ZoneOffset.UTC);

        return client.getPerformance(fromTimestamp, toTimestamp).data()[0];
    }

    private VensysPerformanceData getCurrentPerformance() {
        log.info("getCurrentPerformance() called");

        return client.getCurrentPerformance().data()[0];
    }
}