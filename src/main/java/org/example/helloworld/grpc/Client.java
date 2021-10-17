package org.example.helloworld.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.example.helloworld.GreeterGrpc;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;
import org.example.helloworld.common.ClientRequestBuilder;
import org.example.helloworld.common.ClientResponseProcessor;
import org.example.helloworld.common.HandTriggerRequester;
import org.slf4j.Logger;

import java.util.Scanner;
import java.util.concurrent.*;

import static org.slf4j.LoggerFactory.getLogger;

public class Client implements AutoCloseable {
    private static final Logger logger = getLogger(Client.class);

    @Override
    public void close() throws Exception {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    private static class Ob<T> extends CompletableFuture<T> implements io.grpc.stub.StreamObserver<T> {

        @Override
        public void onNext(T t) {
            this.complete(t);
        }

        @Override
        public void onError(Throwable throwable) {
            this.completeExceptionally(throwable);
        }

        @Override
        public void onCompleted() {

        }
    }

    private final GreeterGrpc.GreeterStub stub;
    private final ManagedChannel channel;

    public Client() {
        String target = "localhost:9999";
//        String target = "localhost:8081";
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .executor(Executors.newFixedThreadPool(4))
//                .offloadExecutor(Executors.newFixedThreadPool(10))
                .build();
//        channel = NettyChannelBuilder.forAddress("localhost", 9999).usePlaintext().executor(Executors.newFixedThreadPool(4)).build();
        stub = GreeterGrpc.newStub(channel);
    }

    public void parallelRequest(int parallelism) {
        CompletableFuture<?>[] all = new CompletableFuture[parallelism];
        for (int i = 1; i <= parallelism; i++) {

            Ob<HelloReply> ob = new Ob<>();
            HelloRequest request = ClientRequestBuilder.makeRequest(i);
            stub.sayHello(request, ob);
            all[i - 1] = ob.thenAccept(ClientResponseProcessor::process);
        }

        try {
            CompletableFuture.allOf(all).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        HandTriggerRequester.trigger(() -> client.parallelRequest(40));
    }
}
