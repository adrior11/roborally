package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.player.*;
import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgainAndSpamCardTest {
    private final RacingCourse course = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
    private TurnManager turnManager;
    private Player player;

    @BeforeEach
    public void setUp() {
        SharedDeck.resetDecks();
        GameManager.getInstance().resetGame();

        LinkedList<Player> players = new LinkedList<>();

        player = createPlayer(1);
        players.add(player);

        turnManager = new TurnManager(players, course);
        GameManager.getInstance().getIsGameActive().set(true);

        turnManager.advancePhase();
        turnManager.advancePhase();
        assertEquals(GameState.PROGRAMMING_PHASE, turnManager.getCurrentPhase());
    }

    @Test
    void testAgainAndSpamCard() {

        // Assert the correct drawing of cards.
        System.out.println(player.cardManager().getHand().getCards());
        assertEquals(26, player.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player.cardManager().getHand().getCards().size());
        player.cardManager().getHand().getCards().forEach(card -> assertEquals(CardType.SPAM, card.getCardType()));

        // Assert the correct filling of cards.
        turnManager.fillEmptyRegisters();
        turnManager.discardAllPlayerHands();
        assertEquals(5, player.programmingRegister().getAllRegisters().size());
        assertTrue(player.programmingRegister().isFilled());
        player.programmingRegister().getAllRegisters().forEach(card -> assertEquals(CardType.SPAM, card.getCardType()));
        assertEquals(21, player.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player.cardManager().getDiscardPile().getCards().size());

        // Activate each register.
        turnManager.advancePhase();
        for (int i = 0; i < 5; i++) {
            assertEquals(i, turnManager.getCurrentRegisterIndex());
            player.programmingRegister().getRegister(i).execute(turnManager, player);
            assertEquals(5 + 1 + i, player.robot().getEnergy());
            turnManager.advanceRegister();
        }

        // Advance to the next round.
        assertEquals(GameState.ACTIVATION_PHASE, turnManager.getCurrentPhase());
        turnManager.resetRound();

        // Assert player values.
        assertEquals(10, player.robot().getEnergy());
        assertEquals(29, player.cardManager().getTotalDeckSizes());
        assertEquals(0, player.programmingRegister().getAllRegisters().size());

        // Assert correct life cycle of again and spam cards.
        assertEquals(2, getAmountOfCardsInDecks(CardType.AGAIN, player));
        assertEquals(9, getAmountOfCardsInDecks(CardType.SPAM, player));
    }

    private LinkedList<Card> createTestDrawDeck() {
        LinkedList<Card> cards = new LinkedList<>();

        for (int i = 1; i <= 15; i++) {
            cards.add(SharedDeck.drawDamageCard(CardType.SPAM));
        }

        cards.add(ProgrammingCard.createPowerUpCard());

        for (int i = 1; i <= 2; i++) {
            cards.add(ProgrammingCard.createAgainCard());
        }

        for (int i = 1; i <= 2; i++) {
            cards.add(ProgrammingCard.createPowerUpCard());
        }

        return cards;
    }

    private Player createPlayer(int id) {
        CardManager cardManager = new CardManager();

        // Remove cards for the assertions.
        Deck drawDeck = new Deck(ProgrammingCard.createUTurnCard(), 15);
        assertEquals(15, drawDeck.getCards().size());

        // Constructs the draw deck for the test.
        cardManager.getDrawDeck().setCards(createTestDrawDeck());
        assertEquals(20, cardManager.getDrawDeck().getCards().size());
        assertEquals(5, cardManager.getDrawDeck().getCards().stream()
                .filter(card -> !card.getCardType().equals(CardType.SPAM))
                .toList()
                .size());

        cardManager.getDrawDeck().addCards(drawDeck.getCards());
        assertEquals(20, cardManager.getDrawDeck().getCards().stream()
                .filter(card -> !card.getCardType().equals(CardType.SPAM))
                .toList()
                .size());

        Robot robot = new Robot();
        robot.setPosition(new Vector(1, id)); // This id will allow the robots to move during the game.
        robot.setStartingPosition(new Vector(0, 0));
        robot.setOrientation(Orientation.RIGHT);

        ProgrammingRegister programmingRegister = new ProgrammingRegister();
        Deck upgrades = new Deck();
        Player.Flags flags = new Player.Flags();

        return new Player(id, robot, cardManager, programmingRegister, upgrades, flags);
    }

    private int getAmountOfCardsInDecks(CardType type, Player player) {
        CardManager cardManager = player.cardManager();

        return Stream.of(cardManager.getDrawDeck(), cardManager.getHand(), cardManager.getDiscardPile())
                .flatMap(deck -> deck.getCards().stream())
                .filter(card -> card.getCardType().equals(type))
                .toList()
                .size();
    }
}
