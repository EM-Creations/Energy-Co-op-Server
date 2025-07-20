package uk.co.emcreations.energycoop.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.service.AuthService;
import uk.co.emcreations.energycoop.service.UserService;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final AuthService authService;

    @Override
    public User getUserFromPrincipal(final OidcUser principal) {
        log.info("getUser() called");

        Map<String, Object> claims = principal.getClaims();
        String userId = claims.get("sub").toString();
        String name = claims.get("name").toString();
        String picture = claims.get("picture").toString();
        String email = claims.get("email").toString();

        return User.builder()
                .userId(userId)
                .name(name)
                .imageURL(picture)
                .email(email)
                .build();
    }

}