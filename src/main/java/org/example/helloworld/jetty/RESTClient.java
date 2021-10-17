package org.example.helloworld.jetty;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.helloworld.HelloReply;
import org.example.helloworld.common.ClientRequestBuilder;
import org.example.helloworld.common.ClientResponseProcessor;
import org.example.helloworld.common.HandTriggerRequester;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class RESTClient {
    private static final Logger logger = getLogger(RESTClient.class);

    private final CloseableHttpClient httpclient;

    public RESTClient() {
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .build();
        httpclient = HttpClients.custom()
                .setDefaultSocketConfig(socketConfig)
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(100)
                .build();
    }

    public void makeRequests(int count) {
        for (int i = 0; i < count; i++) {
            HttpPost httpPost = new HttpPost("http://localhost:8090");
            httpPost.setEntity(new ByteArrayEntity(ClientRequestBuilder.makeRequest(i).toByteArray()));
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                byte[] bytes = IOUtils.readFully(entity.getContent(), (int) entity.getContentLength());
                HelloReply reply = HelloReply.parseFrom(bytes);
                ClientResponseProcessor.process(reply);
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity);
            } catch (Exception e) {
                logger.error("ex", e);
            }
        }
    }

    public static void main(String[] args) {
        RESTClient restClient = new RESTClient();
        HandTriggerRequester.trigger(() -> restClient.makeRequests(40));
    }
}
