package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.utility.Orientation;
import lombok.NonNull;

import java.util.Locale;

public class RebootDirectionHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();
        GameManager gameManager = GameManager.getInstance();
        Player player = gameManager.getPlayerByID(clientId);

        // Assert if the player is rebooting.
        if (!player.flags().isRebooting()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Your either not rebooting or have chosen your reboot direction too late."), clientId);
            return;
        }

        // Assert the correct input of the new orientation.
        String direction = message.messageBody().get("direction").toString();
        Orientation newOrientation;
        try {
            newOrientation = Orientation.valueOf(direction.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Invalid reboot direction provided"), clientId);
            return;
        }

        // Update the player's orientation.
        Orientation oldOrientation = player.robot().getOrientation();
        player.robot().setOrientation(newOrientation);
        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s choose %s as the reboot direction", clientId, newOrientation));

        int steps = newOrientation.getRotationStepsTo(oldOrientation);
        if (3 != steps) {
            for (int i = 0; i < newOrientation.getRotationStepsTo(oldOrientation); i++) {
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(clientId, "counterclockwise"));
            }
        } else {
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(clientId, "clockwise"));
        }
    }
}
