package uk.co.emcreations.energycoop.service;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import uk.co.emcreations.energycoop.model.User;

public interface UserService {
    User getUserFromPrincipal(final OidcUser principal);
}