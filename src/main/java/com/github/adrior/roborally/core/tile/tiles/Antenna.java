package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

@Getter
public class Antenna extends Tile {
    private final Orientation antennaOrientation;

    public Antenna(Orientation orientation, String isOnBoard) {
        this.setIsOnBoard(isOnBoard);
        this.antennaOrientation = orientation;
    }

    @Override
    @NonNull public String getType() {
        return "Antenna";
    }

    @Override
    @NonNull public List<Orientation> getOrientations() {
        return List.of(antennaOrientation);
    }

    @Override
    public boolean canMoveTo(Orientation orientation) {
        return false;
    }
}
