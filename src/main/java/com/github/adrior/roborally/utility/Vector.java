package com.github.adrior.roborally.utility;

import lombok.NonNull;

import java.util.Objects;

/**
 * Represents a 2D vector with integer components (x, y).
 * This record is immutable and provides operations typical of a mathematical vector,
 * such as addition and scalar multiplication. It is used primarily for position
 * and movement calculations within the game, facilitating the handling of coordinates
 * and orientations in a grid-based environment.
 *
 * @param x The x-component of the vector.
 * @param y The y-component of the vector.
 */
public record Vector(int x, int y) {

    /**
     * Adds this vector to another vector and returns the resulting vector.
     * This method performs a component-wise addition, which is useful for combining
     * movement or positions in the game's space.
     *
     * @param other The other vector to add.
     * @return The resulting vector after addition.
     */
    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    /**
     * Calculates the Manhattan distance between this vector and another vector.
     * The Manhattan distance is the sum of the absolute differences of their coordinates.
     *
     * @param other The other vector to calculate the distance to.
     * @return The Manhattan distance to the other vector.
     */
    public int manhattanDistanceTo(Vector other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Checks if this vector is aligned with another vector along a given axis and in a specific orientation.
     *
     * @param other The other vector to check alignment with.
     * @param orientation The orientation to check alignment (RIGHT, LEFT, UP, DOWN).
     * @return {@code true} if the vectors are aligned along the given axis and orientation, {@code false} otherwise.
     */
    public boolean isAligned(Vector other, Orientation orientation) {
        return switch (orientation) {
            case RIGHT -> this.y == other.y && this.x <= other.x;
            case LEFT -> this.y == other.y && this.x >= other.x;
            case TOP -> this.x == other.x && this.y >= other.y;
            case BOTTOM -> this.x == other.x && this.y <= other.y;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector(int ox, int oy)) {
            return this.x == ox && this.y == oy;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    @NonNull
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
