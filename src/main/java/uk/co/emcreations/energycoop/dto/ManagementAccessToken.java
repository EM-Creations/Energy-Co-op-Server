package uk.co.emcreations.energycoop.dto;

import lombok.Builder;

@Builder
public record ManagementAccessToken(String access_token, long expires_in, String token_type) {
}
