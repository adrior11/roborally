package com.github.adrior.roborally.core.map.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.github.adrior.roborally.core.tile.*;
import com.github.adrior.roborally.core.map.data.Board;
import com.github.adrior.roborally.core.tile.tiles.NullTile;
import com.github.adrior.roborally.utility.GsonUtil;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Gson adapter for serializing and deserializing {@link Board} objects.
 * This adapter handles the conversion of a JSON representation of a Board into a
 * nested list structure containing {@link Tile} objects, and vice versa.
 *
 * <p> The Board is represented as a 3D list of Tiles: {@code List<List<List<Tile>>>}.
 * Each list represents a row, which contains cells, each containing a list of Tiles.
 */
public class BoardAdapter extends TypeAdapter<List<List<List<Tile>>>> {

    @Override
    public void write(@NonNull JsonWriter out, @NonNull List<List<List<Tile>>> board) throws IOException {
        TileAdapter tileAdapter = new TileAdapter();
        out.beginArray();
        for (List<List<Tile>> row : board) {
            out.beginArray();
            for (List<Tile> cell : row) {
                out.beginArray();
                for (Tile tile : cell) {
                    if (null == tile || tile instanceof NullTile) {
                        out.nullValue();
                    } else {
                        tileAdapter.write(out, tile);
                    }
                }
                out.endArray();
            }
            out.endArray();
        }
        out.endArray();
    }


    @Override
    @NonNull public List<List<List<Tile>>> read(@NonNull JsonReader in) {
        Gson gson = GsonUtil.getGson();
        List<List<List<Tile>>> board = new ArrayList<>();
        JsonArray boardArray = JsonParser.parseReader(in).getAsJsonArray();

        for (JsonElement rowElement : boardArray) {
            List<List<Tile>> row = new ArrayList<>();
            for (JsonElement cellElement : rowElement.getAsJsonArray()) {
                List<Tile> cell = new ArrayList<>();
                for (JsonElement tileElement : cellElement.getAsJsonArray()) {
                    if (tileElement.isJsonNull()) {
                        cell.add(new NullTile());
                    } else {
                        cell.add(gson.fromJson(tileElement, Tile.class));
                    }
                }
                row.add(cell);
            }
            board.add(row);
        }

        return board;
    }
}
