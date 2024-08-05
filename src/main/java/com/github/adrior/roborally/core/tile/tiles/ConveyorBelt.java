package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ConveyorBelt extends Tile {
    private final Orientation pushOrientation;
    private final boolean isDouble;

    @NonNull private final List<Orientation> intoOrientation;

    public ConveyorBelt(@NonNull List<Orientation> directions, boolean isDouble, String isOnBoard){
        this.setIsOnBoard(isOnBoard);
        this.pushOrientation = directions.removeFirst();
        this.intoOrientation = directions;
        this.isDouble = isDouble;
    }

    @Override
    @NonNull public List<Orientation> getOrientations() {
        List<Orientation> orientations = new ArrayList<>();
        orientations.add(this.pushOrientation);
        orientations.addAll(this.intoOrientation);
        return orientations;
    }

    @Override
    @NonNull public String getType() {
        return "ConveyorBelt";
    }
}
