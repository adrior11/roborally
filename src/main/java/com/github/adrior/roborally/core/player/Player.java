package com.github.adrior.roborally.core.player;

import com.github.adrior.roborally.core.card.Deck;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The PlayerData class represents the essential data of a player in the game.
 * This class uses a record to encapsulate the player's clientId, robot, decks, register and flags.
 *
 * @see Robot
 * @see CardManager
 * @see ProgrammingRegister
 * @see Deck
 *
 * @param clientId The client's unique identifier.
 * @param robot The robot associated with the player.
 * @param cardManager The manager handling the player's cards.
 * @param programmingRegister The register used for programming moves.
 * @param installedUpgrades The deck of installed upgrades for the robot.
 * @param flags The flags representing various states of the player.
 */
public record Player(int clientId, Robot robot, CardManager cardManager, ProgrammingRegister programmingRegister, Deck installedUpgrades, Flags flags) {

    /**
     * The Flags record encapsulates various boolean flags that represent the player's state.
     */
    @Getter @Setter @NoArgsConstructor
    public static class Flags {
        private boolean isAI = false;
        private boolean isSelectingMap = false;
        private boolean isRebooting = false;
        private boolean setStartingPoint = false;
        private boolean decidedUpgradePhase = false;
        private boolean playedRegister = false;
        private boolean selectionFinished = false;
        private boolean awaitingUpgradeCard = false;
        private boolean awaitingDamageSelection = false;
    }
}