package com.github.adrior.roborally.message.utils;

import com.google.gson.*;
import com.github.adrior.roborally.message.Message.MessageType;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * Custom Gson type adapter for serializing and deserializing the {@link MessageType} enum.
 * This adapter ensures that the MessageType is serialized in camel case and deserialized
 * back to the appropriate enum value, according to Protocol V2.0.
 */
public class MessageTypeAdapter implements JsonSerializer<MessageType>, JsonDeserializer<MessageType> {

    @Override
    @NonNull public JsonElement serialize(@NonNull MessageType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(convertToCamelCase(src.name()));
    }


    @Override
    @NonNull public MessageType deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return MessageType.valueOf(convertToEnumName(json.getAsString()));
    }


    /**
     * Converts an uppercase underscore-separated string to camel case.
     *
     * @param s The string to be converted.
     * @return The converted camel case string.
     */
    @NonNull private String convertToCamelCase(@NonNull String s) {
        String[] parts = s.toLowerCase(Locale.ROOT).split("_");
        StringBuilder camelCaseString = new StringBuilder(parts[0].substring(0, 1).toUpperCase(Locale.ROOT)).append(parts[0].substring(1));
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase(Locale.ROOT)).append(parts[i].substring(1));
        }
        return camelCaseString.toString();
    }


    /**
     * Converts a camel case string to an uppercase underscore-separated enum name.
     *
     * @param s The camel case string to be converted.
     * @return The converted enum name in uppercased underscore-separated format.
     */
    @NonNull private String convertToEnumName(@NonNull String s) {
        StringBuilder enumName = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                enumName.append('_').append(c);
            } else {
                enumName.append(Character.toUpperCase(c));
            }
        }
        if ('_' == enumName.charAt(0)) {
            enumName.deleteCharAt(0);
        }
        return enumName.toString();
    }
}
