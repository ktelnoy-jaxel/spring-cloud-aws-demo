package com.jaxel.awsclientsdemo.message.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.awsclientsdemo.message.consumer.model.AddMembersToGroupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqsConsumerTest {

    public static final String ADD_MEMBERS_TO_GROUP_RQ_TEMPL = "{\"chatId\":\"%s\", \"memberIds\":%s}";
    @Mock
    SqsAsyncClient sqsAsyncClient;
    ObjectMapper mapper = new ObjectMapper();
    SqsConsumer sqsConsumer;

    @BeforeEach
    void setUp() {
        this.sqsConsumer = new SqsConsumer("", 10, sqsAsyncClient, mapper);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 10})
    void pollN(int N) {
        List<String> memberIds = List.of("a", "b", "c");
        mockNSQSMessages(N, memberIds);

        Collection<AddMembersToGroupRequest> batch = sqsConsumer.poll();

        assertEquals(N, batch.size());
        batch.forEach(m -> {
            assertNotNull(m.getReceiptHandle());
            assertNotNull(m.getChatId());
            assertEquals(memberIds, m.getMemberIds());
        });
    }

    @Test
    void pollUnappeasable() {
        when(sqsAsyncClient.receiveMessage(isA(Consumer.class)))
                .thenReturn(supplyAsync(() -> ReceiveMessageResponse.builder()
                        .messages(c -> c.body("hey")).build()));

        Collection<AddMembersToGroupRequest> poll = sqsConsumer.poll();

        assertEquals(0, poll.size());
    }

    @Test
    void removeProcessed() {
        when(sqsAsyncClient.deleteMessage(isA(Consumer.class)))
                .thenReturn(supplyAsync(() -> DeleteMessageResponse.builder().build()));

        sqsConsumer.removeProcessed(new AddMembersToGroupRequest(null, "receiptHandle", emptyList()));

        verify(sqsAsyncClient).deleteMessage(isA(Consumer.class));
    }

    @Test
    void removeExceptionally() {
        when(sqsAsyncClient.deleteMessage(isA(Consumer.class)))
                .thenReturn(supplyAsync(() -> {
                    throw new RuntimeException();
                }));

        assertDoesNotThrow(() -> sqsConsumer.removeProcessed(new AddMembersToGroupRequest(null, "receiptHandle", emptyList())));

        verify(sqsAsyncClient).deleteMessage(isA(Consumer.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 10})
    void removeProcessedBatch(int N) {
        when(sqsAsyncClient.deleteMessageBatch(isA(Consumer.class)))
                .thenReturn(supplyAsync(() -> DeleteMessageBatchResponse.builder().build()));
        List<AddMembersToGroupRequest> processedBatch = IntStream.range(0, N)
                .mapToObj(number -> new AddMembersToGroupRequest(null, String.valueOf(number), emptyList())).collect(Collectors.toList());

        sqsConsumer.removeProcessedBatch(processedBatch);

        verify(sqsAsyncClient).deleteMessageBatch(isA(Consumer.class));
    }

    @Test
    void removeProcessedBatchExceptionally() {
        when(sqsAsyncClient.deleteMessageBatch(isA(Consumer.class)))
                .thenReturn(supplyAsync(() -> {
                    throw new RuntimeException();
                }));

        assertDoesNotThrow(() -> sqsConsumer.removeProcessedBatch(List.of(new AddMembersToGroupRequest(null, "receiptHandle", emptyList()))));

        verify(sqsAsyncClient).deleteMessageBatch(isA(Consumer.class));
    }


    private void mockNSQSMessages(int N, List<String> memberIds) {
        when(sqsAsyncClient.receiveMessage(isA(Consumer.class)))
                .thenReturn(supplyAsync(() -> ReceiveMessageResponse.builder()
                        .messages(IntStream.range(0, N)
                                .mapToObj(number -> createSQSMessage(String.valueOf(number), memberIds))
                                .collect(Collectors.toList())).build()));
    }

    private Message createSQSMessage(String chatId, List<String> memberIds) {
        List<String> members = memberIds.stream()
                .map(m -> String.format("\"%s\"", m))
                .collect(Collectors.toList());
        return Message.builder()
                .body(String.format(ADD_MEMBERS_TO_GROUP_RQ_TEMPL, chatId, members))
                .receiptHandle("receiptHandle")
                .build();
    }
}