package com.github.adrior.roborally.core.map.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.github.adrior.roborally.core.tile.*;
import com.github.adrior.roborally.core.tile.tiles.*;
import com.github.adrior.roborally.exceptions.InvalidTileConfigurationException;
import com.github.adrior.roborally.utility.Orientation;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Custom GSON TypeAdapter for deserializing {@link Tile} objects.
 * This adapter handles the conversion of JSON objects to Tile instances.
 */
public class TileAdapter extends TypeAdapter<Tile> {

    @Override
    public void write(@NonNull JsonWriter out, @NonNull Tile tile) throws IOException {
        out.beginObject();
        out.name("type").value(tile.getType());
        out.name("isOnBoard").value(tile.getIsOnBoard());

        List<Orientation> orientations = tile.getOrientations();
        if (null != orientations && !orientations.isEmpty()) {
            out.name("orientations").beginArray();
            for (Orientation orientation : orientations) {
                out.value(orientation.toString().toLowerCase(Locale.ROOT));
            }
            out.endArray();
        }

        switch (tile.getType()) {
            case "Laser" -> out.name("count").value(((Laser) tile).getLaserCount());
            case "ConveyorBelt" -> out.name("speed").value(((ConveyorBelt) tile).isDouble() ? 2 : 1);
            case "Gear" -> {
                out.name("orientations").beginArray();
                out.value(((Gear) tile).isClockwise() ? "clockwise" : "counterclockwise");
                out.endArray();
            }
            case "CheckPoint" -> out.name("count").value(((Checkpoint) tile).getCheckpointNumber());
            case "PushPanel" -> {
                out.name("registers").beginArray();
                for (int register : ((PushPanel) tile).getActiveRegisters()) {
                    out.value(register);
                }
                out.endArray();
            }
        }

        out.endObject();
    }


    @Override
    public Tile read(@NonNull JsonReader in) throws RuntimeException {
        final String[] tilesWithOrientations = new String[]{"Laser", "PushPanel", "ConveyorBelt", "Wall", "Antenna", "RestartPoint"};

        JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();
        String type;
        String isOnBoard;
        JsonArray orientationArray;
        List<Orientation> orientation = new ArrayList<>();

        // Check if required attributes are given.
        try {
            type = jsonObject.get("type").getAsString();
            isOnBoard = jsonObject.get("isOnBoard").getAsString();
        } catch (NullPointerException e) {
            throw new InvalidTileConfigurationException("Received an incomplete tile configuration");
        }

        // Check if orientation is given and valid for specific tiles.
        if (Set.of(tilesWithOrientations).contains(type)) {
            try {
                orientationArray = jsonObject.get("orientations").getAsJsonArray();
                for (JsonElement orientationElement : orientationArray) {
                    String orientationString = orientationElement.getAsString().toUpperCase(Locale.ROOT);
                    orientation.add(Orientation.valueOf(orientationString));
                }
            } catch (NullPointerException e) {
                throw new InvalidTileConfigurationException("Received an invalid tile configuration for " + type);
            }
        }

        // Using a switch statement to create the appropriate Tile object based on the type.
        return switch (type) {
            case "Pit" -> new Pit(isOnBoard);
            case "Wall" -> new Wall(orientation, isOnBoard);
            case "Antenna" -> new Antenna(orientation.getFirst(), isOnBoard);
            case "EnergySpace" -> new EnergySpace(isOnBoard);
            case "StartPoint" -> new StartPoint(isOnBoard);
            case "RestartPoint" -> new RestartPoint(orientation.getFirst(), isOnBoard);
            case "Empty" -> new Empty(isOnBoard);
            case "Laser" -> {
                int count = jsonObject.get("count").getAsInt();
                yield new Laser(orientation.getFirst(), count, isOnBoard);
            }
            case "ConveyorBelt" -> {
                JsonElement speedElement = jsonObject.get("speed");
                int speed = speedElement.getAsNumber().intValue();
                yield new ConveyorBelt(orientation, 2 == speed, isOnBoard);
            }
            case "Gear" -> {
                try {
                    orientationArray = jsonObject.get("orientations").getAsJsonArray();
                    for (JsonElement orientationElement : orientationArray) {
                        String orientationString = orientationElement.getAsString();
                        boolean isClockwise = orientationString.equals("clockwise");
                        yield new Gear(isClockwise, isOnBoard);
                    }
                } catch (NullPointerException e) {
                    throw new InvalidTileConfigurationException("Received an invalid tile configuration for " + type);
                }
                yield null;
            }
            case "CheckPoint" -> {
                int count = jsonObject.get("count").getAsInt();
                yield new Checkpoint(count, isOnBoard);
            }
            case "PushPanel" -> {
                JsonArray registers = jsonObject.get("registers").getAsJsonArray();
                List<Integer> register = new ArrayList<>();
                for (JsonElement registerElement : registers) {
                    register.add(registerElement.getAsNumber().intValue());
                }
                yield new PushPanel(orientation.getFirst(), register, isOnBoard);
            }
            default -> throw new InvalidTileConfigurationException("Received an unknown tile type: " + type);
        };
    }
}
