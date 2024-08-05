package com.github.adrior.roborally.test.networking;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.server.Server;
import com.github.adrior.roborally.test.util.TestUtils;
import lombok.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerCommunicationFacadeTest {
    private static Server server;
    private static AtomicBoolean broadcastReceived;
    private static AtomicBoolean logReceived;
    private static final String testLogMessage = "TEST: log message";

    @BeforeAll
    static void setUp() {
        int testPort = 6009;
        broadcastReceived = new AtomicBoolean(false);
        logReceived = new AtomicBoolean(false);

        server = new Server(testPort, 2) {
            @Override
            public void broadcast(@NonNull Message message) {
                super.broadcast(message);
                broadcastReceived.set(true);
            }

            @Override
            public void log(String message) {
                super.log(message);
                if (message.contains(testLogMessage)) {
                    logReceived.set(true);
                }
            }
        };

        TestUtils.setUpServer(server);
        TestUtils.awaitServerStart(server);

        assertTrue(server.isRunning(), "Server should be running");
    }

    @Test
    void testBroadcastingAndLogging() {
        Message testMessage = PredefinedServerMessages.alive();
        ServerCommunicationFacade.broadcast(testMessage);
        ServerCommunicationFacade.log(testLogMessage);

        TestUtils.waitFor(1000);

        assertTrue(broadcastReceived.get(), "Broadcast message should be received");
        assertTrue(logReceived.get(), "Log message should be received");
    }

    @AfterAll
    static void tearDown() {
        server.shutdown();
        assertFalse(server.isRunning(), "Server should not be running");
    }
}