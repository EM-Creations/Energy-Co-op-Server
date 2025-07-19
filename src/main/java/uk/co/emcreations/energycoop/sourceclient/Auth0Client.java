package uk.co.emcreations.energycoop.sourceclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.emcreations.energycoop.dto.ManagementAccessToken;

/**
 * This interface defines the Auth0 client for accessing the Auth0 Management API.
 * It uses Feign to make HTTP requests to the Auth0 API.
 * <p>
 * This class is used to obtain an access token for the Auth0 Management API, which we then use in subsequent management
 * API calls, to fetch user permissions.
 */
@FeignClient(value = "auth0", url = "${okta.oauth2.issuer}", configuration = Auth0Configuration.class)
public interface Auth0Client {
    @PostMapping(value = "oauth/token")
    ManagementAccessToken getAccessToken();
}
