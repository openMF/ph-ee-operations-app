package org.apache.fineract.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static String topic = "error-codes";

    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public void publishErrorMessage(String message) {
        publish(topic, message);
    }

    public void publish(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            logger.info(String.format("#### -> Error encountered in kafka  -> %s", e.getMessage()));
        }
    }

}

