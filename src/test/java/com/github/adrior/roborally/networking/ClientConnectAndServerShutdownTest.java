package com.github.adrior.roborally.test.networking;

import com.github.adrior.roborally.client.Client;
import com.github.adrior.roborally.server.Server;
import com.github.adrior.roborally.test.util.TestUtils;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientConnectAndServerShutdownTest {
    private Server server;
    private Client client;


    @BeforeEach
    void setUp() {
        int testPort = 6001;
        server = new Server(testPort, 2);
        client = new Client("localhost", testPort);

        TestUtils.setUpServer(server);
        TestUtils.awaitServerStart(server);

        assertTrue(server.isRunning(), "Server should be running");
    }


    @Test
    void testClientConnect() throws RuntimeException{
        client.connect();
        TestUtils.waitFor(1000);
        assertTrue(client.getIsConnected().get(), "Client should be connected after connect call");
    }


    @AfterEach
    void tearDown() throws RuntimeException {
        server.shutdown();
        TestUtils.waitFor(1000);
        assertFalse(server.isRunning(), "Server should not be running");
        assertEquals(0, server.getClients().size(), "Server should have no clients");
    }
}
