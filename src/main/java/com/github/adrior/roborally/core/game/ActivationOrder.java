package com.github.adrior.roborally.core.game;

import com.github.adrior.roborally.core.game.util.LaserTracker;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.map.data.RacingCourseData.Rule;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Vector;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * ActivationOrder enum defines the order of activation for different elements on the game board.
 * Each enum constant corresponds to a specific type of {@link Tile} and includes a method to activate
 * the tiles of that type for all players standing on them.
 *
 * <p> The enum uses functional interfaces, strategies, and actions to dynamically handle the activation
 * of different game elements. The {@link ActivationStrategy} interface allows each enum constant to
 * define its own activation logic. The {@link TileAction} interface is used to encapsulate actions
 * to be performed on tiles and players, promoting code reuse and modularity.
 *
 * @see TurnManager
 * @see Player
 * @see MovementExecutor
 * @see LaserTracker
 */
@Getter
public enum ActivationOrder {
    BLUE_CONVEYOR_BELTS(1, ActivationOrder::activateBlueConveyorBelt),
    GREEN_CONVEYOR_BELTS(2, ActivationOrder::activateGreenConveyorBelt),
    PUSH_PANELS(3, ActivationOrder::activatePushPanels),
    GEARS(4, ActivationOrder::activateGears),
    BOARD_LASERS(5, ActivationOrder::activateBoardLasers),
    ROBOT_LASERS(6, ActivationOrder::activateRobotLasers),
    ENERGY_SPACES(7, ActivationOrder::activateEnergySpaces),
    CHECKPOINTS(8, ActivationOrder::activateCheckpoints);

    private final int order;
    private final ActivationStrategy activationStrategy;

    /**
     * Constructor for the ActivationOrder enum.
     *
     * @param order              The order of activation.
     * @param activationStrategy The strategy for activating the tiles.
     */
    ActivationOrder(int order, ActivationStrategy activationStrategy) {
        this.order = order;
        this.activationStrategy = activationStrategy;
    }

    private static final ActivationOrder[] ACTIVATION_ORDER = values();


    /**
     * Activates the element associated with this enum constant.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    void activateElement(TurnManager turnManager) {
        activationStrategy.activate(turnManager);
    }


    /**
     * Activates the next element in the activation order.
     *
     * @return The next element to activate.
     */
    ActivationOrder activateNextElement() {
        int index = (ordinal() + 1) % ACTIVATION_ORDER.length;
        return ACTIVATION_ORDER[index];
    }


    /**
     * Activates blue conveyor belts.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateBlueConveyorBelt(@NonNull TurnManager turnManager) {
        ServerCommunicationFacade.log("<ActivationOrder> Activating BlueConveyorBelt");
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.animation("BlueConveyorBelt"));

        RacingCourse currentCourse = turnManager.getCurrentCourse();

        MovementExecutor.movePlayersOnConveyorBelt(currentCourse, turnManager.getPlayers(), true);
        MovementExecutor.movePlayersOnConveyorBelt(currentCourse, turnManager.getPlayers(), true);
        if (null != currentCourse.getCourseData().rule() && Rule.CONVEYOR == currentCourse.getCourseData().rule()) {
            MovementExecutor.moveCheckpointsOnConveyorBelt(currentCourse, true);
            MovementExecutor.moveCheckpointsOnConveyorBelt(currentCourse, true);
        }
    }


    /**
     * Activates green conveyor belts.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateGreenConveyorBelt(@NonNull TurnManager turnManager) {
        ServerCommunicationFacade.log("<ActivationOrder> Activating GreenConveyorBelt");
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.animation("GreenConveyorBelt"));

        RacingCourse currentCourse = turnManager.getCurrentCourse();

        MovementExecutor.movePlayersOnConveyorBelt(currentCourse, turnManager.getPlayers(), false);
        if (null != currentCourse.getCourseData().rule() && Rule.CONVEYOR == currentCourse.getCourseData().rule()) {
            MovementExecutor.moveCheckpointsOnConveyorBelt(currentCourse, false);
        }
    }


    /**
     * Activates push panels.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activatePushPanels(@NonNull TurnManager turnManager) {
        activateTiles(turnManager, PushPanel.class, "PushPanel", (tile, player) -> {
            if (tile.getActiveRegisters().contains(turnManager.getCurrentRegisterIndex() + 1)) {
                MovementExecutor.moveRobot(turnManager.getCurrentCourse(), player, turnManager.getPlayers(), tile.getPushOrientation());
            }
        });
    }


    /**
     * Activates gears.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateGears(@NonNull TurnManager turnManager) {
        activateTiles(turnManager, Gear.class, "Gear", (tile, player) ->
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(
                        player.clientId(), tile.isClockwise() ? "clockwise" : "counterclockwise"
        )));
    }


    /**
     * Activates energy spaces.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateEnergySpaces(@NonNull TurnManager turnManager) {
        activateTiles(turnManager, EnergySpace.class, "EnergySpace", (tile, player) ->
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.energy(
                        player.clientId(), player.robot().getEnergy(), tile.getType()
        )));
    }


    /**
     * Activates board lasers.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateBoardLasers(@NonNull TurnManager turnManager) {
        ServerCommunicationFacade.log("<ActivationOrder> Activating WallShooting");
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.animation("WallShooting"));
        LaserTracker.trackTileLaser(turnManager.getCurrentCourse().getLasers(), turnManager.getPlayers(), turnManager.getCurrentCourse().getWalls());
    }


    /**
     * Activates robot lasers.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateRobotLasers(@NonNull TurnManager turnManager) {
        ServerCommunicationFacade.log("<ActivationOrder> Activating PlayerShooting");
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.animation("PlayerShooting"));
        LaserTracker.trackRobotLaser(turnManager.getPlayers().stream().filter(p -> !p.flags().isRebooting()).toList(), turnManager.getCurrentCourse().getWalls());
    }


    /**
     * Activates checkpoints.
     *
     * @param turnManager The TurnManager controlling the game.
     */
    private static void activateCheckpoints(@NonNull TurnManager turnManager) {
        activateTiles(turnManager, Checkpoint.class, "CheckPoint", (tile, player) ->
                assertValidCheckpoint(turnManager, player, tile)
        );
    }


