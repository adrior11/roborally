package com.github.adrior.roborally.core.tile;

import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.utility.Orientation;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for performing operations on {@link Tile} objects.
 * This class provides methods to manipulate tiles, such as rotating them.
 */
@UtilityClass
public class TileUtil {

    /**
     * Rotates the given {@link Tile} 90 degrees to the left (counterclockwise).
     * The rotation is applied to the tile's orientations, and a new instance
     * of the tile is created with the updated orientations.
     *
     * @param tile The tile to be rotated.
     * @return A new {@link Tile} instance with orientations rotated 90 degrees to the left.
     */
    @NonNull public static Tile rotateTile(@NonNull Tile tile) {
        List<Orientation> newOrientations = new ArrayList<>();
        for (Orientation orientation : tile.getOrientations()) {
            newOrientations.add(orientation.turnLeft());
        }

        return switch (tile) {
            case Antenna _ -> new Antenna(newOrientations.getFirst(), tile.getIsOnBoard());
            case ConveyorBelt conveyorBelt -> new ConveyorBelt(newOrientations, conveyorBelt.isDouble(), tile.getIsOnBoard());
            case Laser laser -> new Laser(newOrientations.getFirst(), laser.getLaserCount(), tile.getIsOnBoard());
            case PushPanel pushPanel -> new PushPanel(newOrientations.getFirst(), pushPanel.getActiveRegisters(), tile.getIsOnBoard());
            case Wall _ -> new Wall(newOrientations, tile.getIsOnBoard());
            default -> tile;
        };
    }


    /**
     * Generates a 3D board List with NullTile instances based on the given width and height.
     *
     * @param width  the width of the 3D List
     * @param height the height of the 3D List
     * @return a 3D List structure filled with NullTile instances
     */
    @NonNull public static List<List<List<Tile>>> createNullTileBoard(int width, int height) {
        List<List<List<Tile>>> tiles = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            List<List<Tile>> column = new ArrayList<>();
            for (int j = 0; j < height; j++) {
                List<Tile> row = new ArrayList<>();
                row.add(new NullTile());  // Add NullTile to each position in the row
                column.add(row);
            }
            tiles.add(column);
        }

        return tiles;
    }
}
