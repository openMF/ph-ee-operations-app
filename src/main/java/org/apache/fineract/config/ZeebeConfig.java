package org.apache.fineract.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${zeebe.client.broker.gatewayAddress}")
    String gatewayAddress;

    @Value("${zeebe.client.security.plaintext}")
    boolean plaintext;


    @Bean
    public ZeebeClient zeebeClient() {
        logger.info("setting up Zeebe client with gateway address: {} with plaintext: {}", gatewayAddress, plaintext);

        ZeebeClientBuilder builder = ZeebeClient.newClientBuilder().gatewayAddress(gatewayAddress);
        if (plaintext) {
            builder.usePlaintext();
        }

        return builder.build();
    }
}
