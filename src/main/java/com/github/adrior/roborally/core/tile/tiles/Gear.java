package com.github.adrior.roborally.core.tile.tiles;

import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.Tile;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Gear extends Tile {
    private final boolean isClockwise;

    public Gear(boolean isClockwise, String inOnBoard) {
        this.setIsOnBoard(inOnBoard);
        this.isClockwise = isClockwise;
    }

    @Override
    @NonNull public String getType() {
        return "Gear";
    }

    @Override
    public void activate(@NonNull Player player) {
        if (isClockwise) {
            player.robot().setOrientation(player.robot().getOrientation().turnRight());
        } else {
            player.robot().setOrientation(player.robot().getOrientation().turnLeft());
        }
    }
}
