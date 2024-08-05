package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.StartPoint;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import com.github.adrior.roborally.random_bot.RandomBotData.Robot;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CurrentPlayerHandler implements IMessageHandler<RandomBot> {

    private static final Random random = new Random();

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");

        client.sleepMilliseconds(100);

        List<PositionedTile<StartPoint>> startPoints = RacingCourse.getPositionedTilesOfType(client.getMap(), StartPoint.class);

        if (client.getClientId() == clientId) {
            if (0 == client.getCurrentPhase().get()) {
                client.log("Selecting a starting position");
                selectStartingPoint(client, startPoints);
            } else if (1 == client.getCurrentPhase().get()) {
                client.sendMessage(PredefinedClientMessages.buyUpgrade(false, "Null"));
            } else if (3 == client.getCurrentPhase().get()) {
                client.sendMessage(PredefinedClientMessages.playCard(client.getCurrentCard()));
            }
        }
    }

    private void selectStartingPoint(@NonNull RandomBot client, @NonNull List<PositionedTile<StartPoint>> startPoints) {
        List<Vector> takenPositions = client.getClients().stream()
                .map(RandomBotData::getRobot)
                .map(Robot::getPosition)
                .filter(Objects::nonNull)
                .toList();

        List<Vector> availableStartPositions = startPoints.stream()
                .map(PositionedTile::position)
                .filter(position -> !takenPositions.contains(position))
                .toList();

        Vector chosenPosition = 1 < availableStartPositions.size()
                ? availableStartPositions.get(random.nextInt(availableStartPositions.size()-1))
                : availableStartPositions.getFirst();

        int x = chosenPosition.x();
        int y = chosenPosition.y();

        client.sendMessage(PredefinedClientMessages.setStartingPoint(x, y));
    }
}

