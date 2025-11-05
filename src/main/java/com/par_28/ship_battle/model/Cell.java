package com.par_28.ship_battle.model;

/**
 * Cell model representing a single square on a Grid.
 * <p>
 * A Cell knows its {@link Coordinate}, whether it contains a {@link Ship},
 * and whether it has been shot.
 * </p>
 * The class provides helpers to shoot the cell and to query its state.
 */
public class Cell {
    private final Coordinate coordinate;
    private Ship ship; // nullable
    private boolean shot;

    /**
     * Create a new cell at the given coordinate.
     *
     * @param coordinate coordinate of the cell
     */
    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.ship = null;
        this.shot = false;
    }

    /**
     * Get the coordinate of this cell.
     *
     * @return Coordinate of cell
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Get the ship occupying this cell, or null if empty.
     *
     * @return Ship or null
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Assign a ship to this cell.
     * <p>
     * NOTE: this method does not validate placement overlaps — Grid should enforce that.
     * </p>
     * @param ship Ship to set (nullable)
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * Mark the cell as shot. If a ship is present, it should receive damage.
     * <p>
     * This operation is idempotent (multiple calls remain 'shot').
     * </p>
     */
    public void shoot() {
        if (!shot) {
            shot = true;
            if (ship != null) {
                ship.receiveDamage();
            }
        }
    }

    /**
     * Check whether this cell has been shot.
     *
     * @return true if shot, false otherwise
     */
    public boolean isShot() {
        return shot;
    }

    /**
     * Check whether the cell currently contains a ship.
     *
     * @return true if a ship is present
     */
    public boolean hasShip() {
        return ship != null;
    }

    /**
     * Check whether the cell is empty (no ship).
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return ship == null;
    }
}