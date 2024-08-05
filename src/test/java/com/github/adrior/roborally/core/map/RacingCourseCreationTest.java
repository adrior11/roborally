package com.github.adrior.roborally.test.core.map;

import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RacingCourseCreationTest {

    private static final List<AvailableCourses> courses = List.of(AvailableCourses.values());

    @Test
    void testRacingCourseCreation() {
        courses.forEach(course -> assertNotNull(RacingCourse.createRacingCourse(course)));
    }

    @Test
    void testSpecificRacingCourseCreation() {
        assertNotNull(RacingCourse.createRacingCourse(AvailableCourses.UNDERTOW));
    }
}
