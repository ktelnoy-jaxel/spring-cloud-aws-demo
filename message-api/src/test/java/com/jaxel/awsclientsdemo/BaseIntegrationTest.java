package com.jaxel.awsclientsdemo;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

public abstract class BaseIntegrationTest {

//    public static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName
//            .parse("localstack/localstack:0.12.17"))
//            .withServices(SQS);
//
//    static {
//        localStackContainer.start();
//    }
}
