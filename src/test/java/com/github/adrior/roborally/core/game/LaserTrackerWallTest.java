package com.github.adrior.roborally.test.core.game;

import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.game.recorder.HitRecorder;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LaserTrackerWallTest {
    RacingCourse course;
    TurnManager turnManager;
    final LinkedList<Player> players = new LinkedList<>();

    Player player1;
    Player player2;
    Player player3;
    Player player4;

    @BeforeEach
    void setup() {
        // Reset SharedDeck to prevent a 'NoSuchElement' error when running on mvn test.
        SharedDeck.resetDecks();

        HitRecorder.clearHits();

        players.add(player1 = new Player(1, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player2 = new Player(2, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player3 = new Player(3, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));
        players.add(player4 = new Player(4, new Robot(), new CardManager(), new ProgrammingRegister(), new Deck(),
                new Player.Flags()));

        player1.robot().setPosition(new Vector(1, 1));
        player1.robot().setOrientation(Orientation.RIGHT);

        player2.robot().setPosition(new Vector(10, 8));
        player2.robot().setOrientation(Orientation.RIGHT);

        player3.robot().setPosition(new Vector(10, 3));
        player3.robot().setOrientation(Orientation.RIGHT);

        player4.robot().setPosition(new Vector(17, 2));
        player4.robot().setOrientation(Orientation.RIGHT);
    }

    @Test
    void testGearStripperLasers() {
        course = RacingCourse.createRacingCourse(AvailableCourses.GEAR_STRIPPER);
        turnManager = new TurnManager(players, course);
        turnManager.activateFactoryElements();

        assertEquals(0, HitRecorder.totalHits(), "No Robot should get hit, as all Lasers are blocked by walls");
    }

    @Test
    void testGearStripperTripleLasers() {
        LinkedList<Player> player = new LinkedList<>();
        player.add(player3);

        course = RacingCourse.createRacingCourse(AvailableCourses.GEAR_STRIPPER);
        turnManager = new TurnManager(player, course);
        turnManager.activateFactoryElements();

        assertEquals(0, HitRecorder.totalHits(), "No Robot should get hit, as all Lasers are blocked by walls");
    }
}
