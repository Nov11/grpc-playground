package org.example.helloworld.common;

import org.example.helloworld.grpc.SyncClient;
import org.slf4j.Logger;

import java.util.Scanner;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

public class HandTriggerRequester {
    private static final Logger logger = getLogger(HandTriggerRequester.class);

    public static void trigger(Runnable runnable) {
        Scanner scanner = new Scanner(System.in);
        logger.info("enter something");
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equals("quit")) {
                break;
            }
            logger.info("issue requests");
            runnable.run();
            logger.info("received all replies");
            logger.info("enter something");
        }
    }
}
