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
import uk.co.emcreations.energycoop.util.EntityHelper;

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

        GenerationStatEntry statEntry = EntityHelper.createGenerationStatEntry(energyYield, Site.GRAIG_FATHA);
        entityManager.persist(statEntry);

        log.info("Response = {}", energyYield);
    }

    @Scheduled(cron = "${scheduling.graig-fatha.schedule.performance:0 0 */6 * * *}")
    public void logPerformance() {
        log.info("logPerformance running..");

        VensysPerformanceData performanceData = graigFathaStatsService.getYesterdayPerformance();

        PerformanceStatEntry statEntry = EntityHelper.createPerformanceStatEntry(performanceData, Site.GRAIG_FATHA);
        entityManager.persist(statEntry);

        log.info("Response = {}", performanceData);
    }
}
