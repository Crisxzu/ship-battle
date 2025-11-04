package com.par_28.ship_battle.model;

/**
 * Represents an aircraft carrier (Carrier) in the game.
 *
 * The aircraft carrier is the largest ship in the game, with a length of 5 cells.
 *
 * @author James
 * @version 1.0
 * @since 1.0
 * @see Ship
 */

public class Carrier extends Ship {

    /**
     * Constructor for the Carrier class.
     */
    public Carrier() {
        super("Carrier", 5);
    }
    
}
