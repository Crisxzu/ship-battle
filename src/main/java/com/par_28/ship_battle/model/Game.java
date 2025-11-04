package com.par_28.ship_battle.model;


import com.par_28.ship_battle.model.enums.GameState;

/**
 * Class representing the game state and logic
 *<p>
 * A game consists of two players who take turns attacking each other's ships on a grid.
 * </p>
 * @see com.par_28.ship_battle.model.Player
 * @see com.par_28.ship_battle.model.Coordinate
 * @see com.par_28.ship_battle.model.AttackResponse
 * @see com.par_28.ship_battle.model.enums.GameState
 */
public class Game {
    /**
     * Players in the game
     */
    private final Player player1;
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
     * Constructor to initialize game with two players
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
     * @throws IllegalStateException if game is already started or players have no ships
     */
    public void start() throws IllegalStateException {
        if(gameState != GameState.SETUP) {
            throw new IllegalStateException("Game is already started");
        }

        if(player1.getShips().isEmpty() || player2.getShips().isEmpty()) {
            throw new IllegalStateException("Both players must have ships to start the game");
        }

        gameState = GameState.PLAYER1_TURN;
        currentPlayer = player1;
    }

    /**
     * Play a turn for the current player by attacking the opponent at the given coordinate
     * 
     * If the opponent is defeated after the attack, the game state is set to GAME_OVER.
     * Otherwise, the turn switches to the other player.
     * The current player records the result of the attack on their tracking grid.
     * 
     * @param coord Coordinate to attack
     * @return AttackResponse response of the attack with hit/miss and ship info
     * @throws IllegalStateException if game is already over
     */
    public AttackResponse playTurn(Coordinate coord) {
        if(gameState == GameState.GAME_OVER) {
            throw new IllegalStateException("Game is already over");
        }

        Player opponent = getOpponent();

        AttackResponse response = opponent.receiveAttack(coord);
        currentPlayer.recordAttack(coord, response);

        if(opponent.isDead()) {
            gameState = GameState.GAME_OVER;
        }
        else {
            switchPlayer();
        }

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
}
