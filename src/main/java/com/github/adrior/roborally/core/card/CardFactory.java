package com.github.adrior.roborally.core.card;

import com.github.adrior.roborally.core.card.cards.DamageCard;
import com.github.adrior.roborally.core.card.cards.ProgrammingCard;
import com.github.adrior.roborally.core.card.cards.SpecialProgrammingCard;
import com.github.adrior.roborally.core.card.cards.UpgradeCard;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Factory class to create instances of {@link Card} based on CardType.
 *
 * @see DamageCard
 * @see ProgrammingCard
 * @see SpecialProgrammingCard
 * @see UpgradeCard
 */
@UtilityClass
public final class CardFactory {

    /**
     * Creates a card instance based on the given CardType.
     *
     * @param cardType The type of the card.
     * @return The card instance.
     */
    @NonNull public static Card createCard(@NonNull CardType cardType) {
        return switch (cardType) {
            case MOVE_I     -> ProgrammingCard.createMoveICard();
            case MOVE_II    -> ProgrammingCard.createMoveIICard();
            case MOVE_III   -> ProgrammingCard.createMoveIIICard();
            case TURN_RIGHT -> ProgrammingCard.createTurnRightCard();
            case TURN_LEFT  -> ProgrammingCard.createTurnLeftCard();
            case U_TURN     -> ProgrammingCard.createUTurnCard();
            case BACK_UP    -> ProgrammingCard.createBackUpCard();
            case POWER_UP   -> ProgrammingCard.createPowerUpCard();
            case AGAIN      -> ProgrammingCard.createAgainCard();

            // Damage Cards
            case SPAM       -> DamageCard.createSpamCard();
            case TROJAN     -> DamageCard.createTrojanCard();
            case WORM       -> DamageCard.createWormCard();
            case VIRUS      -> DamageCard.createVirusCard();

            // Special Programming Cards
            case ENERGY_ROUTINE  -> SpecialProgrammingCard.createEnergyRoutineCard();
            case SANDBOX_ROUTINE -> SpecialProgrammingCard.createSandboxRoutineCard();
            case WEASEL_ROUTINE  -> SpecialProgrammingCard.createWeaselRoutineCard();
            case SPEED_ROUTINE   -> SpecialProgrammingCard.createSpeedRoutineCard();
            case SPAM_FOLDER     -> SpecialProgrammingCard.createSpamFolderCard();
            case REPEAT_ROUTINE  -> SpecialProgrammingCard.createRepeatRoutineCard();

            // Upgrade Cards
            case ADMIN_PRIVILEGE -> UpgradeCard.createAdminPrivilegeCard();
            case REAR_LASER      -> UpgradeCard.createRearLaserCard();
            case MEMORY_SWAP     -> UpgradeCard.createMemorySwapCard();
            case SPAM_BLOCKER    -> UpgradeCard.createSpamBlockerCard();
        };
    }
}
