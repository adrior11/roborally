package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.*;
import com.github.adrior.roborally.core.card.cards.DamageCard;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpamBlockerTest {
    private final RacingCourse course = RacingCourse.createRacingCourse(AvailableCourses.DIZZY_HIGHWAY);
    private TurnManager turnManager;
    private Player player;

    @BeforeEach
    public void setUp() {
        SharedDeck.resetDecks();
        GameManager.getInstance().resetGame();

        assertEquals(40, SharedDeck.upgradeDeck.getCards().size());

        LinkedList<Player> players = new LinkedList<>();

        player = createPlayer(1);
        player.installedUpgrades().addCard(CardFactory.createCard(CardType.SPAM_BLOCKER));
        SharedDeck.upgradeDeck.removeFirstCard();
        players.add(player);

        turnManager = new TurnManager(players, course);
        GameManager.getInstance().getIsGameActive().set(true);
    }

    @Test
    void testSpamBlocker() {
        turnManager.dealCards();
        assertEquals(11, player.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player.cardManager().getHand().getCards().size());
        assertEquals(0, player.cardManager().getDiscardPile().getCards().size());
        assertEquals(38, SharedDeck.spamDeck.getCards().size());

        player.installedUpgrades().getCardByType(CardType.SPAM_BLOCKER).execute(turnManager, player);
        assertEquals(2, player.cardManager().getDrawDeck().getCards().size());
        assertEquals(9, player.cardManager().getHand().getCards().size());
        assertEquals(0, player.cardManager().getDiscardPile().getCards().size());
        assertEquals(47, SharedDeck.spamDeck.getCards().size());

        player.cardManager().getHand().getCards().forEach(card -> assertEquals(CardType.POWER_UP, card.getCardType()));
    }

    private LinkedList<Card> createTestDrawDeck() {
        LinkedList<Card> cards = new LinkedList<>();

        for (int i = 1; i <= 9; i++) {
            cards.add(DamageCard.createSpamCard());
        }

        for (int i = 1; i <= 11; i++) {
            cards.add(ProgrammingCard.createPowerUpCard());
        }

        return cards;
    }

    private Player createPlayer(int id) {
        CardManager cardManager = new CardManager();

        // Constructs the draw deck for the test.
        cardManager.getDrawDeck().setCards(createTestDrawDeck());
        assertEquals(20, cardManager.getDrawDeck().getCards().size());

        Robot robot = new Robot();
        robot.setPosition(new Vector(1, id)); // This id will allow the robots to move during the game.
        robot.setStartingPosition(new Vector(0, 0));
        robot.setOrientation(Orientation.RIGHT);

        ProgrammingRegister programmingRegister = new ProgrammingRegister();
        Deck upgrades = new Deck();
        Player.Flags flags = new Player.Flags();

        return new Player(id, robot, cardManager, programmingRegister, upgrades, flags);
    }
}
