package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class Laser extends Tile {
    private final Orientation laserOrientation;
    private final int laserCount;

    public Laser(Orientation laserOrientation, int count, String inOnBoard) {
        this.setIsOnBoard(inOnBoard);
        this.laserOrientation = laserOrientation;
        this.laserCount = count;
    }

    @Override
    @NonNull public List<Orientation> getOrientations() {
        return List.of(laserOrientation);
    }

    @Override
    public boolean canMoveOut(@NonNull Orientation orientation){
        return orientation.ordinal() != (laserOrientation.ordinal()+2 % 4);
    }

    @Override
    public boolean canMoveTo(@NonNull Orientation orientation) {
        return orientation.ordinal() != (laserOrientation.ordinal());
    }

    @Override
    @NonNull public String getType() {
        return "Laser";
    }
}
