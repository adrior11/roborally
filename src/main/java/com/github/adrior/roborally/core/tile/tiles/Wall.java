package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class Wall extends Tile {
    private final List<Orientation> orientations;

    public Wall(List<Orientation> orientations, String inOnBoard) {
        this.orientations = orientations;
        this.setIsOnBoard(inOnBoard);
    }

    @Override
    public List<Orientation> getOrientations()
    {
       return orientations;
    }

    @Override
    @NonNull public String getType() {
        return "Wall";
    }

    @Override
    public boolean canMoveTo(@NonNull Orientation orientation) {
        return !orientations.contains(orientation.uTurn());
    }

    @Override
    public boolean canMoveOut(Orientation orientation) {
        return !orientations.contains(orientation);
    }
}
