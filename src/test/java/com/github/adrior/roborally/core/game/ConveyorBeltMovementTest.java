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
import com.github.adrior.roborally.core.game.recorder.HitRecorder;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConveyorBeltMovementTest {
    RacingCourse course;
    TurnManager turnManager;
    final LinkedList<Player> players = new LinkedList<>();

    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;
    Player player6;

    @BeforeEach
    public void setup() {
        // Reset SharedDeck to prevent a 'NoSuchElement' error when running on mvn test.
        SharedDeck.resetDecks();

        players.add(player1 = new Player(1, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player2 = new Player(2, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player3 = new Player(3, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player4 = new Player(4, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player5 = new Player(5, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player6 = new Player(6, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
    }

    @Nested
    class DizzyHighway {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
            turnManager = new TurnManager(players, course);

            // Conflict with robot 6
            player1.robot().setPosition(new Vector(2, 0));
            player1.robot().setOrientation(Orientation.RIGHT);

            // Gets pushed to EnergySpace
            player2.robot().setPosition(new Vector(2, 9));
            player2.robot().setOrientation(Orientation.RIGHT);

            // Conflict with robot4
            player3.robot().setPosition(new Vector(4, 0));
            player3.robot().setOrientation(Orientation.RIGHT);

            // Conflict with robot3
            player4.robot().setPosition(new Vector(5, 1));
            player4.robot().setOrientation(Orientation.RIGHT);

            // Should rotate
            player5.robot().setPosition(new Vector(4, 7));
            player5.robot().setOrientation(Orientation.RIGHT);

            // Stands at the end of a ConveyorBelt
            player6.robot().setPosition(new Vector(3, 0));
            player6.robot().setOrientation(Orientation.RIGHT);
        }

        @Test
        void testDizzyHighwayMovement() {
            turnManager.activateFactoryElements();

            // Assert conflict at the end of a ConveyorBelt
            assertEquals(new Vector(2, 0), player1.robot().getPosition(), "Robot1 shouldn't move due to conflict");
            assertEquals(new Vector(3, 0), player6.robot().getPosition(), "Robot6 shouldn't be pushed");

            // Assert movement to an EnergySpace
            assertEquals(new Vector( 3, 9), player2.robot().getPosition(), "Robot2 should stand on a EnergySpace");
            assertEquals(6, player2.robot().getEnergy(), "Robot2's energy should be 6");

            // Assert ConveyorBelt conflict
            assertEquals(new Vector(4, 0), player3.robot().getPosition(), "Robot3 shouldn't move due to conflict");
            assertEquals(new Vector(5, 1), player4.robot().getPosition(), "Robot3 shouldn't move due to conflict");

            // Assert ConveyorBelt rotation
            assertEquals(new Vector(5, 8), player5.robot().getPosition(), "Robot5's position should be (5,8) after activation");
            assertEquals(Orientation.TOP, player5.robot().getOrientation());
        }
    }

    @Nested
    class LostBearings {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.LOST_BEARINGS);
            turnManager = new TurnManager(players, course);

            // Gets pushed to gear and rotated counterclockwise
            player1.robot().setPosition(new Vector(5, 3));
            player1.robot().setOrientation(Orientation.TOP);

            // Gets pushed to gear and rotated clockwise
            player2.robot().setPosition(new Vector(10, 3));
            player2.robot().setOrientation(Orientation.TOP);

            // Reboots and gets into reboot conflict with player4
            player3.robot().setPosition(new Vector(11, 0));
            player3.robot().setOrientation(Orientation.RIGHT);

            // Reboots and gets into reboot conflict with player3
            player4.robot().setPosition(new Vector(4, 9));
            player4.robot().setOrientation(Orientation.RIGHT);

            // Should rotate
            player5.robot().setPosition(new Vector(3, 8));
            player5.robot().setOrientation(Orientation.RIGHT);

            // Should move without a conflict
            player6.robot().setPosition(new Vector(4, 8));
            player6.robot().setOrientation(Orientation.RIGHT);
        }

        @Test
        void testLostBearingsMovement() {
            turnManager.activateFactoryElements();

            // Assert push onto gear
            assertEquals(new Vector(5, 4), player1.robot().getPosition(), "Robot1's position should be (5,4) after activation");
            assertEquals(Orientation.LEFT, player1.robot().getOrientation());

            assertEquals(new Vector(10, 4), player2.robot().getPosition(), "Robot2's position should be (10,4) after activation");
            assertEquals(Orientation.RIGHT, player2.robot().getOrientation());

            // Assert ConveyorBelt rotation
            assertEquals(new Vector(4, 8), player5.robot().getPosition(), "Robot5's position should be (4,8) after activation");
            assertEquals(Orientation.BOTTOM, player5.robot().getOrientation());

            // Assert movement without a conflict
            assertEquals(new Vector(4, 9), player6.robot().getPosition(), "Robot6's position should be (4,9) after activation");
            assertEquals(Orientation.RIGHT, player6.robot().getOrientation());

            // Assert reboot conflict 1
            assertTrue(player3.flags().isRebooting());
            assertEquals(new Vector(1, 0), player3.robot().getPosition());

            assertTrue(player4.flags().isRebooting());
            assertEquals(new Vector(0, 0), player4.robot().getPosition());
        }
    }

    @Nested
    class ExtraCrispy {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.EXTRA_CRISPY);
            turnManager = new TurnManager(players, course);

            // Moves into laser
            player1.robot().setPosition(new Vector(11, 2));
            player1.robot().setOrientation(Orientation.TOP);

            // Reboots and gets into reboot conflict with player3
            player2.robot().setPosition(new Vector(6, 1));
            player2.robot().setOrientation(Orientation.TOP);

            // Reboots and gets into reboot conflict with player2
            player3.robot().setPosition(new Vector(9, 8));
            player3.robot().setOrientation(Orientation.RIGHT);

            // Moves into laser
            player4.robot().setPosition(new Vector(4, 2));
            player4.robot().setOrientation(Orientation.RIGHT);

            // Reboots and gets into reboot conflict with player6
            player5.robot().setPosition(new Vector(4, 3));
            player5.robot().setOrientation(Orientation.RIGHT);

            // Reboots and gets into reboot conflict with player5
            player6.robot().setPosition(new Vector(5, 3));
            player6.robot().setOrientation(Orientation.RIGHT);
        }

        @Test
        void testLasersExtraCrispy() {
            turnManager.activateFactoryElements();

            // Assert movement into laser & ConveyorBelt rotation
            assertEquals(new Vector(10, 3), player1.robot().getPosition(), "Robot1's position should be (10,3) after activation");
            assertEquals(Orientation.RIGHT, player1.robot().getOrientation());

            assertEquals(new Vector(5, 3), player4.robot().getPosition(), "Robot4's position should be (5,3) after activation");
            assertEquals(Orientation.TOP, player4.robot().getOrientation());

            Set<Vector> expectedHits = new HashSet<>();
            expectedHits.add(new Vector(5, 3));
            expectedHits.add(new Vector(10, 3));

            System.out.println(HitRecorder.totalHits());
            HitRecorder.getHitRobots().forEach(hit -> System.out.println(hit.toString()));
            System.out.println(Arrays.toString(expectedHits.toArray()));

            expectedHits.forEach(hit -> assertTrue(HitRecorder.wasHit(hit)));
            assertTrue(HitRecorder.totalHits() >= 2);
        }
    }
}
