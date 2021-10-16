package org.example.helloworld.jetty;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.helloworld.HelloReply;
import org.example.helloworld.common.RequestMaker;
import org.example.helloworld.common.ResponseProcessor;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class RESTClient {
    private static final Logger logger = getLogger(RESTClient.class);

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();


        for (int i = 0; i < 100; i++) {
            HttpPost httpPost = new HttpPost("http://localhost:8090");
            httpPost.setEntity(new ByteArrayEntity(RequestMaker.makeRequest(i).toByteArray()));
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                byte[] bytes = IOUtils.readFully(entity.getContent(), (int) entity.getContentLength());
                HelloReply reply = HelloReply.parseFrom(bytes);
                ResponseProcessor.process(reply);
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity);
            }
        }
    }
}
