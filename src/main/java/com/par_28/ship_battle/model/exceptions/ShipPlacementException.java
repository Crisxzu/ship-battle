package com.par_28.ship_battle.model.exceptions;

import com.par_28.ship_battle.model.Coordinate;

/**
 * Exception thrown when ship placement is invalid
 * 
 * @see com.par_28.ship_battle.model.Coordinate
 */
public class ShipPlacementException extends RuntimeException {
    /**
     * Constructor to create exception with invalid ship placement details
     * 
     * @param coord Coordinate where ship placement failed
     */
    public ShipPlacementException(Coordinate coord) {
        super(
            String.format(
                "You can't place a ship on %s. Please choose a position not adjacent and not occupied by a another ship.",
                coord
            )
        );
    }
}
