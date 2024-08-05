package com.github.adrior.roborally.message.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * MessageDeserializer is responsible for deserializing JSON data into {@link Message} objects.
 * It implements the JsonDeserializer interface from the Gson library.
 */
public class MessageDeserializer implements JsonDeserializer<Message> {

    @Override
    @NonNull public Message deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        MessageType messageType = context.deserialize(jsonObject.get("messageType"), MessageType.class);
        JsonObject messageBodyJson = jsonObject.getAsJsonObject("messageBody");
        Map<String, Object> messageBody = new HashMap<>();

        for (Entry<String, JsonElement> entry : messageBodyJson.entrySet()) {
            if (entry.getKey().equals("activeCards")) {
                List<Map<String, Object>> activeCardsList = getActiveCardsList(entry);

                messageBody.put(entry.getKey(), activeCardsList);
            } else if (entry.getKey().equals("gameMap")) {
                // Deserialize gameMap as a 3D List of List<List<List<Tile>>>
                Type gameMapType = new TypeToken<List<List<List<Tile>>>>() {}.getType();

                List<List<List<Tile>>> gameMap = context.deserialize(entry.getValue(), gameMapType);

                messageBody.put(entry.getKey(), gameMap);
            } else {
                messageBody.put(entry.getKey(), convertJsonPrimitive(entry.getValue()));
            }
        }

        return new Message(messageType, messageBody);
    }


    /**
     * Extracts a list of active card information from a given JSON entry.
     * The entry is expected to contain a JSON array of card objects, where each card object
     * has a "clientID" and a "card" field.
     * The method converts each card object to a Map with "clientID" as an Integer and "card" as a String,
     * and collects these maps into a list.
     *
     * @param entry The JSON entry containing the array of card objects.
     * @return A list of maps, each containing "clientID" and "card" information.
     *
     * @see MessageType#CURRENT_CARDS
     */
    @NonNull private static List<Map<String, Object>> getActiveCardsList(@NonNull Entry<String, JsonElement> entry) {
        List<Map<String, Object>> activeCardsList = new ArrayList<>();
        for (JsonElement element : entry.getValue().getAsJsonArray()) {
            JsonObject cardObject = element.getAsJsonObject();
            Map<String, Object> cardInfo = new HashMap<>();

            cardInfo.put("clientID", cardObject.get("clientID").getAsInt());
            cardInfo.put("card", cardObject.get("card").getAsString());

            activeCardsList.add(cardInfo);
        }
        return activeCardsList;
    }


    /**
     * Converts a JSON element to an appropriate Java object type if it's a primitive.
     * If the JSON element is a number, and it represents an integer value, it converts it to an Integer.
     * If the JSON element is a string, it converts it to a String.
     * If the JSON element is a boolean, it converts it to a Boolean.
     * If the JSON element is an array of strings, it converts it to a String[].
     * Otherwise, it returns the original JSON element.
     *
     * @param jsonElement The JSON element to be converted.
     * @return The converted value as an Integer, String, Boolean, or the original JSON element.
     */
    private Object convertJsonPrimitive(@NonNull JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return convertJsonPrimitiveValue(jsonElement.getAsJsonPrimitive());
        }
        if (jsonElement.isJsonArray()) {
            return convertJsonArray(jsonElement.getAsJsonArray());
        }
        return jsonElement;
    }


    /**
     * Converts a JSON primitive to an appropriate Java object type.
     * If the JSON primitive is a number, it delegates to convertJsonNumber.
     * If the JSON primitive is a string, it converts it to a String.
     * If the JSON primitive is a boolean, it converts it to a Boolean.
     * Otherwise, it returns the original JSON primitive.
     *
     * @param jsonPrimitive The JSON primitive to be converted.
     * @return The converted value as an Integer, String, Boolean, or the original JSON primitive.
     */
    private Object convertJsonPrimitiveValue(@NonNull JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isNumber()) {
            return convertJsonNumber(jsonPrimitive.getAsNumber());
        }
        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }
        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }
        return jsonPrimitive;
    }


    /**
     * Converts a JSON number to an appropriate Java number type.
     * If the JSON number represents an integer value, it converts it to an Integer.
     * Otherwise, it returns the original number.
     *
     * @param number The JSON number to be converted.
     * @return The converted value as an Integer or the original number.
     */
    @NonNull private Object convertJsonNumber(@NonNull Number number) {
        if (number.doubleValue() == Math.rint(number.doubleValue())) {
            return number.intValue();
        }
        return number;
    }


    /**
     * Converts a JSON array to an appropriate Java array type.
     * If the JSON array is not empty and contains strings, it converts it to a String[].
     * Otherwise, it returns the original JSON array.
     *
     * @param jsonArray The JSON array to be converted.
     * @return The converted value as a String[] or the original JSON array.
     */
    @NonNull private Object convertJsonArray(@NonNull JsonArray jsonArray) {
        if (!jsonArray.isEmpty() && jsonArray.get(0).isJsonPrimitive() && jsonArray.get(0).getAsJsonPrimitive().isString()) {
            return convertJsonStringArray(jsonArray);
        }
        return jsonArray;
    }


    /**
     * Converts a JSON array of strings to a Java String[].
     *
     * @param jsonArray The JSON array of strings to be converted.
     * @return The converted value as a String[].
     */
    @NonNull private String[] convertJsonStringArray(@NonNull JsonArray jsonArray) {
        String[] stringArray = new String[jsonArray.size()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = jsonArray.get(i).getAsString();
        }
        return stringArray;
    }
}
