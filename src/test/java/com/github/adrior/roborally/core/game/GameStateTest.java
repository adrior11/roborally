package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.game.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateTest {

    @Test
    void testPhaseAdvancement() {
        // Start at SETUP_PHASE
        GameState currentState = GameState.SETUP_PHASE;
        assertEquals(GameState.SETUP_PHASE, currentState, "Initial state should be SETUP_PHASE");

        // Advance to UPGRADE_PHASE
        currentState = currentState.advance();
        assertEquals(GameState.UPGRADE_PHASE, currentState, "After SETUP_PHASE, state should be UPGRADE_PHASE");

        // Advance to PROGRAMMING_PHASE
        currentState = currentState.advance();
        assertEquals(GameState.PROGRAMMING_PHASE, currentState, "After UPGRADE_PHASE, state should be PROGRAMMING_PHASE");

        // Advance to ACTIVATION_PHASE
        currentState = currentState.advance();
        assertEquals(GameState.ACTIVATION_PHASE, currentState, "After PROGRAMMING_PHASE, state should be ACTIVATION_PHASE");

        // Advance to UPGRADE_PHASE again (looping back)
        currentState = currentState.advance();
        assertEquals(GameState.UPGRADE_PHASE, currentState, "After ACTIVATION_PHASE, state should loop back to UPGRADE_PHASE");
    }
}
