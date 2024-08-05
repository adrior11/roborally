package com.github.adrior.roborally.core.game;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.util.PriorityQueue;
import com.github.adrior.roborally.core.game.util.Timer;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.exceptions.CardManagerException;
import com.github.adrior.roborally.exceptions.InvalidGameStateException;
import com.github.adrior.roborally.exceptions.InvalidRegisterException;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Pair;
import com.github.adrior.roborally.utility.Vector;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.IntStream;

/**
 * The TurnManager class handles the management of turns in the RoboRally game.
 * It manages the phases of a round: Setup Phase, Upgrade Phase, Programming Phase, and Activation Phase.
 *
 * @see RacingCourse
 * @see Deck
 * @see Player
 * @see GameState
 */
@Getter
@ToString
public class TurnManager {
    private final RacingCourse currentCourse;
    private final LinkedList<Pair<Integer, Integer>> adminPriorityQueue = new LinkedList<>();
    private LinkedList<Player> players;
    private int roundCounter = 0;

    @NonNull private final Deck upgradeShop;
    @NonNull private GameState currentPhase = GameState.SETUP_PHASE;

    @Setter private int currentPlayerIndex = 0;
    @Setter private Player currentPlayer;
    @Setter private int currentRegisterIndex = 0;

    /**
     * Constructs a TurnManager for managing the game turns.
     *
     * @param players The list of players participating in the game.
     */
    public TurnManager(@NonNull LinkedList<Player> players, RacingCourse currentCourse) {
        this.players = players;
        this.currentPlayer = players.getFirst();
        this.currentCourse = currentCourse;

        executeCurrentPhase();
        upgradeShop = new Deck(SharedDeck.upgradeDeck.drawCards(players.size()));
    }


