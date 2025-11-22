package com.par_28.ship_battle.model;


import com.par_28.ship_battle.model.enums.GameState;
import com.par_28.ship_battle.model.enums.PowerType;
import com.par_28.ship_battle.model.exceptions.*;

/**
 * Game state and logic
 *
 * @see Player
 * @see Coordinate
 * @see AttackResponse
 * @see GameState
 */
public class Game {
    /**
     * Player 1
     */
    private final Player player1;
    /**
     * Player 2
     */
    private final Player player2;

    /**
     * Current player whose turn it is
     */
    private Player currentPlayer;

    /**
     * Current state of the game
     */
    private GameState gameState;

    /**
     * Number of turns played
     */
    private int nbTurns = 0;

    /**
     * Initialize game
     *
     * @param player1 First player
     * @param player2 Second player
     */
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameState = GameState.SETUP;
    }

    /**
     * Start the game if both players have ships placed and game is not already started
     *
     * @throws IllegalGameStateException if game is already started or players have no ships
     */
    public void start() throws IllegalGameStateException {
        if(gameState != GameState.SETUP) {
            throw new IllegalGameStateException("Game is already started");
        }

        if(player1.getShips().isEmpty() || player2.getShips().isEmpty()) {
            throw new IllegalGameStateException("Both players must have ships to start the game");
        }

        gameState = GameState.PLAYER1_TURN;
        currentPlayer = player1;
    }

    /**
     * Play a turn for the current player by attacking the opponent at the given coordinate
     *
     * <p>
     * After the attack, if the opponent has no remaining ships, the game state is updated to GAME_OVER.
     * Otherwise, the turn switches to the other player.
     * The current player records the result of the attack on their tracking grid.
     * Each call to this method increments the turn counter.
     * </p>
     *
     * @param coord Coordinate to attack
     * @return AttackResponse response of the attack with hit/miss and ship info
     * @throws IllegalGameStateException if game is already over
     * @throws InvalidCoordinateException if the coordinate is invalid
     */
    public AttackResponse playTurn(Coordinate coord) throws IllegalGameStateException, InvalidCoordinateException {
        return playTurn(coord, PowerType.NORMAL);
    }

    /**
     * Play a turn for the current player using a specific power type
     *
     * <p>
     * Supports NORMAL attacks, BOMB attacks (3x3 area), and RADAR scans (detection only).
     * After the attack, if the opponent has no remaining ships, the game state is updated to GAME_OVER.
     * For RADAR scans, the turn does not switch.
     * For other attacks, the turn switches to the other player.
     * The current player records the result of the attack on their tracking grid.
     * Each call to this method increments the turn counter.
     * </p>
     *
     * @param coord Coordinate to attack or scan
     * @param powerType Type of power to use (NORMAL, BOMB, RADAR)
     * @return AttackResponse response of the attack with hit/miss and ship info
     * @throws IllegalGameStateException if game is already over or player doesn't have required charges
     * @throws InvalidCoordinateException if the coordinate is invalid
     */
    public AttackResponse playTurn(Coordinate coord, PowerType powerType) throws IllegalGameStateException, InvalidCoordinateException {
        if(gameState == GameState.GAME_OVER) {
            throw new IllegalGameStateException("Game is already over");
        }

        Player opponent = getOpponent();
        AttackResponse response;

        switch (powerType) {
            case BOMB:
                if (!currentPlayer.hasBombCharges()) {
                    throw new IllegalGameStateException("No bomb charges available");
                }
                currentPlayer.useBombCharge();
                response = opponent.getGrid().receiveBombAttack(coord);

                // Record primary hit and additional hits on tracking grid
                currentPlayer.recordAttack(coord, response);
                for (AttackResponse additionalHit : response.getAdditionalHits()) {
                    currentPlayer.recordAttack(additionalHit.getCoordinate(), additionalHit);
                }
                break;

            case RADAR:
                if (!currentPlayer.hasRadarCharges()) {
                    throw new IllegalGameStateException("No radar charges available");
                }
                currentPlayer.useRadarCharge();
                response = opponent.getGrid().radarScan(coord);
                // Radar scan doesn't switch turn, doesn't increment turn counter
                return response;

            case NORMAL:
            default:
                response = opponent.receiveAttack(coord);
                currentPlayer.recordAttack(coord, response);
                break;
        }

        if(opponent.isDead()) {
            gameState = GameState.GAME_OVER;
        }
        else {
            switchPlayer();
        }

        nbTurns++;

        return response;
    }

    /**
     * Switch the current player to the other player
     */
    public void switchPlayer() {
        if(gameState == GameState.PLAYER1_TURN) {
            gameState = GameState.PLAYER2_TURN;
            currentPlayer = player2;
        }
        else {
            gameState = GameState.PLAYER1_TURN;
            currentPlayer = player1;
        }
    }

    /**
     * Check if the game is over
     *
     * @return boolean true if game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameState == GameState.GAME_OVER;
    }

    /**
     * Get the winner of the game if it's over
     *
     * @return Player winner player who won the game, null if game is not over
     */
    public Player getWinner() {
        if(!isGameOver()) {
            return null;
        }

        if(player1.isDead()) {
            return player2;
        }
        else {
            return player1;
        }
    }

    /**
     * Get the current player
     *
     * @return Player current player whose turn it is
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Get the opponent of the current player
     *
     * @return Player opponent player who is not the current player
     */
    public Player getOpponent() {
        return currentPlayer == player1 ? player2 : player1;
    }

    /**
     * Get the current game state
     *
     * @return GameState current state of the game
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Get the number of turns played
     *
     * @return int number of turns played
     */
    public int getNbTurns() {
        return nbTurns;
    }
}
