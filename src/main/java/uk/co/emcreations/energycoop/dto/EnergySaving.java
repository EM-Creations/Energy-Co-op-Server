package uk.co.emcreations.energycoop.dto;

import java.time.LocalDateTime;

public record EnergySaving(double amount, String currency, LocalDateTime from, LocalDateTime to) {}
