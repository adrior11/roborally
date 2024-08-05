package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.CardType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTypeTest {

    @Test
    void testToStringConversion() {
        // Programming Cards
        assertEquals("MoveI", CardType.MOVE_I.toString());
        assertEquals("MoveII", CardType.MOVE_II.toString());
        assertEquals("MoveIII", CardType.MOVE_III.toString());
        assertEquals("TurnLeft", CardType.TURN_LEFT.toString());
        assertEquals("TurnRight", CardType.TURN_RIGHT.toString());
        assertEquals("BackUp", CardType.BACK_UP.toString());
        assertEquals("PowerUp", CardType.POWER_UP.toString());
        assertEquals("Again", CardType.AGAIN.toString());

        // Damage Cards
        assertEquals("Spam", CardType.SPAM.toString());
        assertEquals("Trojan", CardType.TROJAN.toString());
        assertEquals("Worm", CardType.WORM.toString());
        assertEquals("Virus", CardType.VIRUS.toString());

        // Special Programming Cards
        assertEquals("EnergyRoutine", CardType.ENERGY_ROUTINE.toString());
        assertEquals("SandboxRoutine", CardType.SANDBOX_ROUTINE.toString());
        assertEquals("WeaselRoutine", CardType.WEASEL_ROUTINE.toString());
        assertEquals("SpeedRoutine", CardType.SPEED_ROUTINE.toString());
        assertEquals("SpamFolder", CardType.SPAM_FOLDER.toString());
        assertEquals("RepeatRoutine", CardType.REPEAT_ROUTINE.toString());
    }

    @Test
    void testFromStringConversion() {
        // Programming Cards
        assertEquals(CardType.MOVE_I, CardType.fromString("MoveI"));
        assertEquals(CardType.MOVE_II, CardType.fromString("MoveII"));
        assertEquals(CardType.MOVE_III, CardType.fromString("MoveIII"));
        assertEquals(CardType.TURN_LEFT, CardType.fromString("TurnLeft"));
        assertEquals(CardType.TURN_RIGHT, CardType.fromString("TurnRight"));
        assertEquals(CardType.BACK_UP, CardType.fromString("BackUp"));
        assertEquals(CardType.POWER_UP, CardType.fromString("PowerUp"));
        assertEquals(CardType.AGAIN, CardType.fromString("Again"));

        // Damage Cards
        assertEquals(CardType.SPAM, CardType.fromString("Spam"));
        assertEquals(CardType.TROJAN, CardType.fromString("Trojan"));
        assertEquals(CardType.WORM, CardType.fromString("Worm"));
        assertEquals(CardType.VIRUS, CardType.fromString("Virus"));

        // Special Programming Cards
        assertEquals(CardType.ENERGY_ROUTINE, CardType.fromString("EnergyRoutine"));
        assertEquals(CardType.SANDBOX_ROUTINE, CardType.fromString("SandboxRoutine"));
        assertEquals(CardType.WEASEL_ROUTINE, CardType.fromString("WeaselRoutine"));
        assertEquals(CardType.SPEED_ROUTINE, CardType.fromString("SpeedRoutine"));
        assertEquals(CardType.SPAM_FOLDER, CardType.fromString("SpamFolder"));
        assertEquals(CardType.REPEAT_ROUTINE, CardType.fromString("RepeatRoutine"));
    }
}
