package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.co.emcreations.energycoop.dto.DiscordIncomingWebhook;
import uk.co.emcreations.energycoop.service.AlertService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
    private final RestTemplate restTemplate;

    private static final String ALERT_BOT_NAME = "Energy Co-op Alert Bot";
    private static final int DISCORD_MESSAGE_MAX_LENGTH = 2000;
    @Value("${alerts.enabled:false}")
    private boolean alertsEnabled;
    @Value("${alerts.discord.webhook-url}")
    private String discordWebhookURL;

    @Override
    public void sendAlert(final String message) {
        if (alertsEnabled) {
            String trimmedMessage = trimMessage(message);

            log.warn("Sending alert with content: {}", trimmedMessage);

            var discordAlert = new DiscordIncomingWebhook(ALERT_BOT_NAME, trimmedMessage);

            var request = new HttpEntity<>(discordAlert);
            restTemplate.postForObject(discordWebhookURL, request, DiscordIncomingWebhook.class);
        }
    }

    private String trimMessage(final String message) {
        if (DISCORD_MESSAGE_MAX_LENGTH < message.length()) {
            return message.substring(0, DISCORD_MESSAGE_MAX_LENGTH - 3) + "...";
        } else {
            return message;
        }
    }
}
