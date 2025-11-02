package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.model.Site;

import java.security.Principal;

public interface InfoService {
    Site[] getSites();
    Site[] getSitesWithUserOwnership(final Principal principal);
}
