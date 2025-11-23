package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.*;
import com.par_28.ship_battle.model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Player class
 * Tests player ship management, attack handling, and tracking
 */
@DisplayName("Player Logic Tests")
class PlayerTest {

    private Player player;

    @Nested
    @DisplayName("Player Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should create player with name")
        void shouldCreatePlayerWithName() {
            // When
            player = new Player("TestPlayer", 10);

            // Then
            assertEquals("TestPlayer", player.getName());
        }

        @Test
        @DisplayName("Should initialize with empty ship list")
        void shouldInitializeWithEmptyShipList() {
            // When
            player = new Player("TestPlayer", 10);

            // Then
            assertNotNull(player.getShips());
            assertTrue(player.getShips().isEmpty());
        }

        @Test
        @DisplayName("Should create grid and tracking grid")
        void shouldCreateGridAndTrackingGrid() {
            // When
            player = new Player("TestPlayer", 10);

            // Then
            assertNotNull(player.getGrid());
            assertNotNull(player.getTrackingGrid());
        }

        @Test
        @DisplayName("Should create grids with correct size")
        void shouldCreateGridsWithCorrectSize() {
            // When
            player = new Player("TestPlayer", 15);

            // Then
            assertEquals(15, player.getGrid().getWidth());
            assertEquals(15, player.getGrid().getHeight());
            assertEquals(15, player.getTrackingGrid().getWidth());
            assertEquals(15, player.getTrackingGrid().getHeight());
        }
    }

    @Nested
    @DisplayName("Ship Management Tests")
    class ShipManagementTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should add ship to player fleet")
        void shouldAddShipToFleet() {
            // Given
            Ship ship = new Carrier();

            // When
            player.addShip(ship);

            // Then
            assertEquals(1, player.getShips().size());
            assertTrue(player.getShips().contains(ship));
        }

        @Test
        @DisplayName("Should add multiple ships to fleet")
        void shouldAddMultipleShips() {
            // Given
            Ship ship1 = new Carrier();
            Ship ship2 = new Cruiser();
            Ship ship3 = new Destroyer();

            // When
            player.addShip(ship1);
            player.addShip(ship2);
            player.addShip(ship3);

            // Then
            assertEquals(3, player.getShips().size());
            assertTrue(player.getShips().contains(ship1));
            assertTrue(player.getShips().contains(ship2));
            assertTrue(player.getShips().contains(ship3));
        }

        @Test
        @DisplayName("Should place ship on grid")
        void shouldPlaceShipOnGrid() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Destroyer();
            player.addShip(ship);
            Coordinate coord = new Coordinate(2, 3);

            // When
            player.placeShipOnGrid(ship, coord, Direction.HORIZONTAL);

            // Then
            assertEquals(ship, player.getGrid().getCell(coord).getShip());
            assertEquals(Direction.HORIZONTAL, ship.getDirection());
            assertNotNull(ship.getPositions());
            assertEquals(3, ship.getPositions().size());
        }

        @Test
        @DisplayName("Should throw exception when placing ship out of bounds")
        void shouldThrowExceptionWhenPlacingShipOutOfBounds() {
            // Given
            Ship ship = new Carrier();
            player.addShip(ship);
            Coordinate coord = new Coordinate(8, 5);

            // When & Then
            assertThrows(ShipPlacementException.class, () -> {
                player.placeShipOnGrid(ship, coord, Direction.HORIZONTAL);
            });
        }
    }

    @Nested
    @DisplayName("Player Status Tests")
    class PlayerStatusTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should be dead when no ships are added")
        void shouldBeDeadWhenNoShips() {
            // When
            boolean isDead = player.isDead();

            // Then
            assertTrue(isDead, "Player with no ships should be considered dead");
        }

        @Test
        @DisplayName("Should be alive when at least one ship is not destroyed")
        void shouldBeAliveWhenShipNotDestroyed() {
            // Given
            Ship ship1 = new Carrier();
            Ship ship2 = new Cruiser();
            player.addShip(ship1);
            player.addShip(ship2);

            // When
            boolean isDead = player.isDead();

            // Then
            assertFalse(isDead);
        }

        @Test
        @DisplayName("Should be alive when some ships are destroyed but not all")
        void shouldBeAliveWhenSomeShipsDestroyed() {
            // Given
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            Ship ship3 = new Destroyer();
            player.addShip(ship1);
            player.addShip(ship2);
            player.addShip(ship3);

            // Destroy ship1
            ship1.receiveDamage();
            ship1.receiveDamage();

            // When
            boolean isDead = player.isDead();

            // Then
            assertTrue(ship1.isDestroyed());
            assertFalse(ship2.isDestroyed());
            assertFalse(ship3.isDestroyed());
            assertFalse(isDead);
        }

        @Test
        @DisplayName("Should be dead when all ships are destroyed")
        void shouldBeDeadWhenAllShipsDestroyed() {
            // Given
            Ship ship1 = new Torpedo();
            Ship ship2 = new Torpedo();
            player.addShip(ship1);
            player.addShip(ship2);

            // Destroy all ships
            ship1.receiveDamage();
            ship1.receiveDamage();
            ship2.receiveDamage();
            ship2.receiveDamage();

            // When
            boolean isDead = player.isDead();

            // Then
            assertTrue(ship1.isDestroyed());
            assertTrue(ship2.isDestroyed());
            assertTrue(isDead);
        }
    }

    @Nested
    @DisplayName("Attack Handling Tests")
    class AttackHandlingTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should receive attack and return miss for empty cell")
        void shouldReceiveAttackAndReturnMiss() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(3, 4);

            // When
            AttackResponse response = player.receiveAttack(coord);

            // Then
            assertEquals(AttackResult.MISS, response.getResult());
            assertFalse(response.isHit());
        }

        @Test
        @DisplayName("Should receive attack and return hit for cell with ship")
        void shouldReceiveAttackAndReturnHit() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Destroyer();
            player.addShip(ship);
            Coordinate coord = new Coordinate(2, 3);
            player.placeShipOnGrid(ship, coord, Direction.HORIZONTAL);

            // When
            AttackResponse response = player.receiveAttack(coord);

            // Then
            assertEquals(AttackResult.HIT, response.getResult());
            assertTrue(response.isHit());
            assertEquals(ship, response.getShip());
        }

        @Test
        @DisplayName("Should throw InvalidCoordinateException for invalid coordinates")
        void shouldThrowExceptionForInvalidCoordinates() {
            // Given
            Coordinate invalidCoord = new Coordinate(-1, 5);

            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                player.receiveAttack(invalidCoord);
            });
        }
    }

    @Nested
    @DisplayName("Attack Recording Tests")
    class AttackRecordingTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should record miss attack on tracking grid")
        void shouldRecordMissAttackOnTrackingGrid() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(3, 4);
            AttackResponse response = new AttackResponse(AttackResult.MISS, null);

            // When
            player.recordAttack(coord, response);

            // Then
            Cell trackingCell = player.getTrackingGrid().getCell(coord);
            assertTrue(trackingCell.isShot());
            assertFalse(trackingCell.hasShip());
        }

        @Test
        @DisplayName("Should record hit attack and mark ship on tracking grid")
        void shouldRecordHitAttackWithShip() throws InvalidCoordinateException {
            // Given
            Ship ship = new Destroyer();
            Coordinate coord = new Coordinate(3, 4);
            AttackResponse response = new AttackResponse(AttackResult.HIT, ship);

            // When
            player.recordAttack(coord, response);

            // Then
            Cell trackingCell = player.getTrackingGrid().getCell(coord);
            assertTrue(trackingCell.isShot());
            assertTrue(trackingCell.hasShip());
            assertEquals(ship, trackingCell.getShip());
        }

        @Test
        @DisplayName("Should record sunk attack with ship")
        void shouldRecordSunkAttackWithShip() throws InvalidCoordinateException {
            // Given
            Ship ship = new Torpedo();
            ship.receiveDamage();
            ship.receiveDamage();
            Coordinate coord = new Coordinate(5, 6);
            AttackResponse response = new AttackResponse(AttackResult.SUNK, ship);

            // When
            player.recordAttack(coord, response);

            // Then
            Cell trackingCell = player.getTrackingGrid().getCell(coord);
            assertTrue(trackingCell.isShot());
            assertTrue(trackingCell.hasShip());
            assertEquals(ship, trackingCell.getShip());
            assertTrue(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should record multiple attacks on tracking grid")
        void shouldRecordMultipleAttacks() throws InvalidCoordinateException {
            // Given
            Coordinate coord1 = new Coordinate(2, 3);
            Coordinate coord2 = new Coordinate(4, 5);
            Ship ship = new Cruiser();
            AttackResponse missResponse = new AttackResponse(AttackResult.MISS, null);
            AttackResponse hitResponse = new AttackResponse(AttackResult.HIT, ship);

            // When
            player.recordAttack(coord1, missResponse);
            player.recordAttack(coord2, hitResponse);

            // Then
            Cell trackingCell1 = player.getTrackingGrid().getCell(coord1);
            Cell trackingCell2 = player.getTrackingGrid().getCell(coord2);

            assertTrue(trackingCell1.isShot());
            assertFalse(trackingCell1.hasShip());

            assertTrue(trackingCell2.isShot());
            assertTrue(trackingCell2.hasShip());
            assertEquals(ship, trackingCell2.getShip());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct player name")
        void shouldReturnCorrectName() {
            // Given
            player = new Player("Admiral Nelson", 10);

            // When
            String name = player.getName();

            // Then
            assertEquals("Admiral Nelson", name);
        }

        @Test
        @DisplayName("Should return grid")
        void shouldReturnGrid() {
            // Given
            player = new Player("TestPlayer", 10);

            // When
            Grid grid = player.getGrid();

            // Then
            assertNotNull(grid);
            assertEquals(10, grid.getWidth());
            assertEquals(10, grid.getHeight());
        }

        @Test
        @DisplayName("Should return tracking grid")
        void shouldReturnTrackingGrid() {
            // Given
            player = new Player("TestPlayer", 10);

            // When
            Grid trackingGrid = player.getTrackingGrid();

            // Then
            assertNotNull(trackingGrid);
            assertEquals(10, trackingGrid.getWidth());
            assertEquals(10, trackingGrid.getHeight());
        }

        @Test
        @DisplayName("Should return ships list")
        void shouldReturnShipsList() {
            // Given
            player = new Player("TestPlayer", 10);
            Ship ship1 = new Carrier();
            Ship ship2 = new Cruiser();
            player.addShip(ship1);
            player.addShip(ship2);

            // When
            var ships = player.getShips();

            // Then
            assertNotNull(ships);
            assertEquals(2, ships.size());
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should manage complete ship placement workflow")
        void shouldManageCompleteShipPlacementWorkflow() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship1 = new Destroyer();
            Ship ship2 = new Cruiser();

            // When
            player.addShip(ship1);
            player.addShip(ship2);
            player.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            player.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.VERTICAL);

            // Then
            assertEquals(2, player.getShips().size());
            assertFalse(player.isDead());
            assertEquals(3, ship1.getPositions().size());
            assertEquals(4, ship2.getPositions().size());
        }

        @Test
        @DisplayName("Should handle multiple attacks correctly")
        void shouldHandleMultipleAttacks() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Destroyer();
            player.addShip(ship);
            player.placeShipOnGrid(ship, new Coordinate(2, 3), Direction.HORIZONTAL);

            // When - Receive multiple attacks
            AttackResponse response1 = player.receiveAttack(new Coordinate(2, 3));
            player.recordAttack(new Coordinate(2, 3), response1);

            AttackResponse response2 = player.receiveAttack(new Coordinate(3, 3));
            player.recordAttack(new Coordinate(3, 3), response2);

            // Then
            assertEquals(AttackResult.HIT, response1.getResult());
            assertEquals(AttackResult.HIT, response2.getResult());
            assertFalse(player.isDead());
            assertEquals(1, ship.getLife());
        }

        @Test
        @DisplayName("Should transition from alive to dead when all ships destroyed")
        void shouldTransitionFromAliveToDead() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship1 = new Torpedo();
            Ship ship2 = new Torpedo();
            player.addShip(ship1);
            player.addShip(ship2);
            player.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            player.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            assertFalse(player.isDead(), "Player should be alive initially");

            // When - Destroy first ship
            player.receiveAttack(new Coordinate(0, 0));
            player.receiveAttack(new Coordinate(1, 0));

            assertFalse(player.isDead(), "Player should still be alive with one ship");

            // When - Destroy second ship
            player.receiveAttack(new Coordinate(5, 5));
            player.receiveAttack(new Coordinate(6, 5));

            // Then
            assertTrue(player.isDead(), "Player should be dead when all ships destroyed");
        }
    }

    @Nested
    @DisplayName("Reset Fleet Tests")
    class ResetFleetTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should clear all ships when resetting fleet")
        void shouldClearAllShipsWhenResettingFleet() {
            // Given
            player.addShip(new Carrier());
            player.addShip(new Cruiser());
            player.addShip(new Destroyer());
            assertEquals(3, player.getShips().size());

            // When
            player.resetFleet();

            // Then
            assertTrue(player.getShips().isEmpty());
        }

        @Test
        @DisplayName("Should clear grid when resetting fleet")
        void shouldClearGridWhenResettingFleet() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship = new Destroyer();
            player.addShip(ship);
            Coordinate coord = new Coordinate(2, 3);
            player.placeShipOnGrid(ship, coord, Direction.HORIZONTAL);

            // Verify ship is on grid
            assertTrue(player.getGrid().getCell(coord).hasShip());

            // When
            player.resetFleet();

            // Then
            assertFalse(player.getGrid().getCell(coord).hasShip());
        }

        @Test
        @DisplayName("Should allow placing new ships after reset")
        void shouldAllowPlacingNewShipsAfterReset() throws InvalidCoordinateException, ShipPlacementException {
            // Given
            Ship ship1 = new Carrier();
            player.addShip(ship1);
            player.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            player.resetFleet();

            // When
            Ship ship2 = new Cruiser();
            player.addShip(ship2);
            player.placeShipOnGrid(ship2, new Coordinate(0, 0), Direction.HORIZONTAL);

            // Then
            assertEquals(1, player.getShips().size());
            assertTrue(player.getShips().contains(ship2));
            assertEquals(ship2, player.getGrid().getCell(new Coordinate(0, 0)).getShip());
        }

        @Test
        @DisplayName("Should handle reset on empty fleet")
        void shouldHandleResetOnEmptyFleet() {
            // Given - Empty fleet
            assertTrue(player.getShips().isEmpty());

            // When
            player.resetFleet();

            // Then - Should not throw and remain empty
            assertTrue(player.getShips().isEmpty());
        }
    }

    @Nested
    @DisplayName("Power Charges Tests")
    class PowerChargesTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should initialize with default bomb charges")
        void shouldInitializeWithDefaultBombCharges() {
            // Then
            assertEquals(2, player.getBombCharges());
            assertTrue(player.hasBombCharges());
        }

        @Test
        @DisplayName("Should initialize with default radar charges")
        void shouldInitializeWithDefaultRadarCharges() {
            // Then
            assertEquals(3, player.getRadarCharges());
            assertTrue(player.hasRadarCharges());
        }

        @Test
        @DisplayName("Should initialize with custom power charges")
        void shouldInitializeWithCustomPowerCharges() {
            // When
            Player customPlayer = new Player("Custom", 10, 5, 7);

            // Then
            assertEquals(5, customPlayer.getBombCharges());
            assertEquals(7, customPlayer.getRadarCharges());
        }

        @Test
        @DisplayName("Should use bomb charge and decrement count")
        void shouldUseBombChargeAndDecrement() {
            // When
            boolean used = player.useBombCharge();

            // Then
            assertTrue(used);
            assertEquals(1, player.getBombCharges());
        }

        @Test
        @DisplayName("Should return false when no bomb charges available")
        void shouldReturnFalseWhenNoBombCharges() {
            // Given - Use all charges
            player.useBombCharge();
            player.useBombCharge();

            // When
            boolean used = player.useBombCharge();

            // Then
            assertFalse(used);
            assertEquals(0, player.getBombCharges());
            assertFalse(player.hasBombCharges());
        }

        @Test
        @DisplayName("Should use radar charge and decrement count")
        void shouldUseRadarChargeAndDecrement() {
            // When
            boolean used = player.useRadarCharge();

            // Then
            assertTrue(used);
            assertEquals(2, player.getRadarCharges());
        }

        @Test
        @DisplayName("Should return false when no radar charges available")
        void shouldReturnFalseWhenNoRadarCharges() {
            // Given - Use all charges
            player.useRadarCharge();
            player.useRadarCharge();
            player.useRadarCharge();

            // When
            boolean used = player.useRadarCharge();

            // Then
            assertFalse(used);
            assertEquals(0, player.getRadarCharges());
            assertFalse(player.hasRadarCharges());
        }

        @Test
        @DisplayName("Should refill powers to default values")
        void shouldRefillPowersToDefaultValues() {
            // Given - Use all charges
            player.useBombCharge();
            player.useBombCharge();
            player.useRadarCharge();
            player.useRadarCharge();
            player.useRadarCharge();

            assertEquals(0, player.getBombCharges());
            assertEquals(0, player.getRadarCharges());

            // When
            player.refillPowers();

            // Then
            assertEquals(2, player.getBombCharges());
            assertEquals(3, player.getRadarCharges());
        }

        @Test
        @DisplayName("Should return false for isAI on normal player")
        void shouldReturnFalseForIsAI() {
            // When & Then
            assertFalse(player.isAI());
        }
    }

    @Nested
    @DisplayName("Random Ship Placement Tests")
    class RandomPlacementTests {

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 10);
        }

        @Test
        @DisplayName("Should place single ship randomly")
        void shouldPlaceSingleShipRandomly() {
            // Given
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Torpedo());

            // When
            boolean result = player.placeShipsRandomly(fleet);

            // Then
            assertTrue(result);
            assertEquals(1, player.getShips().size());
            assertNotNull(player.getShips().get(0).getPositions());
            assertEquals(2, player.getShips().get(0).getPositions().size());
        }

        @Test
        @DisplayName("Should place multiple ships randomly")
        void shouldPlaceMultipleShipsRandomly() {
            // Given
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Carrier());
            fleet.add(new Cruiser());
            fleet.add(new Destroyer());
            fleet.add(new Torpedo());

            // When
            boolean result = player.placeShipsRandomly(fleet);

            // Then
            assertTrue(result);
            assertEquals(4, player.getShips().size());
            for (Ship ship : player.getShips()) {
                assertNotNull(ship.getPositions());
                assertNotNull(ship.getDirection());
            }
        }

        @Test
        @DisplayName("Should place full fleet randomly")
        void shouldPlaceFullFleetRandomly() {
            // Given - Standard fleet
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Carrier());
            fleet.add(new Cruiser());
            fleet.add(new Cruiser());
            fleet.add(new Destroyer());
            fleet.add(new Destroyer());
            fleet.add(new Destroyer());
            fleet.add(new Torpedo());
            fleet.add(new Torpedo());
            fleet.add(new Torpedo());
            fleet.add(new Torpedo());

            // When
            boolean result = player.placeShipsRandomly(fleet);

            // Then
            assertTrue(result);
            assertEquals(10, player.getShips().size());
        }

        @Test
        @DisplayName("Should place ships deterministically with seed")
        void shouldPlaceShipsDeterministicallyWithSeed() {
            // Given
            List<Ship> fleet1 = new ArrayList<>();
            fleet1.add(new Destroyer());
            fleet1.add(new Torpedo());

            List<Ship> fleet2 = new ArrayList<>();
            fleet2.add(new Destroyer());
            fleet2.add(new Torpedo());

            Player player2 = new Player("TestPlayer2", 10);
            long seed = 12345L;

            // When
            player.placeShipsRandomly(fleet1, seed);
            player2.placeShipsRandomly(fleet2, seed);

            // Then - Same seed should produce same positions
            assertEquals(
                player.getShips().get(0).getPositions(),
                player2.getShips().get(0).getPositions()
            );
            assertEquals(
                player.getShips().get(1).getPositions(),
                player2.getShips().get(1).getPositions()
            );
        }

        @Test
        @DisplayName("Should not overlap ships when placing randomly")
        void shouldNotOverlapShipsWhenPlacingRandomly() {
            // Given
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Carrier());
            fleet.add(new Cruiser());
            fleet.add(new Destroyer());

            // When
            boolean result = player.placeShipsRandomly(fleet);

            // Then
            assertTrue(result);

            // Verify no overlap by checking all positions are unique
            List<Coordinate> allPositions = new ArrayList<>();
            for (Ship ship : player.getShips()) {
                for (Coordinate pos : ship.getPositions()) {
                    assertFalse(allPositions.contains(pos),
                        "Ships should not overlap at " + pos);
                    allPositions.add(pos);
                }
            }
        }

        @Test
        @DisplayName("Should place ships within grid bounds")
        void shouldPlaceShipsWithinGridBounds() {
            // Given
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Carrier());
            fleet.add(new Cruiser());

            // When
            boolean result = player.placeShipsRandomly(fleet);

            // Then
            assertTrue(result);
            int gridWidth = player.getGrid().getWidth();
            int gridHeight = player.getGrid().getHeight();

            for (Ship ship : player.getShips()) {
                for (Coordinate pos : ship.getPositions()) {
                    assertTrue(pos.getX() >= 0 && pos.getX() < gridWidth,
                        "X coordinate should be within bounds");
                    assertTrue(pos.getY() >= 0 && pos.getY() < gridHeight,
                        "Y coordinate should be within bounds");
                }
            }
        }

        @Test
        @DisplayName("Should handle empty fleet list")
        void shouldHandleEmptyFleetList() {
            // Given
            List<Ship> emptyFleet = new ArrayList<>();

            // When
            boolean result = player.placeShipsRandomly(emptyFleet);

            // Then
            assertTrue(result);
            assertTrue(player.getShips().isEmpty());
        }

        @Test
        @DisplayName("Should return false when grid is too small for ships")
        void shouldReturnFalseWhenGridTooSmall() {
            // Given - Very small grid
            Player smallGridPlayer = new Player("SmallGrid", 2);
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Carrier()); // Length 5, won't fit in 2x2 grid

            // When
            boolean result = smallGridPlayer.placeShipsRandomly(fleet);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should set direction for all placed ships")
        void shouldSetDirectionForAllPlacedShips() {
            // Given
            List<Ship> fleet = new ArrayList<>();
            fleet.add(new Destroyer());
            fleet.add(new Torpedo());

            // When
            player.placeShipsRandomly(fleet);

            // Then
            for (Ship ship : player.getShips()) {
                assertNotNull(ship.getDirection());
                assertTrue(
                    ship.getDirection() == Direction.HORIZONTAL ||
                    ship.getDirection() == Direction.VERTICAL
                );
            }
        }

        @Test
        @DisplayName("Should add ships to player's fleet when placing randomly")
        void shouldAddShipsToPlayerFleet() {
            // Given
            List<Ship> fleet = new ArrayList<>();
            Ship carrier = new Carrier();
            Ship cruiser = new Cruiser();
            fleet.add(carrier);
            fleet.add(cruiser);

            // When
            player.placeShipsRandomly(fleet);

            // Then
            assertTrue(player.getShips().contains(carrier));
            assertTrue(player.getShips().contains(cruiser));
        }
    }
}