package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.player.*;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardManagerTest {
    final RacingCourse course = RacingCourse.createRacingCourse(AvailableCourses.getRandomCourse());
    TurnManager turnManager;
    final LinkedList<Player> players = new LinkedList<>();

    Player player1;

    @BeforeEach
    void setup() {
        // Reset SharedDeck to prevent a 'NoSuchElement' error when running on mvn test.
        SharedDeck.resetDecks();

        players.add(player1 = new Player(1, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));

        turnManager = new TurnManager(players, course);
    }

    @Test
    void testDealCards() {
        assertEquals(20, player1.cardManager().getDrawDeck().getCards().size());
        assertEquals(0, player1.cardManager().getHand().getCards().size());
        assertEquals(0, player1.cardManager().getDiscardPile().getCards().size());

        turnManager.dealCards();

        assertEquals(11, player1.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player1.cardManager().getHand().getCards().size());
        assertEquals(0, player1.cardManager().getDiscardPile().getCards().size());

        turnManager.discardAllPlayerHands();

        assertEquals(11, player1.cardManager().getDrawDeck().getCards().size());
        assertEquals(0, player1.cardManager().getHand().getCards().size());
        assertEquals(9, player1.cardManager().getDiscardPile().getCards().size());

        turnManager.dealCards();

        assertEquals(2, player1.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player1.cardManager().getHand().getCards().size());
        assertEquals(9, player1.cardManager().getDiscardPile().getCards().size());

        turnManager.discardAllPlayerHands();

        assertEquals(2, player1.cardManager().getDrawDeck().getCards().size());
        assertEquals(0, player1.cardManager().getHand().getCards().size());
        assertEquals(18, player1.cardManager().getDiscardPile().getCards().size());

        turnManager.dealCards();

        assertEquals(11, player1.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player1.cardManager().getHand().getCards().size());
        assertEquals(0, player1.cardManager().getDiscardPile().getCards().size());
    }
}
