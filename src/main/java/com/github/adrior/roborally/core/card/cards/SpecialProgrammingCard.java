package com.github.adrior.roborally.core.card.cards;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.MovementExecutor;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;

import java.util.List;

import static com.github.adrior.roborally.core.card.CardType.*;

/**
 * Represents a special programming {@link Card} in the game.
 * These cards have unique actions that can affect the game state in various ways.
 *
 * <p> Based on the current state of the given protocol V2.0, none of
 * these special programming cards are obtainable during an active game.
 */
public class SpecialProgrammingCard extends Card {
    private final CardAction action;

    /**
     * Constructs a new SpecialProgrammingCard with the specified parameters.
     *
     * @param cardType The type of the card.
     * @param action   The action to be performed when the card is executed.
     */
    public SpecialProgrammingCard(CardType cardType, CardAction action) {
        super(cardType);
        this.action = action;
    }


    @Override
    public void execute(TurnManager turnManager, Player player) {
        action.execute(turnManager, player);
    }


    /**
     * Creates an EnergyRoutine special programming card.
     *
     * @return An EnergyRoutine special programming card.
     */
    @NonNull public static SpecialProgrammingCard createEnergyRoutineCard() {
        return new SpecialProgrammingCard(CardType.ENERGY_ROUTINE, (_, player) -> {
            player.robot().adjustEnergy(1);
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.energy(player.clientId(), player.robot().getEnergy(), "EnergyRoutineCard"));
        });
    }


    /**
     * Creates a SandboxRoutine special programming card.
     *
     * @return A SandboxRoutine special programming card.
     */
    @NonNull public static SpecialProgrammingCard createSandboxRoutineCard() {
        return new SpecialProgrammingCard(CardType.SANDBOX_ROUTINE, (_, _) -> {
            // SandboxRoutine has no way for being called with the given protocol, as the player has to choose his action.
        });
    }


    /**
     * Creates a WeaselRoutine special programming card.
     *
     * @return A WeaselRoutine special programming card.
     */
    @NonNull public static SpecialProgrammingCard createWeaselRoutineCard() {
        return new SpecialProgrammingCard(CardType.WEASEL_ROUTINE, (_, _) -> {
            // WeaselRoutine has no way for being called with the given protocol, as the player has to choose his action.
        });
    }


    /**
     * Creates a SpeedRoutine special programming card.
     *
     * @return A SpeedRoutine special programming card.
     */
    @NonNull public static SpecialProgrammingCard createSpeedRoutineCard() {
        return new SpecialProgrammingCard(CardType.SPEED_ROUTINE, (turnManager, player) -> MovementExecutor.moveRobotSteps(
                    turnManager.getCurrentCourse(), player, turnManager.getPlayers(), 3));
    }


    /**
     * Creates a SpamFolder special programming card.
     *
     * @return A SpamFolder special programming card.
     */
    @NonNull public static SpecialProgrammingCard createSpamFolderCard() {
        return new SpecialProgrammingCard(CardType.SPAM_FOLDER, (_, player) -> {
            Card spamCard = player.cardManager().getDiscardPile().getCardByType(SPAM);

            if (null != spamCard) {
                SharedDeck.returnDamageCard(spamCard);
            }
        });
    }


    /**
     * Creates a RepeatRoutine special programming card.
     *
     * @return A RepeatRoutine special programming card.
     */
    @NonNull public static SpecialProgrammingCard createRepeatRoutineCard() {
        final List<CardType> damageCardTypes = List.of(SPAM, TROJAN, WORM, VIRUS);

        return new SpecialProgrammingCard(CardType.REPEAT_ROUTINE, (turnManager, player) -> {
            Card previousCard = player.programmingRegister().getRegister(turnManager.getCurrentRegisterIndex() - 1);

            if (damageCardTypes.contains(previousCard.getCardType())) {
                player.cardManager().popCardFromDrawDeck().execute(turnManager, player);
            } else {
                ProgrammingCard.createAgainCard().execute(turnManager, player);
            }
        });
    }
}
