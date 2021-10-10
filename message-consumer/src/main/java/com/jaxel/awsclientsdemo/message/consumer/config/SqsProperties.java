package com.jaxel.awsclientsdemo.message.consumer.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqsProperties {

    private String name;
    private String url;

}
