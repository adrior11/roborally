package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.Tile;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class EnergySpace extends Tile {
    private boolean hasEnergy = true;

    public EnergySpace(String inOnBoard) {
        this.setIsOnBoard(inOnBoard);
    }

    public void reloadEnergy() {
        this.hasEnergy = true;
    }

    @Override
    @NonNull public String getType() {
        return "EnergySpace";
    }

    @Override
    public void activate(@NonNull Player player) {
        if (hasEnergy) {
            player.robot().adjustEnergy(1);
            hasEnergy = false;
        }
    }
}
