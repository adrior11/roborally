package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import lombok.NonNull;

public class StartPoint extends Tile {

    public StartPoint(String inOnBoard) {
        this.setIsOnBoard(inOnBoard);
    }

    @Override
    @NonNull public String getType() {
        return "StartPoint";
    }
}
