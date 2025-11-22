package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EasyAI strategy
 */
class EasyAITest {

    @Nested
    @DisplayName("EasyAI Shot Selection Tests")
    class ShotSelectionTests {

        @Test
        @DisplayName("Should select unique valid coordinates until grid is full")
        void testSelectsUniqueValidCoordinates() {
            // Given
            EasyAI ai = new EasyAI();
            Grid trackingGrid = new Grid(5, 5); // Small grid
            Set<Coordinate> selectedShots = new HashSet<>();

            // When - shoot all 25 cells
            for (int i = 0; i < 25; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());

                // Then - each shot should be valid and unique
                assertTrue(trackingGrid.isValidCoordinate(shot));
                assertFalse(trackingGrid.getCell(shot).isShot(),
                    "AI selected already-shot coordinate: " + shot);
                assertFalse(selectedShots.contains(shot),
                    "AI selected duplicate coordinate: " + shot);

                trackingGrid.getCell(shot).shoot();
                selectedShots.add(shot);
            }

            assertEquals(25, selectedShots.size(), "All cells should have been selected");
        }

        @Test
        @DisplayName("Should only select from unshot cells in partially shot grid")
        void testSelectsFromUnshotCells() {
            // Given
            EasyAI ai = new EasyAI();
            Grid trackingGrid = new Grid(10, 10);

            // Shoot top half of grid
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 5; y++) {
                    trackingGrid.getCell(new Coordinate(x, y)).shoot();
                }
            }

            // When - make 10 shots
            for (int i = 0; i < 10; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());

                // Then - should only select from bottom half (y >= 5)
                assertTrue(shot.getY() >= 5,
                    "Shot " + shot + " should be in unshot area (y >= 5)");
                assertFalse(trackingGrid.getCell(shot).isShot());

                trackingGrid.getCell(shot).shoot();
            }
        }

        @Test
        @DisplayName("Should return fallback coordinate when grid is fully shot")
        void testFallbackWhenGridFull() {
            // Given
            EasyAI ai = new EasyAI();
            Grid trackingGrid = new Grid(3, 3);

            // Shoot all cells
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    trackingGrid.getCell(new Coordinate(x, y)).shoot();
                }
            }

            // When
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());

            // Then - should return fallback (0,0)
            assertEquals(new Coordinate(0, 0), shot);
        }
    }

    @Nested
    @DisplayName("EasyAI Behavior Tests")
    class BehaviorTests {

        @Test
        @DisplayName("Should use seed for deterministic behavior")
        void testDeterministicBehavior() {
            // Given
            long seed = 999;
            EasyAI ai1 = new EasyAI(seed);
            EasyAI ai2 = new EasyAI(seed);
            Grid grid1 = new Grid(10, 10);
            Grid grid2 = new Grid(10, 10);

            // When - both AIs make 10 shots
            List<Coordinate> shots1 = new ArrayList<>();
            List<Coordinate> shots2 = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Coordinate shot1 = ai1.chooseShot(grid1, new ArrayList<>());
                Coordinate shot2 = ai2.chooseShot(grid2, new ArrayList<>());

                grid1.getCell(shot1).shoot();
                grid2.getCell(shot2).shoot();

                shots1.add(shot1);
                shots2.add(shot2);
            }

            // Then
            assertEquals(shots1, shots2, "AIs with same seed should make identical decisions");
        }

        @Test
        @DisplayName("Should ignore remaining ships list (purely random)")
        void testIgnoresRemainingShips() {
            // Given - same seed, different ship lists
            long seed = 123;
            EasyAI ai1 = new EasyAI(seed);
            EasyAI ai2 = new EasyAI(seed);
            Grid grid1 = new Grid(10, 10);
            Grid grid2 = new Grid(10, 10);

            List<Ship> noShips = new ArrayList<>();
            List<Ship> someShips = new ArrayList<>();
            someShips.add(new Carrier());
            someShips.add(new Destroyer());

            // When
            Coordinate shot1 = ai1.chooseShot(grid1, noShips);
            Coordinate shot2 = ai2.chooseShot(grid2, someShips);

            // Then - should be identical regardless of ship list
            assertEquals(shot1, shot2, "EasyAI should ignore remaining ships");
        }
    }
}
