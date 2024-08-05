package com.github.adrior.roborally.core.map.data;

import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.core.tile.tiles.Checkpoint;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;

import java.util.List;

/**
 * Record representing the positions of special tiles on the board.
 *
 * @see PositionedTile
 *
 * @param antenna The positioned tile for the antenna.
 * @param restartPoints The positioned tile for the restart point.
 * @param checkpoints The list of positioned tiles for the checkpoints.
 */
public record SpecialTilePositions(PositionedTile<Antenna> antenna, List<PositionedTile<RestartPoint>> restartPoints, List<PositionedTile<Checkpoint>> checkpoints) {}
