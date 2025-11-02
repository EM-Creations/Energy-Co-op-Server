package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.InfoService;
import uk.co.emcreations.energycoop.util.PrincipalHelper;

import java.security.Principal;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    @Override
    public Site[] getSites() {
        return Site.values();
    }

    /**
     * Returns the sites for which the user has some ownership of.
     *
     * @param principal the security principal
     * @return an array of sites which the user has some ownership of
     */
    @Override
    public Site[] getSitesWithUserOwnership(final Principal principal) {
        EnumMap<Site, Double> ownerships = PrincipalHelper.extractOwnershipsFromPrincipal(principal);

        return ownerships.entrySet().stream()
                .filter(ownership -> 0.0 < ownership.getValue())
                .map(Map.Entry::getKey)
                .toArray(Site[]::new);
    }
}
