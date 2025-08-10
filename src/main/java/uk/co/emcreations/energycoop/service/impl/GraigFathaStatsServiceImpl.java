package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraigFathaStatsServiceImpl implements GraigFathaStatsService {
    @Autowired
    private final VensysGraigFathaClient client;

    @Override
    public VensysMeanData getMeanEnergyYield() {
        log.info("getEnergyYield() called");

        return client.getMeanEnergyYield().data();
    }

    @Override
    public VensysPerformanceData getYesterdayPerformance() {
        log.info("getYesterdayPerformance() called");

        var from = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT).toEpochSecond(ZoneOffset.UTC);
        var to = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);

        return client.getPerformance(from, to).data()[0];
    }
}