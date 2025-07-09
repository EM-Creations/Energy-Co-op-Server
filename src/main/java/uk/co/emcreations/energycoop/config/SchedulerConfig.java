package uk.co.emcreations.energycoop.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntry;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;

@Slf4j
@Configuration
@Transactional
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true", matchIfMissing = false)
public class SchedulerConfig {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    private final GraigFathaStatsService graigFathaStatsService;

    @Scheduled(cron = "${scheduling.graig-fatha.schedule.energy-yield:0 */15 * * * *}")
    public void logEnergyYield() {
        log.info("logEnergyYield running..");

        VensysMeanData energyYield = graigFathaStatsService.getMeanEnergyYield();

        GenerationStatEntry statEntry = createGenerationStatEntry(energyYield, Site.GRAIG_FATHA);
        entityManager.persist(statEntry);

        log.info("Response = {}", energyYield);
    }

    @Scheduled(cron = "${scheduling.graig-fatha.schedule.performance:0 0 */6 * * *}")
    public void logPerformance() {
        log.info("logPerformance running..");

        VensysPerformanceData performanceData = graigFathaStatsService.getYesterdayPerformance();

        PerformanceStatEntry statEntry = createPerformanceStatEntry(performanceData, Site.GRAIG_FATHA);
        entityManager.persist(statEntry);

        log.info("Response = {}", performanceData);
    }

    private GenerationStatEntry createGenerationStatEntry(final VensysMeanData energyYield, final Site site) {
        var statEntry = new GenerationStatEntry();
        statEntry.setSite(site);
        statEntry.setWattsGenerated(energyYield.value());
        return statEntry;
    }

    private PerformanceStatEntry createPerformanceStatEntry(final VensysPerformanceData performanceData, final Site site) {
        var statEntry = new PerformanceStatEntry();
        statEntry.setSite(site);
        statEntry.setForDate(performanceData.date());
        statEntry.setWattsGenerated(performanceData.energyYield());
        statEntry.setAvailability(performanceData.availability());
        statEntry.setAveragePower(performanceData.powerAvg());
        statEntry.setMaxPower(performanceData.powerMax());
        statEntry.setAverageWind(performanceData.windAvg());
        statEntry.setMaxWind(performanceData.windMax());
        statEntry.setPowerProductionTime(performanceData.powerProductionTime());
        statEntry.setLowWindTime(performanceData.lowWindTime());
        statEntry.setErrorTime(performanceData.errorTime());
        statEntry.setServiceTime(performanceData.serviceTime());
        statEntry.setIceTime(performanceData.iceTime());
        statEntry.setStormTime(performanceData.stormTime());
        statEntry.setShadowTime(performanceData.shadowTime());
        statEntry.setTwistTime(performanceData.twistTime());
        statEntry.setGridFailureTime(performanceData.gridFailureTime());
        statEntry.setCommFailureTime(performanceData.commFailureTime());
        statEntry.setVisitTime(performanceData.visitTime());
        statEntry.setServerStopTime(performanceData.serverStopTime());
        statEntry.setFireTime(performanceData.fireTime());
        statEntry.setBatMonitoringTime(performanceData.batMonitoringTime());
        statEntry.setNightShutdownTime(performanceData.nightShutdownTime());

        return statEntry;
    }
}
