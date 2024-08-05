package com.github.adrior.roborally.core.card.cards;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.MovementExecutor;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.exceptions.InvalidGameStateException;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

/**
 * Represents a damage {@link Card} in the game.
 * These cards have negative effects and can disrupt the player's strategy.
 */
public class DamageCard extends Card {
    private final CardAction action;

    /**
     * Constructs a new DamageCard with the specified parameters.
     *
     * @param cardType The type of the card.
     * @param action   The action to be performed when the card is executed.
     */
    public DamageCard(CardType cardType, CardAction action) {
        super(cardType);
        this.action = action;
    }


    @Override
    public void execute(TurnManager turnManager, Player player) {
        action.execute(turnManager, player);
    }


    /**
     * Creates a Spam damage card.
     *
     * @return A Spam damage card.
     */
    @NonNull public static DamageCard createSpamCard() {
        return new DamageCard(CardType.SPAM, (turnManager, player) -> {
            int register = turnManager.getCurrentRegisterIndex();
            try {
                SharedDeck.returnDamageCard(player.programmingRegister().removeCardFromRegister(register));
            } catch (IllegalStateException e) {
                throw new InvalidGameStateException("An Again card will never be able to play a spam card from the previous register.");
            }

            Card replacement = player.cardManager().popCardFromDrawDeck();
            player.programmingRegister().setRegister(register, replacement);

            ServerCommunicationFacade.broadcast(PredefinedServerMessages.replaceCard(register, replacement.getCardType().toString(), player.clientId()));
            replacement.execute(turnManager, player);
        });
    }


    /**
     * Creates a Trojan damage card.
     *
     * @return A Trojan damage card.
     */
    @NonNull public static DamageCard createTrojanCard() {
        return new DamageCard(CardType.TROJAN, (_, player) -> MovementExecutor.drawTwoSpamCards(player));
    }


    /**
     * Creates a Worm damage card.
     *
     * @return A Worm damage card.
     */
    @NonNull public static DamageCard createWormCard() {
        return new DamageCard(CardType.WORM, (turnManager, player) -> {
            RacingCourse course = turnManager.getCurrentCourse();
            Vector position = player.robot().getPosition();
            MovementExecutor.reboot(course, player, position, turnManager.getPlayers());
        });
    }


    /**
     * Creates a Virus damage card.
     *
     * @return A Virus damage card.
     */
    @NonNull public static DamageCard createVirusCard() {
        return new DamageCard(CardType.VIRUS, (turnManager, p) -> {
            Vector robotPosition = p.robot().getPosition();

            for (Player player : turnManager.getPlayers()) {
                if (6 >= player.robot().getPosition().manhattanDistanceTo(robotPosition)) {
                    Card virusCard = SharedDeck.drawDamageCard(CardType.VIRUS);

                    if (null != virusCard) {
                        player.cardManager().addCardToDiscardPile(virusCard);

                        ServerCommunicationFacade.broadcast(PredefinedServerMessages.drawDamage(player.clientId(), new String[]{"Virus"}));
                    } else {
                        String[] availablePiles;
                        try {
                            availablePiles = SharedDeck.assertSharedDeckSizes(1);
                        } catch (InvalidGameStateException e) {
                            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(e.getMessage()), player.clientId());
                            return;
                        }

                        player.flags().setAwaitingDamageSelection(true);
                        ServerCommunicationFacade.sendMessage(PredefinedServerMessages.pickDamage(1, availablePiles), player.clientId());
                    }
                }
            }
        });
    }
}
