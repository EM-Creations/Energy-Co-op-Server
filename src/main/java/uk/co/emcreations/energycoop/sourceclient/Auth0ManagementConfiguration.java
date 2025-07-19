package uk.co.emcreations.energycoop.sourceclient;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class Auth0ManagementConfiguration {
    @Autowired
    private final Auth0ManagementApiToken auth0ManagementApiToken;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("authorization", "Bearer " + auth0ManagementApiToken.getAccessToken());
        };
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
