package com.github.adrior.roborally.test.core.map;

import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.map.data.SpecialTilePositions;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.test.util.TestUtils;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RacingCourseTest {

    @Test
    void testDizzyHighwayCreation() {
        RacingCourse dizzyHighway = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);

        assert dizzyHighway != null : "Error: Dizzy Highway creation failed";
        List<List<List<Tile>>> course = dizzyHighway.getTiles();
        TestUtils.printTileNames(course);

        // Assert the correct size of the combined boards for the RacingCourse.
        assertEquals(13, course.size(), "Course width should be 13 tiles");
        assertEquals(10, course.getFirst().size(), "Course height should be 10 tiles");

        SpecialTilePositions dizzyHighwaySpecialTiles = dizzyHighway.getCourseData().specialTilePositions();
        PositionedTile<Antenna> expectedAntenna = dizzyHighwaySpecialTiles.antenna();
        PositionedTile<RestartPoint> expectedRestartPoint = dizzyHighwaySpecialTiles.restartPoints().getFirst();
        assertNotNull(dizzyHighway.getCourseData());

        // RestartToken position is adjusted after the 5B-board appended to A.
        Vector antennaPosition = expectedAntenna.position();
        Vector restartPointPosition = expectedRestartPoint.position().add(new Vector(3, 0));

        // Assert the correct placement of the Antenna and Reboot Token.
        assertInstanceOf(Antenna.class, course.get(antennaPosition.x()).get(antennaPosition.y()).getFirst(), "Expected Antenna at position " + antennaPosition);
        assertInstanceOf(RestartPoint.class, course.get(restartPointPosition.x()).get(restartPointPosition.y()).getFirst(), "Expected RestartPoint at position " + restartPointPosition);

        // Assert the correct placement of the Walls.
        assertEquals(12, dizzyHighway.getWalls().size());
        dizzyHighway.getWalls().stream().map(PositionedTile::position).forEach(position -> System.out.println(position));
    }

    @Test
    void testDeathTrapCreation() {
        RacingCourse deathTrap = RacingCourse.createRacingCourse(AvailableCourses.DEATH_TRAP);

        assert deathTrap != null : "Error: Death Trap creation failed";
        List<List<List<Tile>>> course = deathTrap.getTiles();
        TestUtils.printTileNames(course);

        // Assert the correct size of the combined boards for the RacingCourse.
        assertEquals(13, course.size(), "Course width should be 13 tiles");
        assertEquals(10, course.getFirst().size(), "Course height should be 10 tiles");

        SpecialTilePositions deathTrapSpecialTiles = deathTrap.getCourseData().specialTilePositions();
        PositionedTile<Antenna> expectedAntenna = deathTrapSpecialTiles.antenna();
        PositionedTile<RestartPoint> expectedRestartPoint = deathTrapSpecialTiles.restartPoints().getFirst();
        assertNotNull(deathTrap.getCourseData());

        Vector antennaPosition = expectedAntenna.position();
        Vector restartPointPosition = expectedRestartPoint.position();

        // Assert the correct placement of the Antenna and Reboot Token.
        assertInstanceOf(Antenna.class, course.get(antennaPosition.x()).get(antennaPosition.y()).getFirst(), "Expected Antenna at position " + antennaPosition);
        assertInstanceOf(RestartPoint.class, course.get(restartPointPosition.x()).get(restartPointPosition.y()).getFirst(), "Expected RestartPoint at position " + restartPointPosition);

        // Assert the correct placement of the Walls.
        assertEquals(21, deathTrap.getWalls().size());
        deathTrap.getWalls().stream().map(PositionedTile::position).forEach(position -> System.out.println(position));
    }

    @Test
    void testLostBearingsCreation() {
        RacingCourse lostBearings = RacingCourse.createRacingCourse(AvailableCourses.LOST_BEARINGS);

        assert lostBearings != null : "Error: Lost Bearings creation failed";
        List<List<List<Tile>>> course = lostBearings.getTiles();
        TestUtils.printTileNames(course);

        // Assert the correct size of the combined boards for the RacingCourse.
        assertEquals(13, course.size(), "Course width should be 13 tiles");
        assertEquals(10, course.getFirst().size(), "Course height should be 10 tiles");

        SpecialTilePositions lostBearingsSpecialTiles = lostBearings.getCourseData().specialTilePositions();
        PositionedTile<Antenna> expectedAntenna = lostBearingsSpecialTiles.antenna();
        PositionedTile<RestartPoint> expectedRestartPoint = lostBearingsSpecialTiles.restartPoints().getFirst();
        assertNotNull(lostBearings.getCourseData());

        Vector antennaPosition = expectedAntenna.position();
        Vector restartPointPosition = expectedRestartPoint.position();

        // Assert the correct placement of the Antenna and Reboot Token.
        assertInstanceOf(Antenna.class, course.get(antennaPosition.x()).get(antennaPosition.y()).getFirst(), "Expected Antenna at position " + antennaPosition);
        assertInstanceOf(RestartPoint.class, course.get(restartPointPosition.x()).get(restartPointPosition.y()).getFirst(), "Expected RestartPoint at position " + restartPointPosition);

        // Assert the correct placement of the Walls.
        assertEquals(10, lostBearings.getWalls().size());
        lostBearings.getWalls().stream().map(PositionedTile::position).forEach(position -> System.out.println(position));
    }

    @Test
    void testExtraCrispyCreation() {
        RacingCourse extraCrispy = RacingCourse.createRacingCourse(AvailableCourses.EXTRA_CRISPY);

        assert extraCrispy != null : "Error: Extra Crispy creation failed";
        List<List<List<Tile>>> course = extraCrispy.getTiles();
        TestUtils.printTileNames(course);

        // Assert the correct size of the combined boards for the RacingCourse.
        assertEquals(13, course.size(), "Course width should be 13 tiles");
        assertEquals(10, course.getFirst().size(), "Course height should be 10 tiles");

        SpecialTilePositions extraCrispySpecialTiles = extraCrispy.getCourseData().specialTilePositions();
        PositionedTile<Antenna> expectedAntenna = extraCrispySpecialTiles.antenna();
        PositionedTile<RestartPoint> expectedRestartPoint = extraCrispySpecialTiles.restartPoints().getFirst();
        assertNotNull(extraCrispy.getCourseData());

        Vector antennaPosition = expectedAntenna.position();
        Vector restartPointPosition = expectedRestartPoint.position();

        // Assert the correct placement of the Antenna and Reboot Token.
        assertInstanceOf(Antenna.class, course.get(antennaPosition.x()).get(antennaPosition.y()).getFirst(), "Expected Antenna at position " + antennaPosition);
        assertInstanceOf(RestartPoint.class, course.get(restartPointPosition.x()).get(restartPointPosition.y()).getFirst(), "Expected RestartPoint at position " + restartPointPosition);

        // Assert the correct placement of the Walls.
        assertEquals(22, extraCrispy.getWalls().size());
        extraCrispy.getWalls().stream().map(PositionedTile::position).forEach(position -> System.out.println(position));
    }
}
