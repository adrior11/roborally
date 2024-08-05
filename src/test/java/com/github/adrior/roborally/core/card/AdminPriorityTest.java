package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.*;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Pair;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminPriorityTest {
    private final RacingCourse course = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
    private final List<int[]> adminQueue = List.of(
            new int[]{4, 5, 3, 6, 2, 1},
            new int[]{2, 4, 5, 3, 6, 1},
            new int[]{4, 5, 3, 6, 2, 1},
            new int[]{1, 4, 5, 3, 6, 2},
            new int[]{4, 6, 3, 5, 2, 1});

    private TurnManager turnManager;

    @BeforeEach
    public void setUp() {
        SharedDeck.resetDecks();
        GameManager.getInstance().resetGame();

        assertEquals(40, SharedDeck.upgradeDeck.getCards().size());

        LinkedList<Player> players = new LinkedList<>();

        for (int i = 1; i <= 6; i++) {
            players.add(createPlayer(i));
        }

        turnManager = new TurnManager(players, course);
        GameManager.getInstance().getIsGameActive().set(true);

        // Set up the AdminPriority
        turnManager.getAdminPriorityQueue().add(new Pair<>(1, 2));
        turnManager.getAdminPriorityQueue().add(new Pair<>(3, 1));
        turnManager.getAdminPriorityQueue().add(new Pair<>(4, 3));
        turnManager.getAdminPriorityQueue().add(new Pair<>(4, 6));
        turnManager.getAdminPriorityQueue().add(new Pair<>(4, 4));

        turnManager.advancePhase();
    }

    @Test
    void testAdminPriority() {
        assertEquals(6, turnManager.getPlayers().size());
        assertEquals(GameState.UPGRADE_PHASE, turnManager.getCurrentPhase());

        turnManager.advancePhase();
        assertEquals(GameState.PROGRAMMING_PHASE, turnManager.getCurrentPhase());

        turnManager.discardAllPlayerHands();
        turnManager.fillEmptyRegisters();
        turnManager.getPlayers().forEach(p -> assertTrue(p.programmingRegister().isFilled()));
        turnManager.advancePhase();

        for (int i = 0; i < 5; i++) {

            System.out.println(i + ": " + Arrays.toString(adminQueue.get(i)));
            System.out.println(i + ": " + Arrays.toString(getQueue()));
            assertArrayEquals(adminQueue.get(i), getQueue());

            if (i!=4) turnManager.advanceRegister();
        }

        turnManager.resetRound();
    }

    private int[] getQueue() {
        return turnManager.getPlayers().stream().mapToInt(Player::clientId).toArray();
    }

    private Player createPlayer(int id) {
        Robot robot = new Robot();
        robot.setPosition(new Vector(1, id));
        robot.setStartingPosition(new Vector(0, 0));
        robot.setOrientation(Orientation.RIGHT);

        Deck upgrades = new Deck();
        upgrades.addCard(CardFactory.createCard(CardType.ADMIN_PRIVILEGE));
        SharedDeck.upgradeDeck.removeFirstCard();

        Player.Flags flags = new Player.Flags();
        CardManager cardManager = new CardManager();
        ProgrammingRegister programmingRegister = new ProgrammingRegister();

        return new Player(id, robot, cardManager, programmingRegister, upgrades, flags);
    }
}
