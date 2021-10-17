package org.example.helloworld.common;

import org.example.helloworld.HelloRequest;

public class ClientRequestBuilder {
    public static HelloRequest makeRequest(int i) {
        return HelloRequest.newBuilder()
                .setName("name" + i)
                .setBuildTimeStamp(System.currentTimeMillis())
                .setId(i)
                .build();
    }
}
