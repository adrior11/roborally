package com.github.adrior.roborally.test.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.cards.DamageCard;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrentCardsTest {
    private static final Gson gson = GsonUtil.getGson();

    final Card testCard1 = ProgrammingCard.createMoveICard();
    final Card testCard2 = DamageCard.createSpamCard();
    LinkedList<Player> players;
    int clientId = 0;

    @BeforeEach
    public void setup() {
        players = new LinkedList<>(List.of(
                createPlayerWithRegister(testCard1),
                createPlayerWithRegister(testCard2)
        ));
    }

    @Test
    void testCurrentCardsMessageSerialization() {
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
                    {"clientID": 1, "card": "MoveI"},
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
    void testCurrentCardsMessageDeserialization() {
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

    private Player createPlayerWithRegister(Card card) {
        ProgrammingRegister register = new ProgrammingRegister();
        register.setRegister(0, card);
        clientId++;
        return new Player(clientId, null, null, register, null, null);
    }
}
