package com.github.adrior.roborally.core.map.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.map.data.Board;
import com.github.adrior.roborally.core.map.adapters.BoardAdapter;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for parsing {@link Board} objects from JSON files.
 * This class uses GSON to deserialize JSON data into Map instances.
 */
@UtilityClass
public final class BoardParser {

    @NonNull private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(new TypeToken<List<List<List<Tile>>>>(){}.getType(), new BoardAdapter());
        gson = builder.create();
    }

    /**
     * Parses a Board object from the specified JSON file.
     *
     * @param filePath the path to the JSON file
     * @return the parsed Map object
     * @throws IOException if an I/O error occurs
     */
    public static Board parseMap(@NonNull String filePath) throws IOException {
        ServerCommunicationFacade.log("Parsing map file " + filePath);
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Board.class);
        }
    }
}
