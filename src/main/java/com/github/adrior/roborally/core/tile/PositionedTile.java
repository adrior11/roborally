package com.github.adrior.roborally.core.tile;

import com.github.adrior.roborally.utility.Vector;

/**
 * Record representing a {@link Tile} positioned at a specific location on the board.
 * Used for special tiles that are treated like figures (Antenna, CheckPoint & RestartPoint).
 *
 * @see Vector
 *
 * @param <T> The type of the tile.
 * @param tile The tile instance.
 * @param position The position of the tile on the board.
 */
public record PositionedTile<T extends Tile> (T tile, Vector position) {}
