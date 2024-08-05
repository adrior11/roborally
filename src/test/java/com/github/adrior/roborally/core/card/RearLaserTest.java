package com.github.adrior.roborally.test.core.card;

import com.github.adrior.roborally.core.card.CardFactory;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.Deck;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.game.recorder.HitRecorder;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.CardManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.ProgrammingRegister;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RearLaserTest {
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

        player1.robot().setPosition(new Vector(0, 0));
        player1.robot().setOrientation(Orientation.RIGHT);

        player2.robot().setPosition(new Vector(0, 1));
        player2.robot().setOrientation(Orientation.TOP);
        player2.installedUpgrades().addCard(CardFactory.createCard(CardType.REAR_LASER));

        player3.robot().setPosition(new Vector(0, 2));
        player3.robot().setOrientation(Orientation.RIGHT);

        player4.robot().setPosition(new Vector(0, 3));
        player4.robot().setOrientation(Orientation.RIGHT);
    }

    @Test
    void testRearLaser() {
        course = RacingCourse.createRacingCourse(AvailableCourses.GEAR_STRIPPER);
        turnManager = new TurnManager(players, course);
        turnManager.activateFactoryElements();

        // Expected hits
        Set<Vector> expectedHits = new HashSet<>();
        expectedHits.add(new Vector(0, 0)); // Laser from (6, 2)
        expectedHits.add(new Vector(0, 2)); // Laser from (4, 6)

        // Verify hits
        expectedHits.forEach(hit -> assertTrue(HitRecorder.wasHit(hit)));
        assertEquals(2, HitRecorder.totalHits(), "2 robots should be hit");
    }
}
