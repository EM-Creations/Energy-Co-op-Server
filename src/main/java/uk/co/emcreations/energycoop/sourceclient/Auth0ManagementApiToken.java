package uk.co.emcreations.energycoop.sourceclient;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * This class holds the Auth0 Management API token.
 * It is a singleton bean, meaning there will only be one instance of this class in the application context.
 * The token can be set and retrieved as needed.
 */
@Data
@Component
@SessionScope
public class Auth0ManagementApiToken {
    private String accessToken;
}
