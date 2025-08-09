package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.security.HasGraigFathaAPIRead;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;

@RestController
@RequestMapping("/api/v1/graigFatha/stats")
@RequiredArgsConstructor
@Tag(name = "Graig Fatha Statistics", description = "Statistics for the Graig Fatha wind farm")
public class GraigFathaStatsController {
    @Autowired
    private final GraigFathaStatsService graigFathaStatsService;

    @HasGraigFathaAPIRead
    @GetMapping(name = "Current energy yield", value = "/energyYield")
    @Operation(summary = "Current energy yield", description = "Returns today's current energy yield")
    public VensysMeanData getEnergyYield() {
        return graigFathaStatsService.getMeanEnergyYield();
    }

    @HasGraigFathaAPIRead
    @GetMapping(name = "Yesterday's performance", value = "/yesterdayPerformance")
    @Operation(summary = "Yesterday's performance", description = "Returns yesterday's performance")
    public VensysPerformanceData getYesterdayPerformance() {
        return graigFathaStatsService.getYesterdayPerformance();
    }
}