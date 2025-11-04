package com.par_28.ship_battle.model;
import java.util.List;  
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
 *
 * @author James
 * @version 1.0
 * @since 1.0
 * @see Carrier
 * @see Cruiser
 * @see Torpedo
 * @see Destroyer
 */


abstract class Ship {
    protected Integer length;
    protected String name;
    protected Integer life;
    protected Direction direction;
    protected List<Coordinate> positions;

    Ship(String name, Integer length){
        this.name = name;
        this.length = length;
        this.life = 5;
        this.direction = direction;
        this.positions = positions;
    }
}