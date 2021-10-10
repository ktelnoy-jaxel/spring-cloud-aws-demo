package com.jaxel.awsclientsdemo.message.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMembersToGroupRequest {
    private String chatId;
    private String receiptHandle;
    private List<String> memberIds;
}
