package com.github.adrior.roborally.test.message;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.map.data.SpecialTilePositions;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.test.util.TestUtils;
import com.github.adrior.roborally.utility.GsonUtil;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStartedMessageSerializationTest {
    private static final Gson gson = GsonUtil.getGson();
    private static final String testServerBoardA = """
            {
                "messageType":"GameStarted",
                "messageBody":{
                    "gameMap":
                    [
                        [
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"StartPoint","isOnBoard":"Start A"}],
                            [{"orientations":["right"],"type":"Antenna","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"StartPoint","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}]
                        ],
                        [
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"StartPoint","isOnBoard":"Start A"}],
                            [{"orientations":["top"],"type":"Wall","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"StartPoint","isOnBoard":"Start A"}],
                            [{"type":"StartPoint","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"orientations":["bottom"],"type":"Wall","isOnBoard":"Start A"}],
                            [{"type":"StartPoint","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}]
                        ],
                        [
                            [{"speed":1,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"orientations":["right"],"type":"Wall","isOnBoard":"Start A"}],
                            [{"orientations":["right"],"type":"Wall","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"type":"Empty","isOnBoard":"Start A"}],
                            [{"speed":1,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"Start A"}]
                        ]
                    ]
                }
            }
            """;

    @Test
    void testGameStartedMessageSerialization() {
        // Create a RacingCourse instance
        RacingCourse originalCourse = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
        assertNotNull(originalCourse, "Error: Dizzy Highway creation failed");

        // Serialization & Deserialization of the original GameStarted message.
        List<List<List<Tile>>> originalBoard = originalCourse.getTiles();
        Message originalGameStartedMessage = PredefinedServerMessages.gameStarted(originalBoard);
        String messageJson = gson.toJson(originalGameStartedMessage);
        System.out.println(messageJson);
        System.out.println("\n" + originalGameStartedMessage.messageBody().get("gameMap"));
        Message gameStartedMessage = gson.fromJson(messageJson, Message.class);

        // Deserialize the gameMap field back to List<List<List<Tile>>>
        JsonObject messageBody = gson.fromJson(gson.toJson(gameStartedMessage.messageBody()), JsonObject.class);
        JsonElement gameMapElement = messageBody.get("gameMap"); // Directly use JsonElement, no need to parse string
        System.out.println(gameMapElement);
        assertNotNull(gameMapElement, "The game map should not be null");

        // Define the type & deserialize the JSON element it to List<List<List<Tile>>>.
        Type boardType = new TypeToken<List<List<List<Tile>>>>() {}.getType();
        List<List<List<Tile>>> deserializedBoard = gson.fromJson(gameMapElement, boardType);
        TestUtils.printTileNames(deserializedBoard);

        // Assert that the original and deserialized boards are equal
        assertEquals(originalBoard.size(), deserializedBoard.size(), "The deserialized board should have the same size as the original");
        assertEquals(13, deserializedBoard.size(), "Course width should be 13 tiles");
        assertEquals(10, deserializedBoard.getFirst().size(), "Course height should be 10 tiles");

        // Assert the correct placement of the Antenna and RestartPoint
        SpecialTilePositions dizzyHighwaySpecialTiles = originalCourse.getCourseData().specialTilePositions();
        PositionedTile<Antenna> expectedAntenna = dizzyHighwaySpecialTiles.antenna();
        PositionedTile<RestartPoint> expectedRestartPoint = dizzyHighwaySpecialTiles.restartPoints().getFirst();

        // RestartToken position is adjusted after the 5B-board appended to A.
        Vector antennaPosition = expectedAntenna.position();
        Vector restartPointPosition = expectedRestartPoint.position().add(new Vector(3, 0));

        // Assert the correct placement of the Antenna and Reboot Token.
        assertInstanceOf(Antenna.class, deserializedBoard.get(antennaPosition.x()).get(antennaPosition.y()).getFirst(), "Expected Antenna at position " + antennaPosition);
        assertInstanceOf(RestartPoint.class, deserializedBoard.get(restartPointPosition.x()).get(restartPointPosition.y()).getFirst(), "Expected RestartPoint at position " + restartPointPosition);
    }

    @Test
    void testGameStartedMessageDeserializationFromTestServer() {
        // Parse the entire JSON string to a JsonObject
        JsonObject fullJsonObject = JsonParser.parseString(testServerBoardA).getAsJsonObject();

        // Extract the 'messageBody' JsonObject, which contains the 'gameMap'
        JsonObject messageBody = fullJsonObject.getAsJsonObject("messageBody");

        // Extract the 'gameMap' JsonElement (which is a JsonArray)
        JsonElement gameMapElement = messageBody.get("gameMap");

        // Check if gameMapElement is indeed a JsonArray
        assertTrue(gameMapElement.isJsonArray(), "Expected 'gameMap' to be a JsonArray");

        // Define the type for Gson deserialization
        Type boardType = new TypeToken<List<List<List<Tile>>>>() {}.getType();

        // Deserialize the JSON array to the desired type
        List<List<List<Tile>>> deserializedBoard = gson.fromJson(gameMapElement, boardType);

        TestUtils.printTileNames(deserializedBoard);
        assertNotNull(deserializedBoard, "Deserialized board should not be null");
        assertFalse(deserializedBoard.isEmpty(), "Deserialized board should not be empty");
    }

}
