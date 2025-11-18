package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;

import java.util.*;

/**
 * Base class for AI
 */
public abstract class AI implements AIStrategy {
    /**
     * Random number generator
     */
    protected final Random random;

    /**
     * History of successful hits
     */
    protected final List<Coordinate> hitHistory;

    /**
     * Grid size
     */
    protected int gridSize;

    /**
     * Create AI with a default random generator.
     */
    public AI() {
        this.random = new Random();
        this.hitHistory = new ArrayList<>();
        this.gridSize = 10; // By default, but will be updated
    }

    /**
     * Create AI with a seeded random generator (for testing).
     *
     * @param seed Random seed for deterministic behavior
     */
    public AI(long seed) {
        this.random = new Random(seed);
        this.hitHistory = new ArrayList<>();
        this.gridSize = 10; // By default, but will be updated
    }

    /**
     * Get all coordinates that haven't been shot yet.
     *
     * @param trackingGrid The AI's tracking grid
     * @return List of available coordinates
     */
    protected List<Coordinate> getAvailableShots(Grid trackingGrid) {
        List<Coordinate> available = new ArrayList<>();
        Cell[][] cells = trackingGrid.getCells();

        for (int x = 0; x < trackingGrid.getWidth(); x++) {
            for (int y = 0; y < trackingGrid.getHeight(); y++) {
                if (!cells[x][y].isShot()) {
                    available.add(new Coordinate(x, y));
                }
            }
        }

        return available;
    }

    @Override
    public void reset() {
        hitHistory.clear();
        this.gridSize = 10; // By default, but will be updated
    }
}
