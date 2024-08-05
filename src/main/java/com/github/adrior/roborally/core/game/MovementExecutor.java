package com.github.adrior.roborally.core.game;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.map.data.RacingCourseData.Rule;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.exceptions.InvalidGameStateException;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import com.github.adrior.roborally.utility.Pair;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The MovementExecutor class provides utility methods to manage and execute robot movements
 * within the game. It handles moving robots based on their orientation, resolving conflicts,
 * and ensuring that movements are valid within the racing course.
 *
 * @see RacingCourse
 * @see Tile
 * @see PositionedTile
 * @see Player
 * @see Vector
 * @see Orientation
 */
@UtilityClass
public class MovementExecutor {

    /**
     * Moves the {@link Robot} in the specified direction. It checks if the robot can move out of the current tile
     * and if it can move into the next tile. If the robot is blocked by another robot, it will call itself
     * recursively to move the blocking robot.
     *
     * @param course       The course the robot is moving on.
     * @param player       The player robot to move.
     * @param otherPlayers The list of all other players.
     * @param orientation  The direction the robot is moving.
     */
    public static void moveRobot(@NonNull RacingCourse course, @NonNull Player player, @NonNull List<Player> otherPlayers, @NonNull Orientation orientation) {
        Player nextTilePlayer = null;
        for (Player otherPlayer : otherPlayers) {
            if (otherPlayer.robot().getPosition().equals(player.robot().getPosition().add(orientation.getVector()))) {
                nextTilePlayer = otherPlayer;
                break;
            }
        }

        if (!canMove(course, player, orientation)) return;

        // Check if the robot has moved into a pit or out of bounds.
        Vector newPosition = player.robot().getPosition().add(orientation.getVector());
        if (checkForPotentialReboot(course, newPosition)) {
            reboot(course, player, newPosition, otherPlayers);
            return;
        }

        // Call move Robot recursively here since the robot is blocked by another robot
        if (null != nextTilePlayer) {
            Player finalNextTilePlayer = nextTilePlayer;
            List<Player> subNextPlayer = otherPlayers.stream().
                    filter(p -> !p.equals(finalNextTilePlayer)).collect(Collectors.toCollection(LinkedList::new));
            subNextPlayer.add(player);
            moveRobot(course, nextTilePlayer, subNextPlayer, orientation);
        }

        // Assert if the next tile is empty
        if (otherPlayers.stream().noneMatch(p -> p.robot().getPosition().equals(newPosition))) {
            ServerCommunicationFacade.log(String.format("<MovementExecutor> Calling player %s's robot move method", player.clientId()));
            player.robot().move(orientation);
            broadcastMovement(player, player.robot().getPosition());
        }
    }


    /**
     * Moves the {@link Robot} a certain number of steps in the {@link Orientation} it is facing.
     * This is a helper method that repeatedly calls moveRobot.
     *
     * @param course       The racing course the robot is moving on.
     * @param player       The player whose robot is to be moved.
     * @param otherPlayers The list of all other players.
     * @param steps        The number of steps the robot should move.
     */
    public static void moveRobotSteps(@NonNull RacingCourse course, @NonNull Player player, @NonNull List<Player> otherPlayers, int steps) {
        for (int i = 0; i < steps; i++) {
            if (!player.flags().isRebooting()) {
                moveRobot(course, player, otherPlayers, player.robot().getOrientation());
            }
        }
    }

