package com.github.adrior.roborally.test.core.map;

import com.github.adrior.roborally.core.map.AvailableCourses;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailableCoursesTest {

    @Test
    void testToStringConversion() {
        assertEquals("Dizzy Highway", AvailableCourses.DIZZY_HIGHWAY.toString());
        assertEquals("Lost Bearings", AvailableCourses.LOST_BEARINGS.toString());
        assertEquals("Death Trap", AvailableCourses.DEATH_TRAP.toString());
        assertEquals("Extra Crispy", AvailableCourses.EXTRA_CRISPY.toString());
    }

    @Test
    void testFromStringConversion() {
        assertEquals(AvailableCourses.DIZZY_HIGHWAY, AvailableCourses.fromString("Dizzy Highway"));
        assertEquals(AvailableCourses.LOST_BEARINGS, AvailableCourses.fromString("Lost Bearings"));
        assertEquals(AvailableCourses.DEATH_TRAP, AvailableCourses.fromString("Death Trap"));
        assertEquals(AvailableCourses.EXTRA_CRISPY, AvailableCourses.fromString("Extra Crispy"));
    }

    @Test
    void testRoundTripConversion() {
        for (AvailableCourses course : AvailableCourses.values()) {
            String formattedName = course.toString();
            AvailableCourses convertedCourse = AvailableCourses.fromString(formattedName);
            assertEquals(course, convertedCourse, "Failed for course: " + course.name());
        }
    }

    @Test
    void testGetFormattedNames() {
        String[] formattedNames = AvailableCourses.getFormattedNames();

        assertEquals(AvailableCourses.values().length, formattedNames.length);
    }

    @Test
    void testFromStringWithInvalidInput() {
        String invalidInput = "Non Existent Course";
        assertThrows(IllegalArgumentException.class, () -> AvailableCourses.fromString(invalidInput), "Expected fromString to throw IllegalArgumentException for invalid input: " + invalidInput);
    }
}
