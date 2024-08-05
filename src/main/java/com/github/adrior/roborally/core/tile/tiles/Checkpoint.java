package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.Tile;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Checkpoint extends Tile {
    private final int checkpointNumber;

    public Checkpoint(int checkpointNumber, String isOnBoard) {
        this.setIsOnBoard(isOnBoard);
        this.checkpointNumber = checkpointNumber;
    }

    @Override
    @NonNull public String getType() {
        return "CheckPoint";
    }

    @Override
    public void activate(@NonNull Player player) {
        if (player.robot().getCheckpoint() == this.checkpointNumber - 1) {
            player.robot().incrementCheckpoint();
        }
    }
}
