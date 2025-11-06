package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.*;
import com.par_28.ship_battle.model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Grid class
 * Tests grid initialization, ship placement, coordinate validation, and attack handling
 */
@DisplayName("Grid Logic Tests")
class GridTest {

    private Grid grid;

    @Nested
    @DisplayName("Grid Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize grid with correct dimensions")
        void shouldInitializeGridWithCorrectDimensions() {
            // When
            grid = new Grid(10, 10);

            // Then
            assertEquals(10, grid.getWidth());
            assertEquals(10, grid.getHeight());
            assertNotNull(grid.getCells());
        }

        @Test
        @DisplayName("Should initialize all cells in grid")
        void shouldInitializeAllCells() {
            // When
            grid = new Grid(10, 10);
            Cell[][] cells = grid.getCells();

            // Then
            assertNotNull(cells);
            assertEquals(10, cells.length);
            for (int i = 0; i < 10; i++) {
                assertEquals(10, cells[i].length);
                for (int j = 0; j < 10; j++) {
                    assertNotNull(cells[i][j]);
                }
            }
        }

        @Test
        @DisplayName("Should handle different grid sizes")
        void shouldHandleDifferentGridSizes() {
            // When & Then
            grid = new Grid(5, 8);
            assertEquals(5, grid.getWidth());
            assertEquals(8, grid.getHeight());

            grid = new Grid(15, 12);
            assertEquals(15, grid.getWidth());
            assertEquals(12, grid.getHeight());
        }

        @Test
        @DisplayName("Should throw exception for non-positive width")
        void shouldThrowExceptionForNonPositiveWidth() {
            // When & Then
            assertThrows(InvalidGridDimension.class, () -> {
                new Grid(0, 10);
            });

            assertThrows(InvalidGridDimension.class, () -> {
                new Grid(-1, 10);
            });
        }

        @Test
        @DisplayName("Should throw exception for non-positive height")
        void shouldThrowExceptionForNonPositiveHeight() {
            // When & Then
            assertThrows(InvalidGridDimension.class, () -> {
                new Grid(10, 0);
            });

            assertThrows(InvalidGridDimension.class, () -> {
                new Grid(10, -1);
            });
        }

