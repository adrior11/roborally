package com.github.adrior.roborally.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.map.adapters.BoardAdapter;
import com.github.adrior.roborally.core.map.adapters.NullTileAdapter;
import com.github.adrior.roborally.core.map.adapters.TileAdapter;
import com.github.adrior.roborally.core.tile.*;
import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;
import com.github.adrior.roborally.message.utils.MessageDeserializer;
import com.github.adrior.roborally.message.utils.MessageTypeAdapter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Utility class for creating a custom Gson instance with the required type adapters.
 * This ensures that the serialization and deserialization of the Message class
 * and its associated enums are handled correctly, according to Protocol V1.0.
 */
@UtilityClass
public class GsonUtil {

    /**
     * Creates a custom Gson instance with registered type adapters for Message and MessageType.
     * This ensures proper handling of camel case for MessageType during serialization
     * and deserialization.
     *
     * @return A custom Gson instance with the necessary type adapters registered.
     */
    @NonNull
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(MessageType.class, new MessageTypeAdapter())
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                .registerTypeAdapter(new TypeToken<List<List<List<Tile>>>>(){}.getType(), new BoardAdapter())
                .registerTypeAdapter(Tile.class, new TileAdapter())
                .registerTypeAdapter(Antenna.class, new TileAdapter())
                .registerTypeAdapter(Empty.class, new TileAdapter())
                .registerTypeAdapter(Checkpoint.class, new TileAdapter())
                .registerTypeAdapter(ConveyorBelt.class, new TileAdapter())
                .registerTypeAdapter(EnergySpace.class, new TileAdapter())
                .registerTypeAdapter(Gear.class, new TileAdapter())
                .registerTypeAdapter(Laser.class, new TileAdapter())
                .registerTypeAdapter(Pit.class, new TileAdapter())
                .registerTypeAdapter(PushPanel.class, new TileAdapter())
                .registerTypeAdapter(RestartPoint.class, new TileAdapter())
                .registerTypeAdapter(StartPoint.class, new TileAdapter())
                .registerTypeAdapter(Wall.class, new TileAdapter())
                .registerTypeAdapter(NullTile.class, new NullTileAdapter())
                .create();
    }
}
