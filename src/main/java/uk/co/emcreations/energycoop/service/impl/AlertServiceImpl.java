package uk.co.emcreations.energycoop.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.co.emcreations.energycoop.dto.DiscordIncomingWebhook;
import uk.co.emcreations.energycoop.entity.Alert;
import uk.co.emcreations.energycoop.entity.AlertRepository;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.AlertService;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
    private final RestTemplate restTemplate;
    @PersistenceContext
    private final EntityManager entityManager;
    private final AlertRepository alertRepository;

    private static final String DISCORD_ALERT_BOT_NAME = "Energy Co-op Alert Bot";
    private static final int MESSAGE_MAX_LENGTH = 1950;
    @Value("${alerts.enabled:false}")
    private boolean discordAlertsEnabled;
    @Value("${alerts.discord.webhook-url}")
    private String discordWebhookURL;

    @Override
    public void sendAlert(final Site site, final String message) {
        if (!hasBeenAnAlertToday(site)) {
            String trimmedMessage = trimMessage(message);

            if (discordAlertsEnabled) {
                var messageWithSite = "[%s]: %s".formatted(site, trimmedMessage);

                log.warn("Sending discord alert with content: {}", messageWithSite);

                var discordAlert = new DiscordIncomingWebhook(DISCORD_ALERT_BOT_NAME, messageWithSite);

                var request = new HttpEntity<>(discordAlert);
                restTemplate.postForObject(discordWebhookURL, request, DiscordIncomingWebhook.class);
            }

            var alertToStore = Alert.builder().site(site).message(trimmedMessage).build();
            entityManager.persist(alertToStore);
        } else {
            log.info("An alert has already been sent today for site {}, skipping..", site);
        }
    }

    private boolean hasBeenAnAlertToday(final Site site) {
        var todayStart = LocalDate.now().atStartOfDay();
        var todayEnd = LocalDate.now().atTime(23, 59, 59);
        var existingAlert = alertRepository.findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(
                site,
                todayStart,
                todayEnd
        );
        return existingAlert.isPresent();
    }

    private String trimMessage(final String message) {
        if (MESSAGE_MAX_LENGTH < message.length()) {
            return message.substring(0, MESSAGE_MAX_LENGTH - 3) + "...";
        } else {
            return message;
        }
    }
}
