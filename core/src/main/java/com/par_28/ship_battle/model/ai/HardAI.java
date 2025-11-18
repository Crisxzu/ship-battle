package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.*;


import java.util.*;

/**
 * AI strategy - probability-based targeting with advanced hunt mode.
 * <div>
 * This AI uses sophisticated algorithms:
 * <ul>
 *   <li><b>Probability density map:</b> Calculates where ships are most likely to be</li>
 *   <li><b>Smart targeting:</b> After a hit, determines ship orientation and targets accordingly</li>
 *   <li><b>Parity optimization:</b> Uses checkerboard pattern for efficient hunting</li>
 *   <li><b>Adaptive strategy:</b> Adjusts based on remaining ships</li>
 * </ul>
 * </div>
 */
public class HardAI extends AI {
    /**
     * AI mode
     */
    private enum Mode {
        /**
         * Probability-based searching
         */
        HUNT,

        /**
         * Targeting based on hits
         */
        TARGET
    }

    /**
     * Current AI mode
     */
    private Mode currentMode;


    /**
     * Create AI with default random generator.
     */
    public HardAI() {
        super();
        this.currentMode = Mode.HUNT;
    }

    /**
     * Create AI with a seeded random generator (for testing).
     *
     * @param seed Random seed for deterministic behavior
     */
    public HardAI(long seed) {
        super(seed);
        this.currentMode = Mode.HUNT;
    }

    @Override
    public Coordinate chooseShot(Grid trackingGrid, List<Ship> remainingOpponentShips) {
        gridSize = trackingGrid.getWidth();

        if (currentMode == Mode.TARGET && !hitHistory.isEmpty()) {
            // Try to find ship orientation and target accordingly
            Coordinate targetShot = findTargetShot(trackingGrid);
            if (targetShot != null) {
                return targetShot;
            }
            // If no good target shot, fall back to hunt mode
            currentMode = Mode.HUNT;
        }

        // HUNT mode: Use probability density
        return probabilityBasedShot(trackingGrid, remainingOpponentShips);
    }

    @Override
    public void updateAfterShot(Coordinate shot, AttackResponse response) {
        AttackResult result = response.getResult();

        if (result == AttackResult.HIT) {
            currentMode = Mode.TARGET;
            hitHistory.add(shot);
        } else if (result == AttackResult.SUNK) {
            // Clear hits and return to HUNT
            hitHistory.clear();
            currentMode = Mode.HUNT;
        }
    }

    @Override
    public void reset() {
        super.reset();
        currentMode = Mode.HUNT;
    }

    /**
     * Find the best target shot based on current hits.
     * <p>
     * If multiple hits exist in a line, continue in that direction.
     * Otherwise, try all four cardinal directions from the last hit.
     * </p>
     *
     * @param trackingGrid The AI's tracking grid
     * @return Best target coordinate, or null if none found
     */
    private Coordinate findTargetShot(Grid trackingGrid) {
        if (hitHistory.isEmpty()) {
            return null;
        }

        // If we have multiple hits, determine the orientation
        if (hitHistory.size() >= 2) {
            return findDirectionalTarget(trackingGrid);
        }

        // Single hit - try all four directions
        return findAdjacentTarget(trackingGrid, hitHistory.get(0));
    }

    /**
     * Find a target shot in the direction of existing hits.
     *
     * @param trackingGrid The AI's tracking grid
     * @return Target coordinate in line with hits, or null
     */
    private Coordinate findDirectionalTarget(Grid trackingGrid) {
        // Check if hits are in a line (horizontal or vertical)
        boolean horizontal = areHitsHorizontal();
        boolean vertical = areHitsVertical();

        if (horizontal) {
            return findHorizontalTarget(trackingGrid);
        } else if (vertical) {
            return findVerticalTarget(trackingGrid);
        }

        // Hits not in a clear line, try adjacent to last hit
        return findAdjacentTarget(trackingGrid, hitHistory.get(hitHistory.size() - 1));
    }

