package com.skax.aiplatform.config;

import com.shinhan.convergence.GwCvgAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gadget on 2025. 10. 16..
 **/
@Configuration
public class SwingConfig {
    @Value("${gw.url}")
    private String swingBaseUrl;
    @Value("${gw.clientId}")
    private String swingClientId;
    @Value("${gw.clientSecret}")
    private String swingClientSecret;

    @Bean
    public GwCvgAgent gwCvgAgent() {
        return new GwCvgAgent(swingBaseUrl, swingClientId, swingClientSecret);
    }
}
