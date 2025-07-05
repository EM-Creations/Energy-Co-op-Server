package uk.co.emcreations.energycoop.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VensysEnergyYield(String field, int value, String tid, LocalDate timestamp, String twinCatVersion) {
}
