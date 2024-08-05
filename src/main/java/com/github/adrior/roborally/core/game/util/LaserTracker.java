package com.github.adrior.roborally.core.game.util;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Laser;
import com.github.adrior.roborally.core.tile.tiles.Wall;
import com.github.adrior.roborally.exceptions.InvalidGameStateException;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.core.game.recorder.HitRecorder;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The LaserTracker class simulates the behavior of lasers in a game, determining their path and effects upon hitting robots.
 * It supports lasers fired from board positions as well as lasers fired by robots, considering only the first robot hit in the line of sight.
 *
 * @see Player
 * @see Laser
 * @see Wall
 * @see PositionedTile
 */
@UtilityClass
public class LaserTracker {

    /**
     * Tracks all lasers on the course, checking their interactions with robots and walls.
     *
     * @param lasers The list of positioned laser tiles.
     * @param players The set of robots on the course.
     * @param walls The list of positioned wall tiles.
     */
    public static void trackTileLaser(@NonNull List<PositionedTile<Laser>> lasers, @NonNull  List<Player> players, @NonNull  List<PositionedTile<Wall>> walls) {
        for (PositionedTile<Laser> laser : lasers) {
            Vector position = laser.position();
            Orientation orientation = laser.tile().getLaserOrientation();
            int laserCount = laser.tile().getLaserCount();

            Objects.requireNonNull(position, "Laser position cannot be null");
            Objects.requireNonNull(orientation, "Laser orientation cannot be null");
            trackLaser(position, orientation, players, walls, laserCount);
        }
    }


    /**
     * Tracks the path of each robot laser, firing in the orientation the robots are facing.
     * The laser starts from the position one tile ahead in the robot's orientation.
     * The laser stops when it hits the first robot or a wall.
     *
     * @param players The set of robots on the course.
     * @param walls The list of positioned wall tiles.
     */
    public static void trackRobotLaser(@NonNull List<Player> players, @NonNull List<PositionedTile<Wall>> walls) {
        for (Player player : players) {
            if (player.flags().isRebooting()) continue;

            Vector position = player.robot().getPosition();
            Orientation orientation = player.robot().getOrientation();

            Objects.requireNonNull(position, "Robot position cannot be null");
            Objects.requireNonNull(orientation, "Robot orientation cannot be null");

            trackLaser(position.add(orientation.getVector()), orientation, players, walls, 1);
            if (null != player.installedUpgrades().getCardByType(CardType.REAR_LASER)) {
                Orientation rearOrientation = orientation.uTurn();
                trackLaser(position.add(rearOrientation.getVector()), rearOrientation, players, walls, 1);
            }
        }
    }


    /**
     * Simulates the firing of lasers by robots, taking into account their positions and orientations.
     * It stops the laser when it hits the first robot or a wall.
     *
     * @param start The starting position of the laser.
     * @param orientation The vector orientation of the laser.
     * @param players The set of robot positions that can be hit by the laser.
     * @param walls The list of positioned walls that block the laser.
     * @param laserCount The number of damage cards to apply when the laser hits a robot.
     */
    private static void trackLaser(@NonNull Vector start, @NonNull Orientation orientation, @NonNull List<Player> players, @NonNull List<PositionedTile<Wall>> walls, int laserCount) {
        Vector current = start;
        int steps = 0;          // Safeguard against infinite loops.
        int maxSteps = 100;     // Maximum steps to prevent infinite loops.

        // Filter out robots that are aligned with the laser's path in the given orientation.
        List<Player> alignedPlayers = players.stream()
                .filter(player -> start.isAligned(player.robot().getPosition(), orientation))
                .toList();

        // Exit early if there are no robots in the laser's potential path.
        if (alignedPlayers.isEmpty()) return;

        // Filter out walls that are aligned with the laser's path in the given orientation.
        Vector finalCurrent = current;
        List<PositionedTile<Wall>> alignedWalls = walls.stream()
                .filter(wall -> start.isAligned(wall.position(), orientation) || wall.position().equals(finalCurrent))
                .toList();

        while (steps < maxSteps) {
            // Check if there's a wall at the current position that blocks the laser.
            Vector finalCurrent1 = current;
            boolean isNoWallOnTile = alignedWalls.stream()
                    .filter(wall -> wall.position().equals(finalCurrent1))
                    .anyMatch(wall -> !wall.tile().canMoveOut(orientation));

            // Calculate the next position of the laser.
            Vector next = current.add(orientation.getVector());

            // Check if there's a wall at the next position that blocks the laser.
            boolean isNoWallOnNextTile = alignedWalls.stream()
                    .filter(wall -> wall.position().equals(next))
                    .anyMatch(wall -> !wall.tile().canMoveTo(orientation));

            // Stop the laser if it hits a wall.
            if (isNoWallOnTile || isNoWallOnNextTile) return;

            // Check if the laser hits a robot at the current position.
            Vector finalCurrent2 = current;
            Optional<Player> hitPlayer = alignedPlayers.stream()
                    .filter(player -> finalCurrent2.equals(player.robot().getPosition()))
                    .findFirst();

            // Apply damage to the robot and stop the laser if it hits one.
            if (hitPlayer.isPresent()) {
                applyDamage(hitPlayer.get(), laserCount);
                return; // Stop after hitting the first robot.
            }

            // Move the laser to the next position.
            current = current.add(orientation.getVector());
            steps++;
        }

        // Safeguard against infinite loops.
        throw new IllegalStateException("Laser tracking exceeded maximum number of steps, possible infinite loop detected.");
    }


    /**
     * Records the impact of a laser hitting a robot and deals out the respective number
     * of damage cards from the {@link SharedDeck} based on the {@code laserCount}.
     *
     * @see Card
     * @see CardType#SPAM
     * @see HitRecorder
     *
     * @param player The robot that got hit by a laser at the player.
     */
    private static void applyDamage(@NonNull Player player, int laserCount) {
        ServerCommunicationFacade.log(String.format("<LaserTracker> Player %s's Robot at %s takes %s damage.",
                player.clientId(), player.robot().getPosition().toString(), laserCount));

        // Retrieve the damage cards if possible.
        List<Card> damageCards = SharedDeck.drawCards(CardType.SPAM, laserCount);

        if (!damageCards.isEmpty()) {
            String[] damageCardStrings = new String[laserCount];
            Arrays.fill(damageCardStrings, CardType.SPAM.toString());

            damageCards.forEach(card -> player.cardManager().addCardToDiscardPile(card));

            ServerCommunicationFacade.broadcast(PredefinedServerMessages.drawDamage(player.clientId(), damageCardStrings));
        } else {
            String[] availablePiles;
            try {
                availablePiles = SharedDeck.assertSharedDeckSizes(2);
            } catch (InvalidGameStateException e) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(e.getMessage()), player.clientId());
                return;
            }

            player.flags().setAwaitingDamageSelection(true);
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.pickDamage(laserCount, availablePiles), player.clientId());
        }

        HitRecorder.recordHit(player.robot().getPosition());
    }
}
