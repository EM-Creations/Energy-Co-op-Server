package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.model.Site;

public interface AlertService {
    void sendAlert(final Site site, final String message);
}
