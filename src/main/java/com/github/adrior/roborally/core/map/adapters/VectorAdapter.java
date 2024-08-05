package com.github.adrior.roborally.core.map.adapters;

import com.google.gson.*;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

import java.lang.reflect.Type;

/**
 * Custom Gson adapter for deserializing {@link Vector} objects.
 * This adapter handles the deserialization of the JSON representation of a Vector,
 * converting it into a Vector instance.
 */
public class VectorAdapter implements JsonDeserializer<Vector> {

    @Override
    @NonNull public Vector deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray jsonArray = json.getAsJsonArray();
            int[] coordinates = new int[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                coordinates[i] = jsonArray.get(i).getAsInt();
            }
            return new Vector(coordinates[0], coordinates[1]);
    }
}
