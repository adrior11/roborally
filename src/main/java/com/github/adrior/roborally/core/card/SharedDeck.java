package com.github.adrior.roborally.core.card;

import com.github.adrior.roborally.core.card.cards.DamageCard;
import com.github.adrior.roborally.core.card.cards.UpgradeCard;
import com.github.adrior.roborally.exceptions.InvalidGameStateException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that holds all the damage and upgrade {@link Card} related decks that are shared between all players.
 * This ensures that the total number of cards during a game-lifecycle stays constant.
 *
 * @see DamageCard
 * @see UpgradeCard
 */
@UtilityClass
public class SharedDeck {
    @NonNull public static final Deck spamDeck;
    @NonNull public static final Deck trojanDeck;
    @NonNull public static final Deck wormDeck;
    @NonNull public static final Deck virusDeck;
    @NonNull public static final Deck upgradeDeck;

    @NonNull private static final List<Card> initialSpamDeck;
    @NonNull private static final List<Card> initialTrojanDeck;
    @NonNull private static final List<Card> initialWormDeck;
    @NonNull private static final List<Card> initialVirusDeck;
    @NonNull private static final List<Card> initialUpgradeDeck;

    private static final String ERROR_MESSAGE = "Unexpected card type: ";

    static {
        initialSpamDeck = createInitialDeck(DamageCard.createSpamCard(), 38);
        initialTrojanDeck = createInitialDeck(DamageCard.createTrojanCard(), 12);
        initialWormDeck = createInitialDeck(DamageCard.createWormCard(), 6);
        initialVirusDeck = createInitialDeck(DamageCard.createVirusCard(), 18);
        initialUpgradeDeck = createInitialUpgradeDeck();

        spamDeck = new Deck(initialSpamDeck);
        trojanDeck = new Deck(initialTrojanDeck);
        wormDeck = new Deck(initialWormDeck);
        virusDeck = new Deck(initialVirusDeck);
        upgradeDeck = new Deck(initialUpgradeDeck);
        upgradeDeck.shuffle();
    }


    /**
     * Helper method to create an initial deck.
     */
    @NonNull private static List<Card> createInitialDeck(Card card, int count) {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            deck.add(card);
        }
        return deck;
    }


    /**
     * Helper method to create the initial upgrade deck.
     */
    @NonNull private static List<Card> createInitialUpgradeDeck() {
        List<Card> upgradeCards = new ArrayList<>();
        for (int i = 0; 10 > i; i++) {
            upgradeCards.add(UpgradeCard.createAdminPrivilegeCard());
            upgradeCards.add(UpgradeCard.createRearLaserCard());
            upgradeCards.add(UpgradeCard.createMemorySwapCard());
            upgradeCards.add(UpgradeCard.createSpamBlockerCard());
        }
        return upgradeCards;
    }


    /**
     * Resets all decks to their initial state.
     */
    public static void resetDecks() {
        spamDeck.reset(initialSpamDeck);
        trojanDeck.reset(initialTrojanDeck);
        wormDeck.reset(initialWormDeck);
        virusDeck.reset(initialVirusDeck);
        upgradeDeck.reset(initialUpgradeDeck);
        upgradeDeck.shuffle();
    }


    /**
     * Retrieves a damage card from the specified deck based on the card type.
     *
     * @param type the card type for the card to be retrieved.
     * @return the card if available, otherwise null.
     */
    public static Card drawDamageCard(@NonNull CardType type) {
        return switch (type) {
            case SPAM   -> drawFromDeck(spamDeck);
            case TROJAN -> drawFromDeck(trojanDeck);
            case WORM   -> drawFromDeck(wormDeck);
            case VIRUS  -> drawFromDeck(virusDeck);
            default -> throw new IllegalStateException(ERROR_MESSAGE + type);
        };
    }


    /**
     * Returns a damage card to the appropriate deck based on its card type.
     *
     * @param card the card to be returned to the deck.
     */
    public static void returnDamageCard(@NonNull Card card) {
        switch (card.getCardType()) {
            case SPAM   -> spamDeck.addCard(card);
            case TROJAN -> trojanDeck.addCard(card);
            case WORM   -> wormDeck.addCard(card);
            case VIRUS  -> virusDeck.addCard(card);
            default -> throw new IllegalStateException(ERROR_MESSAGE + card.getCardType());
        }
    }


    /**
     * Helper method to draw a damage card from a specified deck.
     *
     * @param deck the deck to draw a card from.
     * @return an Optional containing the card if available, otherwise null.
     */
    private static Card drawFromDeck(@NonNull Deck deck) {
        return deck.removeFirstCard();
    }


    /**
     * Draws a specified number of cards from a given deck.
     *
     * @param type the card type for the cards to be retrieved.
     * @param count the number of cards to draw.
     * @return a list of drawn cards.
     */
    public static List<Card> drawCards(@NonNull CardType type, int count) {
        Deck deck = switch (type) {
            case SPAM   -> spamDeck;
            case TROJAN -> trojanDeck;
            case WORM   -> wormDeck;
            case VIRUS  -> virusDeck;
            default -> throw new IllegalStateException(ERROR_MESSAGE + type);
        };

        if (deck.getCards().size() < count) return Collections.emptyList();

        return deck.drawCards(count);
    }


    /**
     * Retrieves a string array containing the names of the shared damage decks that contain at least a specified number of cards.
     *
     * @param minCardCount the minimum number of cards required in the deck.
     * @return the string array containing the names of the decks that meet the criteria.
     */
    @NonNull public static String[] assertSharedDeckSizes(int minCardCount) {
        List<String> availablePiles = new ArrayList<>();
        if (spamDeck.getCards().size()  >= minCardCount) availablePiles.add("Spam");
        if (virusDeck.getCards().size() >= minCardCount) availablePiles.add("Trojan");
        if (wormDeck.getCards().size()  >= minCardCount) availablePiles.add("Worm");
        if (virusDeck.getCards().size() >= minCardCount) availablePiles.add("Virus");

        if (availablePiles.isEmpty()) {
            throw new InvalidGameStateException("Congratulations, you've managed to clear the complete shared deck!");
        }

        return availablePiles.toArray(new String[0]);
    }
}

