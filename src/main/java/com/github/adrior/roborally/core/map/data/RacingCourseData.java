package com.github.adrior.roborally.core.map.data;

import java.util.List;

/**
 * Record representing the data for a racing course in the Robo Rally game.
 * It includes the constellation type, {@link BoardData}, and {@link SpecialTilePositions}.
 *
 * @param constellation The constellation type of the course (e.g., LINE, SQUARE).
 * @param rule The rule applied to the course (NONE, REBOOT, CONVEYOR).
 * @param boardData The list of board data, each containing a board clientId and rotation.
 * @param specialTilePositions The positions of special tiles (antenna, restart points, and checkpoints).
 */
public record RacingCourseData(Constellation constellation, Rule rule, List<BoardData> boardData, SpecialTilePositions specialTilePositions) {

    /**
     * Enum representing the possible constellations of the boards in the course.
     */
    public enum Constellation {
        LINE,
        T,
        CROSS,
        SQUARE
    }

    /**
     * Enum representing the possible rules applied to the course.
     */
    public enum Rule {
        REBOOT,
        CONVEYOR
    }

    /**
     * Record representing the data for an individual board in the course.
     *
     * @param boardId The clientId of the board.
     * @param rotation The rotation of the board in degrees (0, 90, 180, 270).
     */
    public record BoardData(String boardId, int rotation) {}
}
