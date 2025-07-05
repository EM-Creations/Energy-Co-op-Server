package uk.co.emcreations.energycoop.dto;

import java.time.LocalDate;

public record VensysEnergyYield(String field, int value, String tid, LocalDate timestamp, String twinCatVersion) {
}
