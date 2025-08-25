package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.entity.MemberOwnership;
import uk.co.emcreations.energycoop.entity.MemberOwnershipRepository;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.MemberOwnershipService;

import java.time.LocalDate;

/**
 * Although Auth0 stores ownership for each site, this service is required to account for situations where the member's
 * ownership has changed over time. For example if we're calculating savings for an entire year, the member may have
 * sold or bought some of their shares part way through the year. In these cases we need to use the ownership figures
 * that were correct for the date we're calculating savings for.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberOwnershipServiceImpl implements MemberOwnershipService {
    private final MemberOwnershipRepository memberOwnershipRepository;

    @Override
    public double getMemberOwnershipForSite(final Site site, final LocalDate date, final String userId,
                                            final double suppliedOwnership) {
        double memberOwnership = memberOwnershipRepository
                .findTopByUserIdAndSiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(userId, site, date)
                .map(MemberOwnership::getWattageOwnership)
                .orElse(suppliedOwnership);

        log.debug("Retrieved ownership for site {} on date {} and member {}: {}", site, date, userId, memberOwnership);

        return memberOwnership;
    }
}
