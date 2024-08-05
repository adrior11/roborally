package com.github.adrior.roborally.core.card;

import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import lombok.Getter;

/**
 * Represents an abstract card in the game.
 * Each card has a type and defines an action executed when the card is played.
 */
@Getter
public abstract class Card {
    private final CardType cardType;

    /**
     * Constructor to initialize a Card with the given type.
     *
     * @param cardType The type of the card.
     */
    protected Card(CardType cardType) {
        this.cardType = cardType;
    }

    /**
     * Executes the action associated with the card.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    public abstract void execute(TurnManager turnManager, Player player);

    /**
     * Functional interface representing an action to be performed by a card.
     */
    @FunctionalInterface
    public interface CardAction {
        void execute(TurnManager turnManager, Player player);
    }
}