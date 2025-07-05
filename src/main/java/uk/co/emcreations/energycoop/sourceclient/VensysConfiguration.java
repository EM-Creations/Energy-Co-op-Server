package uk.co.emcreations.energycoop.sourceclient;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class VensysConfiguration {
    @Value("${external.api.graig-fatha.tid}")
    String tid;

    @Value("${external.api.graig-fatha.key}")
    String key;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("ApiKey", key);
            requestTemplate.header("TID", tid);
        };
    }
}
