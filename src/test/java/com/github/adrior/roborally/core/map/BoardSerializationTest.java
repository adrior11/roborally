package com.github.adrior.roborally.test.core.map;

import com.google.gson.*;
import com.github.adrior.roborally.core.map.data.Board;
import com.github.adrior.roborally.core.map.parsers.BoardParser;
import com.github.adrior.roborally.utility.GsonUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class BoardSerializationTest {

    @Test
    void testUnpackPackCorrectness() throws IOException {
        URL resource = getClass().getResource("/boards/TestBoard1.json");
        assertNotNull(resource, "File not found!");

        File file = new File(resource.getFile());
        assertTrue(file.exists(), "File does not exist!");

        String originalJson = new String(Files.readAllBytes(file.toPath()));

        JsonObject originalJsonObject = JsonParser.parseString(originalJson).getAsJsonObject();
        JsonElement originalTilesElement = originalJsonObject.get("tiles");

        Board board = BoardParser.parseMap(file.getAbsolutePath());
        Gson gson = GsonUtil.getGson();
        String serializedStr = gson.toJson(board.tiles());
        System.out.println(serializedStr);
        System.out.println(originalTilesElement);
        assertEquals(originalTilesElement.toString().length(), serializedStr.length());
    }
}
