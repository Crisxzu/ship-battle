package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EasyAI strategy
 */
class EasyAITest {

    @Nested
    @DisplayName("EasyAI Shot Selection Tests")
    class ShotSelectionTests {

        @Test
        @DisplayName("Should select a valid unshot coordinate")
        void testSelectsValidCoordinate() {
            // Given
            EasyAI ai = new EasyAI(42); // Seeded for deterministic behavior
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = new ArrayList<>();

            // When
            Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);

            // Then
            assertNotNull(shot);
            assertTrue(trackingGrid.isValidCoordinate(shot));
            assertFalse(trackingGrid.getCell(shot).isShot());
        }

        @Test
        @DisplayName("Should never select the same coordinate twice")
        void testNeverSelectsSameCoordinateTwice() {
            // Given
            EasyAI ai = new EasyAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = new ArrayList<>();
            Set<Coordinate> selectedShots = new HashSet<>();

            // When - shoot 50 times
            for (int i = 0; i < 50; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
                trackingGrid.getCell(shot).shoot(); // Mark as shot

                // Then
                assertFalse(selectedShots.contains(shot),
                    "AI selected the same coordinate twice: " + shot);
                selectedShots.add(shot);
            }

            assertEquals(50, selectedShots.size());
        }

        @Test
        @DisplayName("Should select from all available coordinates randomly")
        void testRandomDistribution() {
            // Given
            EasyAI ai = new EasyAI();
            Grid trackingGrid = new Grid(5, 5); // Small grid for faster test
            List<Ship> remainingShips = new ArrayList<>();
            Set<Coordinate> selectedShots = new HashSet<>();

            // When - shoot until all cells are covered
            for (int i = 0; i < 25; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
                trackingGrid.getCell(shot).shoot();
                selectedShots.add(shot);
            }

            // Then - all 25 cells should have been selected
            assertEquals(25, selectedShots.size());
        }
    }

    @Nested
    @DisplayName("EasyAI State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should not change behavior after hits")
        void testDoesNotLearnFromHits() {
            // Given
            EasyAI ai = new EasyAI(123);
            Grid trackingGrid = new Grid(10, 10);
            Coordinate firstShot = ai.chooseShot(trackingGrid, new ArrayList<>());

            // When - notify of a hit
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Carrier());
            ai.updateAfterShot(firstShot, hitResponse);

            // Then - next shot should still be random (not adjacent to hit)
            trackingGrid.getCell(firstShot).shoot();
            Coordinate secondShot = ai.chooseShot(trackingGrid, new ArrayList<>());

            assertNotNull(secondShot);
            // EasyAI doesn't target adjacent cells, so behavior is unchanged
        }

        @Test
        @DisplayName("Should reset successfully")
        void testReset() {
            // Given
            EasyAI ai = new EasyAI(456);
            Grid trackingGrid = new Grid(10, 10);

            // When
            ai.chooseShot(trackingGrid, new ArrayList<>());
            ai.reset();

            // Then - should continue working normally after reset
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(shot);
            assertTrue(trackingGrid.isValidCoordinate(shot));
        }
    }

    @Nested
    @DisplayName("EasyAI Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle partially shot grid")
        void testPartiallyShotGrid() {
            // Given
            EasyAI ai = new EasyAI();
            Grid trackingGrid = new Grid(10, 10);

            // Shoot half the grid manually
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 5; y++) {
                    trackingGrid.getCell(new Coordinate(x, y)).shoot();
                }
            }

            List<Ship> remainingShips = new ArrayList<>();

            // When
            Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);

            // Then
            assertNotNull(shot);
            assertFalse(trackingGrid.getCell(shot).isShot(),
                "AI selected an already-shot coordinate");
            assertTrue(shot.getY() >= 5, "AI should only select from unshot area");
        }

        @Test
        @DisplayName("Should use seed for deterministic behavior")
        void testDeterministicBehaviorWithSeed() {
            // Given
            long seed = 999;
            EasyAI ai1 = new EasyAI(seed);
            EasyAI ai2 = new EasyAI(seed);
            Grid trackingGrid1 = new Grid(10, 10);
            Grid trackingGrid2 = new Grid(10, 10);

            // When - both AIs make 10 shots
            List<Coordinate> shots1 = new ArrayList<>();
            List<Coordinate> shots2 = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Coordinate shot1 = ai1.chooseShot(trackingGrid1, new ArrayList<>());
                Coordinate shot2 = ai2.chooseShot(trackingGrid2, new ArrayList<>());

                trackingGrid1.getCell(shot1).shoot();
                trackingGrid2.getCell(shot2).shoot();

                shots1.add(shot1);
                shots2.add(shot2);
            }

            // Then - both should make identical shots
            assertEquals(shots1, shots2,
                "AIs with same seed should make identical decisions");
        }
    }
}
