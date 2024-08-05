package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.*;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovementExecutorTest {
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
        SharedDeck.resetDecks();
        GameManager.getInstance().resetGame();
        GameManager.getInstance().getIsGameActive().set(true);

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
    class DeathTrap1 {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.DEATH_TRAP);
            turnManager = new TurnManager(players, course);

            // Pushes player2 onto a conveyor belt
            player1.robot().setPosition(new Vector(10, 8));
            player1.robot().setOrientation(Orientation.LEFT);

            // Plays energy cards
            player2.robot().setPosition(new Vector(9, 8));
            player2.robot().setOrientation(Orientation.LEFT);

            // Pushes player4 next to a pit
            player3.robot().setPosition(new Vector(7, 6));
            player3.robot().setOrientation(Orientation.TOP);

            // Walks after being pushed by player3 into a pit
            player4.robot().setPosition(new Vector(7, 5));
            player4.robot().setOrientation(Orientation.LEFT);

            // Pushes player6 out of the map
            player5.robot().setPosition(new Vector(11, 1));
            player5.robot().setOrientation(Orientation.TOP);

            // Gets pushed out of the map
            player6.robot().setPosition(new Vector(11, 0));
            player6.robot().setOrientation(Orientation.TOP);
        }

        @Test
        @Order(1)
        void testCardExecution() {
            Card move = ProgrammingCard.createMoveICard();
            Card energy = ProgrammingCard.createPowerUpCard();

            // Player1 move1
            turnManager.setCurrentPlayer(player1);
            move.execute(turnManager, player1);
            assertEquals(new Vector(9, 8), player1.robot().getPosition());
            assertEquals(new Vector(8, 8), player2.robot().getPosition());

            // Player2 energy
            turnManager.setCurrentPlayer(player2);
            energy.execute(turnManager, player2);
            assertEquals(6, player2.robot().getEnergy());

            // Player3 move1
            turnManager.setCurrentPlayer(player3);
            move.execute(turnManager, player3);
            assertEquals(new Vector(7, 5), player3.robot().getPosition());
            assertEquals(new Vector(7, 4), player4.robot().getPosition());

            // Player4 move1
            turnManager.setCurrentPlayer(player4);
            move.execute(turnManager, player4);
            assertTrue(player4.flags().isRebooting());
            assertEquals(new Vector(0, 0), player4.robot().getPosition());

            // Player5 move1
            turnManager.setCurrentPlayer(player5);
            move.execute(turnManager, player5);
            assertEquals(new Vector(11, 0), player5.robot().getPosition());
            assertTrue(player6.flags().isRebooting());
        }
    }

    @Nested
    class DeathTrap2 {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.DEATH_TRAP);
            turnManager = new TurnManager(players, course);

            // Pushes player2 & player3
            player1.robot().setPosition(new Vector(10, 5));
            player1.robot().setOrientation(Orientation.BOTTOM);

            // Pushes player3 & gets moved by a ConveyorBelt
            player2.robot().setPosition(new Vector(10, 6));
            player2.robot().setOrientation(Orientation.BOTTOM);

            // Gets pushed out of the map
            player3.robot().setPosition(new Vector(10, 8));
            player3.robot().setOrientation(Orientation.BOTTOM);

            // Gets pushed into a EnergySpace
            player4.robot().setPosition(new Vector(8, 4));
            player4.robot().setOrientation(Orientation.BOTTOM);

            // Pushes player5
            player5.robot().setPosition(new Vector(7, 0));
            player5.robot().setOrientation(Orientation.BOTTOM);

            // Pushes player6
            player6.robot().setPosition(new Vector(7, 1));
            player6.robot().setOrientation(Orientation.TOP);
        }

        @Test
        @Order(1)
        void testCardExecution() {
            Card move1 = ProgrammingCard.createMoveICard();
            Card move2 = ProgrammingCard.createMoveIICard();

            // Player1 move2
            turnManager.setCurrentPlayer(player1);
            move2.execute(turnManager, player1);
            assertEquals(new Vector(10, 7), player1.robot().getPosition());
            assertEquals(new Vector(10, 8), player2.robot().getPosition());
            assertEquals(new Vector(10, 9), player3.robot().getPosition());

            // Player2 move1
            turnManager.setCurrentPlayer(player2);
            move1.execute(turnManager, player2);
            assertEquals(new Vector(10, 9), player2.robot().getPosition());
            assertTrue(player3.flags().isRebooting());
            assertEquals(new Vector(0, 0), player3.robot().getPosition());

            // Player5 move1
            turnManager.setCurrentPlayer(player5);
            move1.execute(turnManager, player5);
            assertEquals(new Vector(7, 1), player5.robot().getPosition());
            assertEquals(new Vector(7, 2), player6.robot().getPosition());

            // Player6 move1
            turnManager.setCurrentPlayer(player6);
            move1.execute(turnManager, player6);
            assertEquals(new Vector(7, 0), player5.robot().getPosition());
            assertEquals(new Vector(7, 1), player6.robot().getPosition());

            // Activate Factory Elements
            turnManager.setCurrentRegisterIndex(1);
            turnManager.activateFactoryElements();

            // Assert ConveyorBelt rotation
            assertEquals(new Vector(6, 0), player5.robot().getPosition());
            assertEquals(Orientation.BOTTOM, player5.robot().getOrientation());

            assertEquals(new Vector(7, 0), player6.robot().getPosition());
            assertEquals(Orientation.LEFT, player6.robot().getOrientation());

            assertEquals(new Vector(11, 9), player2.robot().getPosition());
            assertEquals(Orientation.BOTTOM, player5.robot().getOrientation());

            // Assert PushPanel execution
            assertEquals(new Vector(8, 3), player4.robot().getPosition());
            assertEquals(6, player4.robot().getEnergy());

            assertEquals(new Vector(0, 0), player1.robot().getPosition());
            assertTrue(player1.flags().isRebooting());
            assertEquals(new Vector(1, 0), player3.robot().getPosition());
        }
    }

    @Nested
    class DizzyHighway {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
            turnManager = new TurnManager(players, course);

            // Pushes player2 into a wall
            player1.robot().setPosition(new Vector(6, 3));
            player1.robot().setOrientation(Orientation.RIGHT);

            // Gets pushed by player1 into a wall
            player2.robot().setPosition(new Vector(7, 3));
            player2.robot().setOrientation(Orientation.RIGHT);

            // Sits at the restart point of player6
            player3.robot().setPosition(new Vector(0, 3));
            player3.robot().setOrientation(Orientation.BOTTOM);

            // Moves out of board and reboots at restart point pushing player2 in restart point direction
            player4.robot().setPosition(new Vector(12, 0));
            player4.robot().setOrientation(Orientation.RIGHT);

            // Moves out of board and reboots at starting position
            player5.robot().setPosition(new Vector(0, 2));
            player5.robot().setStartingPosition(new Vector(1, 1));
            player5.robot().setOrientation(Orientation.LEFT);

            // Moves out of board and reboots at starting position where player3 is currently at
            player6.robot().setPosition(new Vector(2, 0));
            player6.robot().setStartingPosition(new Vector(0, 3));
            player6.robot().setOrientation(Orientation.TOP);
        }

        @Test
        @Order(1)
        void testCardExecution() {
            Card move1 = ProgrammingCard.createMoveICard();

            // Move Player1
            turnManager.setCurrentPlayer(player1);
            move1.execute(turnManager, player1);
            assertEquals(new Vector(6, 3), player1.robot().getPosition());
            assertEquals(new Vector(7, 3), player2.robot().getPosition());

            // Move Player2
            turnManager.setCurrentPlayer(player2);
            move1.execute(turnManager, player2);
            assertEquals(new Vector(7, 3), player2.robot().getPosition());

            // Move Player4
            turnManager.setCurrentPlayer(player4);
            move1.execute(turnManager, player4);
            assertTrue(player4.flags().isRebooting());
            assertEquals(new Vector(7, 3), player4.robot().getPosition());
            assertEquals(new Vector(7, 4), player2.robot().getPosition(),
                    "Player4 should be moved on tile down from the restart point.");

            // Move Player5
            turnManager.setCurrentPlayer(player5);
            move1.execute(turnManager, player5);
            assertTrue(player5.flags().isRebooting());
            assertEquals(new Vector(1, 1), player5.robot().getPosition());

            // Move Player6
            turnManager.setCurrentPlayer(player6);
            move1.execute(turnManager, player6);
            assertTrue(player6.flags().isRebooting());
            assertEquals(new Vector(0, 3), player6.robot().getPosition());
            assertNotEquals(new Vector(0, 3), player3.robot().getPosition());
        }
    }

    @Nested
    class Twister {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.TWISTER);
            turnManager = new TurnManager(players, course);

            // Moves out of the map and should reboot a starting position
            player1.robot().setPosition(new Vector(0, 1));
            player1.robot().setStartingPosition(new Vector(1, 1));
            player1.robot().setOrientation(Orientation.LEFT);

            // Moves out of the map and should reboot a starting position
            player2.robot().setPosition(new Vector(0, 1));
            player2.robot().setStartingPosition(new Vector(0, 3));
            player2.robot().setOrientation(Orientation.LEFT);

            // Moves out of the map and should reboot a starting position
            player3.robot().setPosition(new Vector(0, 0));
            player3.robot().setStartingPosition(new Vector(1, 4));
            player3.robot().setOrientation(Orientation.TOP);

            // Gets pushed out of player1's starting position tile
            player4.robot().setPosition(new Vector(1, 1));
            player4.robot().setOrientation(Orientation.TOP);

            // Moves out of the map and should reboot a restart point
            player5.robot().setPosition(new Vector(4, 0));
            player5.robot().setOrientation(Orientation.TOP);

            // Moves out of the map and should reboot a restart point, pushing player5 from the restart point
            player6.robot().setPosition(new Vector(5, 0));
            player6.robot().setOrientation(Orientation.TOP);
        }

        @Test
        void testCardExecution() {
            Card move1 = ProgrammingCard.createMoveICard();

            // Move Player1
            turnManager.setCurrentPlayer(player1);
            move1.execute(turnManager, player1);
            assertTrue(player1.flags().isRebooting());
            assertEquals(new Vector(1, 1), player1.robot().getPosition());

            // Assert Player4 getting pushed out of Player1 starting position tile
            assertNotEquals(new Vector(1, 1), player4.robot().getPosition());
            assertEquals(new Vector(2, 1), player4.robot().getPosition());

            // Move Player2
            turnManager.setCurrentPlayer(player2);
            move1.execute(turnManager, player2);
            assertTrue(player2.flags().isRebooting());
            assertEquals(new Vector(0, 3), player2.robot().getPosition());

            // Move Player4
            turnManager.setCurrentPlayer(player3);
            move1.execute(turnManager, player3);
            assertTrue(player3.flags().isRebooting());
            assertEquals(new Vector(1, 4), player3.robot().getPosition());

            // Move Player5
            turnManager.setCurrentPlayer(player5);
            move1.execute(turnManager, player5);
            assertTrue(player5.flags().isRebooting());
            assertEquals(new Vector(0, 7), player5.robot().getPosition());

            // Move Player6
            turnManager.setCurrentPlayer(player6);
            move1.execute(turnManager, player6);
            assertTrue(player6.flags().isRebooting());
            assertEquals(new Vector(0, 7), player6.robot().getPosition());
            assertEquals(new Vector(1, 7), player5.robot().getPosition());
        }
    }

    @Nested
    class Undertow {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.UNDERTOW);
            turnManager = new TurnManager(players, course);

            // Moves out of the map and should reboot at 3A
            player1.robot().setPosition(new Vector(3, 0));
            player1.robot().setOrientation(Orientation.LEFT);

            // Moves out of the map and should reboot at 1A
            player2.robot().setPosition(new Vector(3, 19));
            player2.robot().setOrientation(Orientation.LEFT);

            // Moves out of the map and should reboot at 1A
            player3.robot().setPosition(new Vector(12, 19));
            player3.robot().setOrientation(Orientation.RIGHT);

            // Moves out of the map and should reboot at 4A
            player4.robot().setPosition(new Vector(22, 5));
            player4.robot().setOrientation(Orientation.TOP);

            // Moves out of the map and should reboot at 4A
            player5.robot().setPosition(new Vector(22, 14));
            player5.robot().setOrientation(Orientation.RIGHT);

            // Moves out of the map and should reboot at B starting position
            player6.robot().setPosition(new Vector(0, 5));
            player6.robot().setStartingPosition(new Vector(1, 5));
            player6.robot().setOrientation(Orientation.LEFT);
        }

        @Test
        @Order(1)
        void testPlayerStandingOnBoard() {
            assertEquals("3A", getPlayerIsOnBoard(player1));
            assertEquals("1A", getPlayerIsOnBoard(player2));
            assertEquals("1A", getPlayerIsOnBoard(player3));
            assertEquals("4A", getPlayerIsOnBoard(player4));
            assertEquals("4A", getPlayerIsOnBoard(player5));
            assertEquals("B", getPlayerIsOnBoard(player6));
        }

        private String getPlayerIsOnBoard(Player player) {
            return course.getTileAt(player.robot().getPosition()).getFirst().getIsOnBoard();
        }

        @Test
        @Order(2)
        void testCardExecution() {
            Card move1 = ProgrammingCard.createMoveICard();

            // Move Player1
            turnManager.setCurrentPlayer(player1);
            move1.execute(turnManager, player1);
            assertTrue(player1.flags().isRebooting());
            assertEquals(new Vector(4, 2), player1.robot().getPosition());

            // Move Player2
            turnManager.setCurrentPlayer(player2);
            move1.execute(turnManager, player2);
            assertTrue(player2.flags().isRebooting());
            assertEquals(new Vector(3, 16), player2.robot().getPosition());

            // Move Player3
            turnManager.setCurrentPlayer(player3);
            move1.execute(turnManager, player3);
            assertTrue(player3.flags().isRebooting());
            assertEquals(new Vector(3, 16), player3.robot().getPosition());
            assertEquals(new Vector(3, 15), player2.robot().getPosition());

            // Move Player4
            turnManager.setCurrentPlayer(player4);
            move1.execute(turnManager, player4);
            assertTrue(player4.flags().isRebooting());
            assertEquals(new Vector(17, 14), player4.robot().getPosition());

            // Move Player5
            turnManager.setCurrentPlayer(player5);
            move1.execute(turnManager, player5);
            assertTrue(player5.flags().isRebooting());
            assertEquals(new Vector(17, 14), player5.robot().getPosition());
            assertEquals(new Vector(17, 13), player4.robot().getPosition());

            // Move Player6
            turnManager.setCurrentPlayer(player6);
            move1.execute(turnManager, player6);
            assertTrue(player6.flags().isRebooting());
            assertEquals(new Vector(1, 5), player6.robot().getPosition());
        }
    }

    @Nested
    class BurnRun {
        @BeforeEach
        public void setupBurnRun() {
            course = RacingCourse.createRacingCourse(AvailableCourses.BURN_RUN);
            LinkedList<Player> plays = new LinkedList<>();
            plays.add(player1);
            turnManager = new TurnManager(plays, course);

            // Moves out of the map and should reboot a starting position
            player1.robot().setPosition(new Vector(4, 2));
            player1.robot().setOrientation(Orientation.RIGHT);
        }

        @Test
        public void testConveyor() {
            player1.robot().setPosition(new Vector(4, 2));
            player1.robot().setOrientation(Orientation.RIGHT);

            Card moveICard = ProgrammingCard.createMoveICard();
            turnManager.setCurrentPlayer(player1);

            turnManager.activateFactoryElements();

            assertEquals(new Vector(3, 2), player1.robot().getPosition());


        }


    }
}
