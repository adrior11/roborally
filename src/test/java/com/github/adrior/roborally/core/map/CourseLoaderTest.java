package com.github.adrior.roborally.test.core.map;

import com.github.adrior.roborally.core.map.data.RacingCourseData;
import com.github.adrior.roborally.core.map.parsers.RacingCourseDataParser;
import com.github.adrior.roborally.core.map.data.SpecialTilePositions;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.core.tile.tiles.Checkpoint;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.ResourceFileUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseLoaderTest {
    @Test
    void testParseRacingCourseData() throws IOException, RuntimeException {
        String path = "/courses/dizzy_highway.json";
        RacingCourseData courseData = RacingCourseDataParser.parseCourseData(ResourceFileUtil.getAbsolutePathFromResourcePath(path));

        // Verify the constellation
        assertEquals(RacingCourseData.Constellation.LINE, courseData.constellation());

        // Verify the board rotations
        List<RacingCourseData.BoardData> boardData = courseData.boardData();
        assertEquals(2, boardData.size());

        RacingCourseData.BoardData firstBoard = boardData.get(0);
        assertEquals("A", firstBoard.boardId());
        assertEquals(0, firstBoard.rotation());

        RacingCourseData.BoardData secondBoard = boardData.get(1);
        assertEquals("5B", secondBoard.boardId());
        assertEquals(0, secondBoard.rotation());

        // Verify special tile positions
        SpecialTilePositions specialTilePositions = courseData.specialTilePositions();

        PositionedTile<Antenna> antenna = specialTilePositions.antenna();
        assertEquals(Orientation.RIGHT, antenna.tile().getOrientations().getFirst());
        assertEquals("A", antenna.tile().getIsOnBoard());
        assertEquals(0, antenna.position().x());
        assertEquals(4, antenna.position().y());

        List<PositionedTile<RestartPoint>> restartPoints = specialTilePositions.restartPoints();
        assertEquals(1, restartPoints.size());
        PositionedTile<RestartPoint> restartPoint = restartPoints.get(0);
        assertEquals(4, restartPoint.position().x());
        assertEquals(3, restartPoint.position().y());
        assertEquals(Orientation.BOTTOM, restartPoint.tile().getOrientations().getFirst());
        assertEquals("5B", restartPoint.tile().getIsOnBoard());

        List<PositionedTile<Checkpoint>> checkpoints = specialTilePositions.checkpoints();
        assertEquals(1, checkpoints.size());
        PositionedTile<Checkpoint> checkpoint = checkpoints.get(0);
        assertEquals(9, checkpoint.position().x());
        assertEquals(3, checkpoint.position().y());
        assertEquals(1, checkpoint.tile().getCheckpointNumber());
        assertEquals("5B", checkpoint.tile().getIsOnBoard());
    }
}
