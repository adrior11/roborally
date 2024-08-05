package com.github.adrior.roborally.core.player;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardFactory;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.Deck;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The CardManager class handles the management of cards for a player, including
 * the draw deck, discard pile, and hand. It provides functionality to draw cards,
 * play cards, and shuffle the discard pile back into the draw deck when needed.
 *
 * @see Card
 * @see Deck
 */
@Getter
public class CardManager {
    @NonNull private final Deck drawDeck;
    @NonNull private final Deck hand;
    @NonNull private final Deck discardPile;

    /**
     * Constructs a new CardManager.
     */
    public CardManager() {
        this.drawDeck = createInitialDrawDeck();
        this.hand = new Deck();
        this.discardPile = new Deck();
        shuffleDrawDeck();
    }


    /**
     * Creates the initial deck for the player with predefined card distributions.
     *
     * @return the Deck initialized with the predefined cards.
     */
    @NonNull
    private static Deck createInitialDrawDeck() {
        List<Card> cards = new ArrayList<>();

        // Add the specific number of each card type for the programming draw deck.
        addCards(cards, CardType.MOVE_I, 5);
        addCards(cards, CardType.MOVE_II, 3);
        addCards(cards, CardType.MOVE_III, 1);
        addCards(cards, CardType.TURN_RIGHT, 3);
        addCards(cards, CardType.TURN_LEFT, 3);
        addCards(cards, CardType.U_TURN, 1);
        addCards(cards, CardType.BACK_UP, 1);
        addCards(cards, CardType.POWER_UP, 1);
        addCards(cards, CardType.AGAIN, 2);

        return new Deck(cards);
    }


    /**
     * Adds the specified number of cards of a given type to the card list.
     *
     * @param cards the list to add cards to.
     * @param cardType the type of card to add.
     * @param count the number of cards to add.
     */
     private static void addCards(@NonNull List<Card> cards, @NonNull CardType cardType, int count) {
        for (int i = 0; i < count; i++) {
            cards.add(CardFactory.createCard(cardType));
        }
    }


    /**
     * Draws a specified number of cards from the draw deck to the hand.
     * If the draw deck does not have enough cards, the discard pile is shuffled
     * back into the draw deck.
     *
     * @param numberOfCards the number of cards to draw.
     */
    public void drawCards(int numberOfCards) {
        hand.addCards(drawDeck.drawCards(numberOfCards));
    }


    /**
     * Retrieves a single card from the top of the draw deck.
     *
     * @return The top card from the draw deck.
     */
    public Card popCardFromDrawDeck() {
        if (assertDrawDeckSize(1)) reshuffleDiscardPileIntoDeck();
        return drawDeck.removeFirstCard();
    }


    /**
     * Adds card to hand.
     *
     * @param card to add.
     */
    public void addCardToHand(Card card) {
        hand.addCard(card);
    }


    /**
     * Discards the remaining cards in the hand to the discard pile.
     */
    public void discardHand() {
        while (!hand.isEmpty()) {
            discardPile.addCard(hand.removeFirstCard());
        }
    }


    /**
     * Adds a card to the discard pile.
     *
     * @param card The card to be added to the discard pile.
     */
    public void addCardToDiscardPile(Card card) {
        discardPile.addCard(card);
    }


    /**
     * Reshuffles the discard pile back into the draw deck.
     */
    public void reshuffleDiscardPileIntoDeck() {
        while (!discardPile.isEmpty()) drawDeck.addCard(discardPile.removeFirstCard());
        shuffleDrawDeck();
    }


    /**
     * Shuffles the draw deck.
     */
    private void shuffleDrawDeck() {
        drawDeck.shuffle();
    }


    /**
     * This will remove one card of the specified type from the hand.
     *
     * @param type The card type for the card to be removed.
     * @return The retrieved card, else null.
     */
    public Card retrieveCardFromHandByType(CardType type) {
        return hand.retrieveCardByType(type);
    }


    /**
     * Asserts if the draw deck size is not enough.
     *
     * @param numberOfCards The number of cards to assert the draw deck with.
     * @return true, if there are not enough cards in the draw deck, to draw numberOfCards, else false.
     */
    public boolean assertDrawDeckSize(int numberOfCards) {
        return (drawDeck.getCards().size() < numberOfCards);
    }


    /**
     * Assert if the hand contains a card given by the type.
     *
     * @param type The card type to assert the hand for.
     * @return true, if the hand contains that card, else false.
     */
    public boolean hasCardTypeInHand(CardType type) {
        return hand.getCards().stream().anyMatch(card -> card.getCardType() == type);
    }


    /**
     * Retrieves the count of all cards contained in the card manager decks.
     *
     * @return the count of all card manager cards.
     */
    public int getTotalDeckSizes() {
        return drawDeck.getCards().size() + hand.getCards().size() + discardPile.getCards().size();
    }

    @Override
    public String toString() {
        int drawDeckSize = drawDeck.getCards().size();
        int handSize = hand.getCards().size();
        int discardPileSize = discardPile.getCards().size();

        return String.format("[Draw: %s | Hand: %s | Discard: %s {Total: %s}]",
                drawDeckSize, handSize, discardPileSize, (drawDeckSize + handSize + discardPileSize));
    }
}
