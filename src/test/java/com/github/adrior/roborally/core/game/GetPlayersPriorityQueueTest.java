package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.game.util.PriorityQueue;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import com.github.adrior.roborally.utility.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetPlayersPriorityQueueTest {
    final Pair<Integer, Integer> dimensions = new Pair<>(10, 10);

    LinkedList<Player> players;
    final Vector antennaPosition = new Vector(1, 5);
    final PositionedTile<Antenna> antenna = new PositionedTile<>(
            new Antenna(Orientation.RIGHT, "A"), antennaPosition);

    @Nested
    class TestGroup1 {
        @BeforeEach
        void SetUp() {
            /*
                Test Map:
                    A Antenna
                    1...6 Robot

                    1 2 3 4 5 6 7 8 9
                   ------------------
                1 | . . . . . . . . .
                2 | . . . . . . . . .
                3 | . . . . . . . . .
                4 | . . 1 5 . . . . .
                5 | A . . . 2 . . . .
                6 | . . . 3 . . . . .
                7 | . . 4 . . . . . .
                8 | . . . . . . . . 6
            */
            players = new LinkedList<>(List.of(
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(4, 6), 1),     // 3rd
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(5, 5), 2),     // 2nd
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(9, 8), 3),     // 6th
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(3, 7), 4),     // 4th
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(3, 4), 5),     // 1st
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(4, 4), 6)      // 5th
            ));
        }

        @Test
        void TestPlayersPriorityQueue() {
            List<Player> playersSortedInPriority = PriorityQueue.getPlayersPriorityQueue(players, antenna, dimensions, null);

            List<Player> expectedOrder = List.of(
                    players.get(4), // (3, 4) - 1st
                    players.get(1), // (5, 5) - 2nd
                    players.get(0), // (4, 6) - 3rd
                    players.get(3), // (3, 7) - 4th
                    players.get(5), // (4, 4) - 5th
                    players.get(2)  // (9, 8) - 6th
            );

            assertEquals(expectedOrder, playersSortedInPriority);
        }

        @Test
        void TestPlayersPriorityQueueWithRobotPosition() {
            List<Player> playersSortedInPriority = PriorityQueue.getPlayersPriorityQueue(players, antenna, dimensions, null);

            List<Vector> expectedPositions = List.of(
                    new Vector(3, 4), // 1st
                    new Vector(5, 5), // 2nd
                    new Vector(4, 6), // 3rd
                    new Vector(3, 7), // 4th
                    new Vector(4, 4), // 5th
                    new Vector(9, 8)  // 6th
            );

            // Extract positions from the sorted players list.
            List<Vector> actualPositions = playersSortedInPriority.stream()
                    .map(player -> player.robot().getPosition())
                    .toList();

            assertEquals(expectedPositions, actualPositions);
        }
    }

    @Nested
    class TestGroup2 {
        @BeforeEach
        void SetUp() {
            /*
                Test Map:
                    A Antenna
                    1...6 Robot

                    1 2 3 4 5 6 7 8 9
                   ------------------
                1 | . . . . . . . . .
                2 | . 5 . . . . . . .
                3 | . . . . . . . . .
                4 | 2 . . . . . . . .
                5 | A 1 . . . . . . .
                6 | . 3 . . . . . . .
                7 | 4 . . . . . . . .
                8 | . . . . . . . . .
                9 | . 6 . . . . . . .
            */
            players = new LinkedList<>(List.of(
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(2, 2), 1),     // 5th
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(2, 9), 2),     // 6th
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(1, 4), 3),     // 2nd
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(2, 5), 4),     // 1st
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(2, 6), 5),     // 3rd
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(1, 7), 6)      // 4th
            ));
        }

        @Test
        void TestStartingBoardPriorityQueue() {
            List<Player> playersSortedInPriority = PriorityQueue.getPlayersPriorityQueue(players, antenna, dimensions, null);

            List<Player> expectedOrder = List.of(
                    players.get(3), // (2, 5) - 1st
                    players.get(2), // (1, 4) - 2nd
                    players.get(4), // (2, 6) - 3rd
                    players.get(5), // (1, 7) - 4th
                    players.get(0), // (2, 2) - 5th
                    players.get(1)  // (2, 9) - 6th
            );

            assertEquals(expectedOrder, playersSortedInPriority);
        }
    }


    @Nested
    class TestGroup3 {
        final Vector antennaPosition = new Vector(4, 2);
        final PositionedTile<Antenna> antenna = new PositionedTile<>(new Antenna(Orientation.TOP, "B"), antennaPosition);

        @BeforeEach
        void SetUp() {
            players = new LinkedList<>(List.of(
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(0, 1), 1),     // 5th
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(2, 2), 2),     // 3rd
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(4, 1), 3),     // 1st
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(5, 1), 4),     // 2nd
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(7, 2), 5),     // 4th
                    createPlayerWithRobotPosition(Orientation.RIGHT, new Vector(9, 1), 6)      // 6th
            ));
        }

        @Test
        void TestStartingBoardPriorityQueue() {
            List<Player> playersSortedInPriority = PriorityQueue.getPlayersPriorityQueue(players, antenna, dimensions, null);

            List<Player> expectedOrder = List.of(
                    players.get(2), // 1st
                    players.get(3), // 2nd
                    players.get(1), // 3rd
                    players.get(4), // 4th
                    players.get(0), // 5th
                    players.get(5)  // 6th
            );

            assertEquals(expectedOrder, playersSortedInPriority);
        }
    }

    
    private Player createPlayerWithRobotPosition(Orientation orientation, Vector position, int id) {
        Robot robot = new Robot();
        robot.setOrientation(orientation);
        robot.setPosition(position);
        return new Player(id, robot, null, null, null, null);
    }
}
