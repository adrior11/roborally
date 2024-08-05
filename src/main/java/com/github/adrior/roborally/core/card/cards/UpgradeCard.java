package com.github.adrior.roborally.core.card.cards;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an upgrade {@link Card} in the game.
 * Upgrade cards can be either permanent or temporary and have an associated cost and description.
 * Each upgrade card has a specific action that it performs when executed.
 */
@Getter
public class UpgradeCard extends Card {
    private final Integer cost;
    private final UpgradeType upgradeType;
    private final String description;
    private final CardAction action;

    /**
     * Constructs a new UpgradeCard with the specified parameters.
     *
     * @param cardType    The type of the card.
     * @param cost        The cost of the card.
     * @param upgradeType The type of upgrade (permanent or temporary).
     * @param description The description of the card's effect.
     * @param action      The action to be performed when the card is executed.
     */
    public UpgradeCard(CardType cardType, Integer cost, UpgradeType upgradeType, String description, CardAction action) {
        super(cardType);
        this.cost = cost;
        this.upgradeType = upgradeType;
        this.description = description;
        this.action = action;
    }


    @Override
    public void execute(TurnManager turnManager, Player player) {
        action.execute(turnManager, player);
    }


    /**
     * Enum representing the type of upgrade card (permanent or temporary).
     */
    public enum UpgradeType {
        PERMANENT,
        TEMPORARY
    }


    /**
     * Creates an AdminPrivilege upgrade card.
     *
     * @return An AdminPrivilege upgrade card.
     */
    @NonNull public static UpgradeCard createAdminPrivilegeCard() {
        return new UpgradeCard(CardType.ADMIN_PRIVILEGE, 3, UpgradeType.PERMANENT,
                "Once per round, you may give your robot priority for one register.", (_, _) -> {
            // AdminPrivilege has no active action that will be executed as it has a distinct message type.
        });
    }


    /**
     * Creates a RearLaser upgrade card.
     *
     * @return A RearLaser upgrade card.
     */
    @NonNull public static UpgradeCard createRearLaserCard() {
        return new UpgradeCard(CardType.REAR_LASER, 2, UpgradeType.PERMANENT,
                "Your robot may shoot backward as well as forward.", (_, _) -> {
            // RearLaser has no active action that will be executed as it is a passive ability.
        });
    }


    /**
     * Creates a MemorySwap upgrade card.
     *
     * @return A MemorySwap upgrade card.
     */
    @NonNull public static UpgradeCard createMemorySwapCard() {
        final String MEMORY_SWAP_DESCRIPTION = "Draw three cards. Then choose three from your hand to put on top of your deck.";

        return new UpgradeCard(CardType.MEMORY_SWAP, 1, UpgradeType.TEMPORARY, MEMORY_SWAP_DESCRIPTION, (_, player) -> {
            // Clear all player registers to prevent issues.
            List<Card> cardsInRegister = player.programmingRegister().removeAllCardsFromRegister();
            player.cardManager().getHand().addCards(cardsInRegister);

            // Inform the players about the cleared registers.
            if (!cardsInRegister.isEmpty()) {
                for (int register = 1; 6 > register; register++) {
                    ServerCommunicationFacade.broadcast(PredefinedServerMessages.cardSelected(player.clientId(), register, false));
                }
            }

            // Assert if the player can draw the three cards.
            int numberOfCards = 3;
            if (player.cardManager().assertDrawDeckSize(numberOfCards)) {
                player.cardManager().reshuffleDiscardPileIntoDeck();
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.shuffleCoding(player.clientId()));
            }
            player.cardManager().drawCards(numberOfCards);

            // Inform the player about his new hand and set his awaiting upgrade card flag to true, allowing to discard some.
            player.flags().setAwaitingUpgradeCard(true);
            ServerCommunicationFacade.sendMessage(
                    PredefinedServerMessages.yourCards(player.cardManager().getHand().getAllCardNames()), player.clientId());
        });
    }


    /**
     * Creates a SpamBlocker upgrade card.
     *
     * @return A SpamBlocker upgrade card.
     */
    @NonNull public static UpgradeCard createSpamBlockerCard() {
        final String SPAM_BLOCKER_DESCRIPTION = "Replace each SPAM damage card in your hand with a card from the top of your deck. " +
                "Immediately discard the SPAM damage cards by placing them in the SPAM card draw pile. " +
                "If you draw new SPAM damage cards from your deck, keep them in your hand for this round.";

        return new UpgradeCard(CardType.SPAM_BLOCKER, 3, UpgradeType.TEMPORARY, SPAM_BLOCKER_DESCRIPTION, (_, player) -> {
            // Clear all player registers to prevent issues.
            List<Card> cardsInRegister = player.programmingRegister().removeAllCardsFromRegister();
            player.cardManager().getHand().addCards(cardsInRegister);

            // Inform the players about the cleared registers.
            if (!cardsInRegister.isEmpty()) {
                for (int register = 1; 6 > register; register++) {
                    ServerCommunicationFacade.broadcast(PredefinedServerMessages.cardSelected(player.clientId(), register, false));
                }
            }

            List<Card> cardsInHand = new ArrayList<>(player.cardManager().getHand().getCards());
            List<Card> newHand = new ArrayList<>();

            int cardsReplaced = 0;
            for (Card card : cardsInHand) {
                if (card.getCardType().equals(CardType.SPAM)) {
                    cardsReplaced++;
                    SharedDeck.returnDamageCard(card);
                    Card replacementCard = player.cardManager().popCardFromDrawDeck();
                    newHand.add(replacementCard);
                } else {
                    newHand.add(card);
                }
            }

            player.cardManager().getHand().reset(newHand);

            String[] newHandCardStrings = player.cardManager().getHand().getAllCardNames();
            ServerCommunicationFacade.log(String.format("<SpamBlocker> Replaced %s cards: %s", cardsReplaced, Arrays.toString(newHandCardStrings)));
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.yourCards(newHandCardStrings));
        });
    }
}

