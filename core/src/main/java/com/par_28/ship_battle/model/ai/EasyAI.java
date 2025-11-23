package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;

import java.util.*;

/**
 * AI strategy - completely random shots.
 * <p>
 * Selects random coordinates from the unshot cells.
 * </p>
 */
public class EasyAI extends AI {
    /**
     * Create AI with a default random generator.
     */
    public EasyAI() {
       super();
    }

    /**
     * Create AI with a seeded random generator (for testing).
     *
     * @param seed Random seed for deterministic behavior
     */
    public EasyAI(long seed) {
        super(seed);
    }

    @Override
    public Coordinate chooseShot(Grid trackingGrid, List<Ship> remainingOpponentShips) {
        List<Coordinate> availableShots = getAvailableShots(trackingGrid);

        if (availableShots.isEmpty()) {
            return new Coordinate(0, 0);
        }

        int randomIndex = random.nextInt(availableShots.size());

        System.out.println("randomIndex: " + randomIndex);
        return availableShots.get(randomIndex);
    }

    @Override
    public void updateAfterShot(Coordinate shot, AttackResponse response) {
        // Purely random so... No needed
    }
}
