package com.github.adrior.roborally.core.map;

import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/**
 * Enum representing the available {@link RacingCourse} in the Robo Rally game.
 * Each course is defined by its file path containing the JSON configuration.
 */
@Getter
public enum AvailableCourses {
    DIZZY_HIGHWAY("/courses/dizzy_highway.json"),
    RISKY_CROSSING("/courses/risky_crossing.json"),
    HIGH_OCTANE("/courses/high_octane.json"),
    SPRINT_CAMP("/courses/sprint_camp.json"),
    CORRIDOR_BLITZ("/courses/corridor_blitz.json"),
    FRACTIONATION("/courses/fractionation.json"),
    BURNOUT("/courses/burnout.json"),
    LOST_BEARINGS("/courses/lost_bearings.json"),
    PASSING_LANE("/courses/passing_lane.json"),
    TWISTER("/courses/twister.json"),
    DODGE_THIS("/courses/dodge_this.json"),
    CHOP_SHOP_CHALLENGE("/courses/chop_shop_challenge.json"),
    UNDERTOW("/courses/undertow.json"),
    HEAVY_MERGE_AREA("/courses/heavy_merge_area.json"),
    DEATH_TRAP("/courses/death_trap.json"),
    PILGRIMAGE("/courses/pilgrimage.json"),
    GEAR_STRIPPER("/courses/gear_stripper.json"),
    EXTRA_CRISPY("/courses/extra_crispy.json"),
    BURN_RUN("/courses/burn_run.json");

    private final String filePath;


    /**
     * Constructor to initialize an AvailableCourse.
     *
     * @param filePath The file path to the JSON configuration for the course.
     */
    AvailableCourses(String filePath) {
        this.filePath = filePath;
    }


    private static final AvailableCourses[] RACING_COURSES = values();
    private static final Random RANDOM = new Random();


    @Override
    @NonNull public String toString() {
        // Convert the enum name to a formatted string with spaces for the SelectMap Message.
        String name = name().toLowerCase(Locale.ROOT);
        String[] parts = name.split("_");
        StringBuilder formattedName = new StringBuilder();
        for (String part : parts) {
            formattedName.append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1))
                    .append(" ");
        }
        return formattedName.toString().trim();
    }


    /**
     * Finds the enum constant from a formatted string with underscores.
     * Converts a formatted string back to the corresponding enum constant.
     *
     * @param formattedName The formatted name string with spaces.
     * @return The corresponding AvailableCourses enum constant.
     */
    @NonNull public static AvailableCourses fromString(@NonNull String formattedName) {
        String name = formattedName.toUpperCase(Locale.ROOT).replace(" ", "_");
        return valueOf(name);
    }


    /**
     * Retrieves the list of formatted string names of all available courses.
     *
     * @return A list of formatted string names representing all available courses.
     */
    @NonNull public static String[] getFormattedNames() {
        return Arrays.stream(values())
                .map(AvailableCourses::toString)
                .toArray(String[]::new);
    }


    /**
     * Returns a random available course.
     *
     * @return A random AvailableCourses enum constant.
     */
    public static AvailableCourses getRandomCourse() {
        int index = RANDOM.nextInt(RACING_COURSES.length);
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.mapSelected(RACING_COURSES[index].toString()));
        return RACING_COURSES[index];
    }
}
