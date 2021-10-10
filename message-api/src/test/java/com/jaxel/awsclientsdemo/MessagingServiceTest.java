package com.jaxel.awsclientsdemo;

import cloud.localstack.awssdkv2.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaxel.awsclientsdemo.message.api.Message;
import com.jaxel.awsclientsdemo.message.api.MessagingService;
import com.jaxel.awsclientsdemo.message.api.config.SqsProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import static cloud.localstack.ServiceName.SQS;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(LocalstackDockerExtension.class)
// platform specified due to the error when running on Apple's silicon
// https://docs.docker.com/desktop/mac/apple-silicon/
// todo: check if 0.12.12 tag fixes DynamoDB error on M1
// https://github.com/localstack/localstack/issues/4625#issuecomment-927872700
@LocalstackDockerProperties(services = SQS, platform = "linux/amd64", imageTag ="0.12.18")
class MessagingServiceTest extends BaseIntegrationTest {

    public static final String TEST_QUEUE = "testQueue";
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    MessagingService messagingService;
    SqsAsyncClient clientSQSAsyncV2;
    String testQueueUrl;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        this.clientSQSAsyncV2 = TestUtils.getClientSQSAsyncV2();
        CreateQueueResponse testQueue = clientSQSAsyncV2
                .createQueue(b -> b.queueName(TEST_QUEUE)).get();
        this.testQueueUrl = testQueue.queueUrl();
        this.messagingService = new MessagingService(clientSQSAsyncV2, new SqsProperties(TEST_QUEUE, testQueueUrl), mapper);
    }

    @SneakyThrows
    @Test
    void sendSuccessfully() {
        Message msg = new Message("uuid", "recipientId", "hi!", null);

        Message sentMsg = messagingService.send(msg);

        assertNotNull(sentMsg.getTimestamp());
        ReceiveMessageResponse sqsMsgs = clientSQSAsyncV2.receiveMessage(b -> b.queueUrl(testQueueUrl)).get();
        assertTrue(sqsMsgs.hasMessages());
        Message parsedMsg = mapper.readValue(sqsMsgs.messages().get(0).body(), Message.class);
        assertEquals(sentMsg, parsedMsg);

    }

    @SneakyThrows
    @Test
    void sendUnsuccessfully() {
        Message msg = new Message("123", "wrer", "hi!", null);

        Message sentMsg = messagingService.send(msg);

        assertNotNull(sentMsg.getTimestamp());
    }

    @AfterEach
    void tearDown() {
        clientSQSAsyncV2.purgeQueue(b -> b.queueUrl(testQueueUrl));
    }
}