package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.ai.enums.AIDifficulty;
import com.par_28.ship_battle.model.enums.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AIPlayer class
 */
class AIPlayerTest {

    @Nested
    @DisplayName("AIPlayer Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create AIPlayer with correct strategy based on difficulty")
        void testCreateWithDifficulty() {
            // Given/When
            AIPlayer easy = new AIPlayer("Easy AI", 10, AIDifficulty.EASY);
            AIPlayer medium = new AIPlayer("Medium AI", 10, AIDifficulty.MEDIUM);
            AIPlayer hard = new AIPlayer("Hard AI", 10, AIDifficulty.HARD);

            // Then
            assertEquals("Easy AI", easy.getName());
            assertEquals(AIDifficulty.EASY, easy.getDifficulty());
            assertInstanceOf(EasyAI.class, easy.getStrategy());
            assertTrue(easy.isAI());

            assertEquals(AIDifficulty.MEDIUM, medium.getDifficulty());
            assertInstanceOf(MediumAI.class, medium.getStrategy());

            assertEquals(AIDifficulty.HARD, hard.getDifficulty());
            assertInstanceOf(HardAI.class, hard.getStrategy());

            // Strategies should be different classes
            assertNotEquals(easy.getStrategy().getClass(), medium.getStrategy().getClass());
            assertNotEquals(medium.getStrategy().getClass(), hard.getStrategy().getClass());
        }

