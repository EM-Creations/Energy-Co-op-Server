package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.co.emcreations.energycoop.dto.DiscordIncomingWebhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {
    private static final String DISCORD_WEBHOOK_URL = "https://discord.webhook.test/url";
    private static final String ALERT_BOT_NAME = "Energy Co-op Alert Bot";
    private static final int DISCORD_MESSAGE_MAX_LENGTH = 2000;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AlertServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "discordWebhookURL", DISCORD_WEBHOOK_URL);
        ReflectionTestUtils.setField(service, "alertsEnabled", true);
    }

    @Nested
    @DisplayName("sendAlert tests")
    class SendAlertTests {
        @Test
        @DisplayName("Sends alert to Discord when alerts are enabled")
        void sendAlert_sendsToDiscord_whenEnabled() {
            String message = "Test alert message";
            var expectedWebhook = new DiscordIncomingWebhook(ALERT_BOT_NAME, message);

            when(restTemplate.postForObject(
                eq(DISCORD_WEBHOOK_URL),
                any(HttpEntity.class),
                eq(DiscordIncomingWebhook.class)
            )).thenReturn(null);

            service.sendAlert(message);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<DiscordIncomingWebhook>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<DiscordIncomingWebhook>>) (Class<?>) HttpEntity.class);

            verify(restTemplate).postForObject(
                    eq(DISCORD_WEBHOOK_URL),
                    requestCaptor.capture(),
                    eq(DiscordIncomingWebhook.class)
            );

            var actualWebhook = requestCaptor.getValue().getBody();
            assertNotNull(actualWebhook, "Webhook body should not be null");
            assertEquals(expectedWebhook.username(), actualWebhook.username());
            assertEquals(expectedWebhook.content(), actualWebhook.content());
        }

        @Test
        @DisplayName("Does not send alert when alerts are disabled")
        void sendAlert_doesNotSend_whenDisabled() {
            ReflectionTestUtils.setField(service, "alertsEnabled", false);
            String message = "Test alert message";

            service.sendAlert(message);

            verify(restTemplate, never()).postForObject(
                    any(),
                    any(),
                    any()
            );
        }

        @Test
        @DisplayName("Trims message when it exceeds max length")
        void sendAlert_trimsMessage_whenTooLong() {
            String longMessage = "a".repeat(DISCORD_MESSAGE_MAX_LENGTH + 50);
            String expectedTrimmed = longMessage.substring(0, DISCORD_MESSAGE_MAX_LENGTH - 3) + "...";

            when(restTemplate.postForObject(
                eq(DISCORD_WEBHOOK_URL),
                any(HttpEntity.class),
                eq(DiscordIncomingWebhook.class)
            )).thenReturn(null);

            service.sendAlert(longMessage);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<DiscordIncomingWebhook>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<DiscordIncomingWebhook>>) (Class<?>) HttpEntity.class);

            verify(restTemplate).postForObject(
                    eq(DISCORD_WEBHOOK_URL),
                    requestCaptor.capture(),
                    eq(DiscordIncomingWebhook.class)
            );

            var actualWebhook = requestCaptor.getValue().getBody();
            assertNotNull(actualWebhook, "Webhook body should not be null");
            assertEquals(expectedTrimmed, actualWebhook.content());
            assertEquals(DISCORD_MESSAGE_MAX_LENGTH, actualWebhook.content().length());
        }

        @Test
        @DisplayName("Does not trim message when within max length")
        void sendAlert_doesNotTrimMessage_whenWithinLimit() {
            String message = "Short message";

            when(restTemplate.postForObject(
                eq(DISCORD_WEBHOOK_URL),
                any(HttpEntity.class),
                eq(DiscordIncomingWebhook.class)
            )).thenReturn(null);

            service.sendAlert(message);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<DiscordIncomingWebhook>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<DiscordIncomingWebhook>>) (Class<?>) HttpEntity.class);

            verify(restTemplate).postForObject(
                    eq(DISCORD_WEBHOOK_URL),
                    requestCaptor.capture(),
                    eq(DiscordIncomingWebhook.class)
            );

            var actualWebhook = requestCaptor.getValue().getBody();
            assertNotNull(actualWebhook, "Webhook body should not be null");
            assertEquals(message, actualWebhook.content());
        }
    }
}
