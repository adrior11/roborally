package com.github.adrior.roborally.test.networking;

import com.github.adrior.roborally.server.Server;
import com.github.adrior.roborally.test.util.TestUtils;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerIntegrationTest {
    private Server server;

    @BeforeEach
    void testServerStart() throws RuntimeException {
        int testPort = 6008;
        server = new Server(testPort, 2);

        TestUtils.setUpServer(server);
        TestUtils.awaitServerStart(server);

        assertTrue(server.isRunning(), "Server should be running");
    }


    @Test
    void testServerShutdown() throws RuntimeException {
        server.shutdown();
        assertFalse(server.isRunning(), "Server should not be running");
    }
}

