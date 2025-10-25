package uk.co.emcreations.energycoop.service;

import uk.co.emcreations.energycoop.entity.Alert;
import uk.co.emcreations.energycoop.model.Site;

import java.util.List;

public interface AlertService {
    void sendAlert(final Site site, final String message);
    List<Alert> getLatestAlerts(final Site site);
}
