package uk.co.emcreations.energycoop.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record VensysPerformanceDataResponse(String code, boolean success, String from, String to, String processTime,
                                            String message, VensysPerformanceData[] data) {
}
