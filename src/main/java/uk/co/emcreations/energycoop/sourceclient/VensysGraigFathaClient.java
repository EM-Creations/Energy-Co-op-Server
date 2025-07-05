package uk.co.emcreations.energycoop.sourceclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.emcreations.energycoop.dto.VensysDataResponse;

@FeignClient(value = "vensys-graig-fatha", url = "${external.api.graig-fatha.url}", configuration = VensysConfiguration.class)
public interface VensysGraigFathaClient {

    @GetMapping(value = "/Customer/MeanData/Show/EnergyYield")
    VensysDataResponse getEnergyYield();
}
