package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import lombok.NonNull;

public class NullTile extends Tile {

    @Override
    @NonNull public String getType() {
        return "";
    }
}
