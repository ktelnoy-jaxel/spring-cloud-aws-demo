package com.jaxel.awsclientsdemo.message.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String uuid;
    private String recipientId;
    private String body;
    private Instant timestamp;
}
