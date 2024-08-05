package com.github.adrior.roborally.core.map.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.map.data.SpecialTilePositions;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.core.tile.tiles.Checkpoint;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Custom Gson adapter for deserializing {@link SpecialTilePositions} objects.
 * This adapter handles the deserialization of the JSON representation of SpecialTilePositions,
 * converting it into a SpecialTilePositions instance.
 */
public final class SpecialTilePositionsAdapter implements JsonDeserializer<SpecialTilePositions> {

    @Override
    @NonNull public SpecialTilePositions deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        PositionedTile<Antenna> antenna = context.deserialize(jsonObject.get("antenna"), new TypeToken<PositionedTile<Antenna>>(){}.getType());
        List<PositionedTile<RestartPoint>> restartPoints = context.deserialize(jsonObject.get("restartPoints"), new TypeToken<List<PositionedTile<RestartPoint>>>(){}.getType());
        List<PositionedTile<Checkpoint>> checkpoints = context.deserialize(jsonObject.get("checkpoints"), new TypeToken<List<PositionedTile<Checkpoint>>>(){}.getType());
        return new SpecialTilePositions(antenna, restartPoints, checkpoints);
    }
}
