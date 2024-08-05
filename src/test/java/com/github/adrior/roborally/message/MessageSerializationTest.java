package com.github.adrior.roborally.test.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.MessageBuilder;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.utility.GsonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageSerializationTest {

    @Test
    void testMessageSerialization() {
        Message message = new MessageBuilder()
                .setMessageType(Message.MessageType.WELCOME)
                .addContent("clientID", 42)
                .build();

        Gson gson = GsonUtil.getGson();

        String jsonOutput = gson.toJson(message);

        // Expected JSON structure
        String expectedJson = """
        {
            "messageType": "Welcome",
            "messageBody": {
                "clientID": 42
            }
        }
        """;

        System.out.println("Output: " + jsonOutput);
        System.out.println("Expected: " + expectedJson);

        // Parse JSON strings into JSONObject
        JsonObject expectedJsonObject = JsonParser.parseString(expectedJson).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
        assertEquals(expectedJsonObject, actualJsonObject);
    }


    @Test
    void testMessageDeserialization() {
        String jsonInput = """
        {
            "messageType": "HelloClient",
            "messageBody": {
                "protocol": "Version 0.1"
            }
        }
        """;

        Gson gson = GsonUtil.getGson();

        Message message = gson.fromJson(jsonInput, Message.class);

        assertEquals(Message.MessageType.HELLO_CLIENT, message.messageType());
        assertEquals("Version 0.1", message.messageBody().get("protocol"));
    }


    @Test
    void testStartingPointTakenSerialization() {
        Message message = new MessageBuilder()
                .setMessageType(Message.MessageType.STARTING_POINT_TAKEN)
                .addContent("x", 4)
                .addContent("y", 2)
                .addContent("clientID", 42)
                .build();

        Gson gson = GsonUtil.getGson();

        String jsonOutput = gson.toJson(message);

        // Expected JSON structure
        String expectedJson = """
        {
            "messageType": "StartingPointTaken",
            "messageBody": {
                "x": 4,
                "y": 2,
                "clientID": 42
            }
        }
        """;

        System.out.println("Output: " + jsonOutput);
        System.out.println("Expected: " + expectedJson);

        // Parse JSON strings into JSONObject
        JsonObject expectedJsonObject = JsonParser.parseString(expectedJson).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
        assertEquals(expectedJsonObject, actualJsonObject);
    }

    @Test
    void testHelloClientSerialization() {
        Message message = PredefinedServerMessages.helloClient("Version 0.1");

        Gson gson = GsonUtil.getGson();

        String jsonOutput = gson.toJson(message);

        // Expected JSON structure
        String expectedJson = """
        {
            "messageType": "HelloClient",
            "messageBody": {
                "protocol": "Version 0.1"
            }
        }
        """;

        System.out.println("Output: " + jsonOutput);
        System.out.println("Expected: " + expectedJson);

        // Parse JSON strings into JSONObject
        JsonObject expectedJsonObject = JsonParser.parseString(expectedJson).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
        assertEquals(expectedJsonObject, actualJsonObject);
    }

    @Test
    void testWelcomeSerializationAndDeserialization() {
        Message message = PredefinedServerMessages.welcome(2);

        Gson gson = GsonUtil.getGson();

        String jsonOutput = gson.toJson(message);

        String expectedJson = """
        {
            "messageType": "Welcome",
            "messageBody": {
                "clientID": 2
            }
        }
        """;

        System.out.println("Output: " + jsonOutput);
        System.out.println("Expected: " + expectedJson);

        // Parse JSON strings into JSONObject
        JsonObject expectedJsonObject = JsonParser.parseString(expectedJson).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
        assertEquals(expectedJsonObject, actualJsonObject);

        Message messageDeserialized = gson.fromJson(jsonOutput, Message.class);
        assertEquals(Message.MessageType.WELCOME, messageDeserialized.messageType());
        assertEquals(2, messageDeserialized.messageBody().get("clientID"));
    }
}
