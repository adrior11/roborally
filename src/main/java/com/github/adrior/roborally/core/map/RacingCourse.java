package com.github.adrior.roborally.core.map;

import com.github.adrior.roborally.core.map.data.Board;
import com.github.adrior.roborally.core.map.data.RacingCourseData;
import com.github.adrior.roborally.core.map.data.RacingCourseData.BoardData;
import com.github.adrior.roborally.core.map.parsers.BoardParser;
import com.github.adrior.roborally.core.map.parsers.RacingCourseDataParser;
import com.github.adrior.roborally.core.tile.*;
import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.ResourceFileUtil;
import com.github.adrior.roborally.utility.Vector;
import com.github.adrior.roborally.utility.Pair;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.util.*;

/**
 * RacingCourse represents a course used in Robo Rally game.
 * It manages the tiles on the course, the boards used, and the special tile placement.
 *
 * @see Tile
 * @see RacingCourseData
 * @see PositionedTile
 */
@Getter
public class RacingCourse {
    private final List<List<List<Tile>>> tiles;
    private final List<String> boards;
    private final String name;
    private final RacingCourseData courseData;
    private final Pair<Integer, Integer> dimensions;

    @NonNull private final List<PositionedTile<Wall>> walls;
    @NonNull private final List<PositionedTile<Laser>> lasers;
    @NonNull private final List<PositionedTile<EnergySpace>> energySpaces;
    @NonNull private final List<PositionedTile<RestartPoint>> restartPoints;
    @NonNull private final List<PositionedTile<Checkpoint>> checkpoints;

    public RacingCourse(String name, @NonNull List<List<List<Tile>>> course, List<String> usedBoards, RacingCourseData courseData, Pair<Integer, Integer> dimensions) {
        this.name = name;
        this.tiles = course;
        this.boards = usedBoards;
        this.courseData = courseData;
        this.dimensions = dimensions;

        // Retrieve positioned tiles once during instantiation.
        this.walls = getPositionedTilesOfType(course, Wall.class);
        this.lasers = getPositionedTilesOfType(course, Laser.class);
        this.energySpaces = getPositionedTilesOfType(course, EnergySpace.class);
        this.restartPoints = getPositionedTilesOfType(course, RestartPoint.class);
        this.checkpoints = getPositionedTilesOfType(course, Checkpoint.class);
    }


    /**
     * Creates a RacingCourse based on the specified {@link AvailableCourses}.
     * This method dynamically constructs the file paths for the boards,
     * parses the JSON files, combines the boards into a single course,
     * and places special tiles (antenna, restart point & check point).
     *
     * @see BoardUtils
     *
     * @param course The available course to be used for creating the racing course.
     * @return A new RacingCourse instance with combined boards and special tiles placed.
     */
    public static RacingCourse createRacingCourse(@NonNull AvailableCourses course) {
        try {
            // Load the course data from the JSON file
            RacingCourseData courseData = RacingCourseDataParser.parseCourseData(ResourceFileUtil.getAbsolutePathFromResourcePath(course.getFilePath()));

            // A map to hold the parsed boards, identified by their board IDs
            Map<String, List<List<List<Tile>>>> parsedBoards = new LinkedHashMap<>();

            // Process each board in the course data & rotate it if necessary
            for (BoardData boardData : courseData.boardData()) {

                // Parse the board from the file
                Board board = BoardParser.parseMap(ResourceFileUtil.getAbsolutePathFromResourcePath("/boards/" + boardData.boardId() + ".json"));

                // Rotate the board as specified in the course data
                List<List<List<Tile>>> rotatedBoard = BoardUtils.rotateBoard(board.tiles(), boardData.rotation());

                // Add the rotated board to the map
                parsedBoards.put(boardData.boardId(), rotatedBoard);
            }

            // Place special tiles on the individual boards before combining them
            BoardUtils.placeSpecialTilesOnBoards(parsedBoards, courseData.specialTilePositions());

            // Combine the boards in order given by the CourseData constellation.
            List<List<List<Tile>>> racingCourseBoard = BoardUtils.combineBoards(parsedBoards.values(), courseData.constellation());

            // Retrieve the dimensions based on the max width and height.
            Pair<Integer, Integer> dimensions = new Pair<>(racingCourseBoard.size(), racingCourseBoard.getFirst().size());

            // Get the list of used board IDs
            List<String> usedBoards = courseData.boardData().stream().map(BoardData::boardId).toList();

            // Create the RacingCourse.
            return new RacingCourse(course.toString(), racingCourseBoard, usedBoards, courseData, dimensions);
        } catch (IOException e) {
            ServerCommunicationFacade.log("Couldn't parse board of: " + course);
        }
        return null;
    }


    /**
     * Sets the specified tile from its current position to a new position based on the given vector.
     *
     * @param tileClass The class of the tile to be moved.
     * @param from The current position of the tile.
     * @param to The new position where the tile should be set.
     * @param <T> The type of the tile.
     */
    public <T extends Tile> void setTilePosition(@NonNull Class<T> tileClass, @NonNull Vector from, @NonNull Vector to) {
        List<Tile> fromTiles = getTileAt(from);

        // Find the tile of the specified class
        T tile = null;
        for (Tile t : fromTiles) {
            if (tileClass.isInstance(t)) {
                tile = tileClass.cast(t);
                break;
            }
        }

        // If the tile is not found, throw an exception
        if (null == tile) throw new IllegalArgumentException("Tile not found at the specified position.");

        // Remove the tile from the current position
        fromTiles.remove(tile);

        // Get the list of tiles at the new position
        List<Tile> toTiles = getTileAt(to);

        // Add the tile to the first place of the list at the new position
        toTiles.addFirst(tile);
    }


    /**
     * Retrieves all tiles of the specified type along with their positions.
     *
     * @param tiles The 3D list representing the course.
     * @param tileClass The class of the tile type to be retrieved.
     * @param <T> The type of the tile.
     * @return A list of PositionedTile containing tiles of the specified type and their positions.
     */
    @NonNull
    public static <T extends Tile> List<PositionedTile<T>> getPositionedTilesOfType(@NonNull List<List<List<Tile>>> tiles, @NonNull Class<T> tileClass) {
        List<PositionedTile<T>> positionedTiles = new ArrayList<>();

        for (int i = 0; i < tiles.size(); i++) {
            for (int j = 0; j < tiles.get(i).size(); j++) {
                for (Tile tile : tiles.get(i).get(j)) {
                    if (tileClass.isInstance(tile)) {
                        positionedTiles.add(new PositionedTile<>(tileClass.cast(tile), new Vector(i, j)));
                    }
                }
            }
        }

        return positionedTiles;
    }


    /**
     * This function returns the tile at the given position in case
     * the parameter is a Vector instead of accessing a nested array.
     *
     * @param position the position of the tile is a 2D vector
     * @return the tile at the given position
     * @throws IllegalArgumentException if the position is not 2D or out of bounds
     */
    public List<Tile> getTileAt(@NonNull Vector position) {
        try {
            return tiles.get(position.x()).get(position.y());
        } catch (IndexOutOfBoundsException e) {
            return new ArrayList<>();
        }
    }
}
