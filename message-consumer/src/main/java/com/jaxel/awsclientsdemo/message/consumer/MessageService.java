package com.jaxel.awsclientsdemo.message.consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageService {
    private SqsConsumer sqsConsumer;
    private DynamodbRepository dynamodbRepository;

    public void process() {

    }
}
