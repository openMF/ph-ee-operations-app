package org.apache.fineract.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaTestConsumer {

    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @KafkaListener(topics = "error-codes")
    public void consume(String message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message));
    }
}