    /**
     * Advances the current phase of the game to the next phase in the sequence.
     * After advancing to the next phase, it executes the newly advanced phase.
     *
     * <p> Phases: Setup Phase, Upgrade Phase, Programming Phase, and Activation Phase.
     *
     * @see GameState
     */
    public void advancePhase() {
        if (!GameManager.getInstance().getIsGameActive().get()) return;
        logPhaseState("Advancing phase");
        currentPhase = currentPhase.advance();
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.activePhase(currentPhase.getPhase()));
        executeCurrentPhase();
    }


    /**
     * Executes the Current Phase by calling the method from the current Phase of the {@link GameState}.
     */
    private void executeCurrentPhase() {
        currentPhase.executePhase(this);
    }


    /**
     * Sets the starting point for the robot of a {@link Player}.
     *
     * @param x The x-coordinate of the starting position.
     * @param y The y-coordinate of the starting position.
     * @param clientId The client clientId of the player.
     */
    public void setStartingPoint(int x, int y, int clientId) {
        ServerCommunicationFacade.log(String.format(
                "<TurnManager> Player %s is setting his starting position to [%s:%s] ", clientId, x, y));

        Player player = players.get(currentPlayerIndex);
        player.robot().setPosition(new Vector(x, y));
        player.robot().setStartingPosition(new Vector(x, y));
        player.robot().setOrientation(currentCourse.getCourseData().specialTilePositions().antenna().tile().getAntennaOrientation());
        player.flags().setSetStartingPoint(true);

        advanceSetUpPhase();
    }


    /**
     * Advances the setup phase of the game.
     *
     * <p> If all players have set their starting points, it advances to the next phase of the game.
     * If not, it allows the next player to set their starting point.
     */
    synchronized void advanceSetUpPhase() {
        if (!GameManager.getInstance().getIsGameActive().get()) return;

        // Advance to the next phase if the last player has set his starting point.
        if (allPlayersHaveSetStartingPoints() || currentPlayerIndex >= players.size()) {
            ServerCommunicationFacade.log("<TurnManager> All Starting positions have been set");
            currentPlayerIndex = 0;
            advancePhase();
            return;
        }

        // Allow the next player to set his starting point.
        if (++currentPlayerIndex < players.size()) {
            currentPlayer = players.get(currentPlayerIndex);
            broadcastCurrentPlayer();
        }
    }


    /**
     * Refills the {@code upgradeShop} if there are fewer cards than players, else the shop gets exchanged.
     *
     * @see Deck
     */
    void refillUpgradeShop() {
        logPhaseState(String.format("Upgrade Shop: %s | Upgrade Deck: %s",
                upgradeShop.getCards().size(), SharedDeck.upgradeDeck.getCards().size()));

        assertTotalCardCount();

        // Retrieve the current PriorityQueue and inform the first player about their turn.
        PositionedTile<Antenna> antenna = currentCourse.getCourseData().specialTilePositions().antenna();
        players = PriorityQueue.getPlayersPriorityQueue(players, antenna, currentCourse.getDimensions(), null);
        currentPlayer = players.get(currentPlayerIndex);

        List<Integer> priorityQueuePlayerIDs = players.stream().map(Player::clientId).toList();
        logPhaseState(String.format("New Priority Queue: %s", priorityQueuePlayerIDs));

        int amount = players.size() - upgradeShop.getCards().size();

        if (0 == roundCounter) {
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.refillShop(upgradeShop.getAllCardNames()));
            broadcastCurrentPlayer();
            return;
        }

        if (0 < amount) {
            List<Card> refilledCards = SharedDeck.upgradeDeck.drawCards(amount);
            upgradeShop.getCards().addAll(refilledCards);

            logPhaseState(String.format("Refilling the upgrade shop with %s upgrade cards", amount));
            logPhaseState(String.format("Current Shop: %s", Arrays.toString(upgradeShop.getAllCardNames())));

            String[] refilledCardStrings = refilledCards.stream()
                            .map(card -> card.getCardType().toString())
                            .toArray(String[]::new);

            logPhaseState(String.format("Sending refilled cards message of: %s", Arrays.toString(refilledCardStrings)));
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.refillShop(refilledCardStrings));
            broadcastCurrentPlayer();
        } else {
            logPhaseState("Exchanging the upgrade shop");
            exchangeUpgradeShop();
        }
    }


    /**
     * Exchanges the current {@code upgradeShop} cards with new ones from the {@link Deck}.
     */
    private void exchangeUpgradeShop() {
        List<Card> oldCards = upgradeShop.getCards();
        SharedDeck.upgradeDeck.addCards(oldCards);
        upgradeShop.getCards().clear();

        SharedDeck.upgradeDeck.shuffle();
        upgradeShop.addCards(SharedDeck.upgradeDeck.drawCards(players.size()));

        logPhaseState(String.format("Upgrade Shop: %s | Upgrade Deck: %s",
                upgradeShop.getCards().size(), SharedDeck.upgradeDeck.getCards().size()));
        assertTotalCardCount();

        logPhaseState(String.format("Current Shop: %s", Arrays.toString(upgradeShop.getAllCardNames())));
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.exchangeShop(upgradeShop.getAllCardNames()));
        broadcastCurrentPlayer();
    }


    /**
     * Advances the upgrade phase of the game.
     *
     * <p> If all players have decided on buying an upgrade card, it advances to the next phase of the game.
     * If not, it allows the next {@link Player} to buy and upgrade card.
     */
    public void advanceUpgradePhase() {
        if (!GameManager.getInstance().getIsGameActive().get()) return;

        // Advance to the next phase if the last player has set his starting point.
        if (allPlayersHaveDecidedUpgradePhase() || currentPlayerIndex >= players.size()) {
            logPhaseState("All players have decided on buying upgrade cards");
            advancePhase();
            return;
        }

        // Allow the next player to buy an upgrade card.
        if (++currentPlayerIndex < players.size()) {
            currentPlayer = players.get(currentPlayerIndex);
            broadcastCurrentPlayer();
        }
    }


    /**
     * Deal cards to the players and shuffles their respective decks if necessary.
     *
     * @see Card
     */
    public void dealCards() {
        int numberOfCards = 9;
        logPhaseState("Dealing programming cards");

        assertTotalCardCount();

        for (Player player : players) {

            // Assert that the player's card manager remains with a minimum of 20 cards over the life cycle.
            if (20 > player.cardManager().getTotalDeckSizes()) {
                ServerCommunicationFacade.log(String.format(
                        "<TurnManager> Error: Shared deck damage cards: [%s, %s, %s, %s] | Shared deck upgrade cards: %s",
                        SharedDeck.spamDeck.getCards().size(), SharedDeck.trojanDeck.getCards().size(),
                        SharedDeck.wormDeck.getCards().size(), SharedDeck.virusDeck.getCards().size(),
                        SharedDeck.upgradeDeck.getCards().size()));
                logPhaseState(String.format("Error: Player %s deck sizes after failing: %s",
                        player.clientId(), player.cardManager()));
                logPhaseState(String.format("Error: Player %s filled register after failing: %s",
                        player.clientId(), player.programmingRegister().getAllRegisters().size()));

                throw new CardManagerException("Card manager cannot have less than 20 cards at the start of the programming phase");
            }

            logPhaseState(String.format("Player %s deck sizes before potential shuffle & dealing out: %s",
                    player.clientId(), player.cardManager()));

            // Assert the deck size of the player & reshuffle the discard pile into it, if insufficient.
            if (player.cardManager().assertDrawDeckSize(numberOfCards)) {
                player.cardManager().reshuffleDiscardPileIntoDeck();
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.shuffleCoding(player.clientId()));
            }

            // Each player draws numberOfCards & the information gets broadcast.
            player.cardManager().drawCards(numberOfCards);
            ServerCommunicationFacade.log(String.format("<TurnManager> Sending YourCards message to Player %s", player.clientId()));
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.yourCards(
                    player.cardManager().getHand().getAllCardNames()), player.clientId());
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.notYourCards(player.clientId(), numberOfCards));

            logPhaseState(String.format("Player %s deck sizes: %s", player.clientId(), player.cardManager()));
            logPhaseState(String.format("Player %s's hand: %s", player.clientId(), Arrays.toString(player.cardManager().getHand().getAllCardNames())));

            assertTotalCardCount();
        }
    }


    /**
     * Iterates of each register for every {@link Player} and fills an empty
     * register with the top {@link Card} of their respective draw decks.
     */
    public void fillEmptyRegisters() {
        assertTotalCardCount();

        for (Player player : players) {
            LinkedList<String> filledCards = new LinkedList<>();
            IntStream.range(0, player.programmingRegister().getRegisters().length)
                    .filter(i -> null == player.programmingRegister().getRegister(i))
                    .forEach(i -> {
                        if (player.cardManager().assertDrawDeckSize(1)) player.cardManager().reshuffleDiscardPileIntoDeck();
                        Card card = player.cardManager().getDrawDeck().removeFirstCard();
                        logPhaseState(String.format("Filled empty register %s of player %s with: %s",
                                i, player.clientId(), card.getCardType().toString()));
                        player.programmingRegister().setRegister(i, card);
                        filledCards.add(card.getCardType().toString());
                    });

            if (!filledCards.isEmpty()) {
                logPhaseState(String.format("Filled the empty registers for %s", player.clientId()));
                String[] cardsYouGotNow = filledCards.toArray(new String[0]);
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.cardsYouGotNow(cardsYouGotNow), player.clientId());
                logPhaseState(String.format("Notified player %s about %s filled cards", player.clientId(), filledCards.size()));
                logPhaseState(String.format("Player %s deck sizes after filling the registers: %s",
                        player.clientId(), player.cardManager()));
            }

            List<String> cardsInRegisterString = player.programmingRegister().getAllRegisters().stream()
                    .map(Card::getCardType)
                    .map(CardType::toString)
                    .toList();

            logPhaseState(String.format("Player %s's register: %s", player.clientId(),
                    Arrays.toString(cardsInRegisterString.toArray())));

            assertTotalCardCount();
            if (!player.programmingRegister().isFilled()) {
                throw new InvalidRegisterException("All register must have a card after filling the registers");
            }
        }
    }


    /**
     * Activates the specified register for all players, executing the card effects in the order of priority.
     * Players are sorted by their distance to the {@link Antenna} position and {@link CardType#ADMIN_PRIVILEGE}.
     *
     * @param register The register index to activate (0-4).
     */
    public void activateRegister(int register) {
        logPhaseState(String.format("Activating register: %s", register));

        // Skip the round if all players are rebooting, as no programming card will be executed.
        if (allPlayerRebooting()) {
            logPhaseState("Skipping round as all players are rebooting...");
            resetRound();
            return;
        }

        resetPlayerHasPlayedRegister();
        currentPlayerIndex = 0;

        // Inform players about all the cards that have been played for that given register.
        Map<Integer, String> activeCardsMap = new LinkedHashMap<>();
        for(Player player : players) {
            try {
                activeCardsMap.put(player.clientId(), player.programmingRegister().getRegister(register).getCardType().toString());
            } catch (NullPointerException e) {
                throw new InvalidRegisterException("Register cannot be empty during activation.");
            }
        }
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.currentCards(activeCardsMap));

        // Retrieve the current PriorityQueue and inform the first player about their turn.
        logPhaseState(String.format("Retrieving the Priority Queue for register: %s", currentRegisterIndex));
        PositionedTile<Antenna> antenna = currentCourse.getCourseData().specialTilePositions().antenna();
        players = PriorityQueue.getPlayersPriorityQueue(players, antenna, currentCourse.getDimensions(),
                getRegisteredAdminPriorityIDsForRegister(register));

        List<Integer> priorityQueuePlayerIDs = players.stream().map(Player::clientId).toList();
        logPhaseState(String.format("New Priority Queue: %s", priorityQueuePlayerIDs));

        assertTotalCardCount();

        currentPlayer = players.get(currentPlayerIndex);

        while (currentPlayer.flags().isRebooting()) {
            logPhaseState(String.format("Skipping first player %s as he's rebooting...", currentPlayer.clientId()));
            currentPlayer.flags().setPlayedRegister(true);
            currentPlayerIndex++;
            currentPlayer = players.get(currentPlayerIndex);
        }

        logPhaseState(String.format("Notifying first player %s to play a card", currentPlayer.clientId()));
        broadcastCurrentPlayer();
    }


    /**
     * Activates all factory elements on the board in the predefined order.
     * This method iterates through the factory elements, starting with BLUE_CONVEYOR_BELTS
     * and proceeding through the defined sequence until all elements have been activated.
     * The {@link ActivationOrder} is based on the rules of the game.
     *
     * <p> The method ensures that each type of factory element is activated once per cycle.
     * The activation sequence is:
     * 1. Blue Conveyor Belts
     * 2. Green Conveyor Belts
     * 3. Push Panels
     * 4. Gears
     * 5. Board Lasers
     * 6. Robot Lasers
     * 7. Energy Spaces
     * 8. Checkpoints
     *
     * <p> The cycle completes when it loops back to the starting element, BLUE_CONVEYOR_BELTS.
     */
    public void activateFactoryElements() {
        logPhaseState("Activating factory");

        ActivationOrder currentElement = ActivationOrder.BLUE_CONVEYOR_BELTS;

        // Complete a full cycle activating each factory element in order, given by the rules.
        while (null != currentElement) {
            currentElement.activateElement(this);
            currentElement = currentElement.activateNextElement();
            if (ActivationOrder.BLUE_CONVEYOR_BELTS == currentElement) break;
        }
    }


    /**
     * Advances the current {@link Player}'s turn to the next player or activates the factory elements if all players have played their current register.
     */
    public synchronized void advancePlayCard() {
        if (!GameManager.getInstance().getIsGameActive().get()) return;

        if (allPlayersDidActivateCurrentRegister() || currentPlayerIndex >= players.size()) {
            logPhaseState(String.format("All cards played for register %s", currentRegisterIndex));

            activateFactoryElements();

            GameManager.getInstance().delayFor(500 * players.size());

            if (4 == currentRegisterIndex) {
                resetRound();
            } else {
                advanceRegister();
            }

            return;
        }

        if (++currentPlayerIndex < players.size() && GameManager.getInstance().getIsGameActive().get()) {
            currentPlayer = players.get(currentPlayerIndex);

            if (currentPlayer.flags().isRebooting()) {
                logPhaseState(String.format("Skipping player %s because of reboot...", currentPlayer.clientId()));

                currentPlayer.flags().setPlayedRegister(true);

                advancePlayCard();
            }

            logPhaseState(String.format("Notifying next player %s to play a card", currentPlayer.clientId()));
            broadcastCurrentPlayer();
        }
    }


    /**
     * Advances to the next register if the current register index is less than 4.
     * Activates the next register and resets the round if the last register is activated.
     */
    public void advanceRegister() {
        if (4 > currentRegisterIndex && GameManager.getInstance().getIsGameActive().get()) {
            logPhaseState("Advancing register");
            activateRegister(++currentRegisterIndex);
        }
    }


    /**
     * Starts the {@link Timer} for the TurnManager.
     */
    public void startTimer() {
        logPhaseState("Starting the timer...");
        Timer.startTimer(this);
    }


    /**
     * Cancels the {@link Timer} for the TurnManager.
     */
    public void cancelTimer() {
        logPhaseState("Canceling the timer...");
        Timer.cancelTimer(this);
    }


    /**
     * Discards all {@link Player} hands if they are not empty.
     */
    public void discardAllPlayerHands() {
        logPhaseState("Discarding all player hands");
        players.stream()
                .filter(player -> !player.cardManager().getHand().getCards().isEmpty())
                .forEach(player -> player.cardManager().discardHand());
    }


    /**
     * Method to check if {@link Player} won the game.
     *
     * <p> It retrieves the total number of checkpoints for the current {@link RacingCourse}
     * and compares it with the player's robot total number of reached checkpoints.
     */
    public void checkWinConditions() {
        int totalCheckpoints = currentCourse.getCourseData().specialTilePositions().checkpoints().size();

        // Check if a player has reached the last checkpoint.
        players.stream()
                .filter(player -> player.robot().getCheckpoint() == totalCheckpoints)
                .findFirst()
                .ifPresent(player -> GameManager.getInstance().endGame(player.clientId()));
    }


    /**
     * Resets the round by resetting energy spaces, {@link Player#flags()}, and advancing the game phase.
     */
    public void resetRound() {
        roundCounter++;
        currentRegisterIndex = 0;
        currentPlayerIndex = 0;
        adminPriorityQueue.clear();

        resetEnergySpaces();
        resetPlayerRegisters();
        resetPlayerGameFlags();

        Timer.resetTimer(this);

        advancePhase();
    }


    /**
     * Clears all registers for all players.
     */
    private void resetPlayerRegisters() {
        players.forEach(player -> {
            List<Card> cardsInRegister = player.programmingRegister().getAllRegisters();
            for (Card card : cardsInRegister) player.cardManager().addCardToDiscardPile(card);
            player.programmingRegister().clearAllRegisters();

            if (player.programmingRegister().isFilled()) {
                throw new InvalidRegisterException("All register must be empty after clearing them");
            }
        });

        assertTotalCardCount();
    }


    /**
     * Resets the {@code playedRegister} finished flag for all players.
     */
    private void resetPlayerHasPlayedRegister() {
        players.forEach(player -> player.flags().setPlayedRegister(false));
    }


    /**
     * Resets the game loop related {@link Player#flags()} for all players.
     */
    private void resetPlayerGameFlags() {
        players.forEach(player -> {
            player.flags().setRebooting(false);
            player.flags().setDecidedUpgradePhase(false);
            player.flags().setSelectionFinished(false);
            player.flags().setAwaitingUpgradeCard(false);
            player.flags().setPlayedRegister(false);
        });
    }


    /**
     * Sets all energy space flags of {@code hasEnergy} back to true.
     */
    private void resetEnergySpaces() {
        this.currentCourse.getEnergySpaces().forEach(energySpace -> energySpace.tile().reloadEnergy());
    }


    /**
     * Check if all players have filled their registers
     *
     * @return true if all players have filled their registers, false if not.
     */
    public boolean allPlayersDidFillRegisters() {
        return players.stream().allMatch(p -> p.flags().isSelectionFinished());
    }


    /**
     * Check if all players have activated their current register.
     *
     * @return true if all players have played their current register, false if not.
     */
    private boolean allPlayersDidActivateCurrentRegister() {
        return players.stream().allMatch(player -> player.flags().isPlayedRegister());
    }


    /**
     * Check if all players have set their starting point.
     *
     * @return true if all players have set their starting point, false if not.
     */
    private boolean allPlayersHaveSetStartingPoints() {
        return players.stream().allMatch(player -> player.flags().isSetStartingPoint());
    }


    /**
     * Check if all players have decided on buying an upgrade card or not.
     *
     * @return true if all players have decided, false if not.
     */
    private boolean allPlayersHaveDecidedUpgradePhase() {
        return players.stream().allMatch(player -> player.flags().isDecidedUpgradePhase());
    }


    /**
     * Check if all players are rebooting.
     *
     * @return true if all players are rebooting, false if not.
     */
    private boolean allPlayerRebooting() {
        return players.stream().allMatch(player -> player.flags().isRebooting());
    }


    /**
     * Retrieves the client IDs of players who have not yet finished their selection.
     *
     * @return An array of client IDs with selection wasn't finished.
     */
    public int[] getClientIDsArrayWithPendingSelections() {
        return players.stream()
                .filter(player -> !player.flags().isSelectionFinished())
                .mapToInt(Player::clientId)
                .toArray();
    }


    /**
     * Checks if a {@link Player} with the specified player ID has registered {@link CardType#ADMIN_PRIVILEGE}.
     *
     * @param clientId The player ID to search for.
     * @return True if the player with the given player ID has registered admin privilege, otherwise false.
     */
    public boolean hasPlayerRegisteredAdminPriority(int clientId) {
        return adminPriorityQueue.stream().anyMatch(pair -> pair.value().equals(clientId));
    }


    /**
     * Retrieves the list of {@link Player} IDs that have registered {@link CardType#ADMIN_PRIVILEGE} for a specific register.
     *
     * @param register The register to search for.
     * @return A list of player IDs that have admin priority for the specified register.
     */
    private List<Integer> getRegisteredAdminPriorityIDsForRegister(int register) {
        return adminPriorityQueue.stream()
                .filter(pair -> pair.key().equals(register))
                .map(Pair::value)
                .toList();
    }


    /**
     * Reduces the {@code currentPlayerIndex} by one.
     *
     * <p> This ensures a proper game flow can occur regarding the
     * {@link #advanceSetUpPhase()}, {@link #advanceUpgradePhase()}
     * and {@link #advancePlayCard()} methods.
     */
    void reduceCurrentPlayerIndex() {
        --currentPlayerIndex;
        logPhaseState("Reduced the current index due to the disconnect of a player to: " + currentPlayerIndex);
    }


    /**
     * Asserts that the total number of cards in all decks and registers is correct.
     *
     * <p> The shared deck contains exactly 74 Damage cards + 40 upgrade cards.
     * Each player has 20 cards in their decks and registers at the start.
     * The total cards should be 114 + (20 * {@code players.size()}).
     *
     * <p> If the total number of cards differentiates from the expected number,
     * a new custom {@link InvalidGameStateException} will be thrown, thus stopping the game.
     *
     * @see SharedDeck
     */
    public synchronized void assertTotalCardCount() {
        int totalExpectedCards = 114 + (20 * players.size());

        int totalCardsInSharedDecks = SharedDeck.spamDeck.getCards().size() +
                SharedDeck.trojanDeck.getCards().size() +
                SharedDeck.wormDeck.getCards().size() +
                SharedDeck.virusDeck.getCards().size() +
                SharedDeck.upgradeDeck.getCards().size();

        int totalCardsInPlayerDecks = players.stream().mapToInt(player ->
                player.cardManager().getDrawDeck().getCards().size() +
                player.cardManager().getHand().getCards().size() +
                player.cardManager().getDiscardPile().getCards().size() +
                player.programmingRegister().getAllRegisters().size() +
                player.installedUpgrades().getCards().size()
        ).sum();

        int totalCardsInUpgradeShop = upgradeShop.getCards().size();

        int totalCards = totalCardsInSharedDecks + totalCardsInPlayerDecks + totalCardsInUpgradeShop;

        ServerCommunicationFacade.log(String.format("<TurnManager> Total expected cards: %d, Total actual cards: %d", totalExpectedCards, totalCards));

        if (totalCards != totalExpectedCards && GameManager.getInstance().getIsGameActive().get()) {
            throw new InvalidGameStateException("Card count mismatch detected! Expected " + totalExpectedCards + " but found " + totalCards);
        }
    }


    /**
     * Logs the current state of the phase with a given message.
     *
     * <p> This method formats the message to include the {@code roundCounter} and logs it
     * using the {@link ServerCommunicationFacade}.
     *
     * @param message The message describing the current state to be logged.
     */
    private void logPhaseState(String message) {
        ServerCommunicationFacade.log(String.format("<TurnManager> [%s] %s", roundCounter, message));
    }


    /**
     * Notifies all players whose turn it is using the {@link ServerCommunicationFacade}.
     */
    private void broadcastCurrentPlayer() {
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.currentPlayer(currentPlayer.clientId()));
    }
}
