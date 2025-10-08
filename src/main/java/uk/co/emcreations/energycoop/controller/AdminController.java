package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.dto.SavingsRateUpdate;
import uk.co.emcreations.energycoop.entity.SavingsRate;
import uk.co.emcreations.energycoop.security.HasSavingsRateSet;
import uk.co.emcreations.energycoop.service.SavingsRateService;
import uk.co.emcreations.energycoop.util.PrincipalHelper;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Administration control for the application.")
public class AdminController {
    @Autowired
    private final SavingsRateService savingsRateService;

    @HasSavingsRateSet
    @PostMapping(name = "Set savings rate", value = "/savings-rate")
    @Operation(summary = "Set savings rate", description = "Sets savings rate for a site for an effective date.")
    public SavingsRate setSavingsRate(@RequestBody final SavingsRateUpdate savingsRateUpdate, final Principal principal) {
        String userId =  PrincipalHelper.extractUserFromPrincipal(principal);

        return savingsRateService.setSavingsRateForDate(savingsRateUpdate.site(), savingsRateUpdate.effectiveDate(),
                savingsRateUpdate.ratePerKWH(), userId);
    }
}
