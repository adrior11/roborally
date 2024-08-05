package com.github.adrior.roborally.core.player;

import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * The Robot class represents a robot in the game.
 * It encapsulates the state and behavior of the robot, including its position, orientation, energy level, and checkpoints reached.
 *
 * @see Vector
 * @see Orientation
 */
@Getter
@NoArgsConstructor
public class Robot {
    private int checkpoint = 0;
    private int energy = 5;

    @Setter Vector position;
    @Setter Vector startingPosition;    // This only gets set once handling rebooting on a starting board.
    @Setter private Orientation orientation;


    /**
     * Moves the robot into the specified orientation.
     *
     * @param orientation The orientation to move the robot to.
     */
    public void move(@NonNull Orientation orientation) {
        position = position.add(orientation.getVector());
    }


    /**
     * Adjusts the robot's energy by a specified amount.
     *
     * @param amount the amount to adjust the energy by (can be positive or negative)
     */
    public void adjustEnergy(int amount) {
        this.energy += amount;
    }


    /**
     * Increments the robot's checkpoint counter by one.
     */
    public void incrementCheckpoint() {
        this.checkpoint++;
    }
}
