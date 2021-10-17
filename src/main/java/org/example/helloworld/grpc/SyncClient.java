package org.example.helloworld.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.helloworld.GreeterGrpc;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;
import org.example.helloworld.common.ClientRequestBuilder;
import org.example.helloworld.common.ClientResponseProcessor;
import org.example.helloworld.common.HandTriggerRequester;
import org.slf4j.Logger;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class SyncClient implements AutoCloseable {
    private static final Logger logger = getLogger(SyncClient.class);

    @Override
    public void close() throws Exception {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    private final GreeterGrpc.GreeterBlockingStub stub;
    private final ManagedChannel channel;

    public SyncClient() {
        String target = "localhost:9999";
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        stub = GreeterGrpc.newBlockingStub(channel);
    }

    public void makeRequests(int parallelism) {
        for (int i = 1; i <= parallelism; i++) {
            HelloRequest request = ClientRequestBuilder.makeRequest(i);
            HelloReply reply = stub.sayHello(request);
            ClientResponseProcessor.process(reply);
        }
    }

    public static void main(String[] args) {
        SyncClient client = new SyncClient();
        HandTriggerRequester.trigger(() -> client.makeRequests(40));
    }
}
