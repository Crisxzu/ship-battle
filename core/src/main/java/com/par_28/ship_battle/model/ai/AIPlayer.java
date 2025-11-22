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
            default -> new EasyAI();
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
     * Randomly place a single ship on the AI's grid.
     *
     * @param ship Ship to place
     * @param random Random number generator
     * @param maxAttempts Maximum number of placement attempts before giving up
     * @return true if ship was successfully placed, false otherwise
     */
    public boolean placeShipRandomly(Ship ship, Random random, int maxAttempts) {
        int gridSize = getGrid().getWidth();
        int attempts = 0;

        while (attempts < maxAttempts) {
            int x = random.nextInt(gridSize);
            int y = random.nextInt(gridSize);
            Coordinate coord = new Coordinate(x, y);

            Direction direction = random.nextBoolean() ? Direction.HORIZONTAL : Direction.VERTICAL;

            try {
                placeShipOnGrid(ship, coord, direction);
                return true;
            } catch (InvalidCoordinateException | ShipPlacementException e) {
                attempts++;
            }
        }

        return false;
    }

    /**
     * Automatically place all ships from a list randomly on the AI's grid.
     *
     * @param ships List of ships to add and place
     * @return true if all ships were successfully placed, false otherwise
     */
    public boolean placeShipsRandomly(List<Ship> ships) {
        Random random = new Random();

        for (Ship ship : ships) {
            addShip(ship);
            boolean placed = placeShipRandomly(ship, random, 1000);

            if (!placed) {
                return false;
            }
        }

        return true;
    }

    /**
     * Automatically place all ships from a list with a custom seed.
     * <p>
     * Useful for deterministic placement in tests.
     * </p>
     *
     * @param ships List of ships to add and place
     * @param seed Random seed for deterministic placement
     * @return true if all ships were successfully placed, false otherwise
     */
    public boolean placeShipsRandomly(List<Ship> ships, long seed) {
        Random random = new Random(seed);

        for (Ship ship : ships) {
            addShip(ship);
            boolean placed = placeShipRandomly(ship, random, 1000);

            if (!placed) {
                return false;
            }
        }

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
