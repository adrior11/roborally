package com.github.adrior.roborally.utility;

import lombok.Getter;
import lombok.NonNull;

/**
 * The Orientation enum represents the four cardinal orientation
 * (right, down, left, up) with associated vectors.
 *
 * <p> It provides methods such as to get the {@link Vector} representation
 * of a orientation or to turn left or right from a given orientation.
 */
@Getter
public enum Orientation {
    RIGHT(new Vector(1, 0)),
    BOTTOM(new Vector(0, 1)),
    LEFT(new Vector(-1, 0)),
    TOP(new Vector(0, -1));

    private final Vector vector;

    /**
     * Constructs a Orientation with the given vector.
     *
     * @param vector the vector representing the orientation.
     */
    Orientation(Vector vector) {
        this.vector = vector;
    }

    // Array of orientation in clockwise order
    private static final Orientation[] ORIENTATION = values();

    /**
     * Turns left (counterclockwise) by 90 degrees.
     *
     * @return the new orientation after turning left.
     */
    public Orientation turnLeft() {
        return ORIENTATION[(this.ordinal() + 3) % 4];
    }

    /**
     * Turns right (clockwise) by 90 degrees.
     *
     * @return the new orientation after turning right.
     */
    public Orientation turnRight() {
        return ORIENTATION[(this.ordinal() + 1) % 4];
    }

    /**
     * Turns by 180 degrees (U-turn).
     *
     * @return the new orientation after making a U-turn.
     */
    public Orientation uTurn() {
        return ORIENTATION[(this.ordinal() + 2) % 4];
    }

    /**
     * Returns the number of 90-degree clockwise rotations needed to reach the specified orientation from the current orientation.
     *
     * @param orientation the target orientation to rotate to.
     * @return the number of 90-degree clockwise rotations needed.
     */
    public int getRotationStepsTo(@NonNull Orientation orientation) {
        int currentOrdinal = this.ordinal();
        int targetOrdinal = orientation.ordinal();
        return (targetOrdinal - currentOrdinal + 4) % 4;
    }


    /**
     * Returns the new orientation of a given orientation after moving from one conveyor belt to another.
     *
     * @param push The push orientation of the starting conveyor belt.
     * @param into The push orientation of the destination conveyor belt.
     * @return The new orientation of the robot if it rotates, else null.
     */
    public Orientation getConveyorRotation(@NonNull Orientation push, @NonNull Orientation into) {
        int pushOrdinal = push.ordinal();
        int intoOrdinal = into.ordinal();

        // Check if the turn is to the left or right
        if ((pushOrdinal + 1) % 4 == intoOrdinal) {
            return this.turnRight();
        } else if ((pushOrdinal + 3) % 4 == intoOrdinal) {
            return this.turnLeft();
        }

        // If not opposite or adjacent, it means orientations are invalid for a turn
        return null;
    }
}
