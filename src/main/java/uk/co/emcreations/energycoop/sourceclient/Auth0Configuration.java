package uk.co.emcreations.energycoop.sourceclient;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class Auth0Configuration {
    @Value("${okta.oauth2.mgmt-audience}")
    private String mgmtAudience;
    @Value("${okta.oauth2.client-id}")
    private String clientId;
    @Value("${okta.oauth2.client-secret}")
    private String clientSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        var body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret + "&audience=" + mgmtAudience;

        return requestTemplate -> {
            requestTemplate.body(body);
        };
    }
}
