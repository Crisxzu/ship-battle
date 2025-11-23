package com.par_28.ship_battle.model.ai;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.ai.enums.*;
import com.par_28.ship_battle.model.enums.*;
import com.par_28.ship_battle.model.exceptions.*;

import java.util.List;
import java.util.Random;

/**
 * AI-controlled player
 * <p>
 * This player uses an AI strategy to automatically select shots during gameplay.
 * The strategy can be configured by difficulty level (Easy, Medium, Hard).
 * </p>
 */
public class AIPlayer extends Player {
    /**
     * Strategy used for decision-making
     */
    private final AIStrategy strategy;

    /**
     * Difficulty level of this AI
     */
    private final AIDifficulty difficulty;

    /**
     * Create an AI player with difficulty-based strategy selection.
     *
     * @param name AI player's display name
     * @param gridSize Size of the game grid
     * @param difficulty Difficulty level (determines which strategy to use)
     */
    public AIPlayer(String name, int gridSize, AIDifficulty difficulty) {
        super(name, gridSize);
        this.difficulty = difficulty;
        this.strategy = createStrategyFromDifficulty(difficulty);
    }

    /**
     * Create the appropriate strategy based on difficulty level.
     *
     * @param difficulty Desired difficulty
     * @return Corresponding AI strategy
     */
    private static AIStrategy createStrategyFromDifficulty(AIDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> new EasyAI();
            case MEDIUM -> new MediumAI();
            case HARD -> new HardAI();
        };
    }

    /**
     * Chooses the next shot coordinate based on its strategy.
     *
     * @param opponentShips List of opponent's ships (to check which are still alive)
     * @return Coordinate selected by the AI to attack
     */
    public Coordinate chooseShot(List<Ship> opponentShips) {
        return strategy.chooseShot(getTrackingGrid(), opponentShips);
    }

    /**
     * Update the AI's internal state after a shot is made.
     *
     * @param shot The coordinate that was attacked
     * @param response The result of the attack
     */
    public void notifyAttackResult(Coordinate shot, AttackResponse response) {
        strategy.updateAfterShot(shot, response);
    }

    /**
     * Reset the AI's strategy state for a new game.
     */
    public void resetStrategy() {
        strategy.reset();
    }

    /**
     * Get the AI's difficulty level.
     *
     * @return Difficulty level
     */
    public AIDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Get the AI's strategy.
     *
     * @return AI strategy
     */
    public AIStrategy getStrategy() {
        return strategy;
    }

    @Override
    public boolean isAI() {
        return true;
    }

    /**
     * Generate a random AI player name from a predefined list.
     *
     * @return Randomly selected AI name
     */
    static public String getRandomName() {
        String[] aiNames = {
            "Tanya",
            "Erza",
            "Azusa",
            "Rika",
            "Kanna",
            "Konata"
        };

        Random random = new Random();
        int i = random.nextInt(aiNames.length);

        return aiNames[i];
    }
}
