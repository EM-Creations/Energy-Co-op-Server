package uk.co.emcreations.energycoop.sourceclient;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * This class holds the Auth0 Management API token.
 * It is a singleton bean, meaning there will only be one instance of this class in the application context.
 * The token can be set and retrieved as needed.
 */
@Data
@Component
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Auth0ManagementApiToken {
    private String accessToken;
}
