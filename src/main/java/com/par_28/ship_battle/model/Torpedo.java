package com.par_28.ship_battle.model;

/**
 * Represents a torpedo boat (Torpedo) in the game.
 * The torpedo boat is the smallest ship in the game, with a length of 2 cells.
 * @author James
 * @version 1.0
 * @since 1.0
 * @see Ship
 */

public class Torpedo extends Ship {
    
    /**
     * Constructor for the Torpedo class.
     */
    public Torpedo() {
        super("Torpedo", 2);
    }
    
}
