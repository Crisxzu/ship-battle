package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.AttackResult;

/**
 * Represents the response to an attack on a ship.
 * Contains the result of the attack and the targeted ship.
 * 
 * @author James
 * @version 1.0
 * @since 1.0
 */
public class AttackResponse {
    
    /**
     * The result of the attack (MISS, HIT, SUNK, etc.)
     */
    private final AttackResult result;
    
    /**
     * The ship that was targeted by the attack
     */
    private final Ship ship;
    
    /**
     * Constructor for AttackResponse.
     * 
     * @param result the result of the attack
     * @param ship the ship that was targeted
     */
    public AttackResponse(AttackResult result, Ship ship) {
        this.result = result;
        this.ship = ship;
    }
    
    /**
     * Get the result of the attack.
     * 
     * @return the attack result
     */
    public AttackResult getResult() {
        return result;
    }
    
    /**
     * Get the ship that was targeted by the attack.
     * 
     * @return the targeted ship
     */
    public Ship getShip() {
        return ship;
    }
    
    /**
     * Check if the attack was a hit.
     * 
     * @return true if the attack hit a ship, false otherwise
     */
    public boolean isHit() {
        return result == AttackResult.HIT || result == AttackResult.SUNK;
    }
}
