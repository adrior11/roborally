package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.cards.UpgradeCard;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

import static com.github.adrior.roborally.core.card.CardType.*;

public class BuyUpgradeHandler implements IMessageHandler<ClientHandler> {

    private static final Set<CardType> UPGRADE_CARDS = Set.of(ADMIN_PRIVILEGE, REAR_LASER, MEMORY_SWAP, SPAM_BLOCKER);

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;
        if (!AssertionHelper.assertValidPhase(clientHandler, GameState.UPGRADE_PHASE)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();

        if (!AssertionHelper.assertPlayerTurn(clientHandler, clientId)) return;

        GameManager gameManager = GameManager.getInstance();
        TurnManager turnManager = gameManager.getTurnManager();

        // Assert if the player wants to buy an upgrade card.
        Player player = gameManager.getPlayerByID(clientId);
        boolean isBuying = TypeCastingHelper.getBooleanFromMessage(clientHandler, message, "isBuying");

        if (!isBuying) {
            ServerCommunicationFacade.log(String.format(
                    "<Handler> Player %s decided not to buy an upgrade card. Advancing...", clientId));
            player.flags().setDecidedUpgradePhase(true);
            turnManager.advanceUpgradePhase();
            return;
        }

        // Retrieve the card type given by the player.
        String card = (String) message.messageBody().get("card");
        if (!AssertionHelper.assertValidCardType(clientHandler, card)) return;
        CardType cardType = CardType.fromString(card);

        // Assert if the given card is an actual upgrade card.
        if (!UPGRADE_CARDS.contains(cardType)) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "The selected card is not an valid upgrade card"), clientId);
            return;
        }

        // Assert if upgrade shop contains the selected card.
        UpgradeCard upgradeCard = (UpgradeCard) turnManager.getUpgradeShop().retrieveCardByType(cardType);

        if (null == upgradeCard) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "The upgrade shop does not contain the selected card"), clientId);
            return;
        }

        // Assert if the player has enough energy to buy the upgrade card.
        if (upgradeCard.getCost() > player.robot().getEnergy()) {
            turnManager.getUpgradeShop().addCard(upgradeCard);
            clientHandler.sendMessage(PredefinedServerMessages.error(
                    "You don't have enough energy to afford this upgrade card"));
            return;
        }

        // Assert if the player has 3 upgrade cards of the given type installed.
        List<Card> upgrades = player.installedUpgrades().getCards().stream()
                .filter(upgrade -> upgradeCard.getUpgradeType() == ((UpgradeCard) upgrade).getUpgradeType())
                .toList();

        if (3 <= upgrades.size()) {
            turnManager.getUpgradeShop().addCard(upgradeCard);
            clientHandler.sendMessage(PredefinedServerMessages.error(String.format(
                    "You've already installed 3 %s upgrade cards", upgradeCard.getUpgradeType().toString())));
            return;
        }

        // Notify the players about the buyers adjusted energy.
        player.robot().adjustEnergy(-upgradeCard.getCost());
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.energy(clientId, player.robot().getEnergy(), "UpgradeShop"));

        // Notify the players about the bought card.
        player.installedUpgrades().addCard(upgradeCard);
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.upgradeBought(clientId, upgradeCard.getCardType().toString()));


        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s paid %s energy to buy: %s", clientId, -upgradeCard.getCost(), upgradeCard.getCardType().toString()));
        player.flags().setDecidedUpgradePhase(true);
        turnManager.advanceUpgradePhase();
    }
}
