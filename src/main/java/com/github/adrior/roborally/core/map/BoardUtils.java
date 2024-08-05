package com.github.adrior.roborally.core.map;

import com.github.adrior.roborally.core.map.data.RacingCourseData;
import com.github.adrior.roborally.core.map.data.RacingCourseData.Constellation;
import com.github.adrior.roborally.core.map.data.SpecialTilePositions;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.core.tile.TileUtil;
import com.github.adrior.roborally.core.tile.tiles.Checkpoint;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * Utility class for manipulating and combining game boards consisting of tiles.
 * This class provides various static methods for combining, rotating, and placing special tiles on boards.
 *
 * @see RacingCourse
 * @see RacingCourseData
 * @see SpecialTilePositions
 * @see TileUtil
 */
@UtilityClass
class BoardUtils {

    /**
     * Combines multiple boards based on the given constellation.
     *
     * @param boards       The collection of boards to be combined.
     * @param constellation The constellation type indicating how the boards should be combined.
     * @return A combined board.
     */
    static List<List<List<Tile>>> combineBoards(@NonNull Collection<List<List<List<Tile>>>> boards, @NonNull Constellation constellation) {
        return switch (constellation) {
            case LINE -> combineBoardsAsLine(boards);
            case T -> combineBoardsAsT(boards);
            case CROSS -> combineBoardsAsCross(boards);
            case SQUARE -> combineBoardsAsSquare(boards);
        };
    }

    /**
     * Combines boards in a line-configuration.
     *
     * @param boards The collection of boards to be combined.
     * @return A combined board in line configuration.
     */
    private static List<List<List<Tile>>> combineBoardsAsLine(@NonNull Collection<List<List<List<Tile>>>> boards) {
        Iterator<List<List<List<Tile>>>> iterator = boards.iterator();
        if (!iterator.hasNext()) return new ArrayList<>();

        List<List<List<Tile>>> combinedBoard = iterator.next();
        while (iterator.hasNext()) {
            List<List<List<Tile>>> nextBoard = iterator.next();
            combinedBoard = combineBoardsHorizontally(combinedBoard, nextBoard);
        }
        return combinedBoard;
    }

    /**
     * Combines boards in a T-configuration.
     *
     * @param boards The collection of boards to be combined.
     * @return A combined board in T configuration.
     */
    @NonNull private static List<List<List<Tile>>> combineBoardsAsT(@NonNull Collection<List<List<List<Tile>>>> boards) {
        if (2 > boards.size()) throw new IllegalArgumentException("T constellation requires a minimum of 2 boards.");

        Iterator<List<List<List<Tile>>>> iterator = boards.iterator();

        int nullBoardWidth = ((boards.size() - 2) * 10) / 2;
        List<List<List<Tile>>> startingBoard = iterator.next();
        List<List<List<Tile>>> nullBoard = TileUtil.createNullTileBoard(nullBoardWidth, startingBoard.getFirst().size());
        List<List<List<Tile>>> bottomHalf = combineBoardsHorizontally(combineBoardsHorizontally(nullBoard, startingBoard), nullBoard);

        List<List<List<Tile>>> topHalf = iterator.next();
        while (iterator.hasNext()) {
            List<List<List<Tile>>> nextBoard = iterator.next();
            topHalf = combineBoardsHorizontally(topHalf, nextBoard);
        }

        return combineBoardsVertically(topHalf, bottomHalf);
    }

    /**
     * Combines boards in a cross-configuration.
     *
     * @param boards The collection of boards to be combined.
     * @return A combined board in cross configuration.
     */
    @NonNull private static List<List<List<Tile>>> combineBoardsAsCross(@NonNull Collection<List<List<List<Tile>>>> boards) {
        if (4 != boards.size()) throw new IllegalArgumentException("Cross constellation requires exactly 4 boards.");

        Iterator<List<List<List<Tile>>>> iterator = boards.iterator();

        List<List<List<Tile>>> startingBoard = iterator.next();
        List<List<List<Tile>>> board2 = iterator.next();
        List<List<List<Tile>>> board3 = iterator.next();
        List<List<List<Tile>>> board4 = iterator.next();

        List<List<List<Tile>>> startingNullBoard = TileUtil.createNullTileBoard(startingBoard.size(), startingBoard.getFirst().size()/2);
        List<List<List<Tile>>> leftThird = combineBoardsVertically(startingNullBoard, combineBoardsVertically(startingBoard, startingNullBoard));
        List<List<List<Tile>>> middleThird = combineBoardsVertically(board2, board3);
        List<List<List<Tile>>> nullBoard = TileUtil.createNullTileBoard(board4.size(), board4.getFirst().size()/2);
        List<List<List<Tile>>> rightThird = combineBoardsVertically(nullBoard, combineBoardsVertically(board4, nullBoard));

        return combineBoardsHorizontally(combineBoardsHorizontally(leftThird, middleThird), rightThird);
    }

