package com.jaxel.awsclientsdemo.message.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.awsclientsdemo.message.consumer.model.AddMembersToGroupRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class SqsConsumer {

    private final String QUEUE_URL;
    private final Integer BATCH_SIZE;
    private SqsAsyncClient sqsClient;
    private ObjectMapper mapper;

    @SneakyThrows
    public Collection<AddMembersToGroupRequest> poll() {
        return sqsClient.receiveMessage(b -> b
                        .queueUrl(QUEUE_URL)
                        .maxNumberOfMessages(BATCH_SIZE)
                        .waitTimeSeconds(1))
                .thenApply(response -> response.messages().stream()
                        .map(this::mapToAddMembersToGroupRQ)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .get(1100, TimeUnit.MILLISECONDS);
    }

    private AddMembersToGroupRequest mapToAddMembersToGroupRQ(Message m) {
        try {
            AddMembersToGroupRequest addMembersToGroupRequest = mapper.readValue(m.body(), AddMembersToGroupRequest.class);
            addMembersToGroupRequest.setReceiptHandle(m.receiptHandle());
            return addMembersToGroupRequest;
        } catch (JsonProcessingException e) {
            log.error("Couldn't parse message from SQS: [{}]", m.body());
        }
        return null;
    }

    public void removeProcessed(AddMembersToGroupRequest message) {
        sqsClient.deleteMessage(d -> d.queueUrl(QUEUE_URL)
                        .receiptHandle(message.getReceiptHandle()))
                .whenCompleteAsync((m, e) -> {
                    if (e != null) {
                        log.error("Couldn't delete processed message from SQS");
                    }
                });
    }

    public void removeProcessedBatch(Collection<AddMembersToGroupRequest> messages) {
        sqsClient.deleteMessageBatch(b -> b.queueUrl(QUEUE_URL)
                        .entries(createDeleteBatchRequest(messages)))
                .whenCompleteAsync((m, e) -> {
                    if (e != null) {
                        log.error("Couldn't delete processed message from SQS");
                    }
                });
    }

    private List<DeleteMessageBatchRequestEntry> createDeleteBatchRequest(Collection<AddMembersToGroupRequest> messages) {
        return messages.stream()
                .map(m -> DeleteMessageBatchRequestEntry.builder()
                        .receiptHandle(m.getReceiptHandle()).build())
                .collect(Collectors.toList());
    }
}
