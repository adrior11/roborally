package com.github.adrior.roborally.test.message;

import com.google.gson.Gson;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.utility.GsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SelectMapArrayRetrievalTest {
    private static final Gson gson = GsonUtil.getGson();

    @Test
    void testSelectMapArrayRetrieval() {
        // String array containing the string value of the expected maps.
        String[] expectedAvailableMaps = AvailableCourses.getFormattedNames();
        System.out.println("Expected Available Maps: " + Arrays.toString(expectedAvailableMaps));

        // Serialization & Deserialization of the original SelectMap message.
        Message originalSelectMapMessage = PredefinedServerMessages.selectMap(expectedAvailableMaps);
        Message selectMapMessage = gson.fromJson(gson.toJson(originalSelectMapMessage), Message.class);
        System.out.println("SelectMap Message: " + selectMapMessage.toString());

        // Retrieve String[] from selectMapMessage.
        String[] availableMaps = (String[]) selectMapMessage.messageBody().get("availableMaps");
        System.out.println("Available Maps String Array: " + Arrays.toString(availableMaps));

        assertNotNull(availableMaps);
        assertInstanceOf(String[].class, availableMaps);
        assertEquals(AvailableCourses.values().length, availableMaps.length);
        assertEquals(Arrays.toString(expectedAvailableMaps), Arrays.toString(availableMaps));
    }
}
