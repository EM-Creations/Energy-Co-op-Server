package uk.co.emcreations.energycoop.service.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
import uk.co.emcreations.energycoop.entity.Alert;
import uk.co.emcreations.energycoop.entity.AlertRepository;
import uk.co.emcreations.energycoop.model.Site;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private EntityManager entityManager;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertServiceImpl service;

    private Site testSite;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "discordWebhookURL", DISCORD_WEBHOOK_URL);
        ReflectionTestUtils.setField(service, "discordAlertsEnabled", true);
        testSite = Site.GRAIG_FATHA;
    }

    @Nested
    class SendAlertTests {
        @Test
        void sendAlert_sendsToDiscordAndPersists_whenNoPreviousAlert() {
            var message = "Test alert message";
            var expectedDiscordMessage = "[" + testSite + "]: " + message;

            when(alertRepository.findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(testSite),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
            )).thenReturn(Optional.empty());

            service.sendAlert(testSite, message);

            // Verify Discord webhook
            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<DiscordIncomingWebhook>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<DiscordIncomingWebhook>>) (Class<?>) HttpEntity.class);

            verify(restTemplate).postForObject(
                    eq(DISCORD_WEBHOOK_URL),
                    requestCaptor.capture(),
                    any()
            );

            DiscordIncomingWebhook actualWebhook = requestCaptor.getValue().getBody();
            assertNotNull(actualWebhook, "Webhook body should not be null");
            assertEquals(ALERT_BOT_NAME, actualWebhook.username());
            assertEquals(expectedDiscordMessage, actualWebhook.content());

            // Verify alert persistence
            ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
            verify(entityManager).persist(alertCaptor.capture());
            Alert savedAlert = alertCaptor.getValue();
            assertEquals(testSite, savedAlert.getSite());
            assertEquals(message, savedAlert.getMessage());
        }

        @Test
        void sendAlert_skipsAlert_whenPreviousAlertExists() {
            var message = "Test alert message";
            var existingAlert = Alert.builder()
                    .site(testSite)
                    .message("Previous alert")
                    .createdAt(LocalDate.now().atTime(12, 0))
                    .build();

            when(alertRepository.findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(testSite),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
            )).thenReturn(Optional.of(existingAlert));

            service.sendAlert(testSite, message);

            verify(restTemplate, never()).postForObject(
                any(),
                any(),
                any()
            );
            verify(entityManager, never()).persist(any());
        }

        @Test
        void sendAlert_onlyPersists_whenAlertsDisabled() {
            ReflectionTestUtils.setField(service, "discordAlertsEnabled", false);
            var message = "Test alert message";

            when(alertRepository.findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(testSite),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
            )).thenReturn(Optional.empty());

            service.sendAlert(testSite, message);

            verify(restTemplate, never()).postForObject(
                any(),
                any(),
                any()
            );

            verify(entityManager).persist(any(Alert.class));
        }

        @Test
        void sendAlert_trimsMessage_whenTooLong() {
            var longMessage = "a".repeat(DISCORD_MESSAGE_MAX_LENGTH + 50);

            when(alertRepository.findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(testSite),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
            )).thenReturn(Optional.empty());

            service.sendAlert(testSite, longMessage);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<DiscordIncomingWebhook>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<DiscordIncomingWebhook>>) (Class<?>) HttpEntity.class);

            verify(restTemplate).postForObject(
                    eq(DISCORD_WEBHOOK_URL),
                    requestCaptor.capture(),
                    any()
            );

            DiscordIncomingWebhook actualWebhook = requestCaptor.getValue().getBody();
            assertNotNull(actualWebhook, "Webhook body should not be null");
            assertTrue(longMessage.length() > actualWebhook.content().length());
            verify(entityManager).persist(any(Alert.class));
        }

        @Test
        void sendAlert_doesNotTrimMessage_whenWithinLimit() {
            var message = "Short message";
            var expectedMessage = "[" + testSite + "]: " + message;

            when(alertRepository.findFirstBySiteAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(testSite),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
            )).thenReturn(Optional.empty());

            service.sendAlert(testSite, message);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<DiscordIncomingWebhook>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<DiscordIncomingWebhook>>) (Class<?>) HttpEntity.class);

            verify(restTemplate).postForObject(
                    eq(DISCORD_WEBHOOK_URL),
                    requestCaptor.capture(),
                    any()
            );

            DiscordIncomingWebhook actualWebhook = requestCaptor.getValue().getBody();
            assertNotNull(actualWebhook, "Webhook body should not be null");
            assertEquals(expectedMessage, actualWebhook.content());
            verify(entityManager).persist(any(Alert.class));
        }
    }
}
