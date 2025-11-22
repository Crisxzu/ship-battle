package com.par_28.ship_battle.model;

import java.util.*;

import com.par_28.ship_battle.model.enums.*;
import com.par_28.ship_battle.model.exceptions.*;

/**
 * Player model representing a game player
 * <p>
 * a player has a name, a grid to place ships on, a tracking grid to track attacks on opponent,
 * </p>
 * @see Grid
 * @see Ship
 * @see Coordinate
 * @see Direction
 * @see AttackResponse
 */
public class Player {
    /**
     * Player's name
     */
    private final String name;

    /**
     * Player's grid to place ships on
     */
    private final Grid grid;

    /**
     * Player's tracking grid to track attacks on opponent
     */
    private final Grid trackingGrid;

    /**
     * List of player's ships
     */
    private final List<Ship> ships;

    /**
     * Constructor to initialize player with name and grid size
     *
     * @param name Player's name
     * @param gridSize Size of the player's grid
     */
    public Player(String name, int gridSize) {
        this.name = name;
        this.grid = new Grid(gridSize, gridSize);
        this.trackingGrid = new Grid(gridSize, gridSize);
        this.ships = new ArrayList<Ship>();
    }

    /**
     * Check if player is dead (all ships destroyed)
     *
     * @return boolean True if player is dead, false otherwise
     */
    public boolean isDead() {
        boolean result = true;

        for (Ship ship: ships) {
            if(!ship.isDestroyed()) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Add ship to player's fleet
     *
     * @param ship Ship to add
     */
    public void addShip(Ship ship) {
        ships.add(ship);
    }

    /**
     * Place ship on player's grid
     *
     * @param ship Ship to place
     * @param coord Starting coordinate to place the ship
     * @param direction Direction to place the ship
     * @throws InvalidCoordinateException if the coordinate is invalid
     * @throws ShipPlacementException if the ship cannot be placed at the given coordinate and direction
     */
    public void placeShipOnGrid(Ship ship, Coordinate coord, Direction direction) throws InvalidCoordinateException, ShipPlacementException {
        grid.placeShip(ship, coord, direction);
    }

    /**
     * Receive attack on player's grid
     *
     * @param coord Coordinate of the attack
     * @return AttackResponse response of the attack with hit/miss and ship info
     * @throws InvalidCoordinateException if the coordinate is invalid
     */
    public AttackResponse receiveAttack(Coordinate coord) throws InvalidCoordinateException {
        return grid.receiveAttack(coord);
    }


    /**
     * Record attack result on tracking grid
     *
     * @param coord Coordinate of the attack
     * @param response Response of the attack with hit/miss and ship info
     */
    public void recordAttack(Coordinate coord, AttackResponse response) {
        Cell trackingCell = trackingGrid.getCell(coord);
        trackingCell.shoot();

        if (response.isHit()) {
            trackingCell.setShip(response.getShip());
        }
    }

    /**
     * Get player's name
     *
     * @return String Player's name
     */
    public String getName(){
        return name;
    }

    /**
     * Get player's grid
     *
     * @return Grid Player's grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get player's tracking grid
     *
     * @return Grid Player's tracking grid
     */
    public Grid getTrackingGrid() {
        return trackingGrid;
    }

    /**
     * Get player's ships
     *
     * @return Player's ships
     */
    public List<Ship> getShips() {
        return ships;
    }


    /**
     * Remove every placed ship from this player and reset their grid references.
     */
    public void resetFleet() {
        ships.clear();
        grid.clearShips();
    }


    /**
     * Check if this player is an AI.
     *
     * @return returns false for normal player and true for AI
     */
    public boolean isAI() {
        return false;
    }
}