    /**
     * Moves the {@link Checkpoint} on {@link ConveyorBelt} based on their push {@link Orientation}.
     * This method handles both single and double conveyor belts.
     *
     * @param course   The racing course containing the tiles and checkpoints.
     * @param isDouble Indicates whether to consider double conveyor belts or single conveyor belts.
     */
    public static void moveCheckpointsOnConveyorBelt(@NonNull RacingCourse course, boolean isDouble) {
        List<PositionedTile<Checkpoint>> checkpoints = RacingCourse.getPositionedTilesOfType(course.getTiles(), Checkpoint.class);

        // Loop through each player to determine their new position and orientation if any.
        for (PositionedTile<Checkpoint> checkpoint : checkpoints) {
            Vector currentPosition = checkpoint.position();
            int checkpointNumber = checkpoint.tile().getCheckpointNumber();

            // Determine new position based on conveyor belt movement.
            ConveyorBelt firstConveyorBelt = getConveyorBeltAtPosition(course, currentPosition, isDouble);

            // Determine new position based on conveyor belt movement.
            Vector newPosition = determineNewPosition(currentPosition, firstConveyorBelt);

            if (null != firstConveyorBelt) {
                // Move the check point towards the push orientation from the conveyor belt.
                course.setTilePosition(Checkpoint.class, currentPosition, newPosition);

                // Broadcast the new checkpoint position to the players.
                broadcastCheckpointMovement(checkpointNumber, newPosition);

                // Check if the new position is off the map or in a pit and reset the checkpoint to its origin if so.
                if (checkForPotentialReboot(course, newPosition)) {
                    Vector checkpointOriginPosition = course.getCheckpoints().stream()
                            .filter(cp -> cp.tile().getCheckpointNumber() == checkpointNumber)
                            .findFirst()
                            .map(PositionedTile::position)
                            .orElse(null);

                    if (null == checkpointOriginPosition)
                        throw new IllegalStateException("Checkpoint reset failed. Checkpoints must have a origin.");

                    ServerCommunicationFacade.log(String.format(
                            "<MovementExecutor> Resetting checkpoint %s at %s to its origin %s",
                            checkpointNumber, newPosition, checkpointOriginPosition));

                    course.setTilePosition(Checkpoint.class, newPosition, checkpointOriginPosition);

                    // Broadcast the new checkpoint position at its origin to the players.
                    broadcastCheckpointMovement(checkpointNumber, checkpointOriginPosition);
                }
            }
        }
    }


    /**
     * Moves the {@link Robot} on {@link ConveyorBelt} according to their push {@link Orientation} and resolves any potential position conflicts.
     * If two robots end up in the same position, neither is moved. It takes into account whether the conveyor belts are double or single and
     * handle the potential change of the robot's orientation.
     *
     * @param course   The current racing course with all the tiles.
     * @param players  The list of players with their robots to be moved.
     * @param isDouble Indicates whether to consider double conveyor belts or single conveyor belts.
     */
    public static void movePlayersOnConveyorBelt(@NonNull RacingCourse course, @NonNull List<Player> players, boolean isDouble) {

        // Stores potential new positions for players.
        Map<Vector, Set<Player>> possibleNewPositions = new HashMap<>();

        // Stores potential new orientations for players.
        Map<Player, Orientation> possibleNewOrientation = new HashMap<>();

        // Tracks players currently on a conveyor belt.
        Set<Player> playersOnConveyor = new HashSet<>();

        // Loop through each player to determine their new position and orientation if any.
        for (Player player : players) {
            Vector currentRobotPosition = player.robot().getPosition();
            Orientation currentRobotOrientation = player.robot().getOrientation();

            // Determine new position based on conveyor belt movement.
            ConveyorBelt firstConveyorBelt = getConveyorBeltAtPosition(course, currentRobotPosition, isDouble);

            // Add the player to the conveyor set if on a conveyor belt.
            if (null != firstConveyorBelt) playersOnConveyor.add(player);

            // Determine new position based on conveyor belt movement.
            Vector newPosition = determineNewPosition(currentRobotPosition, firstConveyorBelt);

            // Check if the new position is off the map or in a pit and reboot if so.
            if (checkForPotentialReboot(course, newPosition)) {
                reboot(course, player, newPosition, players);
                continue;
            }

            // Check if the movement is possible.
            if (null != firstConveyorBelt && !canMove(course, player, firstConveyorBelt.getPushOrientation())) {
                continue;
            }

            // Determine the second conveyor belt at the new position.
            ConveyorBelt secondConveyorBelt = getConveyorBeltAtPosition(course, newPosition, isDouble);

            // Determine new orientation after moving to the new position.
            Orientation newOrientation = determineNewOrientation(currentRobotOrientation, firstConveyorBelt, secondConveyorBelt);

            // Add the player result to the map to determine possible conflicts.
            possibleNewPositions.computeIfAbsent(newPosition, _ -> new HashSet<>()).add(player);
            possibleNewOrientation.put(player, newOrientation);
        }

        // Resolve conflicts and move players to their new positions.
        resolveConflictsAndMovePlayers(course, possibleNewPositions, possibleNewOrientation, playersOnConveyor, players);
    }


