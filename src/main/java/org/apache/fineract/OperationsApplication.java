package org.apache.fineract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;


public class OperationsApplication {
    private static Logger logger = LoggerFactory.getLogger(OperationsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
