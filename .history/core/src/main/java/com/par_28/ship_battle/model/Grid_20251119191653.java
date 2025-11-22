package com.par_28.ship_battle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.par_28.ship_battle.model.enums.AttackResult;
import com.par_28.ship_battle.model.enums.Direction;
import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import com.par_28.ship_battle.model.exceptions.InvalidGridDimension;
import com.par_28.ship_battle.model.exceptions.ShipPlacementException;

/**
 * Grid model representing a player's board.
 * <p>
 * The Grid holds a width x height matrix of {@link Cell} and provides operations
 * to place ships and to receive attacks.
 * </p>
 * <p>
 * Public methods throw {@link InvalidCoordinateException} when coordinates given
 * are out of bounds, and {@link ShipPlacementException} when a ship cannot be placed.
 * </p>
 * @see Cell
 * @see Ship
 * @see Coordinate
 */
public class Grid {
    /**
     * Width of the grid (number of columns).
     */
    private final int width;
    /**
     * Height of the grid (number of rows).
     */
    private final int height;
    /**
     * 2D array of cells in the grid.
     */
    private final Cell[][] cells;

    /**
     * Create a square grid (width x height).
     *
     * @param width number of columns (must be > 0)
     * @param height number of rows (must be > 0)
     * @throws InvalidGridDimension if width or height are not positive
     */
    public Grid(int width, int height) throws InvalidGridDimension {
        if (width <= 0 || height <= 0) {
            throw new InvalidGridDimension(width, height);
        }
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        initializeCells();
    }

    /**
     * Initialize the grid cells.
     * <p>
     * Each cell is created with its corresponding Coordinate.
     * </p>
     */
    private void initializeCells() {
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
    public Cell getCell(Coordinate coord) throws InvalidCoordinateException {
        if (!isValidCoordinate(coord)) {
            throw new InvalidCoordinateException(coord);
        }
        return cells[coord.getX()][coord.getY()];
    }

    /**
     * Calculate the list of Coordinates occupied by a ship starting at {@code start}
     * with given {@code length} and {@link Direction}.
     * <p>
     * Returns an empty list if the computed positions fall outside the grid.
     * </p>
     * @param start starting coordinate
     * @param length ship length
     * @param direction placement direction
     * @return list of Coordinates or empty list if invalid
     */
    private List<Coordinate> calculateShipPositions(Coordinate start, int length, Direction direction) {
        List<Coordinate> res = new ArrayList<>();
        if (start == null || length <= 0 || direction == null){
            return res;
        }

        int dx = 0, dy = 0;

        if (direction == Direction.HORIZONTAL)
            dx = 1;
        else
            dy = 1;

        int x = start.getX(), y = start.getY();

        for (int i = 0; i < length; i++) {
            Coordinate c = new Coordinate(
            x + i*dx,
            y + i*dy
            );

            if (!isValidCoordinate(c)) {
                return new ArrayList<>(); // invalid placement
            }
            res.add(c);
        }
        return res;
    }

    /**
     * Get the list of adjacent cells around a given coordinate.
     * <p>
     * Considers the 8 surrounding cells.
     * </p>
     * @param coord central coordinate
     * @return list of adjacent Cells
     */
    private List<Cell> getAdjacentCells(Coordinate coord) {
        List<Cell> adjacentCells = new ArrayList<>();
        int x = coord.getX();
        int y = coord.getY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // skip the cell itself
                Coordinate adjacentCoord = new Coordinate(x + dx, y + dy);
                if (isValidCoordinate(adjacentCoord)) {
                    adjacentCells.add(getCell(adjacentCoord));
                }
            }
        }
        return adjacentCells;
    }

    /**
     * Check if a ship can be placed at the given start and direction.
     * <p>
     * Returns the list of Coordinates the ship would occupy if placeable,
     * or an empty list if not placeable.
     * The ship cannot overlap or be adjacent to existing ships.
     * And the start coordinate must be valid.
     * </p>
     * @param ship ship to place
     * @param start starting coordinate
     * @param direction direction to place
     * @return list of Coordinates if placeable, empty list otherwise
     * @throws InvalidCoordinateException when start is invalid
     */
    private List<Coordinate> canPlaceShip(Ship ship, Coordinate start, Direction direction) {
        if (ship == null) {
            return new ArrayList<>();
        }

        if (!isValidCoordinate(start))
            throw new InvalidCoordinateException(start);

        List<Coordinate> positions = calculateShipPositions(start, ship.getLength(), direction);

        if (positions.isEmpty()) {
            return new ArrayList<>();
        }

        for (Coordinate c : positions) {
            Cell cell = getCell(c);

            if (cell.hasShip()){
                return new ArrayList<>();
            }

            List<Cell> adjacentCells = getAdjacentCells(c);

            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.hasShip()) {
                    return new ArrayList<>();
                }
            }

        }

        return positions;
    }

    /**
     * Preview a ship placement without mutating the grid.
     *
     * @param ship ship to evaluate
     * @param start starting coordinate
     * @param direction direction for placement
     * @return immutable list of coordinates if placement is possible, empty list otherwise
     */
    public List<Coordinate> previewPlacement(Ship ship, Coordinate start, Direction direction) {
        List<Coordinate> positions;
        try {
            positions = canPlaceShip(ship, start, direction);
        }
        catch (RuntimeException e) {
            return Collections.emptyList();
        }

        if(positions.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(positions);
    }

    /**
     * Place a ship on the grid at the given start and direction.
     *
     * <p>
     * If placement is successful, the ship's positions and direction are set,
     * and the corresponding cells are updated to reference the ship.
     * </p>
     *
     * @param ship ship to place
     * @param start starting coordinate
     * @param direction direction to place
     * @throws InvalidCoordinateException when start coordinate is invalid
     * @throws ShipPlacementException when placement is not possible (overlap or out of bounds)
     * @see #canPlaceShip(Ship, Coordinate, Direction)
     */
    public void placeShip(Ship ship, Coordinate start, Direction direction) throws ShipPlacementException, InvalidCoordinateException {
        List<Coordinate> positions = canPlaceShip(ship, start, direction);

        if(!positions.isEmpty()) {
            // assign positions to ship and to cells
            ship.setDirection(direction);
            ship.setPositions(positions);
            for (Coordinate c : positions) {
                cells[c.getX()][c.getY()].setShip(ship);
            }
        }
        else {
            throw new ShipPlacementException(start);
        }
    }

    /**
     * Receive an attack at the given coordinate.
     *
     * @param coord attack coordinate
     * @return AttackResponse representing result (MISS, HIT, SUNK, etc.)
     * @throws InvalidCoordinateException when coord is outside grid
     */
    public AttackResponse receiveAttack(Coordinate coord) throws InvalidCoordinateException {
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
        }
        else {
            Ship s = target.getShip();

            if (s.isDestroyed()) {
                return new AttackResponse(AttackResult.SUNK, s);
            } else {
                return new AttackResponse(AttackResult.HIT, s);
            }
        }
    }

    /**
     * Get the grid's width.
     *
     * @return width of the grid
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the grid's height.
     *
     * @return height of the grid
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the grid's cells.
     *
     * @return 2D array of Cells
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Clear every ship reference from the grid and reset cell shot states.
     */
    public void clearShips() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].reset();
            }
        }
    }
}
