package com.github.adrior.roborally.server.util;

import com.github.adrior.roborally.exceptions.InvalidMessageConfigurationException;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ClientHandler;
import lombok.experimental.UtilityClass;

/**
 * Utility class for type casting {@link Message} parameters.
 *
 * @see Message
 * @see InvalidMessageConfigurationException
 */
@UtilityClass
public class TypeCastingHelper {

    /**
     * Extracts an integer value from the {@link Message#messageBody()}.
     *
     * @param clientHandler the client handler
     * @param message       the message
     * @param key           the key to extract the value for
     * @return the extracted integer value
     */
    public static int getIntFromMessage(ClientHandler clientHandler, Message message, String key) {
        try {
            return (int) message.messageBody().get(key);
        } catch (ClassCastException | NullPointerException e) {
            clientHandler.sendMessage(PredefinedServerMessages.error("Invalid integer value for key: " + key));
            String errorMessage = "Invalid integer value for key: " + key;
            throw new InvalidMessageConfigurationException(errorMessage);
        }
    }


    /**
     * Extracts a boolean value from the {@link Message#messageBody()}.
     *
     * @param clientHandler the client handler
     * @param message       the message
     * @param key           the key to extract the value for
     * @return the extracted boolean value
     */
    public static boolean getBooleanFromMessage(ClientHandler clientHandler, Message message, String key) {
        try {
            return (boolean) message.messageBody().get(key);
        } catch (ClassCastException | NullPointerException e) {
            clientHandler.sendMessage(PredefinedServerMessages.error("Invalid boolean value for key: " + key));
            String errorMessage = "Invalid boolean value for key: " + key;
            throw new InvalidMessageConfigurationException(errorMessage);
        }
    }


    /**
     * Extracts a string array from the {@link Message#messageBody()}.
     *
     * @param clientHandler the client handler
     * @param message       the message
     * @param key           the key to extract the value for
     * @return the extracted string array
     */
    public static String[] getStringArrayFromMessage(ClientHandler clientHandler, Message message, String key) {
        try {
            return (String[]) message.messageBody().get(key);
        } catch (ClassCastException | NullPointerException e) {
            clientHandler.sendMessage(PredefinedServerMessages.error("Invalid string array value for key: " + key));
            String errorMessage = "Invalid string array value for key: " + key;
            throw new InvalidMessageConfigurationException(errorMessage);
        }
    }
}