    /**
     * Checks if the {@link Player}'s {@link Robot} can move in the given {@link Orientation}
     * by verifying if the movement is allowed out of the current tile and into the next tile.
     *
     * @param course      The current racing course with all the tiles.
     * @param player      The player whose robot's movement is to be checked.
     * @param orientation The direction the robot is attempting to move.
     * @return true if the robot can move out of the current tile and into the next tile, false otherwise.
     */
    private static boolean canMove(@NonNull RacingCourse course, @NonNull Player player, @NonNull Orientation orientation) {
        Vector currentPosition = player.robot().getPosition();
        Vector newPosition = currentPosition.add(orientation.getVector());

        List<Tile> currentTileStack = course.getTileAt(currentPosition);
        boolean canMoveOut = currentTileStack.stream()
                .allMatch(tile -> tile.canMoveOut(orientation));
        boolean canMoveIn = course.getTileAt(newPosition).stream()
                .allMatch(tile -> tile.canMoveTo(orientation));

        return canMoveOut && canMoveIn;
    }


    /**
     * Retrieves the {@link ConveyorBelt} at a given position if it matches the specified type (double or single).
     *
     * @param course   The current racing course with all the tiles.
     * @param position The position to check for a conveyor belt.
     * @param isDouble Indicates whether to consider double conveyor belts or single conveyor belts.
     * @return The conveyor belt at the given position if it exists, otherwise null.
     */
    private static ConveyorBelt getConveyorBeltAtPosition(@NonNull RacingCourse course, @NonNull Vector position, boolean isDouble) {
        return course.getTileAt(position).stream()
                .filter(ConveyorBelt.class::isInstance)
                .map(ConveyorBelt.class::cast)
                .filter(conveyorBelt -> conveyorBelt.isDouble() == isDouble)
                .findFirst()
                .orElse(null);
    }


    /**
     * Determines the new position of a {@link Robot} based on its current position and the {@link ConveyorBelt}'s push {@link Orientation}.
     *
     * @param currentPosition The current position of the robot.
     * @param conveyorBelt    The conveyor belt affecting the robot.
     * @return The new position of the robot.
     */
    private static Vector determineNewPosition(@NonNull Vector currentPosition, ConveyorBelt conveyorBelt) {
        return Optional.ofNullable(conveyorBelt)
                .map(belt -> currentPosition.add(belt.getPushOrientation().getVector()))
                .orElse(currentPosition);
    }


    /**
     * Determines the new {@link Orientation} of a {@link Robot} after moving based on the {@link ConveyorBelt}'s push orientations.
     *
     * @param currentOrientation The current orientation of the robot.
     * @param firstConveyorBelt  The first conveyor belt affecting the robot.
     * @param secondConveyorBelt The second conveyor belt affecting the robot.
     * @return The new orientation of the robot.
     */
    private static Orientation determineNewOrientation(@NonNull Orientation currentOrientation, ConveyorBelt firstConveyorBelt, ConveyorBelt secondConveyorBelt) {
        return null != firstConveyorBelt && null != secondConveyorBelt
                ? currentOrientation.getConveyorRotation(firstConveyorBelt.getPushOrientation(), secondConveyorBelt.getPushOrientation())
                : null;
    }


    /**
     * Resolves position conflicts and moves players to their new positions and orientations.
     *
     * @param course                 The current racing course with all the tiles.
     * @param possibleNewPositions   A map of potential new positions and the players that may move there.
     * @param possibleNewOrientation A map of players and their potential new orientations.
     * @param playersOnConveyor      A set of players currently on a conveyor belt.
     * @param players                The list of players with their robots to be moved.
     */
    private static void resolveConflictsAndMovePlayers(@NonNull RacingCourse course, @NonNull Map<Vector, Set<Player>> possibleNewPositions,
                                                       @NonNull Map<Player, Orientation> possibleNewOrientation, @NonNull Set<Player> playersOnConveyor, @NonNull List<Player> players) {
        possibleNewPositions.entrySet().stream()
                .filter(set -> 1 == set.getValue().size()) // Only move & rotate players with unique new positions.
                .filter(set -> playersOnConveyor.containsAll(set.getValue()))
                .forEach(entry -> {
                    Player player = entry.getValue().iterator().next();

                    ServerCommunicationFacade.log(String.format(
                            "<MovementExecutor> player %s's robot is getting moved by a ConveyorBelt", player.clientId()));

                    player.robot().setPosition(entry.getKey());
                    Vector newPosition = player.robot().getPosition();

                    // Broadcast the movement message.
                    broadcastMovement(player, newPosition);

                    // Broadcast the rotation message.
                    broadcastRotation(player, player.robot().getOrientation(), possibleNewOrientation.get(player));

                    // Check for a pit or out of map, at the new position, and reboot if so.
                    if (checkForPotentialReboot(course, newPosition)) {
                        reboot(course, player, newPosition, players);
                    }
                });
    }


