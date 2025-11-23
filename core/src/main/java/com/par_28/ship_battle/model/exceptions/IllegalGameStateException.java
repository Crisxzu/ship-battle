package com.par_28.ship_battle.model.exceptions;

/**
 * Exception thrown when the game is in an illegal state.
 */
public class IllegalGameStateException extends RuntimeException {
    /**
     * Constructor for IllegalGameStateException with a custom message.
     * 
     * @param message  Error message describing the illegal game state
     */
    public IllegalGameStateException(String message) {
        super(message);
    }
}
