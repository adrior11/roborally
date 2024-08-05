package com.github.adrior.roborally.core.game;

import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Enum representing the various phases of the game.
 * Each phase contains an abstract method executed when the phase is active.
 *
 * @see TurnManager
 */
@Getter
public enum GameState {
    SETUP_PHASE(0, GameState::executeSetupPhase),
    UPGRADE_PHASE(1, GameState::executeUpgradePhase),
    PROGRAMMING_PHASE(2, GameState::executeProgrammingPhase),
    ACTIVATION_PHASE(3, GameState::executeActivationPhase);

    private final int phase;
    private final PhaseAction phaseAction;

    /**
     * Constructor to initialize a GameState with the given phase number and phase action.
     *
     * @param phase The phase number associated with the GameState.
     * @param phaseAction The action to be executed for this phase.
     */
    GameState(int phase, PhaseAction phaseAction) {
        this.phase = phase;
        this.phaseAction = phaseAction;
    }


    private static final GameState[] phases = {UPGRADE_PHASE, PROGRAMMING_PHASE, ACTIVATION_PHASE};


    /**
     * Advances the game state to the next phase.
     * The SetUp phase will be skipped, as it can only be executed once.
     *
     * @return The next GameState in the sequence.
     */
    @NonNull public GameState advance() {
        if (SETUP_PHASE == this) return UPGRADE_PHASE;
        int index = this.ordinal() % phases.length;
        return phases[index];
    }


    /**
     * Executes the actions associated with the current phase.
     *
     * @param turnManager The TurnManager instance managing the game's turns and phases.
     */
    void executePhase(TurnManager turnManager) {
        phaseAction.execute(turnManager);
    }


    /**
     * Executes the setup phase actions.
     * Broadcasts the active phase and current player to all clients.
     *
     * @param turnManager The TurnManager instance managing the game's turns and phases.
     */
    private static void executeSetupPhase(@NonNull TurnManager turnManager) {
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.activePhase(turnManager.getCurrentPhase().getPhase()));

        List<Integer> playerIDs = turnManager.getPlayers().stream().map(Player::clientId).toList();
        ServerCommunicationFacade.log(String.format("Setup Queue: %s", playerIDs));

        GameManager.getInstance().delayFor(500);
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.currentPlayer(turnManager.getCurrentPlayer().clientId()));
    }


    /**
     * Executes the upgrade phase actions.
     * Refills the upgrade shop.
     *
     * @see TurnManager#refillUpgradeShop()
     *
     * @param turnManager The TurnManager instance managing the game's turns and phases.
     */
    private static void executeUpgradePhase(@NonNull TurnManager turnManager) {
        turnManager.refillUpgradeShop();
    }


    /**
     * Executes the programming phase actions.
     * Deals cards to the players.
     *
     * @see TurnManager#dealCards()
     *
     * @param turnManager The TurnManager instance managing the game's turns and phases.
     */
    private static void executeProgrammingPhase(@NonNull TurnManager turnManager) {
        turnManager.dealCards();
    }


    /**
     * Executes the activation phase actions.
     * Activates the current register.
     *
     * @see TurnManager#activateRegister(int)
     *
     * @param turnManager The TurnManager instance managing the game's turns and phases.
     */
    private static void executeActivationPhase(@NonNull TurnManager turnManager) {
        turnManager.activateRegister(turnManager.getCurrentRegisterIndex());
    }


    /**
     * Functional interface representing an action to be performed during a phase.
     */
    @FunctionalInterface
    private interface PhaseAction {
        void execute(TurnManager turnManager);
    }
}
