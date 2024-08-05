package com.github.adrior.roborally.random_bot;

import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import lombok.Data;
import lombok.NonNull;

/**
 * The RandomBotData class represents basic information about a connected client,
 * including the username, figure identifier, and an associated robot.
 * This class is annotated with Lombok {@code @Data} annotation, which automatically generates
 * boilerplate code such as getters, setters, {@code toString()}, {@code equals()}, and {@code hashCode()}
 * methods.
 */
@Data
public class RandomBotData {
    int clientId;
    String username;
    int figure = -1;
    @NonNull Robot robot = new Robot();
    boolean isReady = false;

    /**
     * The RandomBotData Robot class represents the figure-specific information needed for positioning
     * and visualization of the client's respective robot.
     * This includes the robot's position, orientation, checkpoint counter and energy.
     */
    @Data
    public static class Robot {
        Vector position;
        @NonNull protected Orientation orientation = Orientation.RIGHT;
        int checkpointCounter = 0;
        int energy = 5;
    }
}
