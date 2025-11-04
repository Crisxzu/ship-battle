package com.par_28.ship_battle.model.exceptions;

import com.par_28.ship_battle.model.Coordinate;

/**
 * Exception thrown when an invalid coordinate is used
 * 
 * @see com.par_28.ship_battle.model.Coordinate
 */
public class InvalidCoordinateException extends RuntimeException {
    /**
     * Constructor to create exception with invalid coordinate details
     * 
     * @param coord Invalid coordinate
     */
    public InvalidCoordinateException(Coordinate coord) {
        super(String.format(
            "Coordinate %s is invalid. Please choose a coordinate inside the grid",
            coord
        ));
    }
}
