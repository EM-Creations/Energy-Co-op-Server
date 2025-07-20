package uk.co.emcreations.energycoop.model;

import lombok.Builder;

@Builder
public record User(String userId, String name, String email, String imageURL) {
}
