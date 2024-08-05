package com.github.adrior.roborally.test.util;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.server.Server;

import java.util.List;

public class TestUtils {

    /**
     * Starts the given {@link Server} instance in a new thread.
     * This method uses reflection to invoke the protected {@code start} method of the {@link Server}.
     *
     * @param server The {@link Server} instance to start.
     * @throws RuntimeException if there is an error starting the server, typically due to reflection issues.
     */
    public static void setUpServer(Server server) throws RuntimeException {
        new Thread(server::start).start();
    }


    /**
     * Waits for the {@link Server} to start running. It checks the {@link Server} status periodically
     * and waits up to a maximum number of attempts before throwing an AssertionError if the {@link Server} does not start.
     *
     * @param server The {@link Server} instance to check.
     * @throws AssertionError if the {@link Server} does not start within the specified attempts.
     */
    public static void awaitServerStart(Server server) {
        int attempts = 100;
        while (!server.isRunning() && attempts-- > 0) {
            try {
                Thread.sleep(10);   // Wait 10ms before the next check.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (!server.isRunning()) {
            throw new AssertionError("Server did not start");
        }
    }


    /**
     * Waits for one second to allow processes to finish.
     */
    public static void waitFor(int durationInMilliSeconds) {
        try {
            Thread.sleep(durationInMilliSeconds);   // Wait for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Prints out the tile names at each position in the course.
     * Used for debugging purposes.
     */
    public static void printTileNames(List<List<List<Tile>>> racingCourse) {
        for (int x = 0; x < racingCourse.size(); x++) {
            for (int y = 0; y < racingCourse.get(x).size(); y++) {
                List<Tile> tiles = racingCourse.get(x).get(y);
                System.out.print("Position (" + x + "," + y + "): ");
                if (tiles.isEmpty()) {
                    System.out.print("Empty");
                } else {
                    for (Tile tile : tiles) {
                        System.out.print(tile.getClass().getSimpleName() + " ");
                    }
                }
                System.out.println();
            }
        }
    }
}
