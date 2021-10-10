package com.jaxel.awsclientsdemo.message.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaxel.awsclientsdemo.message.api.config.SqsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Configuration
@EnableConfigurationProperties
public class MessagingConfig {

    @Bean
    public SqsAsyncClient getClient(SqsProperties properties) {
        SqsAsyncClient sqsClient = SqsAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        log.info("Creating SQS Client");
        return sqsClient;
    }

    @Bean
    public MessagingService messagingService(SqsAsyncClient sqsAsyncClient, SqsProperties sqsProperties, ObjectMapper sqsObjectMapper) {
        return new MessagingService(sqsAsyncClient, sqsProperties, sqsObjectMapper);
    }

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
