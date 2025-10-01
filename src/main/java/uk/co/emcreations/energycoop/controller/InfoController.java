package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.InfoService;

@RestController
@RequestMapping("/api/v1/info")
@RequiredArgsConstructor
@Tag(name = "Information", description = "Information for the application.")
public class InfoController {
    @Autowired
    private final InfoService infoService;

    @GetMapping(name = "Sites", value = "/sites")
    @Operation(summary = "Sites", description = "Returns all sites supported by the application.")
    public Site[] getSites() {
        return infoService.getSites();
    }
}
