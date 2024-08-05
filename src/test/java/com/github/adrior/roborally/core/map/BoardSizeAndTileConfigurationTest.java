package com.github.adrior.roborally.test.core.map;

import com.github.adrior.roborally.core.map.data.Board;
import com.github.adrior.roborally.core.map.parsers.BoardParser;
import com.github.adrior.roborally.core.tile.Tile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardSizeAndTileConfigurationTest {
    private final List<String> startingBoardStrings = List.of("A", "B");
    private final List<String> boardStrings = List.of("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B", "5A", "5B", "6A", "6B");

    @Test
    void testStartingBoardSizeAndTileConfiguration() throws Exception {
        for (String startingBoardString : startingBoardStrings) {
            List<List<List<Tile>>> startingBoard = createBoardList(startingBoardString);
            assertBoardSize(3, 10, startingBoard);
            assertTilesIsOnBoard(startingBoardString, startingBoard);
        }
    }

    @Test
    void testBoardSizeAndTileConfiguration() throws Exception {
        for (String boardString : boardStrings) {
            List<List<List<Tile>>> board = createBoardList(boardString);
            assertBoardSize(10, 10, board);
            assertTilesIsOnBoard(boardString, board);
        }
    }

    private List<List<List<Tile>>> createBoardList(String boardString) throws Exception {
        URL resource = getClass().getResource("/boards/" + boardString + ".json");
        assertNotNull(resource, "File not found for board " + boardString);

        File file = new File(resource.getFile());
        assertTrue(file.exists(), "File does not exist for board " + boardString);
        System.out.println(file.getAbsolutePath());

        Board map = BoardParser.parseMap(file.getAbsolutePath());
        return map.tiles();
    }

    private void assertBoardSize(int width, int height, List<List<List<Tile>>> board) {
        assertEquals(width, board.size());
        board.forEach(column -> assertEquals(height, column.size()));
    }

    private void assertTilesIsOnBoard(String boardString, List<List<List<Tile>>> board) {
        board.forEach(column -> column.forEach(tiles -> tiles.forEach(
                tile -> assertEquals(tile.getIsOnBoard(), boardString,
                        tile.getIsOnBoard() + " does not equal board " + boardString)
        )));
    }
}
