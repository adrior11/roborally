package com.github.adrior.roborally.core.tile;

import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a tile in the game.
 * Subclasses must implement the methods to define the specific behavior and properties of different types of tiles.
 * The {@code getOrientations} and {@code activate} method provide default implementations,
 * which can be overridden by subclasses if needed.
 */
@Setter
@Getter
@NoArgsConstructor
public abstract class Tile {
    private String isOnBoard;

    /**
     * Gets the type of the tile.
     * Subclasses must implement this method to return their specific type.
     *
     * @return The type of the tile as a string.
     */
    public abstract String getType();

    /**
     * Gets the orientations of the tile, which is empty by default.
     * Subclasses can override this method to return their specific orientations.
     *
     * @return The list of orientations for the tile.
     */
    public List<Orientation> getOrientations() { return new ArrayList<>(); }

    /**
     * This method is called at the end of the turn and is used to apply the effect of the tile.
     * Subclasses can override this method to provide specific functionality.
     *
     * @param player the player on the tile
     */
    public void activate(Player player) {}

    /**
     * This method tests if a movement orientation is possible. The difference to "canMoveOut" is that this is for moving
     * into the tile and "canMoveOut" is for moving out of the tile. Call this on the tile the robot wants to move to.
     *
     * @param orientation is the orientation the (for example) robot wants to move
     */
    public boolean canMoveTo(Orientation orientation) { return true; }

    /**
     * This method tests if a movement orientation is possible. The difference to "canMoveTo" is that this is for moving
     * out of the tile and "canMoveTo" is for moving into the tile. Call this on the tile the robot wants to move out of.
     *
     * @param orientation is the orientation the (for example) robot wants to move
     */
    public boolean canMoveOut(Orientation orientation) { return true; }
}
