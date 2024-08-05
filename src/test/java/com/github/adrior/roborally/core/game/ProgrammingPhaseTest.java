package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ProgrammingPhaseTest {
    private TurnManager turnManager;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;

    @BeforeEach
    public void setup() {
        SharedDeck.resetDecks();
        GameManager.getInstance().resetGame();

        LinkedList<Player> players = new LinkedList<>();
        players.add(player1 = new Player(1, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player2 = new Player(2, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player3 = new Player(3, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player4 = new Player(4, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));

        player1.robot().setPosition(new Vector(1, 0));
        player2.robot().setPosition(new Vector(1, 1));
        player3.robot().setPosition(new Vector(1, 2));
        player4.robot().setPosition(new Vector(1, 3));

        RacingCourse racingCourse = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
        turnManager = new TurnManager(players, racingCourse);
        GameManager.getInstance().getIsGameActive().set(true);
    }

    @Test
    void testProgrammingPhase() {
        // Advance to PROGRAMMING_PHASE
        turnManager.advancePhase();
        turnManager.advancePhase();
        assertEquals(GameState.PROGRAMMING_PHASE, turnManager.getCurrentPhase(), "Should be in PROGRAMMING_PHASE");

        // Assert the correct number of cards in the player hands
        assertEquals(9, player1.cardManager().getHand().getCards().size(), "Should be 9 cards");
        assertEquals(9, player2.cardManager().getHand().getCards().size(), "Should be 9 cards");
        assertEquals(9, player3.cardManager().getHand().getCards().size(), "Should be 9 cards");
        assertEquals(9, player4.cardManager().getHand().getCards().size(), "Should be 9 cards");

        // Simulate player1 placing 5 cards into the register
        for (int i = 0; i < 5; i++) {
            Card card = player1.cardManager().getHand().getCards().get(i);
            player1.programmingRegister().setRegister(i, player1.cardManager().retrieveCardFromHandByType(card.getCardType()));
        }
        assertTrue(player1.programmingRegister().isFilled(), "Player1's registers should be filled");

        // Set player1 selectionFinished to true
        if (player1.programmingRegister().isFilled()) player1.flags().setSelectionFinished(true);
        player1.cardManager().discardHand();
        assertTrue(player1.flags().isSelectionFinished(), "Player 1's selectionFinished flag should be true");
        assertEquals(0, player1.cardManager().getHand().getCards().size(), "Player1's hand should be empty");

        // Timer will be started on the first completion of a register
        turnManager.startTimer();

        // Simulate player2 placing 5 cards into the register
        for (int i = 0; i < 5; i++) {
            Card card = player2.cardManager().getHand().getCards().get(i);
            player2.programmingRegister().setRegister(i, player2.cardManager().retrieveCardFromHandByType(card.getCardType()));
        }
        assertTrue(player2.programmingRegister().isFilled(), "Player2's registers should be filled");

        // Set player2 selectionFinished to true
        if (player2.programmingRegister().isFilled()) player2.flags().setSelectionFinished(true);
        player2.cardManager().discardHand();
        assertTrue(player2.flags().isSelectionFinished(), "Player 2's selectionFinished flag should be true");
        assertEquals(0, player2.cardManager().getHand().getCards().size(), "Player2's hand should be empty");

        // Assert the correct retrieval of player clientId's for player without completed registers
        int[] expectedIDs = new int[]{4, 3};
        int[] pendingIDs = turnManager.getClientIDsArrayWithPendingSelections();
        assertArrayEquals(expectedIDs, pendingIDs, "clientId arrays should be equal");

        // Instantly abort the timer
        turnManager.cancelTimer();

        // Verify hand discard and register fill for players with incomplete selections
        assertEquals(0, player3.cardManager().getHand().getCards().size(), "Player3's hand should be empty");
        assertEquals(0, player4.cardManager().getHand().getCards().size(), "Player4's hand should be empty");

        assertTrue(player3.programmingRegister().isFilled(), "Player3's registers should be filled");
        assertTrue(player4.programmingRegister().isFilled(), "Player4's registers should be filled");

        // Verify the phase has advanced to ACTIVATION_PHASE
        assertEquals(GameState.ACTIVATION_PHASE, turnManager.getCurrentPhase(), "Should be in ACTIVATION_PHASE");
    }
}
