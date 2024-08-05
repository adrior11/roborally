package com.github.adrior.roborally.test.core.map;

import com.google.gson.Gson;
import com.github.adrior.roborally.exceptions.InvalidTileConfigurationException;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.utility.GsonUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardAdapterErrorHandlingTest {

    private static final Gson GSON = GsonUtil.getGson();

    @ParameterizedTest
    @MethodSource("provideInvalidJsons")
    void testInvalidTileConfigurationThrowsException(String json) {
        assertThrows(InvalidTileConfigurationException.class, () -> GSON.fromJson(json, Tile.class));
    }

    private static Stream<Arguments> provideInvalidJsons() {
        return Stream.of(
                Arguments.of(
        """
                        {
                            "type": "Laser"
                        }
                    """
                ),
                Arguments.of(
        """
                        {
                            "type": "Laser",
                            "isOnBoard": "5B"
                        }
                    """
                ),
                Arguments.of(
        """
                        {
                            "type": "Laser",
                            "isOnBoard": "5B",
                            "direction": "-1"
                        }
                    """
                ),
                Arguments.of(
        """
                        {
                            "type": "InvalidTileType",
                            "direction": "NORTH"
                        }
                        """
                )
        );
    }
}