package com.github.adrior.roborally.core.game.recorder;

import com.github.adrior.roborally.utility.Vector;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for recording and verifying hits on robots by lasers.
 * This class is used for testing purposes to ensure that the correct robots are being hit by lasers.
 *
 * @see Vector
 */
@UtilityClass
public class HitRecorder {
    @Getter private static final Set<Vector> hitRobots = new HashSet<>();

    /**
     * Records a hit on a robot at the given position.
     *
     * @param robotPosition The position of the robot that was hit.
     */
    public static void recordHit(Vector robotPosition) {
        hitRobots.add(robotPosition);
    }

    /**
     * Checks if a robot at the given position was hit.
     *
     * @param robotPosition The position of the robot to check.
     * @return {@code true} if the robot at the given position was hit, {@code false} otherwise.
     */
    public static boolean wasHit(Vector robotPosition) {
        return hitRobots.contains(robotPosition);
    }

    /**
     * Resets the hit recorder, clearing all recorded hits.
     */
    public static void clearHits() {
        hitRobots.clear();
    }

    /**
     * Retrieves the total number of hit robots for testing purposes.
     *
     * @return The total number of hit robots.
     */
    public static int totalHits() {
        return hitRobots.size();
    }
}
