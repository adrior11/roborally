package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgrammingRegisterTest {
    private ProgrammingRegister programmingRegister;
    private Card card1, card2;

    @BeforeEach
    void setUp() {
        programmingRegister = new ProgrammingRegister();
        card1 = ProgrammingCard.createMoveICard();
        card2 = ProgrammingCard.createTurnRightCard();
    }

    @Test
    void testSetAndGetRegister() {
        programmingRegister.setRegister(0, card1);
        assertEquals(card1, programmingRegister.getRegister(0));
    }

    @Test
    void testSetRegisterInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.setRegister(-1, card1));
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.setRegister(5, card1));
    }

    @Test
    void testGetRegisterInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.getRegister(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.getRegister(5));
    }

    @Test
    void testClearRegister() {
        programmingRegister.setRegister(0, card1);
        programmingRegister.clearRegister(0);
        assertNull(programmingRegister.getRegister(0));
    }

    @Test
    void testClearRegisterInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.clearRegister(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.clearRegister(5));
    }

    @Test
    void testClearAllRegisters() {
        programmingRegister.setRegister(0, card1);
        programmingRegister.setRegister(1, card2);
        programmingRegister.clearAllRegisters();
        for (int i = 0; i < 5; i++) {
            assertNull(programmingRegister.getRegister(i));
        }
    }

    @Test
    void testRemoveCardFromRegister() {
        programmingRegister.setRegister(0, card1);
        assertEquals(card1, programmingRegister.removeCardFromRegister(0));
        assertNull(programmingRegister.getRegister(0));
    }

    @Test
    void testRemoveCardFromRegisterInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.removeCardFromRegister(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> programmingRegister.removeCardFromRegister(5));
    }

    @Test
    void testRemoveCardFromEmptyRegister() {
        assertNull(programmingRegister.removeCardFromRegister(0));
    }
}
