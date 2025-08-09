package uk.co.emcreations.energycoop.config;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * This configuration uses Okta for OAuth2 login and handles logout.
 * It allows public access to certain endpoints and secures all other requests.
 */
@Configuration
@EnableWebSecurity
@Profile("!dev")
public class SecurityConfig {
    @Value("${okta.oauth2.issuer}")
    private String issuer;
    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorize -> {
                    try {
                        authorize
                                .requestMatchers("/", "/swagger-ui/**", "/images/**", "/error", "/h2-console/**").permitAll()
                                .requestMatchers("/yesterdayPerformance", "/energyYield").hasAuthority("read:gf-api-basic")
                                // all other requests
                                .anyRequest().authenticated()
                                .and()
                                .oauth2ResourceServer().jwt();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        // Send a 401 message to the browser (w/o this, you'll see a blank page)
        Okta.configureResourceServer401ResponseBody(http);

        return http.build();
    }
}