        @Test
        @DisplayName("Should inherit Player functionality")
        void testPlayerInheritance() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.EASY);
            Ship carrier = new Carrier();

            // When
            ai.addShip(carrier);
            ai.placeShipOnGrid(carrier, new Coordinate(0, 0), Direction.HORIZONTAL);

            // Then - should have Player methods and state
            assertNotNull(ai.getGrid());
            assertNotNull(ai.getTrackingGrid());
            assertEquals(1, ai.getShips().size());
            assertTrue(ai.getGrid().getCell(new Coordinate(0, 0)).hasShip());
            assertFalse(ai.isDead()); // Has a ship now
        }
    }

    @Nested
    @DisplayName("AIPlayer Shot Selection Tests")
    class ShotSelectionTests {

        @Test
        @DisplayName("Should choose unique valid shots")
        void testChoosesUniqueValidShots() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.EASY);
            List<Ship> opponentShips = createTestShips();
            Set<Coordinate> shots = new HashSet<>();

            // When - choose 20 shots
            for (int i = 0; i < 20; i++) {
                Coordinate shot = ai.chooseShot(opponentShips);

                // Then
                assertNotNull(shot);
                assertTrue(ai.getTrackingGrid().isValidCoordinate(shot));
                assertFalse(shots.contains(shot), "Should not choose same coordinate twice");

                ai.getTrackingGrid().getCell(shot).shoot();
                shots.add(shot);
            }

            assertEquals(20, shots.size());
        }
    }

    @Nested
    @DisplayName("AIPlayer Strategy Notification Tests")
    class StrategyNotificationTests {

        @Test
        @DisplayName("Should notify strategy of attack results and affect next shot")
        void testNotifyAffectsNextShot() {
            // Given - use MEDIUM AI which changes behavior after HIT
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - notify of a HIT
            ai.notifyAttackResult(hitCoord, new AttackResponse(AttackResult.HIT, new Cruiser()));
            ai.getTrackingGrid().getCell(hitCoord).shoot();

            // Then - next shot should be adjacent (MEDIUM AI targets adjacent after hit)
            Coordinate nextShot = ai.chooseShot(createTestShips());
            assertTrue(isAdjacent(hitCoord, nextShot),
                "After HIT notification, MEDIUM AI should target adjacent cells");
        }

        @Test
        @DisplayName("Should reset strategy state")
        void testResetStrategy() {
            // Given - MEDIUM AI in TARGET mode
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);
            Coordinate hitCoord = new Coordinate(5, 5);

            ai.notifyAttackResult(hitCoord, new AttackResponse(AttackResult.HIT, new Carrier()));
            ai.getTrackingGrid().getCell(hitCoord).shoot();

            // Verify it's in TARGET mode
            Coordinate beforeReset = ai.chooseShot(createTestShips());
            assertTrue(isAdjacent(hitCoord, beforeReset));
            ai.getTrackingGrid().getCell(beforeReset).shoot();

            // When - reset
            ai.resetStrategy();

            // Then - should work normally (back to HUNT mode behavior)
            Coordinate afterReset = ai.chooseShot(createTestShips());
            assertNotNull(afterReset);
            assertTrue(ai.getTrackingGrid().isValidCoordinate(afterReset));
        }
    }

    @Nested
    @DisplayName("AIPlayer Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should play a complete sequence of turns without errors")
        void testCompleteGameSequence() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);
            List<Ship> opponentShips = createTestShips();

            // When - play 30 turns with mixed results
            for (int i = 0; i < 30; i++) {
                Coordinate shot = ai.chooseShot(opponentShips);
                ai.getTrackingGrid().getCell(shot).shoot();

                // Simulate varied responses
                AttackResult result;
                if (i % 10 == 0) {
                    result = AttackResult.SUNK;
                } else if (i % 5 == 0) {
                    result = AttackResult.HIT;
                } else {
                    result = AttackResult.MISS;
                }

                Ship ship = (result != AttackResult.MISS) ? new Torpedo() : null;
                ai.notifyAttackResult(shot, new AttackResponse(result, ship));
            }

            // Then - should still be functional
            Coordinate finalShot = ai.chooseShot(opponentShips);
            assertNotNull(finalShot);
            assertFalse(ai.getTrackingGrid().getCell(finalShot).isShot());
        }

        @Test
        @DisplayName("Should work in full game scenario with ship placement")
        void testFullGameScenario() {
            // Given
            Player human = new Player("Human", 10);
            AIPlayer ai = new AIPlayer("AI", 10, AIDifficulty.MEDIUM);

            // Place human ships
            Ship humanCarrier = new Carrier();
            human.addShip(humanCarrier);
            human.placeShipOnGrid(humanCarrier, new Coordinate(0, 0), Direction.HORIZONTAL);

            // Place AI ships randomly
            List<Ship> aiShips = createTestShips();
            boolean placed = ai.placeShipsRandomly(aiShips);

            // Then
            assertTrue(placed);
            assertEquals(1, human.getShips().size());
            assertEquals(4, ai.getShips().size());

            // Game should start successfully
            Game game = new Game(human, ai);
            game.start();
            assertNotNull(game.getCurrentPlayer());
        }
    }

    @Nested
    @DisplayName("AIPlayer Random Ship Placement Tests")
    class RandomPlacementTests {

        @Test
        @DisplayName("Should place ships without overlap and within bounds")
        void testPlaceShipsCorrectly() {
            // Given
            AIPlayer ai = new AIPlayer("AI", 10, AIDifficulty.MEDIUM);
            List<Ship> ships = createTestShips();

            // When
            boolean placed = ai.placeShipsRandomly(ships);

            // Then
            assertTrue(placed);
            assertEquals(4, ai.getShips().size());

            // Verify no overlaps and within bounds
            Set<Coordinate> allPositions = new HashSet<>();
            for (Ship ship : ai.getShips()) {
                assertNotNull(ship.getPositions());
                assertFalse(ship.getPositions().isEmpty());

                for (Coordinate pos : ship.getPositions()) {
                    // Check bounds
                    assertTrue(pos.getX() >= 0 && pos.getX() < 10);
                    assertTrue(pos.getY() >= 0 && pos.getY() < 10);

                    // Check no overlap
                    assertFalse(allPositions.contains(pos),
                        "Coordinate " + pos + " is occupied by multiple ships");
                    allPositions.add(pos);
                }
            }
        }

        @Test
        @DisplayName("Should place ships deterministically with seed")
        void testDeterministicPlacement() {
            // Given
            long seed = 12345L;
            AIPlayer ai1 = new AIPlayer("AI1", 10, AIDifficulty.EASY);
            AIPlayer ai2 = new AIPlayer("AI2", 10, AIDifficulty.EASY);

            // When
            ai1.placeShipsRandomly(createTestShips(), seed);
            ai2.placeShipsRandomly(createTestShips(), seed);

            // Then - identical placements
            assertEquals(ai1.getShips().size(), ai2.getShips().size());

            for (int i = 0; i < ai1.getShips().size(); i++) {
                Ship ship1 = ai1.getShips().get(i);
                Ship ship2 = ai2.getShips().get(i);

                assertEquals(ship1.getPositions(), ship2.getPositions());
                assertEquals(ship1.getDirection(), ship2.getDirection());
            }
        }

        @Test
        @DisplayName("Should handle small grid")
        void testSmallGrid() {
            // Given - 5x5 grid with small ships
            AIPlayer ai = new AIPlayer("AI", 5, AIDifficulty.EASY);
            List<Ship> ships = new ArrayList<>();
            ships.add(new Torpedo());   // 2 cells
            ships.add(new Destroyer()); // 3 cells

            // When
            boolean placed = ai.placeShipsRandomly(ships);

            // Then
            assertTrue(placed);
            assertEquals(2, ai.getShips().size());
        }
    }

    @Nested
    @DisplayName("AIPlayer Random Name Tests")
    class RandomNameTests {

        @Test
        @DisplayName("Should return a name from the predefined list")
        void testGetRandomName() {
            // Given
            Set<String> validNames = Set.of(
                "Tanya", "Erza", "Azusa", "Rika", "Kanna", "Konata"
            );

            // When - get 20 random names
            Set<String> generatedNames = new HashSet<>();
            for (int i = 0; i < 20; i++) {
                String name = AIPlayer.getRandomName();
                generatedNames.add(name);

                // Then - each name should be from the valid list
                assertTrue(validNames.contains(name),
                    "Generated name '" + name + "' should be from predefined list");
            }

            // Should have generated at least a few different names (probabilistic)
            assertTrue(generatedNames.size() > 1,
                "Should generate different names over multiple calls");
        }
    }

    /**
     * Helper method to check if two coordinates are adjacent
     */
    private boolean isAdjacent(Coordinate c1, Coordinate c2) {
        int dx = Math.abs(c1.getX() - c2.getX());
        int dy = Math.abs(c1.getY() - c2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    /**
     * Helper method to create a list of test ships
     */
    private List<Ship> createTestShips() {
        List<Ship> ships = new ArrayList<>();
        ships.add(new Carrier());
        ships.add(new Cruiser());
        ships.add(new Destroyer());
        ships.add(new Torpedo());
        return ships;
    }
}
