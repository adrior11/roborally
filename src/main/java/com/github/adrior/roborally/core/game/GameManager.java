package com.github.adrior.roborally.core.game;

import com.github.adrior.roborally.core.card.*;
import com.github.adrior.roborally.core.game.util.Timer;
import com.github.adrior.roborally.core.player.*;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player.Flags;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * The GameManager class is responsible for managing the overall state and flow of the game.
 * It handles the addition of players and initializes the racing course.
 *
 * @see Player
 * @see TurnManager
 * @see RacingCourse
 */
@Getter
public final class GameManager {
    private static final AtomicReference<GameManager> instance = new AtomicReference<>(); // Thread-safe instance

    private final LinkedList<Player> players = new LinkedList<>();
    private final AtomicBoolean isGameActive = new AtomicBoolean(false);
    private TurnManager turnManager;

    @Setter private int minPlayers;
    @Setter private RacingCourse course;

    // Private constructor to prevent instantiation
    private GameManager() {}


    /**
     * Returns the singleton instance of the GameManager.
     *
     * @return the singleton instance.
     */
    public static GameManager getInstance() {
        if (null == instance.get()) {
            synchronized (GameManager.class) {
                if (null == instance.get()) {
                    instance.set(new GameManager());
                }
            }
        }
        return instance.get();
    }


    /**
     * Adds a new {@link Player} to the game.
     *
     * @param clientId the clientId of the client representing the player.
     */
    public void addPlayer(int clientId) {
        ServerCommunicationFacade.log("<GameManager> adding Player " + clientId);
        Player player = new Player(
                clientId, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(), new Flags());
        players.add(player);
    }


    /**
     * Initializes the {@link RacingCourse} for the game.
     *
     * @param course the available course to be used for the game.
     */
    public void initializeCourse(@NonNull AvailableCourses course) {
        ServerCommunicationFacade.log("<GameManager> Initializing course " + course.name());
        this.course = RacingCourse.createRacingCourse(course);
    }


    /**
     * Start the core gameplay loop by activating the {@link TurnManager}.
     */
    public void startGame() {
        if (isGameActive.compareAndSet(false, true)) {
            turnManager = new TurnManager(players, course);
        }
    }


    /**
     * Ends the game and broadcasts the game finished message via the {@link ServerCommunicationFacade}.
     * It then continues with the {@link #resetGame()} logic allowing a new game instance to be initiated.
     *
     * @param clientId the clientId of the player who triggered the game end.
     */
    public void endGame(int clientId) {
        if (isGameActive.get()) {
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.gameFinished(clientId));
            resetGame();
        }
    }


    /**
     * Method to remove a {@link Player} from the game.
     *
     * @param clientID the client id of the player to be removed.
     */
    public void removePlayer(int clientID) {
        ServerCommunicationFacade.log(String.format(
                "<GameManager> Removing player with clientId %s from the game", clientID));

        Player toDelete = getPlayerByID(clientID);

        if (null == toDelete) {
            ServerCommunicationFacade.log("<GameManager> Error: Player not found");
            return;
        }

        if (isGameActive.get()) {
            returnPlayerCards(toDelete);
            turnManager.getPlayers().remove(toDelete);

            // Advance the turn manager state if the current/awaited player is the one to be removed.
            if (turnManager.getCurrentPlayer() == toDelete && 1 < turnManager.getPlayers().size()) {
                turnManager.reduceCurrentPlayerIndex();
                        
                if (GameState.SETUP_PHASE == turnManager.getCurrentPhase()) {
                    turnManager.advanceSetUpPhase();
                } else if (GameState.UPGRADE_PHASE == turnManager.getCurrentPhase()) {
                    turnManager.advanceUpgradePhase();
                } else if (GameState.ACTIVATION_PHASE == turnManager.getCurrentPhase()) {
                    turnManager.advancePlayCard();
                }
            } else if (turnManager.allPlayersDidFillRegisters()
                    && GameState.PROGRAMMING_PHASE == turnManager.getCurrentPhase()
                    && Timer.getInstance(turnManager).getIsRunning().get()) {
                turnManager.cancelTimer();
            }
        }

        // Immediately reset the game if there are no players left.
        players.remove(toDelete);
        ServerCommunicationFacade.log(String.format("<GameManager> Remaining players in the game: %s", players.size()));

        if (players.isEmpty()) resetGame();
    }


    /**
     * Returns the player's damage and upgrade cards to the {@link SharedDeck} when a {@link Player} is deleted.
     *
     * @see CardManager
     * @see Deck
     *
     * @param toDelete the player whose cards are to be returned to the shared deck.
     */
    private void returnPlayerCards(@NonNull Player toDelete) {
        CardManager cardManager = toDelete.cardManager();

        // Set of damage card types to be returned to the shared deck.
        Set<CardType> damageCardTypes = EnumSet.of(
                CardType.SPAM,
                CardType.TROJAN,
                CardType.WORM,
                CardType.VIRUS
        );

        // Collect the damage cards from all decks associated with the player & return them to the shared deck.
        Stream.of(cardManager.getDrawDeck(), cardManager.getHand(), cardManager.getDiscardPile())
                .flatMap(deck -> deck.getCards().stream())
                .filter(card -> damageCardTypes.contains(card.getCardType()))
                .forEach(SharedDeck::returnDamageCard);

        // Return the upgrade cards associated with the player to the shared deck.
        SharedDeck.upgradeDeck.addCards(toDelete.installedUpgrades().getCards());
    }


    /**
     * Finds a {@link Player} by their clientId.
     *
     * @param clientID The clientId of the player to find.
     * @return The Player instance corresponding to the client clientId, or null if not found.
     */
    public Player getPlayerByID(int clientID) {
        return players.stream()
                .filter(player -> player.clientId() == clientID)
                .findFirst()
                .orElse(null);
    }


    /**
     * Checks if any {@link Player} is currently selecting a map.
     *
     * @return true if at least one player is selecting a map, false otherwise.
     */
    public boolean isSomeoneSelectingMap() {
        return players.stream()
                .anyMatch(player -> player.flags().isSelectingMap());
    }


    /**
     * Finds and returns the first non-AI {@link Player} who is connected.
     *
     * @return The first non-AI Player instance, or null if none found.
     */
    public Player getFirstConnectedNonAIPlayer() {
        return players.stream()
                .filter(player -> !player.flags().isAI())
                .findFirst()
                .orElse(null);
    }


    /**
     * Introduces a delay in the game flow by pausing the execution
     * of the current thread for the specified amount of time.
     *
     * <p> This will only occur if there is at least one non AI player
     * participating, to allow a proper transition in the player's GUI.
     *
     * @param milliseconds The time to delay in milliseconds.
     */
    public void delayFor(int milliseconds) {
        if (null != getFirstConnectedNonAIPlayer()) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ServerCommunicationFacade.log("<GameManager> Wait interrupted: " + e.getMessage());
            }
        }
    }


    /**
     * Resets the game state, clearing all players and resetting the {@link SharedDeck}.
     */
    public void resetGame() {
        ServerCommunicationFacade.log("<GameManager> Resetting all game resources");
        Timer.resetInstance();

        this.isGameActive.set(false);
        this.turnManager = null;
        this.course = null;
        this.players.clear();

        ServerCommunicationFacade.kickAIClients();
        SharedDeck.resetDecks();
    }
}
