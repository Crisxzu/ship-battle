package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.*;

import java.util.*;

/**
 * AI strategy - intelligent hunting after hits.
 * <div>
 * This AI operates in two modes:
 * <ul>
 *   <li><b>HUNT mode:</b> Randomly selects unshot cells until a hit is made</li>
 *   <li><b>TARGET mode:</b> After hitting a ship, targets adjacent cells to sink it</li>
 * </ul>
 * Once a ship is sunk, it returns to HUNT mode.
 * </div>
 */
public class MediumAI extends AI {
    /**
     * AI mode
     */
    private enum Mode {
        /**
         * Random searching
         */
        HUNT,
        /**
         * Targeting adjacent cells after a hit
         */
        TARGET
    }

    /**
     * Current AI mode
     */
    private Mode currentMode;

    /**
     * Stack of coordinates to target
     */
    private final Stack<Coordinate> targetStack;

    /**
     * Create AI with default random generator.
     */
    public MediumAI() {
        super();
        this.currentMode = Mode.HUNT;
        this.targetStack = new Stack<>();
    }

    /**
     * Create AI with a seeded random generator (for testing).
     *
     * @param seed Random seed for deterministic behavior
     */
    public MediumAI(long seed) {
        super(seed);
        this.currentMode = Mode.HUNT;
        this.targetStack = new Stack<>();
    }

    @Override
    public Coordinate chooseShot(Grid trackingGrid, List<Ship> remainingOpponentShips) {
        if (currentMode == Mode.TARGET && !targetStack.isEmpty()) {
            Coordinate targetCoord = targetStack.pop();

            // Check because it possible to have invalid coordinate
            while(!trackingGrid.isValidCoordinate(targetCoord)) {
                targetCoord = targetStack.pop();
            }

            return targetCoord;
        }

        return huntModeShot(trackingGrid);
    }

    @Override
    public void updateAfterShot(Coordinate shot, AttackResponse response) {
        AttackResult result = response.getResult();

        if (result == AttackResult.HIT) {
            currentMode = Mode.TARGET;
            hitHistory.add(shot);
            addAdjacentTargets(shot);
        } else if (result == AttackResult.SUNK) {
            currentMode = Mode.HUNT;
            targetStack.clear();
            hitHistory.clear();
        }

        // For MISS or ALREADY_HIT, continue current mode
    }

    @Override
    public void reset() {
        super.reset();
        currentMode = Mode.HUNT;
        targetStack.clear();
    }

    /**
     * Select a random unshot coordinate (HUNT mode).
     *
     * @param trackingGrid The AI's tracking grid
     * @return Random available coordinate
     */
    private Coordinate huntModeShot(Grid trackingGrid) {
        List<Coordinate> availableShots = getAvailableShots(trackingGrid);

        if (availableShots.isEmpty()) {
            return new Coordinate(0, 0);
        }

        int randomIndex = random.nextInt(availableShots.size());
        return availableShots.get(randomIndex);
    }

    /**
     * Add adjacent coordinates (up, down, left, right) to the target stack.
     * <p>
     * Only adds coordinates haven't been shot yet.
     * </p>
     *
     * @param coord The coordinate that was just hit
     */
    private void addAdjacentTargets(Coordinate coord) {
        int x = coord.getX();
        int y = coord.getY();

        int[][] directions = {
            {0, -1},  // Up
            {0, 1},   // Down
            {-1, 0},  // Left
            {1, 0}    // Right
        };

        for (int[] dir : directions) {
            Coordinate adjacent = new Coordinate(x + dir[0], y + dir[1]);

            if (!targetStack.contains(adjacent) && !hitHistory.contains(adjacent)) {
                targetStack.add(adjacent);
            }
        }
    }
}