    /**
     * Checks if the {@link Player}'s {@link Robot} needs to reboot due to being on a pit tile or out of map.
     *
     * @see #reboot(RacingCourse, Player, Vector, List)
     *
     * @param course   The current racing course with all the tiles.
     * @param position The vector of the player with the robot to be checked.
     * @return true if the robot is on a pit tile or out of map and needs to reboot, false otherwise.
     */
    private static boolean checkForPotentialReboot(@NonNull RacingCourse course, @NonNull Vector position) {
        List<Tile> tiles = course.getTileAt(position);
        if (tiles.isEmpty()) return true;

        Pit pit = tiles.stream()
                .filter(Pit.class::isInstance)
                .map(Pit.class::cast)
                .findFirst()
                .orElse(null);

        NullTile nullTile = tiles.stream()
                .filter(NullTile.class::isInstance)
                .map(NullTile.class::cast)
                .findFirst()
                .orElse(null);

        return null != pit || null != nullTile;
    }


    /**
     * This method synchronizes the position of the {@link Robot} after a reboot.
     *
     * @param course The course the robot is moving on
     * @param player The player with the robot that gets rebooted
     */
    public static void reboot(@NonNull RacingCourse course, @NonNull Player player, @NonNull Vector newPosition, @NonNull List<Player> players) {
        ServerCommunicationFacade.log(String.format("<MovementExecutor> Rebooting player %s", player.clientId()));

        Robot robot = player.robot();
        player.flags().setRebooting(true);

        // Send the old position to the client for the animation
        broadcastMovement(player, newPosition);

        // Broadcast the reboot message
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.reboot(player.clientId()));

        // Align robot orientation to the TOP by default.
        broadcastRotation(player, robot.getOrientation(), Orientation.TOP);

        // Determine the reboot position.
        Pair<Vector, Orientation> rebootPosition = determineRebootPosition(course, player);

        // Clear the tile position and set the new position
        clearTilePosition(course, players, rebootPosition.key(), rebootPosition.value());
        robot.setPosition(rebootPosition.key());
        ServerCommunicationFacade.log(String.format(
                "<MovementExecutor> Players %s's robot is rebooting at %s", player.clientId(), rebootPosition.key()));

        // Broadcast the new position
        broadcastMovement(player, robot.getPosition());