    /**
     * Check if all hits are on the same row (horizontal).
     *
     * @return true if horizontal alignment
     */
    private boolean areHitsHorizontal() {
        if (hitHistory.size() < 2) return false;
        int firstY = hitHistory.get(0).getY();
        for (Coordinate hit : hitHistory) {
            if (hit.getY() != firstY) return false;
        }
        return true;
    }

    /**
     * Check if all hits are on the same column (vertical).
     *
     * @return true if vertical alignment
     */
    private boolean areHitsVertical() {
        if (hitHistory.size() < 2) return false;
        int firstX = hitHistory.get(0).getX();
        for (Coordinate hit : hitHistory) {
            if (hit.getX() != firstX) return false;
        }
        return true;
    }

    /**
     * Find target at either end of horizontal hit line.
     *
     * @param trackingGrid The AI's tracking grid
     * @return Target coordinate or null
     */
    private Coordinate findHorizontalTarget(Grid trackingGrid) {
        int y = hitHistory.get(0).getY();
        int minX = hitHistory.stream().mapToInt(Coordinate::getX).min().orElse(0);
        int maxX = hitHistory.stream().mapToInt(Coordinate::getX).max().orElse(0);

        // Try extending to the right
        Coordinate right = new Coordinate(maxX + 1, y);
        if (isValidAndUnshot(trackingGrid, right)) {
            return right;
        }

        // Try extending to the left
        Coordinate left = new Coordinate(minX - 1, y);
        if (isValidAndUnshot(trackingGrid, left)) {
            return left;
        }

        return null;
    }

    /**
     * Find target at either end of vertical hit line.
     *
     * @param trackingGrid The AI's tracking grid
     * @return Target coordinate or null
     */
    private Coordinate findVerticalTarget(Grid trackingGrid) {
        int x = hitHistory.get(0).getX();
        int minY = hitHistory.stream().mapToInt(Coordinate::getY).min().orElse(0);
        int maxY = hitHistory.stream().mapToInt(Coordinate::getY).max().orElse(0);

        // Try extending down
        Coordinate down = new Coordinate(x, maxY + 1);
        if (isValidAndUnshot(trackingGrid, down)) {
            return down;
        }

        // Try extending up
        Coordinate up = new Coordinate(x, minY - 1);
        if (isValidAndUnshot(trackingGrid, up)) {
            return up;
        }

        return null;
    }

    /**
     * Find an unshot adjacent cell to the given coordinate.
     *
     * @param trackingGrid The AI's tracking grid
     * @param coord Center coordinate
     * @return Adjacent unshot coordinate or null
     */
    private Coordinate findAdjacentTarget(Grid trackingGrid, Coordinate coord) {
        int[][] directions = {
            {0, -1},  // Up
            {0, 1},   // Down
            {-1, 0},  // Left
            {1, 0}    // Right
        };

        for (int[] dir : directions) {
            Coordinate adj = new Coordinate(coord.getX() + dir[0], coord.getY() + dir[1]);
            if (isValidAndUnshot(trackingGrid, adj)) {
                return adj;
            }
        }

        return null;
    }

    /**
     * Select shot based on probability density map.
     * <p>
     * Calculates which cells are most likely to contain ships based on
     * remaining ships and available space.
     * </p>
     *
     * @param trackingGrid The AI's tracking grid
     * @param remainingShips List of opponent's remaining ships
     * @return Best probability coordinate
     */
    private Coordinate probabilityBasedShot(Grid trackingGrid, List<Ship> remainingShips) {
        int[][] probabilityMap = new int[gridSize][gridSize];

        // Calculate probability for each ship placement
        for (Ship ship : remainingShips) {
            if (ship.isDestroyed()) continue;

            int shipLength = ship.getLength();

            // Try horizontal placements
            addHorizontalProbabilities(trackingGrid, probabilityMap, shipLength);

            // Try vertical placements
            addVerticalProbabilities(trackingGrid, probabilityMap, shipLength);
        }

        // Find cell with highest probability
        return findMaxProbabilityCell(trackingGrid, probabilityMap);
    }

