package org.apache.fineract.tasklist;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class MockJobHandler implements JobHandler {

    private static Logger logger = LoggerFactory.getLogger(MockJobHandler.class);


    @Override
    @JobWorker(timeout = 2592000000L, type = "setVars", name = "setVars")
    public void handle(JobClient client, ActivatedJob job) {

        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        variablesAsMap.put("endToEndId", UUID.randomUUID().toString());
        variablesAsMap.put("debtorBic", "BINXHUH0");
        variablesAsMap.put("creditorBic", "OTPVHUH0");
        variablesAsMap.put("debtorName", "Fules Lajos");
        variablesAsMap.put("creditorName", "Bajszos Miklos");
        variablesAsMap.put("rtpAmount", "500000000000000 EUR");
        variablesAsMap.put("internalCorrelationId", UUID.randomUUID().toString());
        variablesAsMap.put("amount", new BigDecimal(5000));
        client.newCompleteCommand(job).variables(variablesAsMap).send().join();
    }

}