    /**
     * Activates tiles of a specific type for all players.
     *
     * @param turnManager The TurnManager controlling the game.
     * @param tileClass   The class of the tile type to activate.
     * @param type        The type string of the tile.
     * @param action      The action to perform on the tile.
     * @param <T>         The type of the tile.
     */
    private static <T extends Tile> void activateTiles(@NonNull TurnManager turnManager, @NonNull Class<T> tileClass, String type, @NonNull TileAction<T> action) {
        for (Player player : turnManager.getPlayers()) {
            Vector position = player.robot().getPosition();
            List<Tile> tiles = turnManager.getCurrentCourse().getTileAt(position);
            for (Tile tile : tiles) {
                if (tileClass.isInstance(tile)) {
                    ServerCommunicationFacade.log("<ActivationOrder> Activating " + type);
                    ServerCommunicationFacade.broadcast(PredefinedServerMessages.animation(type));
                    if (!tile.getType().equals("CheckPoint")) tile.activate(player);
                    action.execute(tileClass.cast(tile), player);
                }
            }
        }
    }


    /**
     * Validates and processes the activation of a checkpoint tile for a player.
     * If the player has reached a new checkpoint, it broadcasts a message and checks win conditions.
     *
     * @see TurnManager#checkWinConditions()
     *
     * @param turnManager The TurnManager controlling the game.
     * @param player      The player whose checkpoint is being validated.
     * @param checkpoint  The checkpoint tile being activated.
     */
    private static void assertValidCheckpoint(@NonNull TurnManager turnManager, @NonNull Player player, @NonNull Checkpoint checkpoint) {
        int currentCheckpointsReached = player.robot().getCheckpoint();
        ServerCommunicationFacade.log(String.format(
                "<ActivationOrder> Player %s reached reached a checkpoint tile. Current count: %s",
                player.clientId(), currentCheckpointsReached));

        checkpoint.activate(player);
        ServerCommunicationFacade.log(String.format(
                "<ActivationOrder> Activated the Checkpoint tile. Player %s checkpoint count: %s",
                player.clientId(), player.robot().getCheckpoint()));

        boolean reachedValidCheckpoint = currentCheckpointsReached != player.robot().getCheckpoint();
        ServerCommunicationFacade.log(String.format(
                "<ActivationOrder> Did players checkpoint counter change: %s", reachedValidCheckpoint));

        if (reachedValidCheckpoint) {
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.checkpointReached(
                    player.clientId(), checkpoint.getCheckpointNumber()));
            turnManager.checkWinConditions();
        }
    }


    /**
     * Functional interface representing an action to be performed on a tile and a player.
     *
     * @param <T> The type of the tile.
     */
    @FunctionalInterface
    private interface TileAction<T> {
        void execute(T tile, Player player);
    }


    /**
     * Functional interface representing a strategy for activating tiles.
     */
    @FunctionalInterface
    private interface ActivationStrategy {
        void activate(TurnManager turnManager);
    }
}
