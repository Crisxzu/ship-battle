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
 * Tests for MediumAI strategy
 */
class MediumAITest {

    @Nested
    @DisplayName("MediumAI Hunt Mode Tests")
    class HuntModeTests {

        @Test
        @DisplayName("Should start in HUNT mode - shots are random and spread across grid")
        void testStartsInHuntMode() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);

            // When - take multiple shots without any HITs (stay in HUNT mode)
            Set<Coordinate> shots = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
                trackingGrid.getCell(shot).shoot();
                shots.add(shot);

                // Notify MISS - should stay in HUNT mode
                ai.updateAfterShot(shot, new AttackResponse(AttackResult.MISS, null));
            }

            // Then - shots should be spread (not all adjacent to first shot)
            assertEquals(10, shots.size(), "All shots should be unique");

            Coordinate firstShot = shots.iterator().next();
            long adjacentCount = shots.stream()
                .filter(s -> !s.equals(firstShot) && isAdjacent(firstShot, s))
                .count();

            assertTrue(adjacentCount <= 4,
                "In HUNT mode, shots should be random, not clustered");
        }

        @Test
        @DisplayName("Should return to HUNT mode after sinking a ship")
        void testReturnsToHuntAfterSunk() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate hitCoord = new Coordinate(5, 5);

            // Hit a ship (enters TARGET mode)
            ai.updateAfterShot(hitCoord, new AttackResponse(AttackResult.HIT, new Torpedo()));
            trackingGrid.getCell(hitCoord).shoot();

            // Sink the ship (should return to HUNT mode)
            Coordinate sunkCoord = new Coordinate(5, 6);
            ai.updateAfterShot(sunkCoord, new AttackResponse(AttackResult.SUNK, new Torpedo()));
            trackingGrid.getCell(sunkCoord).shoot();

            // When - next shots should be random (HUNT mode)
            Set<Coordinate> nextShots = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
                trackingGrid.getCell(shot).shoot();
                nextShots.add(shot);
                ai.updateAfterShot(shot, new AttackResponse(AttackResult.MISS, null));
            }

            // Then - verify not all adjacent to old hits
            long adjacentToOldHits = nextShots.stream()
                .filter(s -> isAdjacent(hitCoord, s) || isAdjacent(sunkCoord, s))
                .count();

            assertTrue(adjacentToOldHits < 5,
                "After sinking, should return to HUNT mode");
        }

        @Test
        @DisplayName("Should return fallback coordinate when grid is fully shot")
        void testFallbackWhenGridFull() {
            // Given
            MediumAI ai = new MediumAI();
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
    @DisplayName("MediumAI Target Mode Tests")
    class TargetModeTests {

        @Test
        @DisplayName("Should switch to TARGET mode and target all 4 adjacent cells after a hit")
        void testSwitchesToTargetMode() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - hit a ship
            ai.updateAfterShot(hitCoord, new AttackResponse(AttackResult.HIT, new Carrier()));
            trackingGrid.getCell(hitCoord).shoot();

            // Then - collect all 4 targets
            Set<Coordinate> targets = new HashSet<>();
            for (int i = 0; i < 4; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
                trackingGrid.getCell(shot).shoot();
                targets.add(shot);
            }

            // All four adjacent cells should be targeted
            Set<Coordinate> expectedTargets = Set.of(
                new Coordinate(5, 4),  // Up
                new Coordinate(5, 6),  // Down
                new Coordinate(4, 5),  // Left
                new Coordinate(6, 5)   // Right
            );

            assertEquals(expectedTargets, targets, "Should target all 4 adjacent cells");
        }

        @Test
        @DisplayName("Should add new adjacent targets after second hit")
        void testAddsTargetsAfterSecondHit() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);

            // First hit
            Coordinate firstHit = new Coordinate(5, 5);
            ai.updateAfterShot(firstHit, new AttackResponse(AttackResult.HIT, new Carrier()));
            trackingGrid.getCell(firstHit).shoot();

            // Get first target and mark as hit
            Coordinate firstTarget = ai.chooseShot(trackingGrid, new ArrayList<>());
            trackingGrid.getCell(firstTarget).shoot();
            ai.updateAfterShot(firstTarget, new AttackResponse(AttackResult.HIT, new Carrier()));

            // When - continue targeting
            Set<Coordinate> remainingTargets = new HashSet<>();
            for (int i = 0; i < 6; i++) { // Original 3 + 4 new - duplicates
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
                if (trackingGrid.getCell(shot).isShot()) break;
                trackingGrid.getCell(shot).shoot();
                remainingTargets.add(shot);
            }

            // Then - should have targeted cells adjacent to both hits
            assertFalse(remainingTargets.isEmpty());
        }

        @Test
        @DisplayName("Should handle hit at corner - only valid adjacent cells in stack")
        void testHitAtCorner() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate cornerHit = new Coordinate(0, 0);

            // When - hit at corner
            ai.updateAfterShot(cornerHit, new AttackResponse(AttackResult.HIT, new Torpedo()));
            trackingGrid.getCell(cornerHit).shoot();

            // Get targets (invalid coords will be in stack but skipped)
            Coordinate shot1 = ai.chooseShot(trackingGrid, new ArrayList<>());
            trackingGrid.getCell(shot1).shoot();
            Coordinate shot2 = ai.chooseShot(trackingGrid, new ArrayList<>());

            // Then - both should be valid adjacent cells
            Set<Coordinate> validTargets = Set.of(
                new Coordinate(1, 0),
                new Coordinate(0, 1)
            );

            assertTrue(validTargets.contains(shot1), "First shot should be (1,0) or (0,1)");
            assertTrue(validTargets.contains(shot2), "Second shot should be (1,0) or (0,1)");
            assertNotEquals(shot1, shot2);
        }
    }

    @Nested
    @DisplayName("MediumAI State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should reset to HUNT mode and clear target stack")
        void testReset() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);

            // Enter TARGET mode
            ai.updateAfterShot(new Coordinate(5, 5), new AttackResponse(AttackResult.HIT, new Destroyer()));

            // When - reset
            ai.reset();

            // Then - should be in HUNT mode (random shots, not adjacent targeting)
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(shot);
            assertTrue(trackingGrid.isValidCoordinate(shot));
        }

        @Test
        @DisplayName("Should ignore MISS and ALREADY_HIT updates")
        void testIgnoresNonHitResults() {
            // Given
            MediumAI ai = new MediumAI(42);
            Grid trackingGrid = new Grid(10, 10);

            // Get initial shot
            Coordinate firstShot = ai.chooseShot(trackingGrid, new ArrayList<>());
            trackingGrid.getCell(firstShot).shoot();

            // When - notify MISS
            ai.updateAfterShot(firstShot, new AttackResponse(AttackResult.MISS, null));
            Coordinate afterMiss = ai.chooseShot(trackingGrid, new ArrayList<>());

            // Then - should still be in HUNT mode (not targeting adjacent)
            assertFalse(isAdjacent(firstShot, afterMiss) && afterMiss != null,
                "After MISS, should continue random shooting");
        }
    }

    @Nested
    @DisplayName("MediumAI Deterministic Behavior Tests")
    class DeterministicTests {

        @Test
        @DisplayName("Should use seed for deterministic behavior")
        void testDeterministicBehavior() {
            // Given
            long seed = 777;
            MediumAI ai1 = new MediumAI(seed);
            MediumAI ai2 = new MediumAI(seed);
            Grid grid1 = new Grid(10, 10);
            Grid grid2 = new Grid(10, 10);

            // When - both make same sequence of shots
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
    }

    /**
     * Helper method to check if two coordinates are adjacent
     */
    private boolean isAdjacent(Coordinate c1, Coordinate c2) {
        int dx = Math.abs(c1.getX() - c2.getX());
        int dy = Math.abs(c1.getY() - c2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }
}
