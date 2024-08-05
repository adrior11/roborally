package com.github.adrior.roborally.core.card;

import com.github.adrior.roborally.core.card.cards.DamageCard;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.card.cards.SpecialProgrammingCard;
import com.github.adrior.roborally.core.card.cards.UpgradeCard;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the various types of cards in the game.
 * Each {@link Card} type has a display name and is used to differentiate between different card actions and behaviors.
 *
 * @see DamageCard
 * @see ProgrammingCard
 * @see SpecialProgrammingCard
 * @see UpgradeCard
 */
public enum CardType {

    // Programming Cards
    MOVE_I("MoveI"),
    MOVE_II("MoveII"),
    MOVE_III("MoveIII"),
    TURN_RIGHT("TurnRight"),
    TURN_LEFT("TurnLeft"),
    U_TURN("UTurn"),
    BACK_UP("BackUp"),
    POWER_UP("PowerUp"),
    AGAIN("Again"),

    // Damage Cards
    SPAM("Spam"),
    TROJAN("Trojan"),
    WORM("Worm"),
    VIRUS("Virus"),

    // Special Programming Cards
    ENERGY_ROUTINE("EnergyRoutine"),
    SANDBOX_ROUTINE("SandboxRoutine"),
    WEASEL_ROUTINE("WeaselRoutine"),
    SPEED_ROUTINE("SpeedRoutine"),
    SPAM_FOLDER("SpamFolder"),
    REPEAT_ROUTINE("RepeatRoutine"),

    // Upgrade Cards
    ADMIN_PRIVILEGE("ADMIN PRIVILEGE"),
    REAR_LASER("REAR LASER"),
    MEMORY_SWAP("MEMORY SWAP"),
    SPAM_BLOCKER("SPAM BLOCKER");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    private static final Map<String, CardType> DISPLAY_NAME_MAP = new HashMap<>();

    static {
        for (CardType cardType : values()) {
            DISPLAY_NAME_MAP.put(cardType.displayName, cardType);
        }
    }

    /**
     * Finds the enum constant from a formatted string with spaces.
     *
     * @param formattedName The formatted name string with spaces.
     * @return The corresponding CardType enum constant.
     */
    @NonNull public static CardType fromString(String formattedName) {
        CardType cardType = DISPLAY_NAME_MAP.get(formattedName);
        if (null == cardType) throw new IllegalArgumentException("No enum constant for formatted name: " + formattedName);
        return cardType;
    }
}
