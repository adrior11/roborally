package com.github.adrior.roborally.test.core.map;

import com.github.adrior.roborally.core.map.data.Board;
import com.github.adrior.roborally.core.map.parsers.BoardParser;
import com.github.adrior.roborally.core.tile.*;
import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.test.util.TestUtils;
import com.github.adrior.roborally.utility.GsonUtil;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.ResourceFileUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardParserTest {

    @Test
    void testParseBoard() throws IOException {
        Board map = BoardParser.parseMap(ResourceFileUtil.getAbsolutePathFromResourcePath("/boards/TestBoard1.json"));
        List<List<List<Tile>>> tiles = map.tiles();

        TestUtils.printTileNames(tiles);
        String messageString = GsonUtil.getGson().toJson(PredefinedServerMessages.gameStarted(map.tiles()));
        System.out.println(messageString);

        assertEquals("TestBoard", map.name());
        assertFalse(map.startingBoard());

        // Verify the structure and contents of the file
        assertEquals(2, tiles.size());

        // First row
        List<List<Tile>> firstRow = tiles.getFirst();
        assertEquals(2, firstRow.size());

        // First tile in the first row
        Tile firstTile = firstRow.getFirst().getFirst();
        assertInstanceOf(ConveyorBelt.class, firstTile);
        ConveyorBelt conveyorBelt = (ConveyorBelt) firstTile;
        assertTrue(conveyorBelt.isDouble());
        assertEquals(Orientation.TOP, conveyorBelt.getPushOrientation());
        assertEquals(2, conveyorBelt.getIntoOrientation().size());
        assertTrue(conveyorBelt.getIntoOrientation().contains(Orientation.RIGHT));
        assertTrue(conveyorBelt.getIntoOrientation().contains(Orientation.BOTTOM));

        // Second tile in the first row
        Tile secondTile = firstRow.get(1).getFirst();
        assertInstanceOf(PushPanel.class, secondTile);
        PushPanel pushPanel = (PushPanel) secondTile;
        assertEquals(Orientation.LEFT, pushPanel.getPushOrientation()); // 0
        assertEquals(2, pushPanel.getActiveRegisters().size());
        assertTrue(pushPanel.getActiveRegisters().contains(2));
        assertTrue(pushPanel.getActiveRegisters().contains(4));

        // Second row
        List<List<Tile>> secondRow = tiles.get(1);
        assertEquals(2, secondRow.getFirst().size()); // 2

        // First tile of the first tile list in the second row
        Tile thirdTileFirst = secondRow.getFirst().getFirst();
        assertInstanceOf(Wall.class, thirdTileFirst);
        Wall wall = (Wall) thirdTileFirst;
        assertEquals(2, wall.getOrientations().size());
        assertTrue(wall.getOrientations().contains(Orientation.TOP));
        assertTrue(wall.getOrientations().contains(Orientation.RIGHT));

        // Second tile of the first tile list in the second row
        Tile thirdTileSecond = secondRow.getFirst().getLast();
        assertInstanceOf(Laser.class, thirdTileSecond);
        Laser laser = (Laser) thirdTileSecond;
        assertEquals(Orientation.BOTTOM, laser.getLaserOrientation());
        assertEquals(2, laser.getLaserCount());

        // Second tile in the second row
        Tile fourthTile = secondRow.getLast().getFirst();
        assertInstanceOf(NullTile.class, fourthTile);
    }
}
