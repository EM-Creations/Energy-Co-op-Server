package uk.co.emcreations.energycoop.util;

import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.entity.GenerationStatEntry;
import uk.co.emcreations.energycoop.entity.PerformanceStatEntry;
import uk.co.emcreations.energycoop.model.Site;

public class EntityHelper {
    public static GenerationStatEntry createGenerationStatEntry(final VensysMeanData energyYield, final Site site) {
        var statEntry = new GenerationStatEntry();
        statEntry.setSite(site);
        statEntry.setWattsGenerated(energyYield.value());
        return statEntry;
    }

    public static PerformanceStatEntry createPerformanceStatEntry(final VensysPerformanceData performanceData, final Site site) {
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
