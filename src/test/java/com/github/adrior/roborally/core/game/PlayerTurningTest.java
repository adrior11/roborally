package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTurningTest {
    RacingCourse course;
    TurnManager turnManager;
    final LinkedList<Player> players = new LinkedList<>();

    Player player1;
    Player player2;

    private final Card turnRight = ProgrammingCard.createTurnRightCard();
    private final Card turnLeft = ProgrammingCard.createTurnLeftCard();
    private final Card uTurn = ProgrammingCard.createUTurnCard();
    private final Card move1 = ProgrammingCard.createMoveICard();
    private final Card move2 = ProgrammingCard.createMoveIICard();
    private final Card backUp = ProgrammingCard.createBackUpCard();

    @BeforeEach
    public void setup() {
        // Reset SharedDeck to prevent a 'NoSuchElement' error when running on mvn test.
        SharedDeck.resetDecks();

        players.add(player1 = new Player(1, new Robot(), new CardManager(), new ProgrammingRegister(), null,
                new Player.Flags()));
        players.add(player2 = new Player(2, new Robot(), new CardManager(), new ProgrammingRegister(), null,
                new Player.Flags()));
    }

    @Nested
    class DizzyHighway {
        @BeforeEach
        public void setUp() {
            course = RacingCourse.createRacingCourse(AvailableCourses.DEATH_TRAP);
            turnManager = new TurnManager(players, course);

            // SetUp Player1
            player1.robot().setPosition(new Vector(0, 3));
            player1.robot().setOrientation(Orientation.RIGHT);

            // Setup Player2
            player2.robot().setPosition(new Vector(1, 5));
            player2.robot().setOrientation(Orientation.RIGHT);
        }

        @Test
        @Order(1)
        void testPlayer1() {

            turnManager.setCurrentPlayer(player1);

            // TL
            turnLeft.execute(turnManager, player1);
            assertEquals(Orientation.TOP, player1.robot().getOrientation());

            // MOVE1
            move1.execute(turnManager, player1);
            assertEquals(new Vector(0, 2), player1.robot().getPosition());

            // TR
            turnRight.execute(turnManager, player1);
            assertEquals(Orientation.RIGHT, player1.robot().getOrientation());

            // MOVE1
            move1.execute(turnManager, player1);
            assertEquals(new Vector(1, 2), player1.robot().getPosition());

            // U-TURN
            uTurn.execute(turnManager, player1);
            assertEquals(Orientation.LEFT, player1.robot().getOrientation());
        }

        @Test
        @Order(2)
        void testPlayer2() {

            turnManager.setCurrentPlayer(player2);

            // Move II
            move2.execute(turnManager, player2);
            assertEquals(new Vector(2, 5), player2.robot().getPosition(),"Player should be blocked by the wall");

            // TR
            turnRight.execute(turnManager, player2);
            assertEquals(Orientation.BOTTOM, player2.robot().getOrientation());

            // Move I
            move1.execute(turnManager, player2);
            assertEquals(new Vector(2, 6), player2.robot().getPosition());

            // TR
            turnRight.execute(turnManager, player2);
            assertEquals(Orientation.LEFT, player2.robot().getOrientation());

            // Backup
            backUp.execute(turnManager, player2);
            assertEquals(new Vector(3, 6), player2.robot().getPosition());
        }
    }
}
