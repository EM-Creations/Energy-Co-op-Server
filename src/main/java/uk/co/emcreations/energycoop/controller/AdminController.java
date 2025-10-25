package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uk.co.emcreations.energycoop.dto.SavingsRateUpdate;
import uk.co.emcreations.energycoop.entity.Alert;
import uk.co.emcreations.energycoop.entity.SavingsRate;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.security.HasAlertsRead;
import uk.co.emcreations.energycoop.security.HasSavingsRateSet;
import uk.co.emcreations.energycoop.service.AlertService;
import uk.co.emcreations.energycoop.service.SavingsRateService;
import uk.co.emcreations.energycoop.util.PrincipalHelper;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Administration control for the application.")
public class AdminController {
    private final SavingsRateService savingsRateService;
    private final AlertService alertService;

    @HasSavingsRateSet
    @PostMapping(name = "Set savings rate", value = "/savings-rate")
    @Operation(summary = "Set savings rate", description = "Sets savings rate for a site for an effective date.")
    public SavingsRate setSavingsRate(@RequestBody final SavingsRateUpdate savingsRateUpdate, final Principal principal) {
        String userId =  PrincipalHelper.extractUserFromPrincipal(principal);

        return savingsRateService.setSavingsRateForDate(savingsRateUpdate.site(), savingsRateUpdate.effectiveDate(),
                savingsRateUpdate.ratePerKWH(), userId);
    }

    @HasAlertsRead
    @GetMapping(name = "Get alerts", value = "/alerts/{site}")
    @Operation(summary = "Get alerts", description = "Gets alerts for a site.")
    public List<Alert> getAlerts(@PathVariable final Site site) {
        return alertService.getLatestAlerts(site);
    }
}
