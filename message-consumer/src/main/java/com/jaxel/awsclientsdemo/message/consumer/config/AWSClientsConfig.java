package com.jaxel.awsclientsdemo.message.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Configuration
@EnableConfigurationProperties
public class AWSClientsConfig {

    @Bean
    public SqsAsyncClient getSQSClient(SqsProperties properties) {
        SqsAsyncClient sqsClient = SqsAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        log.info("Creating SQS Client");
        return sqsClient;
    }

//    @Bean
//    public DynamoDbAsyncClient getDynamoDBClient(DynamoDBProperties properties) {
//        DynamoDbAsyncClient dynamoDbAsyncClient = DynamoDbAsyncClient.builder()
//                .credentialsProvider(ProfileCredentialsProvider.create())
//                .build();
//        log.info("Creating DynamoDB Client");
//        return dynamoDbAsyncClient;
//    }

    @Bean
    @ConfigurationProperties(prefix = "sqs.topic")
    public SqsProperties messageProperties() {
        return new SqsProperties();
    }

    @Bean
    public ObjectMapper sqsObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new JavaTimeModule());
        return objectMapper;
    }
}
