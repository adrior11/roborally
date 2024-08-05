package com.github.adrior.roborally.core.game.util;

import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.Antenna;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import com.github.adrior.roborally.utility.Pair;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * The PriorityQueue class provides utility methods for sorting players based on their proximity
 * to a specified {@link Antenna} position on the game board. The sorting accounts for both
 * the {@link Vector#manhattanDistanceTo(Vector)} distance to the antenna and the relative
 * y- or x-coordinate to handle ties.
 *
 * <p> The sorting method is efficient given the constraints of the game, ensuring quick and effective
 * determination of player priorities during the game phases.
 *
 * @see Player
 * @see PositionedTile
 * @see Pair
 */
@UtilityClass
public final class PriorityQueue {

    /**
     * Returns a list of players sorted by their distance to a given {@link Antenna} position
     * and their {@link CardType#ADMIN_PRIVILEGE} using the {@link #movePlayerToFront(LinkedList, int)} method.
     * If two players are equidistant from the antenna, they are further sorted by the
     * {@link #transformY(int, int, int)} or {@link #transformX(int, int, int)} method
     * which prioritizes robots based on their y- or x-coordinate relative to the
     * antenna position/orientation and the maximum dimensions of the course.
     *
     * <p> Time Complexity:
     * - The theoretical time complexity is O(n log n) due to the sorting step.
     * - However, given the constraints of 0 < n <= 6 (number of players) and a constant map dimensions of
     *  <30 tiles, the practical time complexity is effectively O(1). Sorting a small, constant number of
     *  elements (one to six players) is very fast and can be treated as constant time for practical purposes.
     *
     * @param players            The list of players to be sorted.
     * @param antenna            The position tile of the antenna.
     * @param dimensions         The maximum dimensions of the course.
     * @param priorityPlayerIDs  The list player IDs indicating admin priority.
     * @return A linked list of players sorted by distance to the antenna and y-coordinate priority.
     */
    @NonNull public static LinkedList<Player> getPlayersPriorityQueue(
            @NonNull List<Player> players, @NonNull PositionedTile<Antenna> antenna,
            @NonNull Pair<Integer, Integer> dimensions, List<Integer> priorityPlayerIDs) {
        Vector antennaPosition = antenna.position();
        boolean isAxisY = isAxisY(antenna);

        int antennaY = antennaPosition.y();
        int antennaX = antennaPosition.x();

        LinkedList<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt((Player player) -> player.robot().getPosition().manhattanDistanceTo(antennaPosition))
                        .thenComparingInt(player -> {
                            int robotY = player.robot().getPosition().y();
                            int robotX = player.robot().getPosition().x();
                            return isAxisY
                                    ? transformY(robotY, antennaY, dimensions.key())
                                    : transformX(robotX, antennaX, dimensions.value());
                        }))
                .collect(Collectors.toCollection(LinkedList::new));

        // Move players with admin priority to the front of the queue
        if (null != priorityPlayerIDs) priorityPlayerIDs.forEach(id -> movePlayerToFront(sortedPlayers, id));

        return sortedPlayers;
    }


    /**
     * Moves the {@link Player} with the specified clientId to the front of the linked list.
     *
     * @param players  the LinkedList of players
     * @param clientId the clientId of the player to move to the front
     */
    private static void movePlayerToFront(@NonNull LinkedList<Player> players, int clientId) {
        ListIterator<Player> iterator = players.listIterator();
        Player targetPlayer = null;

        // Find the player with the given clientId
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.clientId() == clientId) {
                targetPlayer = player;
                iterator.remove(); // Remove the player from the list
                break;
            }
        }

        // If the player was found, add them to the front of the list
        if (null != targetPlayer) {
            players.addFirst(targetPlayer);
        }

        ServerCommunicationFacade.log("<PriorityQueue> Adjusted priority for player: " + clientId);
    }


    /**
     * Determines if the {@link Antenna} is oriented along the Y- or X-axis.
     *
     * @param antenna The positioned tile of the antenna.
     * @return True if the antenna is oriented along the Y-axis, otherwise false.
     */
    private static boolean isAxisY(@NonNull PositionedTile<Antenna> antenna) {
        return antenna.tile().getOrientations().stream()
                .anyMatch(orientation -> Orientation.RIGHT == orientation || Orientation.LEFT == orientation);
    }


    /**
     * Transforms the y-coordinate of a robot based on its position relative to the {@link Antenna}
     * and the maximum height of the course. If the robot's y-coordinate is greater than or
     * equal to the antenna's y-coordinate, it returns the robot's y-coordinate. Otherwise,
     * it adds the maximum height to the robot's y-coordinate.
     *
     * @param robotY    The y-coordinate of the robot.
     * @param antennaY  The y-coordinate of the antenna.
     * @param maxHeight The maximum height of the course.
     * @return The transformed y-coordinate for sorting priority.
     */
    private static int transformY(int robotY, int antennaY, int maxHeight) {
        return (robotY >= antennaY) ? robotY : robotY + maxHeight;
    }

    /**
     * Transforms the x-coordinate of a robot based on its position relative to the {@link Antenna}
     * and the maximum height of the course. If the robot's x-coordinate is greater than or
     * equal to the antenna's x-coordinate, it returns the robot's x-coordinate. Otherwise,
     * it adds the maximum width to the robot's x-coordinate.
     *
     * @param robotX    The x-coordinate of the robot.
     * @param antennaX  The x-coordinate of the antenna.
     * @param maxWidth  The maximum height of the course.
     * @return The transformed x-coordinate for sorting priority.
     */
    private static int transformX(int robotX, int antennaX, int maxWidth) {
        return (robotX >= antennaX) ? robotX : robotX + maxWidth;
    }
}
