package uk.co.emcreations.energycoop.service;

public interface AuthService {
    void setManagementAccessToken();

    String getPermissionsForUser(final String userId);
}