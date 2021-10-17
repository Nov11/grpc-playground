package org.example.helloworld.common;

import com.google.protobuf.ByteString;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;

public class ServerRequestProcessor {
    private static final ByteString largeData = makeLargeData(); // might be used as large payload to show how grpc process it in http2 protocol

    private static ByteString makeLargeData() {
        byte[] ret = new byte[1024000];
        for (int i = 0; i < 1024000; i++) {
            ret[i] = (byte) i;
        }
        return ByteString.copyFrom(ret);
    }

    public static HelloReply process(HelloRequest request) {
        long enterTS = System.currentTimeMillis();
        return HelloReply.newBuilder()
                .setMessage("Hello " + request.getName())
                .setId(request.getId())
                .setNetworkDelay(enterTS - request.getBuildTimeStamp())
                .setProcessDuration(System.currentTimeMillis() - enterTS)
                .setReplayTimeStamp(System.currentTimeMillis())
//                .setLargeData(largeData)
                .build();
    }
}
