package com.github.adrior.roborally.core.card.cards;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.game.MovementExecutor;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Pair;
import lombok.NonNull;

/**
 * Represents a programming {@link Card} in the game.
 * Programming cards define various actions that a robot can perform during its turn.
 */
public class ProgrammingCard extends Card {
    private static final Pair<String, String> ROTATIONS = new Pair<>("clockwise", "counterclockwise");

    private final CardAction action;

    /**
     * Constructs a new ProgrammingCard with the specified parameters.
     *
     * @param cardType The type of the card.
     * @param action   The action to be performed when the card is executed.
     */
    public ProgrammingCard(CardType cardType, CardAction action) {
        super(cardType);
        this.action = action;
    }


    @Override
    public void execute(TurnManager turnManager, Player player) {
        action.execute(turnManager, player);
    }


    /**
     * Creates a MoveI programming card.
     *
     * @return A MoveI programming card.
     */
    @NonNull public static ProgrammingCard createMoveICard() {
        return new ProgrammingCard(CardType.MOVE_I, (turnManager, player) -> MovementExecutor.moveRobotSteps(
                turnManager.getCurrentCourse(), player, turnManager.getPlayers(), 1));
    }


    /**
     * Creates a MoveII programming card.
     *
     * @return A MoveII programming card.
     */
    @NonNull public static ProgrammingCard createMoveIICard() {
        return new ProgrammingCard(CardType.MOVE_II, (turnManager, player) -> MovementExecutor.moveRobotSteps(
                turnManager.getCurrentCourse(), player, turnManager.getPlayers(), 2));
    }


    /**
     * Creates a MoveIII programming card.
     *
     * @return A MoveIII programming card.
     */
    @NonNull public static ProgrammingCard createMoveIIICard() {
        return new ProgrammingCard(CardType.MOVE_III, (turnManager, player) -> MovementExecutor.moveRobotSteps(
                turnManager.getCurrentCourse(), player, turnManager.getPlayers(), 3));
    }


    /**
     * Creates a TurnRight programming card.
     *
     * @return A TurnRight programming card.
     */
    @NonNull public static ProgrammingCard createTurnRightCard() {
        return new ProgrammingCard(CardType.TURN_RIGHT, (_, player) -> {
            Robot robot = player.robot();
            robot.setOrientation(robot.getOrientation().turnRight());
            broadcastPlayTurning(player.clientId(), ROTATIONS.key());
        });
    }


    /**
     * Creates a TurnBack programming card.
     *
     * @return A TurnBack programming card.
     */
    @NonNull public static ProgrammingCard createTurnLeftCard() {
        return new ProgrammingCard(CardType.TURN_LEFT, (_, player) -> {
            Robot robot = player.robot();
            robot.setOrientation(robot.getOrientation().turnLeft());
            broadcastPlayTurning(player.clientId(), ROTATIONS.value());
        });
    }


    /**
     * Creates a UTurn programming card.
     *
     * @return A UTurn programming card.
     */
    @NonNull public static ProgrammingCard createUTurnCard() {
        return new ProgrammingCard(CardType.U_TURN, (_, player) -> {
            Robot robot = player.robot();
            robot.setOrientation(robot.getOrientation().uTurn());
            broadcastPlayTurning(player.clientId(), ROTATIONS.key());
            broadcastPlayTurning(player.clientId(), ROTATIONS.key());
        });
    }


    /**
     * Creates a BackUp programming card.
     *
     * @return A BackUp programming card.
     */
    @NonNull public static ProgrammingCard createBackUpCard() {
        return new ProgrammingCard(CardType.BACK_UP, (turnManager, player) -> {
            Orientation moveDirection = player.robot().getOrientation().uTurn();
            MovementExecutor.moveRobot(turnManager.getCurrentCourse(), player, turnManager.getPlayers(), moveDirection);
        });
    }


    /**
     * Creates a PowerUp programming card.
     *
     * @return A PowerUp programming card.
     */
    @NonNull public static ProgrammingCard createPowerUpCard() {
        return new ProgrammingCard(CardType.POWER_UP, (_, player) -> {
            player.robot().adjustEnergy(1);
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.energy(player.clientId(), player.robot().getEnergy(), "PowerUpCard"));
        });
    }


    /**
     * Creates an Again programming card.
     *
     * @return An Again programming card.
     */
    @NonNull public static ProgrammingCard createAgainCard() {
        return new ProgrammingCard(CardType.AGAIN, (turnManager, player) -> {
            if (0 == turnManager.getCurrentRegisterIndex()) return; // Based on the rules, an again card in the first register cannot be executed.

            Card previousCard = null;
            int register = turnManager.getCurrentRegisterIndex() - 1;

            while (0 <= register) {
                previousCard = player.programmingRegister().getRegister(register);
                if (CardType.AGAIN != previousCard.getCardType()) break;
                register--;
            }

            if (null != previousCard && CardType.AGAIN != previousCard.getCardType()) {
                ServerCommunicationFacade.log("Activating " + previousCard.getCardType() + " card from previous register");
                previousCard.execute(turnManager, player);
            }
        });
    }


    /**
     * Broadcasts a player's turning action.
     *
     * @param id       The ID of the player who is turning.
     * @param rotation The rotation of the turn (clockwise, counterclockwise).
     */
    private static void broadcastPlayTurning(int id, String rotation) {
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(id, rotation));
    }
}
