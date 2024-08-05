package com.github.adrior.roborally.core.map.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.github.adrior.roborally.core.tile.tiles.NullTile;
import lombok.NonNull;

import java.io.IOException;

/**
 * Custom Gson adapter for deserializing {@link NullTile} objects.
 * This adapter handles the deserialization of the JSON representation of a NullTile,
 * converting it into a NullTile instance needed for spacing tiles in the 2D Grid.
 */
public class NullTileAdapter extends TypeAdapter<NullTile> {

    @Override
    public void write(@NonNull JsonWriter out, NullTile value) throws IOException {
        out.nullValue();
    }


    @Override
    @NonNull public NullTile read(JsonReader in) {
        return new NullTile();
    }
}
