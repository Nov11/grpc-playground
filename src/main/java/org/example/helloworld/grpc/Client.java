package org.example.helloworld.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.helloworld.GreeterGrpc;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;
import org.example.helloworld.common.RequestMaker;
import org.example.helloworld.common.ResponseProcessor;
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
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .executor(Executors.newFixedThreadPool(10))
//                .offloadExecutor(Executors.newFixedThreadPool(10))
                .build();
        stub = GreeterGrpc.newStub(channel);
    }

    public void parallelRequest(int parallelism) throws ExecutionException, InterruptedException {
        CompletableFuture<Void>[] all = new CompletableFuture[parallelism];
        for (int i = 1; i <= parallelism; i++) {

            Ob<HelloReply> ob = new Ob<>();
            HelloRequest request = RequestMaker.makeRequest(i);
            stub.sayHello(request, ob);
            all[i - 1] = ob.thenAccept(ResponseProcessor::process);
        }

        CompletableFuture.allOf(all).get();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        logger.info("enter something");
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equals("quit")) {
                break;
            }
            logger.info("issue requests");
            client.parallelRequest(40);
            logger.info("received all replies");
            logger.info("enter something");
        }
    }
}
