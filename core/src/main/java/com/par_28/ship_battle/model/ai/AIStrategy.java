package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.model.Grid;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.AttackResponse;

import java.util.List;

/**
 * Base for AI shooting strategies.
 * <p>
 * Each implementation represents a different difficulty level or approach
 * to selecting target coordinates during the game.
 * </p>
 */
public interface AIStrategy {
    /**
     * Choose the next coordinate to shoot at based on the AI's tracking grid
     * and knowledge of remaining opponent ships.
     *
     * @param trackingGrid The AI's tracking grid showing previous shot results
     * @param remainingOpponentShips List of ships the opponent still has (not sunk)
     * @return Coordinate to attack
     */
    Coordinate chooseShot(Grid trackingGrid, List<Ship> remainingOpponentShips);

    /**
     * Update the AI's internal state based on the result of the last shot.
     * <p>
     * This allows the AI to learn from each attack and adjust its strategy.
     * </p>
     *
     * @param shot The coordinate that was just attacked
     * @param response The result of the attack
     */
    void updateAfterShot(Coordinate shot, AttackResponse response);

    /**
     * Reset the AI's internal state for a new game.
     */
    void reset();
}
