package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.SharedDeck;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SharedDeckTest {

    @Test
    void testSharedUpgradeDeck() {
        SharedDeck.resetDecks();
        assertEquals(40, SharedDeck.upgradeDeck.getCards().size());

        List<Card> cards = SharedDeck.upgradeDeck.drawCards(5);
        assertEquals(5, cards.size());
        assertEquals(35, SharedDeck.upgradeDeck.getCards().size());

        SharedDeck.upgradeDeck.addCard(cards.removeFirst());
        assertEquals(4, cards.size());
        assertEquals(36, SharedDeck.upgradeDeck.getCards().size());

        SharedDeck.resetDecks();
        assertEquals(40, SharedDeck.upgradeDeck.getCards().size());
    }
}
