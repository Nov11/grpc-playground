package org.example.helloworld.common;

import org.example.helloworld.HelloReply;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ResponseProcessor {
    private static final Logger logger = getLogger(ResponseProcessor.class);

    public static void process(HelloReply r) {
        logger.info("id :{}, network delay(client->server): {} process duration: {} network delay(server->client) {}",
                r.getId(),
                r.getNetworkDelay(),
                r.getProcessDuration(),
                System.currentTimeMillis() - r.getReplayTimeStamp());
    }
}
