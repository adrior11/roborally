package com.github.adrior.roborally.test.utility;

import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrientationTest {
    @Test
    void testGetVector() {
        assertEquals(new Vector(1, 0), Orientation.RIGHT.getVector(), "Direction.RIGHT should return vector (1, 0)");
        assertEquals(new Vector(0, 1), Orientation.BOTTOM.getVector(), "Direction.DOWN should return vector (0, 1)");
        assertEquals(new Vector(-1, 0), Orientation.LEFT.getVector(), "Direction.LEFT should return vector (-1, 0)");
        assertEquals(new Vector(0, -1), Orientation.TOP.getVector(), "Direction.UP should return vector (0, -1)");
    }


    @Test
    void testTurnLeft() {
        assertEquals(Orientation.TOP, Orientation.RIGHT.turnLeft(), "Turning left from RIGHT should give UP");
        assertEquals(Orientation.LEFT, Orientation.TOP.turnLeft(), "Turning left from UP should give LEFT");
        assertEquals(Orientation.BOTTOM, Orientation.LEFT.turnLeft(), "Turning left from LEFT should give DOWN");
        assertEquals(Orientation.RIGHT, Orientation.BOTTOM.turnLeft(), "Turning left from DOWN should give RIGHT");
    }


    @Test
    void testTurnRight() {
        assertEquals(Orientation.BOTTOM, Orientation.RIGHT.turnRight(), "Turning right from RIGHT should give DOWN");
        assertEquals(Orientation.LEFT, Orientation.BOTTOM.turnRight(), "Turning right from DOWN should give LEFT");
        assertEquals(Orientation.TOP, Orientation.LEFT.turnRight(), "Turning right from LEFT should give UP");
        assertEquals(Orientation.RIGHT, Orientation.TOP.turnRight(), "Turning right from UP should give RIGHT");
    }


    @Test
    void testFullCircleTurnLeft() {
        Orientation direction = Orientation.RIGHT;
        direction = direction.turnLeft(); // UP
        direction = direction.turnLeft(); // LEFT
        direction = direction.turnLeft(); // DOWN
        direction = direction.turnLeft(); // RIGHT
        assertEquals(Orientation.RIGHT, direction, "Turning left four times from RIGHT should give RIGHT");
    }


    @Test
    void testFullCircleTurnRight() {
        Orientation direction = Orientation.RIGHT;
        direction = direction.turnRight(); // DOWN
        direction = direction.turnRight(); // LEFT
        direction = direction.turnRight(); // UP
        direction = direction.turnRight(); // RIGHT
        assertEquals(Orientation.RIGHT, direction, "Turning right four times from RIGHT should give RIGHT");
    }

    @Test
    void testGetRotationStepsTo() {
        Orientation top = Orientation.TOP;
        Orientation right = Orientation.RIGHT;
        Orientation bottom = Orientation.BOTTOM;
        Orientation left = Orientation.LEFT;

        assertEquals(1, top.getRotationStepsTo(right));
        assertEquals(2, top.getRotationStepsTo(bottom));
        assertEquals(3, top.getRotationStepsTo(left));
        assertEquals(0, top.getRotationStepsTo(top));
    }

    @Test
    void testGetConveyorRotation() {
        Orientation bottom = Orientation.BOTTOM;
        Orientation top = Orientation.TOP;
        Orientation right = Orientation.RIGHT;
        Orientation left = Orientation.LEFT;

        // Test cases where orientations are opposite
        assertNull(bottom.getConveyorRotation(top, bottom), "Should return the same orientation (BOTTOM)");
        assertNull(top.getConveyorRotation(bottom, top), "Should return the same orientation (TOP)");

        // Test cases where orientations are next to each other
        assertEquals(left, bottom.getConveyorRotation(top, right), "BOTTOM facing conveyor (TOP to RIGHT) should turn to LEFT");
        assertEquals(right, bottom.getConveyorRotation(top, left), "BOTTOM facing conveyor (TOP to LEFT) should turn to RIGHT");

        assertEquals(bottom, right.getConveyorRotation(bottom, left), "RIGHT facing conveyor (BOTTOM to LEFT) should turn to BOTTOM");
        assertEquals(top, right.getConveyorRotation(bottom, right), "RIGHT facing conveyor (BOTTOM to RIGHT) should turn to TOP");

        assertEquals(left, top.getConveyorRotation(bottom, right), "TOP facing conveyor (BOTTOM to RIGHT) should turn to LEFT");
        assertEquals(right, top.getConveyorRotation(bottom, left), "TOP facing conveyor (BOTTOM to LEFT) should turn to RIGHT");
    }

    @Test
    void testInvalidOrientationString() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Orientation.valueOf("INVALID"));
        System.out.println(exception.getMessage());
        assertNotNull(exception);
    }
}
