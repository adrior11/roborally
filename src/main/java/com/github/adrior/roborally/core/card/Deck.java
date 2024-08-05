package com.github.adrior.roborally.core.card;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Deck class represents a deck of cards. It holds the cards in a specific order
 * and provides methods to manipulate the deck.
 *
 * @see Card
 */
@Getter @Setter
public class Deck {
    private LinkedList<Card> cards;

    /**
     * Constructs an empty Deck.
     */
    public Deck() {
        this.cards = new LinkedList<>();
    }


    /**
     * Constructs a Deck initialized with the specified list of cards.
     *
     * @param cards the list of cards to initialize the deck with.
     */
    public Deck(@NonNull List<Card> cards) {
        this.cards = new LinkedList<>(cards);
    }


    /**
     * Constructs a Deck initialized with a specified number of duplicates of a single card.
     *
     * @param card the card to duplicate.
     * @param count the number of duplicates.
     */
    public Deck(Card card, int count) {
        this.cards = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            cards.add(card);
        }
    }


    /**
     * Adds a card to the deck.
     *
     * @param card the card to add.
     */
    public void addCard(Card card) {
        cards.add(card);
    }


    /**
     * Adds a list of cards to the deck.
     *
     * @param cards the cards to add.
     */
    public void addCards(List<Card> cards) {
        this.cards.addAll(cards);
    }


    /**
     * Adds a card to the top of the deck
     *
     * @param card the card to add.
     */
    public void addCardToTop(Card card) {
        cards.addFirst(card);
    }


    /**
     * Draws a specified number of cards from the deck. And removes them from the deck.
     *
     * @param numberOfCards the number of cards to draw.
     * @return the drawn cards. As a {@link LinkedList}
     */
    @NonNull public List<Card> drawCards(int numberOfCards) {
        List<Card> drawnCards = new LinkedList<>();
        for (int i = 0; i < numberOfCards; i++) {
            drawnCards.add(cards.removeFirst());
        }
        return drawnCards;
    }


    /**
     * Removes a card from the deck.
     *
     * @param card the card to remove.
     */
    private void removeCard(Card card) {
        cards.remove(card);
    }


    /**
     * Removes and returns the first card from the deck.
     *
     * @return the first card from the deck, or null if the deck is empty.
     */
    public Card removeFirstCard() {
        return cards.removeFirst();
    }


    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }


    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }


    /**
     * Gets the first card from the list of cards.
     *
     * @param type The card type for the card to get.
     * @return the first card, which matches the specified type, or null if no such card exists.
     */
    public Card getCardByType(@NonNull CardType type) {
        return cards.stream()
                .filter(card -> card.getCardType() == type)
                .findFirst()
                .orElse(null);
    }


    /**
     * Gets and removes the first card from the list of cards.
     *
     * @param type The card type for the card to be retrieved.
     * @return the retrieved card from the deck.
     */
    public Card retrieveCardByType(CardType type) {
        Card retrievedCard = getCardByType(type);
        removeCard(retrievedCard);
        return retrievedCard;
    }


    /**
     * Retrieves an array of all card names in the deck.
     *
     * @return an array of all card names.
     */
    @NonNull public String[] getAllCardNames() {
        return cards.stream()
                .map(card -> card.getCardType().toString())
                .toArray(String[]::new);
    }


    /**
     * Resets the deck to the specified initial state.
     * Clears the current deck and adds all cards from the given list.
     *
     * @param cards The list of cards to reset the deck to.
     */
    public void reset(List<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
    }
}
