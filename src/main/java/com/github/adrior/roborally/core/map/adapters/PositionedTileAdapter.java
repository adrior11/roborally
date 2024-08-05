package com.github.adrior.roborally.core.map.adapters;

import com.google.gson.*;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

import java.lang.reflect.Type;

/**
 * Custom Gson adapter for deserializing {@link PositionedTile} objects.
 * This adapter handles the deserialization of the JSON representation of a PositionedTile,
 * converting it into a PositionedTile instance.
 *
 * @param <T> The type of the tile contained in the PositionedTile.
 */
public final class PositionedTileAdapter<T extends Tile> implements JsonDeserializer<PositionedTile<T>> {

    @Override
    @NonNull public PositionedTile<T> deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Use TileAdapter to deserialize the tile part
        T tile = context.deserialize(jsonObject.get("tile"), Tile.class);
        Vector position = context.deserialize(jsonObject.get("position"), Vector.class);

        return new PositionedTile<>(tile, position);
    }
}
