package uk.co.emcreations.energycoop.util;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.co.emcreations.energycoop.model.Site;

import java.security.Principal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PrincipalHelper {
    public static EnumMap<Site, Double> extractOwnershipsFromPrincipal(final Principal principal) {
        EnumMap<Site, Double> siteOwnerships = new EnumMap<>(Site.class);

        if (!(principal instanceof JwtAuthenticationToken token)) {
            throw new IllegalArgumentException("Principal must be a JwtAuthenticationToken");
        }

        Map<String, Object> attributes = token.getTokenAttributes();

        Object ownershipsObj = attributes.get("ownerships");
        Map<String, Double> ownershipsFromToken = new HashMap<>();
        if (ownershipsObj instanceof Map<?, ?>) {
            ((Map<?, ?>) ownershipsObj).forEach((key, value) -> {
                if (key instanceof String && value instanceof Number) {
                    ownershipsFromToken.put((String) key, ((Number) value).doubleValue());
                }
            });
        }

        Arrays.stream(Site.values()).forEach(site -> {
            double siteOwnership = ownershipsFromToken.getOrDefault(site.getOwnershipKey(), 0.0);
            siteOwnerships.put(site, siteOwnership);
        });

        return siteOwnerships;
    }

}
