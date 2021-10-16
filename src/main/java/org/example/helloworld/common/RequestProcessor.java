package org.example.helloworld.common;

import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;

public class RequestProcessor {
    public static HelloReply process(HelloRequest request) {
        long enterTS = System.currentTimeMillis();
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Hello " + request.getName())
                .setId(request.getId())
                .setNetworkDelay(enterTS - request.getBuildTimeStamp())
                .setProcessDuration(System.currentTimeMillis() - enterTS)
                .setReplayTimeStamp(System.currentTimeMillis())
                .build();
        return reply;
    }
}
