package org.example.helloworld.common;

import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;

public class ServerRequestProcessor {
    public static HelloReply process(HelloRequest request) {
        long enterTS = System.currentTimeMillis();
        return HelloReply.newBuilder()
                .setMessage("Hello " + request.getName())
                .setId(request.getId())
                .setNetworkDelay(enterTS - request.getBuildTimeStamp())
                .setProcessDuration(System.currentTimeMillis() - enterTS)
                .setReplayTimeStamp(System.currentTimeMillis())
                .build();
    }
}