    /**
     * Combines boards in a square-configuration.
     *
     * @param boards The collection of boards to be combined.
     * @return A combined board in square configuration.
     */
    @NonNull private static List<List<List<Tile>>> combineBoardsAsSquare(@NonNull Collection<List<List<List<Tile>>>> boards) {
        if (5 != boards.size()) throw new IllegalArgumentException("Square constellation requires exactly 5 boards.");

        Iterator<List<List<List<Tile>>>> iterator = boards.iterator();

        List<List<List<Tile>>> startingBoard = iterator.next();
        List<List<List<Tile>>> board2 = iterator.next();
        List<List<List<Tile>>> board3 = iterator.next();
        List<List<List<Tile>>> board4 = iterator.next();
        List<List<List<Tile>>> board5 = iterator.next();

        List<List<List<Tile>>> topHalf = combineBoardsHorizontally(board2, board3);
        List<List<List<Tile>>> bottomHalf = combineBoardsHorizontally(board4, board5);
        List<List<List<Tile>>> squareBoard = combineBoardsVertically(topHalf, bottomHalf);

        List<List<List<Tile>>> nullBoard = TileUtil.createNullTileBoard(startingBoard.size(), startingBoard.getFirst().size());
        List<List<List<Tile>>> leftBoard = combineBoardsVertically(startingBoard, nullBoard);

        return combineBoardsHorizontally(leftBoard, squareBoard);
    }


    /**
     * Combines two boards horizontally.
     *
     * @param boardTiles The tiles of the starting board.
     * @param boardTilesToAppend The tiles of the additional board.
     * @return A combined list of tiles.
     */
    @NonNull private static List<List<List<Tile>>> combineBoardsHorizontally(@NonNull List<List<List<Tile>>> boardTiles, @NonNull List<List<List<Tile>>> boardTilesToAppend) {
        if (boardTiles.getFirst().size() != boardTilesToAppend.getFirst().size()) throw new IllegalArgumentException("Both boards must have the same height to be combined horizontally");
        List<List<List<Tile>>> combinedTiles = new ArrayList<>();
        combinedTiles.addAll(boardTiles);
        combinedTiles.addAll(boardTilesToAppend);
        return combinedTiles;
    }


    /**
     * Combines two boards vertically.
     *
     * @param boardTiles The tiles of the starting board.
     * @param boardTilesToAppend The tiles of the additional board.
     * @return A combined list of tiles.
     */
    @NonNull private static List<List<List<Tile>>> combineBoardsVertically(@NonNull List<List<List<Tile>>> boardTiles, @NonNull List<List<List<Tile>>> boardTilesToAppend) {
        if (boardTiles.size() != boardTilesToAppend.size()) throw new IllegalArgumentException("Both boards must have the same width to be combined vertically");
        for (int i = 0; i < boardTiles.size(); i++) {
            boardTiles.get(i).addAll(boardTilesToAppend.get(i));
        }
        return boardTiles;
    }


    /**
     * Rotates the given course (board) by the specified number of 90-degree steps.
     * Positive steps rotate the board clockwise, and negative steps rotate it counterclockwise.
     *
     * @param tiles The board of tiles to be rotated.
     * @param steps The number of 90-degree steps to rotate the board.
     * @return The rotated board.
     */
    static List<List<List<Tile>>> rotateBoard(List<List<List<Tile>>> tiles, int steps) {
        int rotations = (steps % 4 + 4) % 4; // Normalize steps to a value between 0 and 3

        List<List<List<Tile>>> rotatedBoard = tiles;

        for (int i = 0; i < rotations; i++) {
            rotatedBoard = rotate90DegreesClockwise(rotatedBoard);
        }

        return rotatedBoard;
    }


    /**
     * Rotates the given board 90 degrees clockwise.
     *
     * @param tiles The board of tiles to be rotated.
     * @return The rotated board.
     */
    @NonNull private static List<List<List<Tile>>> rotate90DegreesClockwise(@NonNull List<List<List<Tile>>> tiles) {
        int numRows = tiles.size();
        int numCols = tiles.getFirst().size();
        List<List<List<Tile>>> rotatedBoard = new ArrayList<>(numCols);

        for (int col = 0; col < numCols; col++) {
            List<List<Tile>> newRow = new ArrayList<>(numRows);
            for (int row = numRows - 1; 0 <= row; row--) {
                List<Tile> oldTiles = tiles.get(row).get(col);
                List<Tile> newTiles = new ArrayList<>();
                for (Tile tile : oldTiles) {
                    newTiles.add(TileUtil.rotateTile(tile));
                }
                newRow.add(newTiles);
            }
            rotatedBoard.add(newRow);
        }

        return rotatedBoard;
    }


    /**
     * Places the special tiles on the respective boards based on the board name.
     *
     * @param parsedBoards The map of board names to their tiles.
     * @param specialTilePositions The positions of the special tiles.
     */
    static void placeSpecialTilesOnBoards(@NonNull Map<String, List<List<List<Tile>>>> parsedBoards, @NonNull SpecialTilePositions specialTilePositions) {
        placeSpecialTileOnBoard(parsedBoards, specialTilePositions.antenna());

        for (PositionedTile<RestartPoint> restartPoint : specialTilePositions.restartPoints()) {
            placeSpecialTileOnBoard(parsedBoards, restartPoint);
        }

        for (PositionedTile<Checkpoint> checkpoint : specialTilePositions.checkpoints()) {
            placeSpecialTileOnBoard(parsedBoards, checkpoint);
        }
    }


    /**
     * Places a special tile (antenna, restart point, checkpoint) on the respective board.
     *
     * @param parsedBoards The map of board names to their tiles.
     * @param positionedTile The positioned tile to be placed on the course.
     */
    private static void placeSpecialTileOnBoard(@NonNull Map<String, List<List<List<Tile>>>> parsedBoards, @NonNull PositionedTile<? extends Tile> positionedTile) {
        String isOnBoard = positionedTile.tile().getIsOnBoard();
        Vector position = positionedTile.position();

        List<List<List<Tile>>> board = parsedBoards.get(isOnBoard);
        List<Tile> tiles = board.get(position.x()).get(position.y());
        tiles.addFirst(positionedTile.tile());  // Insert the tile at the start of the list.
    }
}
