package com.jaxel.awsclientsdemo.message.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.awsclientsdemo.message.api.config.SqsProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.Instant;

@Slf4j
@AllArgsConstructor
public class MessagingService {

    private SqsAsyncClient sqsClient;
    private SqsProperties sqsProperties;
    private ObjectMapper mapper;

    @SneakyThrows
    public Message send(Message msg) {
        msg.setTimestamp(Instant.now());
        String serializedMessage = mapper.writeValueAsString(msg);
        SendMessageResponse sendMessageResponse = sqsClient.sendMessage(b -> b
                        .queueUrl(sqsProperties.getUrl())
                        .messageBody(serializedMessage))
                .get();

        log.info("Successfully sent message with id: {}", sendMessageResponse.messageId());
        return msg;
    }

}
