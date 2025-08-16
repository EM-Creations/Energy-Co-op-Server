package uk.co.emcreations.energycoop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntry;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityHelperTest {
    @Test
    @DisplayName("createGenerationStatEntry sets all fields correctly")
    void testCreateGenerationStatEntry() {
        VensysMeanData meanData = VensysMeanData.builder().value(123.45).build();
        GenerationStatEntry entry = EntityHelper.createGenerationStatEntry(meanData, Site.GRAIG_FATHA);
        assertEquals(Site.GRAIG_FATHA, entry.getSite());
        assertEquals(123.45, entry.getWattsGenerated());
    }

    @Test
    @DisplayName("createPerformanceStatEntry sets all fields correctly")
    void testCreatePerformanceStatEntry() {
        LocalDateTime now = LocalDateTime.now();
        VensysPerformanceData perfData = VensysPerformanceData.builder()
                .date(now)
                .energyYield(10.0)
                .availability(99.9)
                .powerAvg(5.5)
                .powerMax(8.8)
                .windAvg(3.3)
                .windMax(7.7)
                .powerProductionTime(100)
                .lowWindTime(200)
                .errorTime(300)
                .serviceTime(400)
                .iceTime(500)
                .stormTime(600)
                .shadowTime(700)
                .twistTime(800)
                .gridFailureTime(900)
                .build();
        PerformanceStatEntry entry = EntityHelper.createPerformanceStatEntry(perfData, Site.GRAIG_FATHA);
        assertEquals(Site.GRAIG_FATHA, entry.getSite());
        assertEquals(now, entry.getForDate());
        assertEquals(10.0, entry.getWattsGenerated());
        assertEquals(99.9, entry.getAvailability());
        assertEquals(5.5, entry.getAveragePower());
        assertEquals(8.8, entry.getMaxPower());
        assertEquals(3.3, entry.getAverageWind());
        assertEquals(7.7, entry.getMaxWind());
        assertEquals(100, entry.getPowerProductionTime());
        assertEquals(200, entry.getLowWindTime());
        assertEquals(300, entry.getErrorTime());
        assertEquals(400, entry.getServiceTime());
        assertEquals(500, entry.getIceTime());
        assertEquals(600, entry.getStormTime());
        assertEquals(700, entry.getShadowTime());
        assertEquals(800, entry.getTwistTime());
        assertEquals(900, entry.getGridFailureTime());
    }
}

