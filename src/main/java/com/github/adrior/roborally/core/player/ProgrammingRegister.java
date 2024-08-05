package com.github.adrior.roborally.core.player;

import com.github.adrior.roborally.core.card.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The Register class represents the register that holds the cards to be executed in the game.
 * It contains five registers that can be filled with cards from the draw deck.
 *
 * @see Card
 */
@Getter
@NoArgsConstructor
@ToString
public class ProgrammingRegister {
    private final Card[] registers = new Card[5];

    /**
     * Sets a card in a specific register.
     *
     * @param index the register index (0-4).
     * @param card  the card to place in the register.
     */
    public void setRegister(int index, Card card) {
        assertRegisterIndex(index);
        registers[index] = card;
    }


    /**
     * Gets the card from a specific register.
     *
     * @param index the register index (0-4).
     * @return the card in the register, or null if the register is empty.
     */
    public Card getRegister(int index) {
        assertRegisterIndex(index);
        return registers[index];
    }


    /**
     * Clears the register at the given index.
     *
     * @param index the register index (0-4).
     */
    public void clearRegister(int index) {
        assertRegisterIndex(index);
        registers[index] = null;
    }


    /**
     * Clears all registers.
     */
    public void clearAllRegisters() {
        for (int i = 0; 4 >= i; i++) {
            clearRegister(i);
        }
    }


    /**
     * Removes and returns the card from a specific slot.
     *
     * @param index the register index (0-4).
     * @return the card that was in the register or null if the register was empty.
     */
    public Card removeCardFromRegister(int index) {
        assertRegisterIndex(index);
        Card card = registers[index];
        clearRegister(index);
        return card;
    }


    /**
     * Removes and returns all cards currently placed in the registers.
     *
     * @return the list of cards placed in the registers.
     */
    @NonNull public List<Card> removeAllCardsFromRegister() {
        List<Card> cards = getAllRegisters();
        clearAllRegisters();
        return cards;
    }


    /**
     * Asserts that the register index is valid (0-4).
     *
     * @param index the index to check.
     */
    private void assertRegisterIndex(int index) {
        if(0 > index || 4 < index) throw new IndexOutOfBoundsException("Register index must be between 0 and 4.");
    }


    /**
     * Checks if the register is filled.
     *
     * @return true, if the register is filled, else otherwise.
     */
    public boolean isFilled() {
        return Arrays.stream(registers).allMatch(Objects::nonNull);
    }


    /**
     * Retrieves all cards placed in the registers that are not null.
     *
     * @return the list of cards placed in the registers.
     */
    @NonNull public List<Card> getAllRegisters() {
        return Arrays.stream(registers)
                .filter(Objects::nonNull)
                .toList();
    }
}