        // Draw two SPAM cards.
        drawTwoSpamCards(player);
    }


    /**
     * Determines the reboot position for the {@link Player}'s {@link Robot}.
     *
     * @param course The course the robot is moving on.
     * @param player The player whose robot is being rebooted.
     * @return The positioned tile of the restart point.
     */
    @NonNull private static Pair<Vector, Orientation> determineRebootPosition(@NonNull RacingCourse course, @NonNull Player player) {
        Robot robot = player.robot();

        String currentBoard = course.getTileAt(robot.getPosition()).getFirst().getIsOnBoard();
        String startingBoard = course.getCourseData().boardData().getFirst().boardId();

        boolean hasRebootRule = null != course.getCourseData().rule() && Rule.REBOOT == course.getCourseData().rule();

        List<PositionedTile<RestartPoint>> restartPoints = course.getRestartPoints().stream()
                .filter(rp -> rp.tile().getIsOnBoard().equals(currentBoard) || rp.tile().getIsOnBoard().equals(startingBoard))
                .toList();

        PositionedTile<RestartPoint> restartPointOfCurrentBoard = restartPoints.stream()
                .filter(rp -> rp.tile().getIsOnBoard().equals(currentBoard))
                .findFirst()
                .orElse(null);

        PositionedTile<RestartPoint> restartPointOfStartingBoard = restartPoints.stream()
                .filter(rp -> rp.tile().getIsOnBoard().equals(startingBoard))
                .findFirst()
                .orElse(null);

        if (currentBoard.equals(startingBoard) && !hasRebootRule) {
            return new Pair<>(robot.getStartingPosition(), Orientation.RIGHT);
        } else if (null != restartPointOfCurrentBoard) {
            return new Pair<>(restartPointOfCurrentBoard.position(), restartPointOfCurrentBoard.tile().getRestartOrientation());
        } else if (null != restartPointOfStartingBoard) {
            return new Pair<>(restartPointOfStartingBoard.position(), restartPointOfStartingBoard.tile().getRestartOrientation());
        } else {
            throw new IllegalStateException("Reboot failed. Robot must have a restartPosition.");
        }
    }


    /**
     * Clears the {@link Tile} position and moves the {@link Robot} if necessary.
     *
     * @param course   The course the robot is moving on.
     * @param players  The list of players.
     * @param position The position to clear.
     * @param clearDir The direction to clear.
     */
    private static void clearTilePosition(@NonNull RacingCourse course, @NonNull List<Player> players, Vector position, @NonNull Orientation clearDir) {
        players.stream()
                .filter(player -> player.robot().getPosition().equals(position))
                .findFirst()
                .ifPresent(player -> moveRobot(course, player, players, clearDir));
    }


    /**
     * Draws two {@link CardType#SPAM} cards for the {@link Player} and adds them to the discard pile.
     *
     * @param player The player to draw SPAM cards for.
     */
    public static void drawTwoSpamCards(@NonNull Player player) {
        List<Card> damageCards = SharedDeck.drawCards(CardType.SPAM, 2);

        if (!damageCards.isEmpty()) {
            damageCards.forEach(card -> player.cardManager().addCardToDiscardPile(card));

            ServerCommunicationFacade.broadcast(PredefinedServerMessages.drawDamage(player.clientId(), new String[]{"Spam", "Spam"}));
        } else {
            String[] availablePiles;
            try {
                availablePiles = SharedDeck.assertSharedDeckSizes(2);
            } catch (InvalidGameStateException e) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(e.getMessage()), player.clientId());
                return;
            }

            player.flags().setAwaitingDamageSelection(true);
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.pickDamage(2, availablePiles), player.clientId());
        }
    }


    /**
     * Broadcasts the movement of a {@link Player} to the new position via the {@link ServerCommunicationFacade}.
     *
     * @param player      The player whose movement is to be broadcast.
     * @param newPosition The new position of the player's robot.
     */
    private static void broadcastMovement(@NonNull Player player, @NonNull Vector newPosition) {
        int x = newPosition.x();
        int y = newPosition.y();
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.movement(player.clientId(), x, y));
    }


    /**
     * Broadcasts the movement of a {@link Checkpoint} to the new position via the {@link ServerCommunicationFacade}.
     *
     * @param checkpointNumber The checkpoint given by its number, whose movement is to be broadcast.
     * @param newPosition      The new position of the player's robot.
     */
    private static void broadcastCheckpointMovement(int checkpointNumber, @NonNull Vector newPosition) {
        int x = newPosition.x();
        int y = newPosition.y();
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.checkpointMoved(checkpointNumber, x, y));
    }


    /**
     * Broadcasts the rotation of a {@link Player} to the new {@link Orientation} via the {@link ServerCommunicationFacade}.
     *
     * @param player            The player whose rotation is to be broadcast.
     * @param orientation       The current orientation of the player's robot.
     * @param targetOrientation The new orientation of the player's robot.
     */
    private static void broadcastRotation(@NonNull Player player, @NonNull Orientation orientation, Orientation targetOrientation) {
        if (null != targetOrientation) {
            int steps = targetOrientation.getRotationStepsTo(orientation);

            if (3 != steps) {
                for (int i = 0; i < steps; i++) {
                    ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(player.clientId(), "counterclockwise"));
                }
            } else {
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(player.clientId(), "clockwise"));
            }

            ServerCommunicationFacade.log(String.format(
                    "<MovementExecutor> Player %s's robot orientation has been set to %s", player.clientId(), targetOrientation));
            player.robot().setOrientation(targetOrientation);
        }
    }
}
