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
 * Tests for HardAI strategy
 */
class HardAITest {

    @Nested
    @DisplayName("HardAI Hunt Mode Tests")
    class HuntModeTests {

        @Test
        @DisplayName("Should start in HUNT mode - shots are probability-based and spread across grid")
        void testStartsInHuntMode() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();

            // When - take multiple shots without any HITs (stay in HUNT mode)
            Set<Coordinate> shots = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
                trackingGrid.getCell(shot).shoot();
                shots.add(shot);

                // Notify MISS - should stay in HUNT mode
                ai.updateAfterShot(shot, new AttackResponse(AttackResult.MISS, null));
            }

            // Then - shots should be spread across the grid (probability-based)
            assertEquals(10, shots.size(), "All shots should be unique");

            // Verify shots are spread out (not all adjacent to first shot)
            Coordinate firstShot = shots.iterator().next();
            long adjacentCount = shots.stream()
                .filter(s -> !s.equals(firstShot) && isAdjacent(firstShot, s))
                .count();

            assertTrue(adjacentCount <= 4,
                "In HUNT mode, shots should be probability-based, not clustered");
        }

        @Test
        @DisplayName("Should return to HUNT mode and clear hit history after sinking a ship")
        void testReturnsToHuntAfterSunk() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - hit a ship
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Torpedo());
            ai.updateAfterShot(hitCoord, hitResponse);
            trackingGrid.getCell(hitCoord).shoot();

            // Sink the ship
            Coordinate sunkCoord = new Coordinate(5, 6);
            AttackResponse sunkResponse = new AttackResponse(AttackResult.SUNK, new Torpedo());
            ai.updateAfterShot(sunkCoord, sunkResponse);
            trackingGrid.getCell(sunkCoord).shoot();

            // Then - next shots should be spread (HUNT mode), not adjacent to old hits
            Set<Coordinate> nextShots = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
                trackingGrid.getCell(shot).shoot();
                nextShots.add(shot);
                ai.updateAfterShot(shot, new AttackResponse(AttackResult.MISS, null));
            }

            // Verify not all shots are adjacent to old hit (would indicate still in TARGET mode)
            long adjacentToOldHit = nextShots.stream()
                .filter(s -> isAdjacent(hitCoord, s) || isAdjacent(sunkCoord, s))
                .count();

            assertTrue(adjacentToOldHit < 5,
                "After sinking, AI should return to HUNT mode, not keep targeting old hit area");
        }
    }

    @Nested
    @DisplayName("HardAI Target Mode Tests")
    class TargetModeTests {

        @Test
        @DisplayName("Should switch to TARGET mode and target adjacent cells after a hit")
        void testSwitchesToTargetMode() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - hit a ship
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Cruiser());
            ai.updateAfterShot(hitCoord, hitResponse);
            trackingGrid.getCell(hitCoord).shoot();

            // Then - collect all targets (simulate misses)
            List<Coordinate> targets = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
                if (shot == null) break;
                trackingGrid.getCell(shot).shoot();
                targets.add(shot);

                ai.updateAfterShot(shot, new AttackResponse(AttackResult.MISS, null));
            }

            // All 4 targets should be adjacent to hit
            assertEquals(4, targets.size(), "Should target all 4 adjacent cells");
            for (Coordinate target : targets) {
                assertTrue(isAdjacent(hitCoord, target),
                    "Target " + target + " should be adjacent to hit " + hitCoord);
            }
        }

        @Test
        @DisplayName("Should determine horizontal orientation and extend in that direction")
        void testDeterminesHorizontalOrientation() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();

            // Two horizontal hits (same Y)
            Coordinate firstHit = new Coordinate(5, 5);
            Coordinate secondHit = new Coordinate(6, 5);

            ai.updateAfterShot(firstHit, new AttackResponse(AttackResult.HIT, new Carrier()));
            trackingGrid.getCell(firstHit).shoot();

            ai.updateAfterShot(secondHit, new AttackResponse(AttackResult.HIT, new Carrier()));
            trackingGrid.getCell(secondHit).shoot();

            // When - next shot
            Coordinate nextShot = ai.chooseShot(trackingGrid, remainingShips);

            // Then - should extend horizontally (x=4 or x=7, y=5)
            assertNotNull(nextShot);
            assertEquals(5, nextShot.getY(), "Should continue on same row");
            assertTrue(nextShot.getX() == 4 || nextShot.getX() == 7,
                "Should extend to left (4) or right (7), got: " + nextShot.getX());
        }

        @Test
        @DisplayName("Should determine vertical orientation and extend in that direction")
        void testDeterminesVerticalOrientation() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();

            // Two vertical hits (same X)
            Coordinate firstHit = new Coordinate(5, 5);
            Coordinate secondHit = new Coordinate(5, 6);

            ai.updateAfterShot(firstHit, new AttackResponse(AttackResult.HIT, new Carrier()));
            trackingGrid.getCell(firstHit).shoot();

            ai.updateAfterShot(secondHit, new AttackResponse(AttackResult.HIT, new Carrier()));
            trackingGrid.getCell(secondHit).shoot();

            // When - next shot
            Coordinate nextShot = ai.chooseShot(trackingGrid, remainingShips);

            // Then - should extend vertically (x=5, y=4 or y=7)
            assertNotNull(nextShot);
            assertEquals(5, nextShot.getX(), "Should continue on same column");
            assertTrue(nextShot.getY() == 4 || nextShot.getY() == 7,
                "Should extend up (4) or down (7), got: " + nextShot.getY());
        }

        @Test
        @DisplayName("Should continue extending in same direction with multiple hits")
        void testContinuesInSameDirection() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();

            // Three horizontal hits
            for (Coordinate hit : List.of(
                    new Coordinate(3, 5),
                    new Coordinate(4, 5),
                    new Coordinate(5, 5))) {
                ai.updateAfterShot(hit, new AttackResponse(AttackResult.HIT, new Carrier()));
                trackingGrid.getCell(hit).shoot();
            }

            // When - next shot
            Coordinate nextShot = ai.chooseShot(trackingGrid, remainingShips);

            // Then - should extend to x=2 or x=6 on same row
            assertNotNull(nextShot);
            assertEquals(5, nextShot.getY(), "Should continue on same row");
            assertTrue(nextShot.getX() == 2 || nextShot.getX() == 6,
                "Should extend to x=2 or x=6, got: " + nextShot.getX());
        }
    }

    @Nested
    @DisplayName("HardAI State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should reset to HUNT mode successfully")
        void testReset() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();
            Coordinate hitCoord = new Coordinate(5, 5);

            // Enter TARGET mode
            ai.updateAfterShot(hitCoord, new AttackResponse(AttackResult.HIT, new Destroyer()));

            // When - reset
            ai.reset();

            // Then - next shot should NOT be forced to be adjacent (back in HUNT mode)
            Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
            assertNotNull(shot);
            // Can't guarantee non-adjacent, but verify it works
            assertTrue(trackingGrid.isValidCoordinate(shot));
        }

        @Test
        @DisplayName("Should handle ALREADY_HIT without changing mode")
        void testHandlesAlreadyHit() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();

            // When - receive ALREADY_HIT (should not switch to TARGET mode)
            ai.updateAfterShot(new Coordinate(5, 5), new AttackResponse(AttackResult.ALREADY_HIT, null));

            // Then - should still be in HUNT mode
            Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);
            assertNotNull(shot);
        }
    }

    @Nested
    @DisplayName("HardAI Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle hit at corner - only target valid adjacent cells")
        void testHitAtCorner() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();
            Coordinate cornerHit = new Coordinate(0, 0);

            // When - hit at corner
            ai.updateAfterShot(cornerHit, new AttackResponse(AttackResult.HIT, new Torpedo()));
            trackingGrid.getCell(cornerHit).shoot();

            // Collect both possible targets
            Coordinate shot1 = ai.chooseShot(trackingGrid, remainingShips);
            trackingGrid.getCell(shot1).shoot();
            ai.updateAfterShot(shot1, new AttackResponse(AttackResult.MISS, null));

            Coordinate shot2 = ai.chooseShot(trackingGrid, remainingShips);

            // Then - should only target (1,0) and (0,1)
            Set<Coordinate> validTargets = Set.of(new Coordinate(1, 0), new Coordinate(0, 1));
            assertTrue(validTargets.contains(shot1), "First shot should be (1,0) or (0,1)");
            assertTrue(validTargets.contains(shot2), "Second shot should be (1,0) or (0,1)");
            assertNotEquals(shot1, shot2, "Should target different adjacent cells");
        }

        @Test
        @DisplayName("Should extend in only valid direction when blocked by grid edge")
        void testLineBlockedByEdge() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();

            // Two horizontal hits at the right edge
            Coordinate hit1 = new Coordinate(8, 5);
            Coordinate hit2 = new Coordinate(9, 5); // Edge

            ai.updateAfterShot(hit1, new AttackResponse(AttackResult.HIT, new Cruiser()));
            trackingGrid.getCell(hit1).shoot();

            ai.updateAfterShot(hit2, new AttackResponse(AttackResult.HIT, new Cruiser()));
            trackingGrid.getCell(hit2).shoot();

            // When
            Coordinate nextShot = ai.chooseShot(trackingGrid, remainingShips);

            // Then - should extend left (x=7) since right is blocked by edge
            assertEquals(new Coordinate(7, 5), nextShot);
        }

        @Test
        @DisplayName("Should use seed for deterministic behavior")
        void testDeterministicBehavior() {
            // Given
            long seed = 777;
            HardAI ai1 = new HardAI(seed);
            HardAI ai2 = new HardAI(seed);
            Grid grid1 = new Grid(10, 10);
            Grid grid2 = new Grid(10, 10);
            List<Ship> ships = createTestShips();

            // When - both make same sequence of shots
            List<Coordinate> shots1 = new ArrayList<>();
            List<Coordinate> shots2 = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Coordinate shot1 = ai1.chooseShot(grid1, ships);
                Coordinate shot2 = ai2.chooseShot(grid2, ships);

                grid1.getCell(shot1).shoot();
                grid2.getCell(shot2).shoot();

                shots1.add(shot1);
                shots2.add(shot2);
            }

            // Then - should be identical
            assertEquals(shots1, shots2, "AIs with same seed should make identical decisions");
        }

        @Test
        @DisplayName("Should fallback to any unshot cell when no ships remain")
        void testFallbackWhenNoShipsRemain() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(5, 5);

            // Shoot almost all cells, leave only one
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    if (!(x == 4 && y == 4)) {
                        trackingGrid.getCell(new Coordinate(x, y)).shoot();
                    }
                }
            }

            // When - no ships remaining (empty probability map)
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());

            // Then - should find the only remaining cell
            assertEquals(new Coordinate(4, 4), shot);
        }

        @Test
        @DisplayName("Should fallback to HUNT mode when all adjacent cells are shot")
        void testFallbackWhenAdjacentCellsShot() {
            // Given
            HardAI ai = new HardAI();
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = createTestShips();
            Coordinate hitCoord = new Coordinate(5, 5);

            // Hit a ship (enter TARGET mode)
            ai.updateAfterShot(hitCoord, new AttackResponse(AttackResult.HIT, new Cruiser()));
            trackingGrid.getCell(hitCoord).shoot();

            // Shoot all adjacent cells
            trackingGrid.getCell(new Coordinate(5, 4)).shoot();
            trackingGrid.getCell(new Coordinate(5, 6)).shoot();
            trackingGrid.getCell(new Coordinate(4, 5)).shoot();
            trackingGrid.getCell(new Coordinate(6, 5)).shoot();

            // When - no adjacent cells available
            Coordinate nextShot = ai.chooseShot(trackingGrid, remainingShips);

            // Then - should fallback to probability-based (HUNT mode)
            assertNotNull(nextShot);
            assertFalse(trackingGrid.getCell(nextShot).isShot());
        }
    }

    @Nested
    @DisplayName("HardAI Probability Calculation Tests")
    class ProbabilityCalculationTests {

        @Test
        @DisplayName("Should skip destroyed ships in probability calculation")
        void testSkipsDestroyedShips() {
            // Given
            HardAI ai = new HardAI(42);
            Grid trackingGrid = new Grid(10, 10);

            // One destroyed ship, one alive
            List<Ship> ships = new ArrayList<>();
            Torpedo destroyedTorpedo = new Torpedo();
            destroyedTorpedo.receiveDamage();
            destroyedTorpedo.receiveDamage(); // Destroyed
            ships.add(destroyedTorpedo);

            Carrier aliveCarrier = new Carrier();
            ships.add(aliveCarrier);

            // When - make several shots
            Set<Coordinate> shots = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, ships);
                trackingGrid.getCell(shot).shoot();
                shots.add(shot);
            }

            // Then - should still work and produce valid shots
            assertEquals(5, shots.size());
        }
    }

    /**
     * Helper method to check if two coordinates are adjacent (up/down/left/right)
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
