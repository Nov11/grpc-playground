package org.example.helloworld.common;

import org.example.helloworld.HelloRequest;

public class RequestMaker {
    public static HelloRequest makeRequest(int i) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName("name" + i)
                .setBuildTimeStamp(System.currentTimeMillis())
                .setId(i)
                .build();
        return request;
    }
}
