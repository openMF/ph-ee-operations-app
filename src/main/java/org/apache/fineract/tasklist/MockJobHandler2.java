package org.apache.fineract.tasklist;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

@Component
public class MockJobHandler2 implements JobHandler {

    private static Logger logger = LoggerFactory.getLogger(MockJobHandler2.class);


    @Override
    @JobWorker(timeout = 2592000000L, type = "printVars", name = "printVars", autoComplete = false)
    public void handle(JobClient client, ActivatedJob job) {

        job.getVariablesAsMap().forEach((s, o) -> {
            logger.info(format("%s - %s", s, o));
        });

        client.newCompleteCommand(job).send().join();
    }
}
