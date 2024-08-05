package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Checkpoint;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Pair;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointMovementTest {
    RacingCourse course;
    TurnManager turnManager;
    final LinkedList<Player> players = new LinkedList<>();

    Player player1;
    Player player2;

    @BeforeEach
    public void setup() {
        // Reset SharedDeck to prevent a 'NoSuchElement' error when running on mvn test.
        SharedDeck.resetDecks();

        players.add(player1 = new Player(1, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player2 = new Player(2, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));

        player1.robot().setPosition(new Vector(0, 1));
        player1.robot().setOrientation(Orientation.RIGHT);

        player2.robot().setPosition(new Vector(0, 2));
        player2.robot().setOrientation(Orientation.RIGHT);
    }

    @Nested
    class Twister {

        @BeforeEach
        void testTwisterCheckpointOrigins() {
            course = RacingCourse.createRacingCourse(AvailableCourses.TWISTER);
            turnManager = new TurnManager(players, course);

            List<PositionedTile<Checkpoint>> positionedCheckpointOrigins = course.getCheckpoints();
            assertEquals(4, positionedCheckpointOrigins.size());

            // Assert first checkpoint origin.
            PositionedTile<Checkpoint> checkpoint1 = getCheckpointFromNumber(1, positionedCheckpointOrigins);
            assertNotNull(checkpoint1);
            assertEquals(new Vector(10, 1), checkpoint1.position());

            // Assert second checkpoint origin.
            PositionedTile<Checkpoint> checkpoint2 = getCheckpointFromNumber(2, positionedCheckpointOrigins);
            assertNotNull(checkpoint2);
            assertEquals(new Vector(6, 7), checkpoint2.position());

            // Assert third checkpoint origin.
            PositionedTile<Checkpoint> checkpoint3 = getCheckpointFromNumber(3, positionedCheckpointOrigins);
            assertNotNull(checkpoint3);
            assertEquals(new Vector(5, 3), checkpoint3.position());

            // Assert fourth checkpoint origin.
            PositionedTile<Checkpoint> checkpoint4 = getCheckpointFromNumber(4, positionedCheckpointOrigins);
            assertNotNull(checkpoint4);
            assertEquals(new Vector(9, 7), checkpoint4.position());
        }

        @Test
        void testTwisterCheckpointMovement() {
            turnManager.activateFactoryElements();

            // Retrieve the checkpoint positions after their movement.
            List<PositionedTile<Checkpoint>> positionedCheckpoints = RacingCourse.getPositionedTilesOfType(course.getTiles(), Checkpoint.class);

            // Assert first checkpoint movement.
            PositionedTile<Checkpoint> checkpoint1 = getCheckpointFromNumber(1, positionedCheckpoints);
            assertNotNull(checkpoint1);
            assertNotEquals(new Vector(10, 1), checkpoint1.position());
            assertEquals(new Vector(11, 2), checkpoint1.position());

            // Assert second checkpoint movement.
            PositionedTile<Checkpoint> checkpoint2 = getCheckpointFromNumber(2, positionedCheckpoints);
            assertNotNull(checkpoint2);
            assertNotEquals(new Vector(6, 7), checkpoint2.position());
            assertEquals(new Vector(5, 8), checkpoint2.position());

            // Assert third checkpoint movement.
            PositionedTile<Checkpoint> checkpoint3 = getCheckpointFromNumber(3, positionedCheckpoints);
            assertNotNull(checkpoint3);
            assertNotEquals(new Vector(5, 3), checkpoint3.position());
            assertEquals(new Vector(4, 2), checkpoint3.position());

            // Assert fourth checkpoint movement.
            PositionedTile<Checkpoint> checkpoint4 = getCheckpointFromNumber(4, positionedCheckpoints);
            assertNotNull(checkpoint4);
            assertNotEquals(new Vector(9, 7), checkpoint4.position());
            assertEquals(new Vector(10, 6), checkpoint4.position());
        }

        @Test
        void testTwisterSecondCheckpointMovement() {
            turnManager.activateFactoryElements();
            turnManager.activateFactoryElements();

            // Retrieve the checkpoint positions after their movement.
            List<PositionedTile<Checkpoint>> positionedCheckpoints = RacingCourse.getPositionedTilesOfType(course.getTiles(), Checkpoint.class);

            // Assert first checkpoint movement.
            PositionedTile<Checkpoint> checkpoint1 = getCheckpointFromNumber(1, positionedCheckpoints);
            assertNotNull(checkpoint1);
            assertNotEquals(new Vector(10, 1), checkpoint1.position());
            assertNotEquals(new Vector(11, 2), checkpoint1.position());
            assertEquals(new Vector(10, 3), checkpoint1.position());

            // Assert second checkpoint movement.
            PositionedTile<Checkpoint> checkpoint2 = getCheckpointFromNumber(2, positionedCheckpoints);
            assertNotNull(checkpoint2);
            assertNotEquals(new Vector(6, 7), checkpoint2.position());
            assertNotEquals(new Vector(5, 8), checkpoint2.position());
            assertEquals(new Vector(4, 7), checkpoint2.position());

            // Assert third checkpoint movement.
            PositionedTile<Checkpoint> checkpoint3 = getCheckpointFromNumber(3, positionedCheckpoints);
            assertNotNull(checkpoint3);
            assertNotEquals(new Vector(5, 3), checkpoint3.position());
            assertNotEquals(new Vector(4, 2), checkpoint3.position());
            assertEquals(new Vector(5, 1), checkpoint3.position());

            // Assert fourth checkpoint movement.
            PositionedTile<Checkpoint> checkpoint4 = getCheckpointFromNumber(4, positionedCheckpoints);
            assertNotNull(checkpoint4);
            assertNotEquals(new Vector(9, 7), checkpoint4.position());
            assertNotEquals(new Vector(10, 6), checkpoint4.position());
            assertEquals(new Vector(11, 7), checkpoint4.position());
        }
    }

    @Nested
    class GearStripper {
        @BeforeEach
        void testGearStripperCheckpointOrigins() {
            course = RacingCourse.createRacingCourse(AvailableCourses.GEAR_STRIPPER);
            turnManager = new TurnManager(players, course);

            List<PositionedTile<Checkpoint>> positionedCheckpointOrigins = course.getCheckpoints();
            assertEquals(3, positionedCheckpointOrigins.size());

            // Assert first checkpoint origin.
            PositionedTile<Checkpoint> checkpoint1 = getCheckpointFromNumber(1, positionedCheckpointOrigins);
            assertNotNull(checkpoint1);
            assertEquals(new Vector(1, 6), checkpoint1.position());

            // Assert second checkpoint origin.
            PositionedTile<Checkpoint> checkpoint2 = getCheckpointFromNumber(2, positionedCheckpointOrigins);
            assertNotNull(checkpoint2);
            assertEquals(new Vector(19, 1), checkpoint2.position());

            // Assert third checkpoint origin.
            PositionedTile<Checkpoint> checkpoint3 = getCheckpointFromNumber(3, positionedCheckpointOrigins);
            assertNotNull(checkpoint3);
            assertEquals(new Vector(5, 3), checkpoint3.position());
        }

        @Test
        void testGearStripperCheckpointMovement() {
            turnManager.activateFactoryElements();

            // Retrieve the checkpoint positions after their movement.
            List<PositionedTile<Checkpoint>> positionedCheckpoints = RacingCourse.getPositionedTilesOfType(course.getTiles(), Checkpoint.class);

            // Assert first checkpoint movement.
            PositionedTile<Checkpoint> checkpoint1 = getCheckpointFromNumber(1, positionedCheckpoints);
            assertNotNull(checkpoint1);
            assertNotEquals(new Vector(1, 6), checkpoint1.position());
            assertEquals(new Vector(1, 7), checkpoint1.position());

            // Assert second checkpoint movement.
            PositionedTile<Checkpoint> checkpoint2 = getCheckpointFromNumber(2, positionedCheckpoints);
            assertNotNull(checkpoint2);
            assertNotEquals(new Vector(19, 1), checkpoint2.position());
            assertEquals(new Vector(17, 1), checkpoint2.position());

            // Assert third checkpoint movement.
            PositionedTile<Checkpoint> checkpoint3 = getCheckpointFromNumber(3, positionedCheckpoints);
            assertNotNull(checkpoint3);
            assertEquals(new Vector(5, 3), checkpoint3.position());
        }

        @Test
        void testGearStripperCheckpointFallingInPit() {
            // Position checkpoint 2 close to the pit on board 1B
            course.setTilePosition(Checkpoint.class, new Vector(19, 1), new Vector(13, 5));

            // Retrieve the checkpoint positions before their movement.
            List<PositionedTile<Checkpoint>> positionedCheckpoints = RacingCourse.getPositionedTilesOfType(course.getTiles(), Checkpoint.class);
            PositionedTile<Checkpoint> checkpoint2 = getCheckpointFromNumber(2, positionedCheckpoints);
            assertNotEquals(new Vector(19, 1), checkpoint2.position());
            assertEquals(new Vector( 13, 5), checkpoint2.position());

            // Retrieve the checkpoint positions after their movement.
            turnManager.activateFactoryElements();
            positionedCheckpoints = RacingCourse.getPositionedTilesOfType(course.getTiles(), Checkpoint.class);

            // Assert first checkpoint movement.
            PositionedTile<Checkpoint> checkpoint1 = getCheckpointFromNumber(1, positionedCheckpoints);
            assertNotNull(checkpoint1);
            assertNotEquals(new Vector(1, 6), checkpoint1.position());
            assertEquals(new Vector(1, 7), checkpoint1.position());

            // Assert second checkpoint movement.
            checkpoint2 = getCheckpointFromNumber(2, positionedCheckpoints);
            assertNotNull(checkpoint2);
            assertNotEquals(new Vector( 13, 5), checkpoint2.position());
            assertNotEquals(new Vector( 13, 6), checkpoint2.position());
            assertNotEquals(new Vector( 13, 7), checkpoint2.position());
            assertEquals(new Vector(19, 1), checkpoint2.position());

            // Assert third checkpoint movement.
            PositionedTile<Checkpoint> checkpoint3 = getCheckpointFromNumber(3, positionedCheckpoints);
            assertNotNull(checkpoint3);
            assertEquals(new Vector(5, 3), checkpoint3.position());
        }
    }

    PositionedTile<Checkpoint> getCheckpointFromNumber(int checkpointNumber, List<PositionedTile<Checkpoint>> positionedCheckpointTiles) {
        List<Pair<Integer, PositionedTile<Checkpoint>>> checkpoints = positionedCheckpointTiles.stream().map(cp -> {
            int cpNumber = cp.tile().getCheckpointNumber();
            return new Pair<>(cpNumber, cp);
        }).toList();

        return checkpoints.stream().filter(cp -> cp.key() == checkpointNumber).map(Pair::value).findFirst().orElse(null);
    }
}
