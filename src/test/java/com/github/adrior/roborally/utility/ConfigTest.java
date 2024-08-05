package com.github.adrior.roborally.test.utility;

import com.github.adrior.roborally.utility.Config;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void testConfigLoading() {
        // Load the config instance
        Config config = Config.getInstance();
        assertNotNull(config, "Config instance should not be null");

        System.out.println(config.getProtocolVersion());
        System.out.println(config.isMusicEnabled());
        System.out.println(config.isSavingLog());
    }

    @Test
    void testFileCanBeFoundAndRead() {
        URL resource = getClass().getResource("/config.json");

        assertNotNull(resource, "File not found!");

        File file = new File(resource.getFile());

        assertTrue(file.exists(), "File does not exist!");
        assertTrue(file.canRead(), "File cannot be read!");
    }
}
