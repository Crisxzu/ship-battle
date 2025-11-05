package com.par_28.ship_battle.model;
import java.util.List; 
import java.util.ArrayList; 
import com.par_28.ship_battle.model.enums.Direction;

/**
 * Abstract class representing a ship in the Battleship game.
 *
 * A ship has a name, length, hit points (HP), an orientation, and a set of
 * board positions. Ships can take damage and are destroyed when their hit
 * points are fully depleted.
 *
 * Concrete subclasses of Ship must define their specific name and length
 * in their constructors.

 * @author James
 * @version 1.0
 * @since 1.0
 * @see Carrier
 * @see Cruiser
 * @see Torpedo
 * @see Destroyer
 */

public abstract class Ship {
    /**
     * The length of the ship in cells.
     */
    protected Integer length;

    /**
     * The name of the ship.
     */
    protected String name;

    /**
     * The hit points (HP) of the ship. When this reaches zero, the ship is destroyed.
     */
    protected Integer life;

    /**
     * The orientation of the ship on the board (horizontal or vertical).
     */
    protected Direction direction;

    /**
     * The list of coordinates occupied by the ship on the board.
     */
    protected List<Coordinate> positions;

    /**
     * Reduces the ship's life by one when it takes a hit.
     */
    Ship(String name, Integer length) {
        this.name = name;
        this.length = length;
        this.life = this.length;
    }

    /**
     * Reduces the ship's life by one when it takes a hit.
     */
    public void receiveDamage() {
        if (!isDestroyed()) {
            this.life -= 1;
        } 
    }

    /**
     * Checks if the ship is destroyed (life <= 0).
     *
     * @return true if the ship is destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        return this.life <= 0;
    }

    /**
        * Get the name of the ship.
        *
        * @return the name of the ship.
    */
    public String getName() {
        return name;
    }

    /**
     * Get the length of the ship.
     *
     * @return the length of the ship.
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Get the current life (hit points) of the ship.
     *
     * @return the current life of the ship.
     */
    public Integer getLife() {
        return life;
    }

    /**
     * Get the direction of the ship.
     *
     * @return the direction of the ship.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Set the direction of the ship.
     *
     * @param direction the new direction of the ship.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Set the positions occupied by the ship.
     *
     * @param positions the list of coordinates occupied by the ship.
     */
    public void setPositions(List<Coordinate> positions) {
        this.positions = positions;
    }

    /**
     * Get the list of coordinates occupied by the ship.
     *
     * @return the list of coordinates occupied by the ship.
     */
    public List<Coordinate> getPositions() {
        return positions;
    }

    /**
     * Check if the ship occupies a specific coordinate.
     *
     * @param coordinate the coordinate to check.
     * @return true if the ship occupies the coordinate, false otherwise.
     */
    public boolean occupiesPositions(Coordinate coordinate) {
        return positions != null && positions.contains(coordinate);
    }

}