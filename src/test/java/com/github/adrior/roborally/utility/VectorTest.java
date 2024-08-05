package com.github.adrior.roborally.test.utility;

import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    @Test
    void testAdd() {
        Vector v1 = new Vector(1, 2);
        Vector v2 = new Vector(3, 4);

        Vector result = v1.add(v2);

        assertEquals(result, new Vector(4, 6), "Vector addition should return the sum of components");
    }
}
