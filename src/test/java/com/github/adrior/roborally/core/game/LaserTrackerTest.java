package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Laser;
import com.github.adrior.roborally.core.tile.tiles.Wall;
import com.github.adrior.roborally.core.game.util.LaserTracker;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.core.game.recorder.HitRecorder;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LaserTrackerTest {
    private static final List<PositionedTile<Laser>> lasers = new ArrayList<>();
    private static final List<Player> players = new ArrayList<>();
    private static final List<PositionedTile<Wall>> walls = new ArrayList<>();

    Player player1;
    Player player2;
    Player player3;
    Player player4;

    /*
      Test Map:
           > Laser
           R Robot
           W Wall

          0 1 2 3 4 5 6 7 8 9
        ---------------------
      0 | . . . . . . . . . .
      1 | . . . . . . . . . .
      2 | . . > . . . R . W .
      3 | . . > . W . . . . .
      4 | . . . . . . . . . .
      5 | . . > . . . R . W .
      6 | . W > . R . R . W .
      7 | . . . . . . W . . .
      8 | . . . . . . . . . .
     */
    @BeforeEach
    void setUpMap() {
        // Set up tile lasers
        lasers.add(new PositionedTile<>(new Laser(Orientation.RIGHT, 1, "inOnBoard"), new Vector(2, 2))); // Shoots at robot1.
        lasers.add(new PositionedTile<>(new Laser(Orientation.RIGHT, 1, "inOnBoard"), new Vector(2, 3))); // Shoots at wall.
        lasers.add(new PositionedTile<>(new Laser(Orientation.RIGHT, 1, "inOnBoard"), new Vector(2, 5))); // Shoots at robot2.
        lasers.add(new PositionedTile<>(new Laser(Orientation.RIGHT, 1, "inOnBoard"), new Vector(2, 6))); // Shoots at robot3 with robot4 in los.

        // Set up robots
        players.add(player1 = new Player(1, new Robot(), new CardManager(), null, new Deck(), new Player.Flags()));
        players.add(player2 = new Player(2, new Robot(), new CardManager(), null, new Deck(), new Player.Flags()));
        players.add(player3 = new Player(3, new Robot(), new CardManager(), null, new Deck(), new Player.Flags()));
        players.add(player4 = new Player(4, new Robot(), new CardManager(), null, new Deck(), new Player.Flags()));

        player1.robot().setPosition(new Vector(6, 2));
        player2.robot().setPosition(new Vector(6, 5));
        player3.robot().setPosition(new Vector(4, 6));
        player4.robot().setPosition(new Vector(6, 6));

        player1.robot().setOrientation(Orientation.BOTTOM); // Shoots at robot2 with robot4 in los.
        player2.robot().setOrientation(Orientation.RIGHT);  // Shoots at wall.
        player3.robot().setOrientation(Orientation.RIGHT);  // Shoots at robot4.
        player4.robot().setOrientation(Orientation.LEFT);   // Shoots at robot3.

        // Set up walls
        walls.add(new PositionedTile<>(new Wall(List.of(Orientation.RIGHT), "inOnBoard"), new Vector(8, 2)));
        walls.add(new PositionedTile<>(new Wall(List.of(Orientation.RIGHT), "inOnBoard"), new Vector(4, 3)));
        walls.add(new PositionedTile<>(new Wall(List.of(Orientation.RIGHT), "inOnBoard"), new Vector(8, 5)));
        walls.add(new PositionedTile<>(new Wall(List.of(Orientation.RIGHT), "inOnBoard"), new Vector(1, 6)));
        walls.add(new PositionedTile<>(new Wall(List.of(Orientation.RIGHT), "inOnBoard"), new Vector(8, 6)));
        walls.add(new PositionedTile<>(new Wall(List.of(Orientation.RIGHT), "inOnBoard"), new Vector(6, 7)));
    }


    @Test
    @Order(1)
    void testTrackTileLaser() {
        HitRecorder.clearHits();

        // Expected hits
        Set<Vector> expectedHits = new HashSet<>();
        expectedHits.add(new Vector(4, 6)); // Laser from (2, 6)
        expectedHits.add(new Vector(6, 5)); // Laser from (2, 5)
        expectedHits.add(new Vector(6, 2)); // Laser from (2, 2)

        // Track the lasers
        LaserTracker.trackTileLaser(lasers, new LinkedList<>(players), walls);

        // Verify hits & non-hits
        expectedHits.forEach(hit -> assertTrue(HitRecorder.wasHit(hit)));
        assertEquals(3, HitRecorder.totalHits(), "3 robots should be hit");
        assertFalse(HitRecorder.wasHit(new Vector(6, 6)), "Robot at (6, 6) should not be hit");

    }


    @Test
    @Order(2)
    void testTrackRobotLaser() {
        HitRecorder.clearHits();

        // Expected hits
        Set<Vector> expectedHits = new HashSet<>();
        expectedHits.add(new Vector(6, 5)); // Laser from (6, 2)
        expectedHits.add(new Vector(6, 6)); // Laser from (4, 6)
        expectedHits.add(new Vector(4, 6)); // Laser from (6, 6)

        // Track the lasers
        LaserTracker.trackRobotLaser(players, walls);

        // Verify hits & non-hits
        expectedHits.forEach(hit -> assertTrue(HitRecorder.wasHit(hit)));
        assertEquals(3, HitRecorder.totalHits(), "3 robots should be hit");
        assertFalse(HitRecorder.wasHit(new Vector(6, 2)), "Robot at (6, 2) should not be hit");
    }
}
