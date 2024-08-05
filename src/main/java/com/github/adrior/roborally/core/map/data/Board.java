package com.github.adrior.roborally.core.map.data;

import com.github.adrior.roborally.core.tile.Tile;

import java.util.List;

/**
 * Represents the map configuration for a game.
 * This record is structured to work with JSON deserialization,
 * aligning its fields with the expected structure of the JSON map files.
 *
 * @param name The name of the board.
 * @param startingBoard Indicates if this is the starting board.
 * @param tiles The 3D tiles configuration of the board.
 */
public record Board(String name, boolean startingBoard, List<List<List<Tile>>> tiles) {}
