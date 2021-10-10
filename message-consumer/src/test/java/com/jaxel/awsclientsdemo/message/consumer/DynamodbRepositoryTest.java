package com.jaxel.awsclientsdemo.message.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class DynamodbRepositoryTest {

    @Mock
    DynamoDbAsyncClient dynamoDbClient;
    DynamodbRepository dynamodbRepository;

    @BeforeEach
    void setUp() {
        this.dynamodbRepository = new DynamodbRepository("", dynamoDbClient, 3);
    }

    @Test
    void updateMemberChatId() {
    }

    @ParameterizedTest(name = "ids={1}")
    @MethodSource("groupedIdsProvider")
    void splitToBatches(List<String> expectedGroupedIds, List<String> ids) {
        List<String> groupedIds = dynamodbRepository.splitToBatches(ids);

        assertIterableEquals(expectedGroupedIds, groupedIds);
    }

    static Stream<Arguments> groupedIdsProvider() {
        return Stream.of(
                arguments(emptyList(), null),
                arguments(emptyList(), emptyList()),
                arguments(asList("a"), asList("a")),
                arguments(asList("ab,cd,ef"), asList("ab", "cd", "ef")),
                arguments(asList("ab,cd,ef", "gh"), asList("ab", "cd", "ef", "gh")),
                arguments(asList("ab,cd,ef", "gh,ij,kl", "mn"), asList("ab", "cd", "ef", "gh", "ij", "kl", "mn"))
        );
    }
}