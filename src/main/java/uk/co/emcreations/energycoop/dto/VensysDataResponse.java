package uk.co.emcreations.energycoop.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VensysDataResponse(String code, boolean success, LocalDate from, LocalDate to, String processTime, String message, VensysEnergyYield data) {
}
