package com.github.adrior.roborally.test.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.card.cards.DamageCard;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.utility.GsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YourCardsTest {
    private static final Gson gson = GsonUtil.getGson();
    final LinkedList<Player> players = new LinkedList<>();
    Player player1;
    Player player2;

    @BeforeEach
    public void setup() {
        // Create test players
        player1 = new Player(1, null, new CardManager(), new ProgrammingRegister(), null, null);
        player2 = new Player(2, null, new CardManager(), new ProgrammingRegister(), null, null);

        // Add players to the player list
        players.add(player1);
        players.add(player2);

        // Set the first register of the players
        player1.programmingRegister().setRegister(0, DamageCard.createSpamCard());
        player2.programmingRegister().setRegister(0, DamageCard.createSpamCard());
    }

    @Test
    void testYourCardsMessageSerialization() {
        // Create a list of maps representing player IDs and card types
        Map<Integer, String> activeCardsMap = new LinkedHashMap<>();
        for (Player player : players) {
            activeCardsMap.put(player.clientId(), player.programmingRegister().getRegister(0).getCardType().toString());
        }

        // Create a message using the predefined method
        Message message = PredefinedServerMessages.currentCards(activeCardsMap);

        String jsonOutput = gson.toJson(message);

        // Expected JSON structure
        String expectedJson = """
        {
            "messageType": "CurrentCards",
            "messageBody": {
                "activeCards": [
                    {"clientID": 1, "card": "Spam"},
                    {"clientID": 2, "card": "Spam"}
                ]
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
    void testYourCardsMessageDeserialization() {
        // Expected JSON structure
        String jsonInput = """
        {
            "messageType": "CurrentCards",
            "messageBody": {
                "activeCards": [
                    {"clientID": 1, "card": "MoveI"},
                    {"clientID": 2, "card": "Spam"}
                ]
            }
        }
        """;

        // Deserialize JSON string to Message an object
        Message message = gson.fromJson(jsonInput, Message.class);

        // Verify the message type
        assertEquals(Message.MessageType.CURRENT_CARDS, message.messageType());

        // Retrieve and verify the activeCards content
        List<Map<String, Object>> activeCards = (List<Map<String, Object>>) message.messageBody().get("activeCards");
        assertEquals(2, activeCards.size());

        Map<String, Object> card1 = activeCards.get(0);
        assertEquals(1, card1.get("clientID"));
        assertEquals("MoveI", card1.get("card"));

        Map<String, Object> card2 = activeCards.get(1);
        assertEquals(2, card2.get("clientID"));
        assertEquals("Spam", card2.get("card"));
    }
}
