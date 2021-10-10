package com.jaxel.awsclientsdemo.message.api.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
@AllArgsConstructor
public class SqsHealthCheck extends AbstractHealthIndicator {

    private SqsAsyncClient sqsClient;
    private SqsProperties sqsProperties;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            log.info("Checking health for SQS topic");
            Boolean queueExists = sqsClient.listQueues()
                    .thenApply(l -> l.queueUrls().contains(sqsProperties.getUrl()))
                    .get(3, SECONDS);
            if (queueExists) {
                builder.up().withDetail("sqsClient", "Queue exists");
            } else {
                builder.down().withDetail("sqsClient", String.format("SQS queue [%s] not found", sqsProperties.getUrl()));
            }
        } catch (AwsServiceException | SdkClientException e) {
            log.error("Error during health check: {}", e.toString());
            builder.down().withException(e);
        }
    }
}
