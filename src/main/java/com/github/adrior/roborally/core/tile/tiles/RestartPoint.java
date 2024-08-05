package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class RestartPoint extends Tile {
    private final Orientation restartOrientation;

    public RestartPoint(Orientation orientation, String inOnBoard) {
        this.setIsOnBoard(inOnBoard);
        this.restartOrientation = orientation;
    }

    @Override
    @NonNull public String getType() {
        return "RestartPoint";
    }

    @Override
    @NonNull public List<Orientation> getOrientations() {
        return List.of(restartOrientation);
    }
}