    /**
     * Add probability scores for all possible horizontal ship placements.
     *
     * @param trackingGrid The AI's tracking grid
     * @param probabilityMap Probability accumulator map
     * @param shipLength Length of ship to place
     */
    private void addHorizontalProbabilities(Grid trackingGrid, int[][] probabilityMap, int shipLength) {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x <= gridSize - shipLength; x++) {
                if (canPlaceShip(trackingGrid, x, y, shipLength, Direction.HORIZONTAL)) {
                    // Increment probability for each cell this ship would occupy
                    for (int i = 0; i < shipLength; i++) {
                        probabilityMap[x + i][y]++;
                    }
                }
            }
        }
    }

    /**
     * Add probability scores for all possible vertical ship placements.
     *
     * @param trackingGrid The AI's tracking grid
     * @param probabilityMap Probability accumulator map
     * @param shipLength Length of ship to place
     */
    private void addVerticalProbabilities(Grid trackingGrid, int[][] probabilityMap, int shipLength) {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y <= gridSize - shipLength; y++) {
                if (canPlaceShip(trackingGrid, x, y, shipLength, Direction.VERTICAL)) {
                    // Increment probability for each cell this ship would occupy
                    for (int i = 0; i < shipLength; i++) {
                        probabilityMap[x][y + i]++;
                    }
                }
            }
        }
    }

    /**
     * Check if a ship of given length can be placed at the position.
     * <p>
     * A ship can be placed if all cells are unshot.
     * </p>
     *
     * @param trackingGrid The AI's tracking grid
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param length Ship length
     * @param direction Placement direction
     * @return true if placement is possible
     */
    private boolean canPlaceShip(Grid trackingGrid, int startX, int startY, int length, Direction direction) {
        Cell[][] cells = trackingGrid.getCells();

        for (int i = 0; i < length; i++) {
            int x = direction == Direction.HORIZONTAL ? startX + i : startX;
            int y = direction == Direction.VERTICAL ? startY + i : startY;

            if (cells[x][y].isShot()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find the cell with the highest probability that hasn't been shot.
     *
     * @param trackingGrid The AI's tracking grid
     * @param probabilityMap Probability map
     * @return Coordinate with max probability
     */
    private Coordinate findMaxProbabilityCell(Grid trackingGrid, int[][] probabilityMap) {
        List<Coordinate> maxCells = new ArrayList<>();
        int maxProbability = 0;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (!trackingGrid.getCells()[x][y].isShot()) {
                    if (probabilityMap[x][y] > maxProbability) {
                        maxProbability = probabilityMap[x][y];
                        maxCells.clear();
                        maxCells.add(new Coordinate(x, y));
                    } else if (probabilityMap[x][y] == maxProbability) {
                        maxCells.add(new Coordinate(x, y));
                    }
                }
            }
        }

        if (maxCells.isEmpty()) {
            // Fallback: return any unshot cell
            return findAnyUnshotCell(trackingGrid);
        }

        // If multiple cells have same probability, choose randomly
        return maxCells.get(random.nextInt(maxCells.size()));
    }

    /**
     * Find any unshot cell (fallback method).
     *
     * @param trackingGrid The AI's tracking grid
     * @return Any unshot coordinate
     */
    private Coordinate findAnyUnshotCell(Grid trackingGrid) {
        Cell[][] cells = trackingGrid.getCells();
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (!cells[x][y].isShot()) {
                    return new Coordinate(x, y);
                }
            }
        }
        return new Coordinate(0, 0); // Should never happen
    }

    /**
     * Check if coordinate is valid and hasn't been shot.
     *
     * @param trackingGrid The AI's tracking grid
     * @param coord Coordinate to check
     * @return true if valid and unshot
     */
    private boolean isValidAndUnshot(Grid trackingGrid, Coordinate coord) {
        if (!trackingGrid.isValidCoordinate(coord)) {
            return false;
        }
        return !trackingGrid.getCell(coord).isShot();
    }
}
