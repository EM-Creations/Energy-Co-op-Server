package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.dto.VensysEnergyYield;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

@RestController
@RequiredArgsConstructor
@Tag(name = "Graig Fatha Statistics", description = "Statistics for the Graig Fatha wind farm")
public class GraigFathaStatsController {
    @Autowired
    private final VensysGraigFathaClient client;

    @GetMapping(name = "Energy Yield", value = "/energyYield")
    @Operation(summary = "Energy Yield", description = "Returns today's energy yield")
    public VensysEnergyYield getEnergyYield() {
        return client.getEnergyYield().data();
    }
}