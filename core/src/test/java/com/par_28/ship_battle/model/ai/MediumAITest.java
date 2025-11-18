package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MediumAI strategy
 */
class MediumAITest {

    @Nested
    @DisplayName("MediumAI Hunt Mode Tests")
    class HuntModeTests {

        @Test
        @DisplayName("Should start in HUNT mode with random shots")
        void testStartsInHuntMode() {
            // Given
            MediumAI ai = new MediumAI(42);
            Grid trackingGrid = new Grid(10, 10);
            List<Ship> remainingShips = new ArrayList<>();

            // When
            Coordinate shot = ai.chooseShot(trackingGrid, remainingShips);

            // Then
            assertNotNull(shot);
            assertTrue(trackingGrid.isValidCoordinate(shot));
        }

        @Test
        @DisplayName("Should return to HUNT mode after sinking a ship")
        void testReturnsToHuntAfterSunk() {
            // Given
            MediumAI ai = new MediumAI(42);
            Grid trackingGrid = new Grid(10, 10);
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - hit a ship
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Torpedo());
            ai.updateAfterShot(hitCoord, hitResponse);

            // Then - sink the ship
            AttackResponse sunkResponse = new AttackResponse(AttackResult.SUNK, new Torpedo());
            ai.updateAfterShot(new Coordinate(5, 6), sunkResponse);

            // AI should be back in HUNT mode (can't directly test, but check behavior)
            trackingGrid.getCell(hitCoord).shoot();
            trackingGrid.getCell(new Coordinate(5, 6)).shoot();

            Coordinate nextShot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(nextShot);
        }
    }

    @Nested
    @DisplayName("MediumAI Target Mode Tests")
    class TargetModeTests {

        @Test
        @DisplayName("Should switch to TARGET mode after a hit")
        void testSwitchesToTargetMode() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - hit a ship
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Cruiser());
            ai.updateAfterShot(hitCoord, hitResponse);

            // Mark the hit coordinate as shot
            trackingGrid.getCell(hitCoord).shoot();

            // Then - next shot should be adjacent to the hit
            Coordinate nextShot = ai.chooseShot(trackingGrid, new ArrayList<>());

            assertNotNull(nextShot);
            assertTrue(isAdjacent(hitCoord, nextShot),
                "After a hit, AI should target adjacent cells. Hit: " + hitCoord + ", Next: " + nextShot);
        }

        @Test
        @DisplayName("Should target all four adjacent cells after a hit")
        void testTargetsAllAdjacentCells() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate hitCoord = new Coordinate(5, 5);

            // When - hit a ship
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Carrier());
            ai.updateAfterShot(hitCoord, hitResponse);
            trackingGrid.getCell(hitCoord).shoot();

            // Then - collect all suggested targets
            List<Coordinate> targets = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
                trackingGrid.getCell(shot).shoot();
                targets.add(shot);
            }

            // All four adjacent cells should be targeted
            assertTrue(targets.contains(new Coordinate(5, 4)), "Should target up");
            assertTrue(targets.contains(new Coordinate(5, 6)), "Should target down");
            assertTrue(targets.contains(new Coordinate(4, 5)), "Should target left");
            assertTrue(targets.contains(new Coordinate(6, 5)), "Should target right");
        }

        @Test
        @DisplayName("Should handle multiple hits and continue targeting")
        void testMultipleHits() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate firstHit = new Coordinate(5, 5);
            Coordinate secondHit = new Coordinate(5, 6);

            // When - first hit
            AttackResponse hitResponse1 = new AttackResponse(AttackResult.HIT, new Carrier());
            ai.updateAfterShot(firstHit, hitResponse1);
            trackingGrid.getCell(firstHit).shoot();

            // Get first target shot
            Coordinate firstTarget = ai.chooseShot(trackingGrid, new ArrayList<>());
            trackingGrid.getCell(firstTarget).shoot();

            // Second hit
            AttackResponse hitResponse2 = new AttackResponse(AttackResult.HIT, new Carrier());
            ai.updateAfterShot(secondHit, hitResponse2);

            // Then - should continue targeting
            Coordinate nextShot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(nextShot);
        }
    }

    @Nested
    @DisplayName("MediumAI State Management Tests")
    class StateManagementTests {

        @Test
        @DisplayName("Should reset successfully")
        void testReset() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);

            // Hit a ship to enter TARGET mode
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Destroyer());
            ai.updateAfterShot(new Coordinate(5, 5), hitResponse);

            // When - reset
            ai.reset();

            // Then - should be back in HUNT mode (random behavior)
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(shot);
            assertTrue(trackingGrid.isValidCoordinate(shot));
        }

        @Test
        @DisplayName("Should ignore MISS updates")
        void testIgnoresMiss() {
            // Given
            MediumAI ai = new MediumAI(42);
            Grid trackingGrid = new Grid(10, 10);
            Coordinate missCoord = new Coordinate(3, 3);

            // When - notify of a miss
            AttackResponse missResponse = new AttackResponse(AttackResult.MISS, null);
            ai.updateAfterShot(missCoord, missResponse);

            // Then - should continue in HUNT mode (random shots)
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(shot);
        }

        @Test
        @DisplayName("Should handle ALREADY_HIT gracefully")
        void testHandlesAlreadyHit() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate coord = new Coordinate(5, 5);

            // When
            AttackResponse alreadyHitResponse = new AttackResponse(AttackResult.ALREADY_HIT, new Cruiser());
            ai.updateAfterShot(coord, alreadyHitResponse);

            // Then - should continue working
            Coordinate shot = ai.chooseShot(trackingGrid, new ArrayList<>());
            assertNotNull(shot);
        }
    }

    @Nested
    @DisplayName("MediumAI Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle hit near grid edge")
        void testHitNearEdge() {
            // Given
            MediumAI ai = new MediumAI();
            Grid trackingGrid = new Grid(10, 10);
            Coordinate edgeHit = new Coordinate(0, 0); // Top-left corner

            // When - hit at corner
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, new Torpedo());
            ai.updateAfterShot(edgeHit, hitResponse);
            trackingGrid.getCell(edgeHit).shoot();

            // Then - should only target valid adjacent cells (right and down)
            Coordinate shot1 = ai.chooseShot(trackingGrid, new ArrayList<>());
            trackingGrid.getCell(shot1).shoot();
            Coordinate shot2 = ai.chooseShot(trackingGrid, new ArrayList<>());

            assertTrue(trackingGrid.isValidCoordinate(shot1));
            assertTrue(trackingGrid.isValidCoordinate(shot2));
        }

        @Test
        @DisplayName("Should use seed for deterministic behavior")
        void testDeterministicBehavior() {
            // Given
            long seed = 777;
            MediumAI ai1 = new MediumAI(seed);
            MediumAI ai2 = new MediumAI(seed);
            Grid grid1 = new Grid(10, 10);
            Grid grid2 = new Grid(10, 10);

            // When - both make same sequence of actions
            Coordinate shot1a = ai1.chooseShot(grid1, new ArrayList<>());
            Coordinate shot2a = ai2.chooseShot(grid2, new ArrayList<>());

            // Then - should be identical
            assertEquals(shot1a, shot2a);
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
}
