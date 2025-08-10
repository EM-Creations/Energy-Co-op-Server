package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.security.HasGraigFathaStatsRead;
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;
import uk.co.emcreations.energycoop.util.PrincipalHelper;

import java.security.Principal;
import java.util.EnumMap;

@RestController
@RequestMapping("/api/v1/graigFatha/member")
@RequiredArgsConstructor
@Tag(name = "Graig Fatha Membership", description = "Membership endpoints for the Graig Fatha wind farm")
public class GraigFathaMemberController {
    private final GraigFathaMemberService graigFathaMemberService;

    @HasGraigFathaStatsRead
    @GetMapping(name = "Today's Savings", value = "/todaySavings")
    @Operation(summary = "Today's Savings", description = "Returns this user's current savings today")
    public double getTodaySavings(final Principal principal) {
        final EnumMap<Site, Double> ownerships = PrincipalHelper.extractOwnershipsFromPrincipal(principal);

        return graigFathaMemberService.getTodaySavings(ownerships.get(Site.GRAIG_FATHA));
    }
}