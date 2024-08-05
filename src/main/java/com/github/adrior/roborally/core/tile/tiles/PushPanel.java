package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Orientation;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PushPanel extends Tile {
    private final Orientation pushOrientation;
    private final List<Integer> activeRegisters;

    public PushPanel(Orientation pushPanelOrientation, List<Integer> activeRegisters, String inOnBoard) {
        this.setIsOnBoard(inOnBoard);
        this.pushOrientation = pushPanelOrientation;
        this.activeRegisters = activeRegisters;
    }

    @Override
    @NonNull public List<Orientation> getOrientations() {
        List<Orientation> orientations = new ArrayList<>();
        orientations.add(pushOrientation);
        return orientations;
    }

    @Override
    @NonNull public String getType() {
        return "PushPanel";
    }
}
