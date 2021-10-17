package org.example.helloworld.jetty;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;
import org.example.helloworld.common.ClientRequestBuilder;
import org.example.helloworld.common.ClientResponseProcessor;
import org.example.helloworld.common.HandTriggerRequester;
import org.example.helloworld.grpc.Client;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.slf4j.LoggerFactory.getLogger;

public class RESTAsyncClient {
    private static final Logger logger = getLogger(RESTAsyncClient.class);

    private static class CB<T> extends CompletableFuture<T> implements FutureCallback<T> {

        @Override
        public void completed(T t) {
            this.complete(t);
        }

        @Override
        public void failed(Exception e) {
            this.completeExceptionally(e);
        }

        @Override
        public void cancelled() {
            this.cancel(true);
        }
    }

    private final CloseableHttpAsyncClient httpAsyncClient;

    private final ExecutorService executorService;

    public RESTAsyncClient() {
//        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setTcpNoDelay(true).build();
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().build();
        httpAsyncClient = HttpAsyncClients.custom()
                .setMaxConnTotal(100).setMaxConnPerRoute(100)
                .setDefaultIOReactorConfig(ioReactorConfig)
                .build();
        httpAsyncClient.start();
        executorService = Executors.newFixedThreadPool(4);
    }

    public void parallelRequest(int parallelism) {
        CompletableFuture<?>[] all = new CompletableFuture[parallelism];
        for (int i = 1; i <= parallelism; i++) {
            HttpPost httpPost = new HttpPost("http://localhost:8090");
            httpPost.setEntity(new ByteArrayEntity(ClientRequestBuilder.makeRequest(i).toByteArray()));
            CB<HttpResponse> cb = new CB<>();
            httpAsyncClient.execute(httpPost, cb);
            all[i - 1] = cb.thenApplyAsync(httpResponse -> {
                try {
                    HttpEntity entity = httpResponse.getEntity();
                    byte[] bytes = IOUtils.readFully(entity.getContent(), (int) entity.getContentLength());
                    HelloReply reply = HelloReply.parseFrom(bytes);
                    ClientResponseProcessor.process(reply);
                    // do something useful with the response body
                    // and ensure it is fully consumed
                    EntityUtils.consume(entity);
                    return reply;
                } catch (Exception e) {
                    logger.info("error", e);
                }
                return null;
            }, executorService).thenAccept(ClientResponseProcessor::process);
        }

        try {
            CompletableFuture.allOf(all).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RESTAsyncClient restAsyncClient = new RESTAsyncClient();
        HandTriggerRequester.trigger(() -> restAsyncClient.parallelRequest(40));
    }
}
