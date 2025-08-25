package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;

public interface MemberOwnershipService {
    double getMemberOwnershipForSite(final Site site, final LocalDate date, final String userId,
                                     final double suppliedOwnership);
}

