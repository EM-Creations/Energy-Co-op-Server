package uk.co.emcreations.energycoop.sourceclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This interface defines the Auth0 Management API client.
 * It uses Feign to make HTTP requests to the Auth0 Management API.
 * This allows us to retrieve permissions for a user by their user ID.
 */
@FeignClient(value = "auth0-management", url = "${okta.oauth2.issuer}", configuration = Auth0ManagementConfiguration.class)
public interface Auth0ManagementClient {
    @GetMapping(value = "api/v2/users/{userId}/permissions")
    String getPermissionsForUser(@PathVariable String userId);

    @GetMapping(value = "api/v2/users/{userId}")
    String getUser(@PathVariable String userId);
}
