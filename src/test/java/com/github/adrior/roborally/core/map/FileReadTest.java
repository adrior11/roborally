package com.github.adrior.roborally.test.core.map;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class FileReadTest {

    @Test
    void testFileCanBeFoundAndRead() {
        URL resource = getClass().getResource("/boards/3B.json");

        assertNotNull(resource, "File not found!");

        File file = new File(resource.getFile());

        assertTrue(file.exists(), "File does not exist!");
        assertTrue(file.canRead(), "File cannot be read!");
    }
}