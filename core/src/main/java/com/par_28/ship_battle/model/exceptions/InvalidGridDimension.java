package com.par_28.ship_battle.model.exceptions;

/**
 * Exception thrown when grid dimensions are invalid (non-positive).
 * @see com.par_28.ship_battle.model.Grid
 */
public class InvalidGridDimension extends RuntimeException {
    /**
     * Constructor for InvalidGridDimension exception.
     * 
     * @param width Invalid width of the grid
     * @param height Invalid height of the grid
     */
    public InvalidGridDimension(int  width, int height) {
        super(String.format(
            "Invalid Grid Dimension: %d, %d. Dimensions have to be positive",
            width,
            height
        ));
    }
}
