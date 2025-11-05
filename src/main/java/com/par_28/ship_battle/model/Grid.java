package com.par_28.ship_battle.model;

import java.util.ArrayList;
import java.util.List;

import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import com.par_28.ship_battle.model.exceptions.ShipPlacementException;

/**
 * Grid model representing a player's board.
 * 
 * The Grid holds a width x height matrix of {@link Cell} and provides operations
 * to place ships and to receive attacks.
 * 
 * Public methods throw {@link InvalidCoordinateException} when coordinates given
 * are out of bounds, and {@link ShipPlacementException} when a ship cannot be placed.
 * 
 * @see com.par_28.ship_battle.model.Cell
 * @see com.par_28.ship_battle.model.Ship
 * @see com.par_28.ship_battle.model.Coordinate
 */
public class Grid {
    private final int width;
    private final int height;
    private final Cell[][] cells;

    /**
     * Create a square grid (width x height).
     *
     * @param width number of columns (must be > 0)
     * @param height number of rows (must be > 0)
     */
    public Grid(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        initialize();
    }

    private void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(new Coordinate(x, y));
            }
        }
    }

    /**
     * Check whether the coordinate is inside the grid bounds.
     *
     * @param coord coordinate to check
     * @return true if valid, false otherwise
     */
    public boolean isValidCoordinate(Coordinate coord) {
        if (coord == null) return false;
        int x = coord.getX();
        int y = coord.getY();
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Return the Cell at the given coordinate.
     *
     * @param coord coordinate of requested cell
     * @return Cell at coordinate
     * @throws InvalidCoordinateException if coord is outside the grid
     */
    public Cell getCell(Coordinate coord) {
        if (!isValidCoordinate(coord)) {
            throw new InvalidCoordinateException(coord);
        }
        return cells[coord.getX()][coord.getY()];
    }

    /**
     * Calculate the list of Coordinates occupied by a ship starting at {@code start}
     * with given {@code length} and {@link Direction}.
     *
     * Returns an empty list if the computed positions fall outside the grid.
     *
     * @param start starting coordinate
     * @param length ship length
     * @param direction placement direction
     * @return list of Coordinates or empty list if invalid
     */
    public List<Coordinate> calculateShipPositions(Coordinate start, int length, Direction direction) {
        List<Coordinate> res = new ArrayList<>();
        if (start == null || length <= 0 || direction == null){
            return res;

        } 

        int dx = 0, dy = 0;
        if (direction == Direction.HORIZONTAL) dx = 1; else dy = 1;

        int x = start.getX(), y = start.getY();
        for (int i = 0; i < length; i++) {
            Coordinate c = new Coordinate(x + i*dx, y + i*dy);
            if (!isValidCoordinate(c)) {
                return new ArrayList<>(); // invalid placement
            }
            res.add(c);
        }
        return res;
    }

    /**
     * Check whether a ship can be placed at start in the given direction.
     * Validates coordinates and ensures no overlap with existing ships.
     *
     * @param ship ship to place
     * @param start starting coordinate
     * @param direction direction to place
     * @return true if placeable, false otherwise
     * @throws InvalidCoordinateException when start is invalid
     */
    public boolean canPlaceShip(Ship ship, Coordinate start, Direction direction) {
        if (ship == null) {
            return false;
        }
        if (!isValidCoordinate(start)) throw new InvalidCoordinateException(start);
        List<Coordinate> positions = calculateShipPositions(start, ship.getLength(), direction);
        if (positions.isEmpty()) {
            return false;
        }
        for (Coordinate c : positions) {
            Cell cell = getCell(c);
            if (cell.hasShip()){
                return false;
            } 
        }
        return true;
    }

    /**
     * Place a ship on the grid at the given start and direction.
     *
     * @param ship ship to place
     * @param start starting coordinate
     * @param direction direction to place
     * @throws InvalidCoordinateException when start coordinate is invalid
     * @throws ShipPlacementException when placement is not possible (overlap or out of bounds)
     */
    public void placeShip(Ship ship, Coordinate start, Direction direction) {
        if (!isValidCoordinate(start)) {
            throw new InvalidCoordinateException(start);
        }
        List<Coordinate> positions = calculateShipPositions(start, ship.getLength(), direction);
        if (positions.isEmpty()) {
            throw new ShipPlacementException(start);
        }
        for (Coordinate c : positions) {
            Cell cell = getCell(c);
            if (cell.hasShip()) {
                throw new ShipPlacementException(c);
            }
        }
        // assign positions to ship and to cells
        ship.setDirection(direction);
        ship.setPositions(positions);
        for (Coordinate c : positions) {
            cells[c.getX()][c.getY()].setShip(ship);
        }
    }

    /**
     * Receive an attack at the given coordinate.
     *
     * @param coord attack coordinate
     * @return AttackResponse representing result (MISS, HIT, SUNK, etc.)
     * @throws InvalidCoordinateException when coord is outside grid
     */
    public AttackResponse receiveAttack(Coordinate coord) {
        if (!isValidCoordinate(coord)) {
            throw new InvalidCoordinateException(coord);
        }
        Cell target = getCell(coord);
        if (target.isShot()) {
            return new AttackResponse(AttackResult.ALREADY_HIT, target.getShip());
        }
        target.shoot();
        if (!target.hasShip()) {
            return new AttackResponse(AttackResult.MISS, null);
        } else {
            Ship s = target.getShip();
            if (s.isDestroyed()) {
                return new AttackResponse(AttackResult.SUNK, s);
            } else {
                return new AttackResponse(AttackResult.HIT, s);
            }
        }
    }

    public int getWidth() { 
        return width; 
    }
    public int getHeight() { 
        return height; 
    }
    public Cell[][] gCells() {
        return cells;
    
    }
}