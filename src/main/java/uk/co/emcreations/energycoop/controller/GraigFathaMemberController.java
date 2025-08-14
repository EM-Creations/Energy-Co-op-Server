package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.dto.EnergySaving;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.security.HasGraigFathaStatsRead;
import uk.co.emcreations.energycoop.service.GraigFathaMemberService;
import uk.co.emcreations.energycoop.util.PrincipalHelper;

import java.security.Principal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/graigFatha/member")
@RequiredArgsConstructor
@Tag(name = "Graig Fatha Membership", description = "Membership endpoints for the Graig Fatha wind farm")
public class GraigFathaMemberController {
    private final GraigFathaMemberService graigFathaMemberService;

    @HasGraigFathaStatsRead
    @GetMapping(name = "Today's Savings", value = "/todaySavings")
    @Operation(summary = "Today's Savings", description = "Returns this user's current savings today")
    public EnergySaving getTodaySavings(final Principal principal) {
        final EnumMap<Site, Double> ownerships = PrincipalHelper.extractOwnershipsFromPrincipal(principal);

        return graigFathaMemberService.getTodaySavings(ownerships.get(Site.GRAIG_FATHA));
    }

    @HasGraigFathaStatsRead
    @GetMapping(name = "Get savings between dates", value = "/savings/{from}/{to}")
    @Operation(summary = "Get savings between dates", description = "Returns this user's savings between dates")
    public Set<EnergySaving> getSavings(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
                                        final Principal principal) {
        final EnumMap<Site, Double> ownerships = PrincipalHelper.extractOwnershipsFromPrincipal(principal);

        return graigFathaMemberService.getSavings(from, to, ownerships.get(Site.GRAIG_FATHA));
    }
}