package org.example.helloworld.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.example.helloworld.GreeterGrpc;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;
import org.example.helloworld.common.RequestProcessor;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class LocalServer {
    private static final Logger logger = getLogger(LocalServer.class);

    private static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = RequestProcessor.process(request);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            logger.info("sayHello replied");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9999)
                .addService(new GreeterImpl())
                .build()
                .start();

        logger.info("server started");
        server.awaitTermination();
    }
}
