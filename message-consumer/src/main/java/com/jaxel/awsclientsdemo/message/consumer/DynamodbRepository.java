package com.jaxel.awsclientsdemo.message.consumer;

import com.jaxel.awsclientsdemo.message.consumer.model.AddMembersToGroupRequest;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DynamodbRepository {

    private String targetTableName;
    private DynamoDbAsyncClient dynamoDbClient;
    private Integer batchSize;

    // https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.OperatorsAndFunctions.html
    public void updateMemberChatId(AddMembersToGroupRequest addMembersToGroupRequest) {
        splitToBatches(addMembersToGroupRequest.getMemberIds())
                .forEach(batch -> dynamoDbClient
                        .updateItem(builder -> builder.tableName(targetTableName)
                                .conditionExpression(String.format("PersonId IN (%s)", batch))
                                .updateExpression("ADD ChatIds :m")
                                .expressionAttributeValues(Map
                                        .of(":m", AttributeValue.builder().ss(addMembersToGroupRequest.getChatId()).build()))));
    }

    protected List<String> splitToBatches(List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return Collections.emptyList();
        }
        int idsSize = memberIds.size();
        int batches = idsSize % batchSize == 0 ? idsSize / batchSize : idsSize / batchSize + 1;
        List<String> memberIdBatches = new ArrayList<>();
        int count = 0;
        do {
            List<String> memberIdBatch = memberIds.subList(count * batchSize, Math.min((count + 1) * batchSize, idsSize));
            memberIdBatches.add(memberIdBatch.stream().collect(Collectors.joining(",")));
            count++;
        } while (count < batches);
        return memberIdBatches;
    }
}
