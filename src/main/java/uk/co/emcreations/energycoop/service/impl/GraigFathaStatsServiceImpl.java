package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysMeanDataResponse;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceDataResponse;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.AlertService;
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
    private final VensysGraigFathaClient client;
    private final AlertService alertService;
    @Value("${alerts.thresholds.availability:75.0}")
    private double availabilityThreshold;
    @Value("${alerts.thresholds.failure-time:100.0}")
    private double failureTimeThreshold;

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

        VensysPerformanceDataResponse response = client.getPerformance(fromTimestamp, toTimestamp);
        validatePerformanceData(response);

        return response.data()[0];
    }

    private VensysPerformanceData getCurrentPerformance() {
        log.info("getCurrentPerformance() called");

        VensysPerformanceDataResponse response = client.getCurrentPerformance();
        validatePerformanceData(response);

        return response.data()[0];
    }

    private void validatePerformanceData(final VensysPerformanceDataResponse response) {
        var alertMessage = new StringBuilder();

        if (response == null) {
            alertMessage.append("Performance response is null.\n");
        } else {
            VensysPerformanceData data = response.data()[0];

            if (availabilityThreshold >= data.availability()) {
                alertMessage.append("Availability (").append(data.availability()).append(") less than threshold (")
                        .append(availabilityThreshold).append(").\n");
            }

            if (failureTimeThreshold < data.fireTime()) {
                alertMessage.append("Fire time (").append(data.fireTime()).append(") exceeds threshold (")
                        .append(failureTimeThreshold).append(").\n");
            }

            if (failureTimeThreshold < data.commFailureTime()) {
                alertMessage.append("Comm failure time (").append(data.commFailureTime()).append(") exceeds threshold (")
                        .append(failureTimeThreshold).append(").\n");
            }

            if (failureTimeThreshold < data.gridFailureTime()) {
                alertMessage.append("Grid failure time (").append(data.gridFailureTime()).append(") exceeds threshold (")
                        .append(failureTimeThreshold).append(").\n");
            }

            if (failureTimeThreshold < data.errorTime()) {
                alertMessage.append("Error time (").append(data.errorTime()).append(") exceeds threshold (")
                        .append(failureTimeThreshold).append(").\n");
            }
        }

        if (!alertMessage.isEmpty()) {
            var timeStr = (null != response) ? response.from() + " -> " + response.to() + "\n" : "Unknown";

            alertMessage.insert(0, "(" + timeStr + "): ");

            alertService.sendAlert(Site.GRAIG_FATHA, alertMessage.toString());
        }
    }
}