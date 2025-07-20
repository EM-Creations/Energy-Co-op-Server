package uk.co.emcreations.energycoop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.dto.ManagementAccessToken;
import uk.co.emcreations.energycoop.sourceclient.Auth0Client;
import uk.co.emcreations.energycoop.sourceclient.Auth0ManagementApiToken;
import uk.co.emcreations.energycoop.sourceclient.Auth0ManagementClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class Auth0Service implements AuthService {
    @Autowired
    private final Auth0Client auth0Client;

    @Autowired
    private final Auth0ManagementClient auth0ManagementClient;

    @Autowired
    private final Auth0ManagementApiToken auth0ManagementApiToken;

    @Override
    public String getPermissionsForUser(final String userId) {
        log.info("getPermissionsForUser() called");

        setManagementAccessToken();

        String permissions = auth0ManagementClient.getPermissionsForUser(userId);

        log.info("Permissions for user {}: {}", userId, permissions);

        return permissions;
    }

    @Override
    public void setManagementAccessToken() {
        ManagementAccessToken accessToken = auth0Client.getAccessToken();
        auth0ManagementApiToken.setAccessToken(accessToken.access_token());
    }

}