package uk.co.emcreations.energycoop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.emcreations.energycoop.service.GraigFathaStatsService;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    @Autowired
    private final GraigFathaStatsService graigFathaStatsService;

    @Scheduled(cron = "${scheduling.graig-fatha.schedule:*/15 * * * *}")
    public void logEnergyYield() {
        log.info("logEnergyYield running..");

        log.info("Response = {}", graigFathaStatsService.getMeanEnergyYield());
    }
}
