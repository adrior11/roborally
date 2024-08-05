package com.github.adrior.roborally.core.map.parsers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.map.data.RacingCourseData;
import com.github.adrior.roborally.core.map.data.SpecialTilePositions;
import com.github.adrior.roborally.core.map.adapters.TileAdapter;
import com.github.adrior.roborally.core.map.adapters.PositionedTileAdapter;
import com.github.adrior.roborally.core.map.adapters.SpecialTilePositionsAdapter;
import com.github.adrior.roborally.core.map.adapters.VectorAdapter;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.core.tile.tiles.Checkpoint;
import com.github.adrior.roborally.core.tile.tiles.RestartPoint;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class for parsing {@link RacingCourseData} objects from JSON files.
 * This class uses GSON to deserialize JSON data into RacingCourseData instances.
 */
@UtilityClass
public class RacingCourseDataParser {

    @NonNull private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(new TypeToken<PositionedTile<Antenna>>(){}.getType(), new PositionedTileAdapter<Antenna>());
        builder.registerTypeAdapter(new TypeToken<PositionedTile<RestartPoint>>(){}.getType(), new PositionedTileAdapter<RestartPoint>());
        builder.registerTypeAdapter(new TypeToken<PositionedTile<Checkpoint>>(){}.getType(), new PositionedTileAdapter<Checkpoint>());
        builder.registerTypeAdapter(SpecialTilePositions.class, new SpecialTilePositionsAdapter());
        builder.registerTypeAdapter(Vector.class, new VectorAdapter());
        builder.registerTypeAdapter(Tile.class, new TileAdapter());
        gson = builder.create();
    }

    /**
     * Parses a RacingCourseData object from the specified JSON file.
     *
     * @param filePath The path to the JSON file.
     * @return The parsed RacingCourseData object.
     * @throws IOException if an I/O error occurs.
     */
    public static RacingCourseData parseCourseData(@NonNull String filePath) throws IOException {
        ServerCommunicationFacade.log("Parsing racing course data file " + filePath);
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, RacingCourseData.class);
        }
    }
}
