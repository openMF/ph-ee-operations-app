package org.apache.fineract.api.interceptor;

import org.apache.fineract.core.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternalErrorInterceptor {

    @Autowired
    KafkaService kafkaService;

    public <T> T intercept(Callback<T> callback){
        try {
            return callback.call();
        } catch (Exception e) {
            kafkaService.publishErrorMessage(e.getMessage());
            return callback.call();
        }
    }
}