        @Test
        @DisplayName("Should throw exception for non-positive value for both dimension")
        void shouldThrowExceptionForNonPositiveDimensions() {
            // When & Then
            assertThrows(InvalidGridDimension.class, () -> {
                new Grid(-10, -10);
            });
        }
    }

    @Nested
    @DisplayName("Coordinate Validation Tests")
    class CoordinateValidationTests {

        @BeforeEach
        void setUp() {
            grid = new Grid(10, 10);
        }

        @Test
        @DisplayName("Should validate coordinate within bounds")
        void shouldValidateCoordinateWithinBounds() {
            // Given
            Coordinate coord = new Coordinate(5, 5);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Should invalidate coordinate with negative X")
        void shouldInvalidateNegativeX() {
            // Given
            Coordinate coord = new Coordinate(-1, 5);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should invalidate coordinate with negative Y")
        void shouldInvalidateNegativeY() {
            // Given
            Coordinate coord = new Coordinate(5, -1);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should invalidate negative coordinate")
        void shouldInvalidateNegative() {
            // Given
            Coordinate coord = new Coordinate(-5, -1);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should invalidate coordinate with X >= width")
        void shouldInvalidateXBeyondWidth() {
            // Given
            Coordinate coord = new Coordinate(10, 5);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should invalidate coordinate with Y >= height")
        void shouldInvalidateYBeyondHeight() {
            // Given
            Coordinate coord = new Coordinate(5, 10);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should invalidate coordinate beyond limits")
        void shouldInvalidateBeyond() {
            // Given
            Coordinate coord = new Coordinate(10, 10);

            // When
            boolean isValid = grid.isValidCoordinate(coord);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should validate corner coordinates")
        void shouldValidateCornerCoordinates() {
            // When & Then
            assertTrue(grid.isValidCoordinate(new Coordinate(0, 0)), "Top-left corner should be valid");
            assertTrue(grid.isValidCoordinate(new Coordinate(9, 9)), "Bottom-right corner should be valid");
            assertTrue(grid.isValidCoordinate(new Coordinate(0, 9)), "Bottom-left corner should be valid");
            assertTrue(grid.isValidCoordinate(new Coordinate(9, 0)), "Top-right corner should be valid");
        }

        @Test
        @DisplayName("Should invalidate null coordinate")
        void shouldInvalidateNullCoordinate() {
            // When
            boolean isValid = grid.isValidCoordinate(null);

            // Then
            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Cell Access Tests")
    class CellAccessTests {

        @BeforeEach
        void setUp() {
            grid = new Grid(10, 10);
        }

        @Test
        @DisplayName("Should get cell at valid coordinate")
        void shouldGetCellAtValidCoordinate() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(3, 4);

            // When
            Cell cell = grid.getCell(coord);

            // Then
            assertNotNull(cell);
            assertEquals(coord, cell.getCoordinate());
        }

        @Test
        @DisplayName("Should throw exception for invalid coordinate")
        void shouldThrowExceptionForInvalidCoordinate() {
            // Given
            Coordinate coord = new Coordinate(-1, 5);

            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                grid.getCell(coord);
            });
        }

        @Test
        @DisplayName("Should throw exception for null coordinate")
        void shouldThrowExceptionForNullCoordinate() {
            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                grid.getCell(null);
            });
        }
    }

    @Nested
    @DisplayName("Ship Placement Tests")
    class ShipPlacementTests {

        @BeforeEach
        void setUp() {
            grid = new Grid(10, 10);
        }

        @Test
        @DisplayName("Should place ship horizontally on empty grid")
        void shouldPlaceShipHorizontally() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Destroyer(); // Length 3
            Coordinate start = new Coordinate(2, 3);

            // When
            grid.placeShip(ship, start, Direction.HORIZONTAL);

            // Then
            assertEquals(Direction.HORIZONTAL, ship.getDirection());
            assertNotNull(ship.getPositions());
            assertEquals(3, ship.getPositions().size());

            // Verify cells have ship
            assertTrue(grid.getCell(new Coordinate(2, 3)).hasShip());
            assertTrue(grid.getCell(new Coordinate(3, 3)).hasShip());
            assertTrue(grid.getCell(new Coordinate(4, 3)).hasShip());
        }

        @Test
        @DisplayName("Should place ship vertically on empty grid")
        void shouldPlaceShipVertically() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Cruiser(); // Length 4
            Coordinate start = new Coordinate(5, 2);

            // When
            grid.placeShip(ship, start, Direction.VERTICAL);

            // Then
            assertEquals(Direction.VERTICAL, ship.getDirection());
            assertNotNull(ship.getPositions());
            assertEquals(4, ship.getPositions().size());

            // Verify cells have ship
            assertTrue(grid.getCell(new Coordinate(5, 2)).hasShip());
            assertTrue(grid.getCell(new Coordinate(5, 3)).hasShip());
            assertTrue(grid.getCell(new Coordinate(5, 4)).hasShip());
            assertTrue(grid.getCell(new Coordinate(5, 5)).hasShip());
        }

        @Test
        @DisplayName("Should throw exception when ship placement goes out of bounds horizontally")
        void shouldThrowExceptionWhenShipGoesOutOfBoundsHorizontally() {
            // Given
            Ship ship = new Carrier(); // Length 5
            Coordinate start = new Coordinate(8, 5); // 8 + 5 > 10

            // When & Then
            assertThrows(ShipPlacementException.class, () -> {
                grid.placeShip(ship, start, Direction.HORIZONTAL);
            });
        }

        @Test
        @DisplayName("Should throw exception when ship placement goes out of bounds vertically")
        void shouldThrowExceptionWhenShipGoesOutOfBoundsVertically() {
            // Given
            Ship ship = new Cruiser(); // Length 4
            Coordinate start = new Coordinate(5, 8); // 8 + 4 > 10

            // When & Then
            assertThrows(ShipPlacementException.class, () -> {
                grid.placeShip(ship, start, Direction.VERTICAL);
            });
        }

        @Test
        @DisplayName("Should throw exception when ship overlaps with existing ship")
        void shouldThrowExceptionWhenShipOverlaps() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship1 = new Destroyer();
            Ship ship2 = new Destroyer();
            Coordinate start = new Coordinate(2, 3);

            // Place first ship
            grid.placeShip(ship1, start, Direction.HORIZONTAL);

            // When & Then - Try to place overlapping ship
            assertThrows(ShipPlacementException.class, () -> {
                grid.placeShip(ship2, start, Direction.HORIZONTAL);
            });
        }

        @Test
        @DisplayName("Should throw exception when ships are adjacent (diagonal)")
        void shouldThrowExceptionWhenShipsAreAdjacent() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship1 = new Destroyer();
            Ship ship2 = new Destroyer();

            grid.placeShip(ship1, new Coordinate(2, 2), Direction.HORIZONTAL);

            // When & Then - Try to place ship adjacent (diagonally)
            assertThrows(ShipPlacementException.class, () -> {
                grid.placeShip(ship2, new Coordinate(3, 3), Direction.HORIZONTAL);
            });
        }

        @Test
        @DisplayName("Should place multiple non-overlapping and non-adjacent ships")
        void shouldPlaceMultipleNonOverlappingShips() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship1 = new Destroyer();
            Ship ship2 = new Cruiser();

            // Ship 1: Horizontal at (0, 0), length 3
            // Ship 2: Vertical at (5, 5), length 4 (far enough away)

            // When
            grid.placeShip(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            grid.placeShip(ship2, new Coordinate(5, 5), Direction.VERTICAL);

            // Then - Should not throw exception
            assertEquals(3, ship1.getPositions().size());
            assertEquals(4, ship2.getPositions().size());
        }

        @Test
        @DisplayName("Should throw exception for invalid starting coordinate")
        void shouldThrowExceptionForInvalidStartCoordinate() {
            // Given
            Ship ship = new Destroyer();
            Coordinate invalidStart = new Coordinate(-1, 5);

            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                grid.placeShip(ship, invalidStart, Direction.HORIZONTAL);
            });
        }

        @Test
        @DisplayName("Should throw exception for null ship")
        void shouldThrowExceptionForNullShip() {
            // Given
            Coordinate start = new Coordinate(2, 3);

            // When & Then
            assertThrows(ShipPlacementException.class, () -> {
                grid.placeShip(null, start, Direction.HORIZONTAL);
            });
        }
    }

    @Nested
    @DisplayName("Attack Handling Tests")
    class AttackHandlingTests {

        @BeforeEach
        void setUp() {
            grid = new Grid(10, 10);
        }

        @Test
        @DisplayName("Should return MISS when attacking empty cell")
        void shouldReturnMissWhenAttackingEmptyCell() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(3, 4);

            // When
            AttackResponse response = grid.receiveAttack(coord);

            // Then
            assertNotNull(response);
            assertEquals(AttackResult.MISS, response.getResult());
            assertFalse(response.isHit());
            assertNull(response.getShip());
        }

        @Test
        @DisplayName("Should return HIT when attacking cell with ship")
        void shouldReturnHitWhenAttackingShip() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Destroyer(); // Length 3, Life 3
            Coordinate start = new Coordinate(2, 3);
            grid.placeShip(ship, start, Direction.HORIZONTAL);

            // When
            AttackResponse response = grid.receiveAttack(start);

            // Then
            assertNotNull(response);
            assertEquals(AttackResult.HIT, response.getResult());
            assertTrue(response.isHit());
            assertEquals(ship, response.getShip());
            assertEquals(2, ship.getLife()); // Should have received damage
        }

        @Test
        @DisplayName("Should return SUNK when attacking destroys ship")
        void shouldReturnSunkWhenShipDestroyed() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Torpedo(); // Length 2, Life 2
            Coordinate start = new Coordinate(5, 5);
            grid.placeShip(ship, start, Direction.HORIZONTAL);

            // Attack until ship is destroyed
            grid.receiveAttack(new Coordinate(5, 5)); // First hit

            // When - Final hit
            AttackResponse response = grid.receiveAttack(new Coordinate(6, 5));

            // Then
            assertNotNull(response);
            assertEquals(AttackResult.SUNK, response.getResult());
            assertTrue(response.isHit());
            assertEquals(ship, response.getShip());
            assertTrue(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should return ALREADY_HIT when attacking same cell twice")
        void shouldReturnAlreadyHitWhenAttackingSameCellTwice() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(3, 4);

            // First attack
            grid.receiveAttack(coord);

            // When - Second attack on same cell
            AttackResponse response = grid.receiveAttack(coord);

            // Then
            assertNotNull(response);
            assertEquals(AttackResult.ALREADY_HIT, response.getResult());
        }

        @Test
        @DisplayName("Should throw exception when attacking invalid coordinate")
        void shouldThrowExceptionWhenAttackingInvalidCoordinate() {
            // Given
            Coordinate invalidCoord = new Coordinate(-1, 5);

            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                grid.receiveAttack(invalidCoord);
            });
        }

        @Test
        @DisplayName("Should mark cell as shot after attack")
        void shouldMarkCellAsShotAfterAttack() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(3, 4);

            // When
            grid.receiveAttack(coord);
            Cell cell = grid.getCell(coord);

            // Then
            assertTrue(cell.isShot());
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete game scenario")
        void shouldHandleCompleteGameScenario() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            grid = new Grid(10, 10);
            Ship ship1 = new Destroyer();
            Ship ship2 = new Cruiser();

            // When - Place ships
            grid.placeShip(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            grid.placeShip(ship2, new Coordinate(5, 5), Direction.VERTICAL);

            // Attack miss
            AttackResponse missResponse = grid.receiveAttack(new Coordinate(9, 9));
            assertEquals(AttackResult.MISS, missResponse.getResult());

            // Attack hit
            AttackResponse hitResponse = grid.receiveAttack(new Coordinate(0, 0));
            assertEquals(AttackResult.HIT, hitResponse.getResult());
            assertEquals(ship1, hitResponse.getShip());

            // Attack already hit
            AttackResponse alreadyHitResponse = grid.receiveAttack(new Coordinate(0, 0));
            assertEquals(AttackResult.ALREADY_HIT, alreadyHitResponse.getResult());

            // Then
            assertFalse(ship1.isDestroyed());
            assertFalse(ship2.isDestroyed());
        }

        @Test
        @DisplayName("Should sink ship with multiple attacks")
        void shouldSinkShipWithMultipleAttacks() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            grid = new Grid(10, 10);
            Ship ship = new Torpedo(); // Length 2
            grid.placeShip(ship, new Coordinate(3, 3), Direction.HORIZONTAL);

            // When
            AttackResponse response1 = grid.receiveAttack(new Coordinate(3, 3));
            assertEquals(AttackResult.HIT, response1.getResult());
            assertFalse(ship.isDestroyed());

            AttackResponse response2 = grid.receiveAttack(new Coordinate(4, 3));
            assertEquals(AttackResult.SUNK, response2.getResult());

            // Then
            assertTrue(ship.isDestroyed());
            assertEquals(0, ship.getLife());
        }
    }
}