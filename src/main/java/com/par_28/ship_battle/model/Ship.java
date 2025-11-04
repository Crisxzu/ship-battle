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

abstract class Ship {
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
    protected List<Coordinate> positions = new ArrayList<>();

    /**
     * Reduces the ship's life by one when it takes a hit.
     */
    Ship(String name, Integer length) {
        this.name = name;
        this.length = length;
        this.life = this.length;
    }
}