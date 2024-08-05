package com.github.adrior.roborally.core.game.util;

import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Timer class to manage a single instance timer for the {@link TurnManager}.
 * Ensures the timer can only be started once and handles broadcasting messages at the start and end.
 */
public final class Timer {
    private static final AtomicReference<Timer> instance = new AtomicReference<>(); // Thread-safe instance
    private static final ReentrantLock lock = new ReentrantLock();
    private final TurnManager turnManager;
    private final AtomicBoolean hasStartedForRound = new AtomicBoolean(false);

    @NonNull private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Getter private final AtomicBoolean isRunning = new AtomicBoolean(false);

    // Private constructor to prevent instantiation.
    private Timer(TurnManager turnManager) {
        this.turnManager = turnManager;
    }


    /**
     * Retrieves the singleton instance of the Timer class.
     *
     * @param turnManager The TurnManager instance.
     * @return the singleton instance of the Timer class
     */
    public static Timer getInstance(TurnManager turnManager) {
        if (null == instance.get()) {
            synchronized (Timer.class) {
                if (null == instance.get()) {
                    instance.set(new Timer(turnManager));
                }
            }
        }
        return instance.get();
    }


    /**
     * Starts the timer if it is not already running.
     *
     * @param turnManager The TurnManager instance.
     */
    @Synchronized
    public static void startTimer(TurnManager turnManager) {
        lock.lock();
        try {
            Timer timerInstance = getInstance(turnManager);
            if (null != timerInstance && timerInstance.isRunning.compareAndSet(false, true)) {
                if (timerInstance.hasStartedForRound.compareAndSet(false, true)) {
                    ServerCommunicationFacade.log("<Timer> Has been started");
                    ServerCommunicationFacade.broadcast(PredefinedServerMessages.timerStarted());
                    timerInstance.scheduler.schedule(timerInstance::finishTimer, 30, TimeUnit.SECONDS);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Cancels the timer and cancels the scheduled task.
     */
    public static void cancelTimer(TurnManager turnManager) {
        lock.lock();
        try {
            Timer timerInstance = getInstance(turnManager);
            if (null != timerInstance && timerInstance.isRunning.get()) {
                ServerCommunicationFacade.log("<Timer> Aborting");
                if (null != turnManager) timerInstance.finishTimer();
                timerInstance.scheduler.shutdownNow();
                timerInstance.scheduler = Executors.newSingleThreadScheduledExecutor();
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Method to handle timer completion logic.
     * Broadcasts the timer end message and invokes the finishing logic in TurnManager.
     */
    private void finishTimer() {
        if (isRunning.compareAndSet(true, false)
                && GameState.PROGRAMMING_PHASE == turnManager.getCurrentPhase()) {
            ServerCommunicationFacade.log("<Timer> Ended");
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.timerEnded(
                    turnManager.getClientIDsArrayWithPendingSelections()));

            turnManager.discardAllPlayerHands();
            turnManager.fillEmptyRegisters();
            turnManager.advancePhase();
        }
    }



    /**
     * Resets the timer atomic boolean flags.
     */
    public static void resetTimer(TurnManager turnManager) {
        Timer timerInstance = getInstance(turnManager);
        if (null != timerInstance) {
            timerInstance.isRunning.set(false);
            timerInstance.hasStartedForRound.set(false);
        }
    }


    /**
     * Resets the timer instance to null.
     */
    public static void resetInstance() {
        lock.lock();
        try {
            Timer timerInstance = instance.get();
            if (null != timerInstance) {
                ServerCommunicationFacade.log("<Timer> Resetting instance");
                if (timerInstance.isRunning.get()) {
                    timerInstance.scheduler.shutdownNow();
                }
                instance.set(null);
            }
        } finally {
            lock.unlock();
        }
    }
}
