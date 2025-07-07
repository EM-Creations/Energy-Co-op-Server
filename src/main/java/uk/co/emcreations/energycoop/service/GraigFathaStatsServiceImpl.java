package uk.co.emcreations.energycoop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GraigFathaStatsServiceImpl implements GraigFathaStatsService {
    private static final Site site = Site.GRAIG_FATHA;

    @Autowired
    private final VensysGraigFathaClient client;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public VensysMeanData getMeanEnergyYield() {
        log.info("getEnergyYield() called");
        VensysMeanData energyYield = client.getMeanEnergyYield().data();

        var statEntry = new GenerationStatEntry();
        statEntry.setWattsGenerated(energyYield.value());
        statEntry.setSite(site);

        entityManager.persist(statEntry);

        return energyYield;
    }

    @Override
    public VensysPerformanceData getYesterdayPerformance() {
        log.info("getYesterdayPerformance() called");

        var from = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT).toEpochSecond(ZoneOffset.UTC);
        var to = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);

        VensysPerformanceData performance = client.getPerformance(from, to).data()[0];

//        var statEntry = new GenerationStatEntry();
//        statEntry.setWattsGenerated(energyYield.value());
//        statEntry.setSite(site);
//
//        entityManager.persist(statEntry);

        return performance;
    }
}