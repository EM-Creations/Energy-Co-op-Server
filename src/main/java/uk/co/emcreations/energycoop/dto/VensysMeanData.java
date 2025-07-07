package uk.co.emcreations.energycoop.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VensysMeanData(String field, double value, String tid, LocalDate timestamp, String twinCatVersion) {
}
