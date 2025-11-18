package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.ai.enums.AIDifficulty;
import com.par_28.ship_battle.model.enums.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AIPlayer class
 */
class AIPlayerTest {

    @Nested
    @DisplayName("AIPlayer Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create AIPlayer with EASY difficulty")
        void testCreateEasyAI() {
            // When
            AIPlayer player = new AIPlayer("Easy AI", 10, AIDifficulty.EASY);

            // Then
            assertNotNull(player);
            assertEquals("Easy AI", player.getName());
            assertEquals(AIDifficulty.EASY, player.getDifficulty());
            assertInstanceOf(EasyAI.class, player.getStrategy());
            assertTrue(player.isAI());
        }

        @Test
        @DisplayName("Should create AIPlayer with MEDIUM difficulty")
        void testCreateMediumAI() {
            // When
            AIPlayer player = new AIPlayer("Medium AI", 10, AIDifficulty.MEDIUM);

            // Then
            assertNotNull(player);
            assertEquals(AIDifficulty.MEDIUM, player.getDifficulty());
            assertInstanceOf(MediumAI.class, player.getStrategy());
            assertTrue(player.isAI());
        }

        @Test
        @DisplayName("Should create AIPlayer with HARD difficulty")
        void testCreateHardAI() {
            // When
            AIPlayer player = new AIPlayer("Hard AI", 10, AIDifficulty.HARD);

            // Then
            assertNotNull(player);
            assertEquals(AIDifficulty.HARD, player.getDifficulty());
            assertInstanceOf(HardAI.class, player.getStrategy());
            assertTrue(player.isAI());
        }
    }

    @Nested
    @DisplayName("AIPlayer Shot Selection Tests")
    class ShotSelectionTests {

        @Test
        @DisplayName("Should choose valid shot coordinates")
        void testChoosesValidShot() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.EASY);
            List<Ship> opponentShips = createTestShips();

            // When
            Coordinate shot = ai.chooseShot(opponentShips);

            // Then
            assertNotNull(shot);
            assertTrue(ai.getTrackingGrid().isValidCoordinate(shot));
        }

        @Test
        @DisplayName("Should choose different shots each time")
        void testChoosesDifferentShots() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.EASY);
            List<Ship> opponentShips = createTestShips();

            // When - choose 10 shots and record them
            Coordinate shot1 = ai.chooseShot(opponentShips);
            ai.getTrackingGrid().getCell(shot1).shoot();

            Coordinate shot2 = ai.chooseShot(opponentShips);
            ai.getTrackingGrid().getCell(shot2).shoot();

            Coordinate shot3 = ai.chooseShot(opponentShips);

            // Then
            assertNotEquals(shot1, shot2);
            assertNotEquals(shot2, shot3);
            assertNotEquals(shot1, shot3);
        }
    }

    @Nested
    @DisplayName("AIPlayer Attack Result Notification Tests")
    class AttackResultTests {

        @Test
        @DisplayName("Should update strategy after hit notification")
        void testNotifyHit() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);
            Coordinate shot = new Coordinate(5, 5);
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Cruiser());

            // When
            ai.notifyAttackResult(shot, response);

            // Then - strategy should have been notified (can't directly test internal state)
            // But we can verify the AI still works
            Coordinate nextShot = ai.chooseShot(createTestShips());
            assertNotNull(nextShot);
        }

        @Test
        @DisplayName("Should update strategy after sunk notification")
        void testNotifySunk() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);
            Coordinate shot = new Coordinate(3, 3);
            Ship destroyedShip = new Destroyer();
            destroyedShip.receiveDamage();
            destroyedShip.receiveDamage();
            destroyedShip.receiveDamage();
            AttackResponse response = new AttackResponse(AttackResult.SUNK, destroyedShip);

            // When
            ai.notifyAttackResult(shot, response);

            // Then
            Coordinate nextShot = ai.chooseShot(createTestShips());
            assertNotNull(nextShot);
        }

        @Test
        @DisplayName("Should update strategy after miss notification")
        void testNotifyMiss() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.HARD);
            Coordinate shot = new Coordinate(7, 7);
            AttackResponse response = new AttackResponse(AttackResult.MISS, null);

            // When
            ai.notifyAttackResult(shot, response);

            // Then
            Coordinate nextShot = ai.chooseShot(createTestShips());
            assertNotNull(nextShot);
        }
    }

    @Nested
    @DisplayName("AIPlayer Strategy Reset Tests")
    class ResetTests {

        @Test
        @DisplayName("Should reset strategy successfully")
        void testResetStrategy() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);

            // Make some moves
            Coordinate shot1 = ai.chooseShot(createTestShips());
            ai.getTrackingGrid().getCell(shot1).shoot();

            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Carrier());
            ai.notifyAttackResult(shot1, hitResponse);

            // When - reset
            ai.resetStrategy();

            // Then - should work normally after reset
            Coordinate shotAfterReset = ai.chooseShot(createTestShips());
            assertNotNull(shotAfterReset);
            assertTrue(ai.getTrackingGrid().isValidCoordinate(shotAfterReset));
        }
    }

    @Nested
    @DisplayName("AIPlayer Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should play a complete sequence of turns")
        void testCompleteGameSequence() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.MEDIUM);
            List<Ship> opponentShips = createTestShips();

            // When - play 20 turns
            for (int i = 0; i < 20; i++) {
                Coordinate shot = ai.chooseShot(opponentShips);
                ai.getTrackingGrid().getCell(shot).shoot();

                // Simulate random response
                AttackResult result = (i % 5 == 0) ? AttackResult.HIT : AttackResult.MISS;
                Ship ship = (result == AttackResult.HIT) ? new Torpedo() : null;
                AttackResponse response = new AttackResponse(result, ship);

                ai.notifyAttackResult(shot, response);
            }

            // Then - should still be functional
            Coordinate finalShot = ai.chooseShot(opponentShips);
            assertNotNull(finalShot);
        }

        @Test
        @DisplayName("Should inherit Player functionality")
        void testPlayerInheritance() {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.EASY);

            // Then - should have Player methods
            assertNotNull(ai.getName());
            assertNotNull(ai.getGrid());
            assertNotNull(ai.getTrackingGrid());
            assertNotNull(ai.getShips());
            assertTrue(ai.isDead()); // No ships added yet, so player is "dead"
        }

        @Test
        @DisplayName("Should work with ship placement")
        void testWithShipPlacement() throws Exception {
            // Given
            AIPlayer ai = new AIPlayer("Test AI", 10, AIDifficulty.HARD);
            Ship carrier = new Carrier();

            // When - add and place a ship
            ai.addShip(carrier);
            ai.placeShipOnGrid(carrier, new Coordinate(0, 0), Direction.HORIZONTAL);

            // Then
            assertEquals(1, ai.getShips().size());
            assertTrue(ai.getGrid().getCell(new Coordinate(0, 0)).hasShip());
        }
    }

    @Nested
    @DisplayName("AIPlayer Difficulty Behavior Tests")
    class DifficultyBehaviorTests {

        @Test
        @DisplayName("Different difficulties should use different strategies")
        void testDifferentStrategies() {
            // Given
            AIPlayer easy = new AIPlayer("Easy", 10, AIDifficulty.EASY);
            AIPlayer medium = new AIPlayer("Medium", 10, AIDifficulty.MEDIUM);
            AIPlayer hard = new AIPlayer("Hard", 10, AIDifficulty.HARD);

            // Then
            assertNotEquals(easy.getStrategy().getClass(), medium.getStrategy().getClass());
            assertNotEquals(medium.getStrategy().getClass(), hard.getStrategy().getClass());
            assertNotEquals(easy.getStrategy().getClass(), hard.getStrategy().getClass());
        }
    }

    @Nested
    @DisplayName("AIPlayer Random Ship Placement Tests")
    class RandomPlacementTests {

        @Test
        @DisplayName("Should place multiple ships randomly without overlap")
        void testPlaceMultipleShipsRandomly() {
            // Given
            AIPlayer ai = new AIPlayer("AI", 10, AIDifficulty.MEDIUM);
            List<Ship> ships = createTestShips();

            // When
            boolean allPlaced = ai.placeShipsRandomly(ships);

            // Then
            assertTrue(allPlaced, "All ships should be placed successfully");
            assertEquals(4, ai.getShips().size(), "AI should have 4 ships");

            // Verify each ship is placed
            for (Ship ship : ai.getShips()) {
                assertNotNull(ship.getPositions());
                assertFalse(ship.getPositions().isEmpty());
            }
        }

        @Test
        @DisplayName("Should place ships deterministically with seed")
        void testDeterministicPlacementWithSeed() {
            // Given
            long seed = 12345L;
            AIPlayer ai1 = new AIPlayer("AI1", 10, AIDifficulty.EASY);
            AIPlayer ai2 = new AIPlayer("AI2", 10, AIDifficulty.EASY);

            List<Ship> ships1 = createTestShips();
            List<Ship> ships2 = createTestShips();

            // When
            ai1.placeShipsRandomly(ships1, seed);
            ai2.placeShipsRandomly(ships2, seed);

            // Then - both should have identical placements
            assertEquals(ai1.getShips().size(), ai2.getShips().size());

            for (int i = 0; i < ai1.getShips().size(); i++) {
                Ship ship1 = ai1.getShips().get(i);
                Ship ship2 = ai2.getShips().get(i);

                assertEquals(ship1.getPositions(), ship2.getPositions(),
                    "Ships with same seed should have identical positions");
                assertEquals(ship1.getDirection(), ship2.getDirection(),
                    "Ships with same seed should have identical directions");
            }
        }

        @Test
        @DisplayName("Should respect grid boundaries when placing ships")
        void testRespectGridBoundaries() {
            // Given
            AIPlayer ai = new AIPlayer("AI", 10, AIDifficulty.HARD);
            List<Ship> ships = createTestShips();

            // When
            boolean placed = ai.placeShipsRandomly(ships);

            // Then
            assertTrue(placed);

            for (Ship ship : ai.getShips()) {
                for (Coordinate pos : ship.getPositions()) {
                    assertTrue(pos.getX() >= 0 && pos.getX() < 10,
                        "X coordinate should be within grid bounds");
                    assertTrue(pos.getY() >= 0 && pos.getY() < 10,
                        "Y coordinate should be within grid bounds");
                }
            }
        }

        @Test
        @DisplayName("Should not place overlapping ships")
        void testNoOverlappingShips() {
            // Given
            AIPlayer ai = new AIPlayer("AI", 10, AIDifficulty.MEDIUM);
            List<Ship> ships = createTestShips();

            // When
            ai.placeShipsRandomly(ships);

            // Then - collect all occupied coordinates
            List<Coordinate> allPositions = new ArrayList<>();
            for (Ship ship : ai.getShips()) {
                for (Coordinate pos : ship.getPositions()) {
                    assertFalse(allPositions.contains(pos),
                        "Coordinate " + pos + " is occupied by multiple ships");
                    allPositions.add(pos);
                }
            }
        }

        @Test
        @DisplayName("Should handle small grid gracefully")
        void testSmallGrid() {
            // Given - 5x5 grid with 2 small ships
            AIPlayer ai = new AIPlayer("AI", 5, AIDifficulty.EASY);
            List<Ship> ships = new ArrayList<>();
            ships.add(new Torpedo());  // 2 cells
            ships.add(new Destroyer()); // 3 cells

            // When
            boolean placed = ai.placeShipsRandomly(ships);

            // Then
            assertTrue(placed, "Should be able to place ships on small grid");
        }

        @Test
        @DisplayName("Should work after placement in full game scenario")
        void testFullGameScenario() throws Exception {
            // Given
            Player human = new Player("Human", 10);
            AIPlayer ai = new AIPlayer("AI", 10, AIDifficulty.MEDIUM);

            // Place human ships manually
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
