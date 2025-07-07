package uk.co.emcreations.energycoop.sourceclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.emcreations.energycoop.dto.VensysMeanDataResponse;
import uk.co.emcreations.energycoop.dto.VensysPerformanceDataResponse;

@FeignClient(value = "vensys-graig-fatha", url = "${external.api.graig-fatha.url}", configuration = VensysConfiguration.class)
public interface VensysGraigFathaClient {

    @GetMapping(value = "/Customer/MeanData/Show/EnergyYield")
    VensysMeanDataResponse getMeanEnergyYield();

    @GetMapping(value = "/Customer/Performance?From={from}&To={to}")
    VensysPerformanceDataResponse getPerformance(@RequestParam long from, @RequestParam long to);
}
