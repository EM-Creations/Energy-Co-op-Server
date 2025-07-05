package uk.co.emcreations.energycoop.dto;

import java.time.LocalDate;

public record VensysDataResponse(String code, boolean success, LocalDate from, LocalDate to, String processTime, String message, VensysEnergyYield data) {
}
