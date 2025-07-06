package uk.co.emcreations.energycoop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.VensysEnergyYield;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import java.time.LocalDate;

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
    public VensysEnergyYield getEnergyYield() {
        log.info("getEnergyYield() called");
        VensysEnergyYield energyYield = client.getEnergyYield().data();

        var statEntry = new GenerationStatEntry();
        statEntry.setWattsGenerated(energyYield.value());
        statEntry.setSite(site);

        entityManager.persist(statEntry);

        return energyYield;
    }
}