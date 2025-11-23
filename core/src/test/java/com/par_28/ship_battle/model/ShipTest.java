package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Ship class and its subclasses
 * Tests ship damage, destruction, position management
 */
@DisplayName("Ship Logic Tests")
class ShipTest {

    @Nested
    @DisplayName("Ship Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should create Carrier with correct name and length")
        void shouldCreateCarrierWithNameAndLength() {
            // Given & When
            Ship carrier = new Carrier();

            // Then
            assertEquals("Carrier", carrier.getName());
            assertEquals(5, carrier.getLength());
            assertEquals(5, carrier.getLife());
        }

        @Test
        @DisplayName("Should create Cruiser with correct name and length")
        void shouldCreateCruiserWithNameAndLength() {
            // Given & When
            Ship cruiser = new Cruiser();

            // Then
            assertEquals("Cruiser", cruiser.getName());
            assertEquals(4, cruiser.getLength());
            assertEquals(4, cruiser.getLife());
        }

        @Test
        @DisplayName("Should create Destroyer with correct name and length")
        void shouldCreateDestroyerWithNameAndLength() {
            // Given & When
            Ship destroyer = new Destroyer();

            // Then
            assertEquals("Destroyer", destroyer.getName());
            assertEquals(3, destroyer.getLength());
            assertEquals(3, destroyer.getLife());
        }

        @Test
        @DisplayName("Should create Torpedo with correct name and length")
        void shouldCreateTorpedoWithNameAndLength() {
            // Given & When
            Ship torpedo = new Torpedo();

            // Then
            assertEquals("Torpedo", torpedo.getName());
            assertEquals(2, torpedo.getLength());
            assertEquals(2, torpedo.getLife());
        }

        @Test
        @DisplayName("Should initialize ship with life equal to length")
        void shouldInitializeShipWithLifeEqualToLength() {
            // Given & When
            Ship carrier = new Carrier();

            // Then
            assertEquals(carrier.getLength(), carrier.getLife());
            assertFalse(carrier.isDestroyed());
        }

        @Test
        @DisplayName("Should initialize ship without positions")
        void shouldInitializeShipWithoutPositions() {
            // Given & When
            Ship ship = new Destroyer();

            // Then
            assertNull(ship.getPositions());
        }

        @Test
        @DisplayName("Should initialize ship without direction")
        void shouldInitializeShipWithoutDirection() {
            // Given & When
            Ship ship = new Torpedo();

            // Then
            assertNull(ship.getDirection());
        }
    }

    @Nested
    @DisplayName("Damage and Destruction Tests")
    class DamageTests {

        @Test
        @DisplayName("Should reduce life when receiving damage")
        void shouldReduceLifeWhenReceivingDamage() {
            // Given
            Ship ship = new Cruiser(); // Length 4, Life 4

            // When
            ship.receiveDamage();

            // Then
            assertEquals(3, ship.getLife());
            assertFalse(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should be destroyed when life reaches zero")
        void shouldBeDestroyedWhenLifeReachesZero() {
            // Given
            Ship ship = new Destroyer(); // Length 3, Life 3

            // When
            ship.receiveDamage();
            ship.receiveDamage();
            ship.receiveDamage();

            // Then
            assertEquals(0, ship.getLife());
            assertTrue(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should not reduce life below zero")
        void shouldNotReduceLifeBelowZero() {
            // Given
            Ship ship = new Torpedo(); // Length 2, Life 2
            ship.receiveDamage();
            ship.receiveDamage();

            // When - Extra damage
            ship.receiveDamage();

            // Then
            assertEquals(0, ship.getLife());
            assertTrue(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should handle multiple damage calls correctly")
        void shouldHandleMultipleDamageCalls() {
            // Given
            Ship ship = new Carrier(); // Length 5, Life 5

            // When
            for (int i = 0; i < 3; i++) {
                ship.receiveDamage();
            }

            // Then
            assertEquals(2, ship.getLife());
            assertFalse(ship.isDestroyed());

            // When - Finish destroying
            ship.receiveDamage();
            ship.receiveDamage();

            // Then
            assertEquals(0, ship.getLife());
            assertTrue(ship.isDestroyed());
        }
    }

    @Nested
    @DisplayName("Refill Life Tests")
    class RefillLifeTests {

        @Test
        @DisplayName("Should refill life to maximum after damage")
        void shouldRefillLifeAfterDamage() {
            // Given
            Ship ship = new Destroyer(); // Length 3
            ship.receiveDamage();
            ship.receiveDamage();
            assertEquals(1, ship.getLife());

            // When
            ship.refillLife();

            // Then
            assertEquals(3, ship.getLife());
            assertFalse(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should refill destroyed ship")
        void shouldRefillDestroyedShip() {
            // Given
            Ship ship = new Torpedo(); // Length 2
            ship.receiveDamage();
            ship.receiveDamage();
            assertTrue(ship.isDestroyed());

            // When
            ship.refillLife();

            // Then
            assertEquals(2, ship.getLife());
            assertFalse(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should have no effect on full life ship")
        void shouldHaveNoEffectOnFullLifeShip() {
            // Given
            Ship ship = new Carrier();
            assertEquals(5, ship.getLife());

            // When
            ship.refillLife();

            // Then
            assertEquals(5, ship.getLife());
        }
    }

    @Nested
    @DisplayName("Direction Management Tests")
    class DirectionTests {

        @Test
        @DisplayName("Should set and get direction")
        void shouldSetAndGetDirection() {
            // Given
            Ship ship = new Cruiser();

            // When
            ship.setDirection(Direction.HORIZONTAL);

            // Then
            assertEquals(Direction.HORIZONTAL, ship.getDirection());
        }

        @Test
        @DisplayName("Should allow changing direction")
        void shouldAllowChangingDirection() {
            // Given
            Ship ship = new Cruiser();
            ship.setDirection(Direction.HORIZONTAL);

            // When
            ship.setDirection(Direction.VERTICAL);

            // Then
            assertEquals(Direction.VERTICAL, ship.getDirection());
        }
    }

    @Nested
    @DisplayName("Position Management Tests")
    class PositionTests {

        @Test
        @DisplayName("Should set and get positions")
        void shouldSetAndGetPositions() {
            // Given
            Ship ship = new Destroyer();
            List<Coordinate> positions = new ArrayList<>();
            positions.add(new Coordinate(0, 0));
            positions.add(new Coordinate(0, 1));
            positions.add(new Coordinate(0, 2));

            // When
            ship.setPositions(positions);

            // Then
            assertEquals(positions, ship.getPositions());
            assertEquals(3, ship.getPositions().size());
        }

        @Test
        @DisplayName("Should verify if ship occupies a position")
        void shouldVerifyIfShipOccupiesPosition() {
            // Given
            Ship ship = new Cruiser();
            List<Coordinate> positions = new ArrayList<>();
            Coordinate coord1 = new Coordinate(2, 3);
            Coordinate coord2 = new Coordinate(2, 4);
            Coordinate coord3 = new Coordinate(2, 5);
            Coordinate coord4 = new Coordinate(2, 6);
            positions.add(coord1);
            positions.add(coord2);
            positions.add(coord3);
            positions.add(coord4);
            ship.setPositions(positions);

            // When & Then
            assertTrue(ship.occupiesPositions(coord1));
            assertTrue(ship.occupiesPositions(coord2));
            assertTrue(ship.occupiesPositions(coord3));
            assertTrue(ship.occupiesPositions(coord4));
            assertFalse(ship.occupiesPositions(new Coordinate(2, 7)));
        }

        @Test
        @DisplayName("Should return false for occupiesPosition when no positions set")
        void shouldReturnFalseWhenNoPositionsSet() {
            // Given
            Ship ship = new Destroyer();

            // When & Then
            assertFalse(ship.occupiesPositions(new Coordinate(0, 0)));
        }

        @Test
        @DisplayName("Should handle empty position list")
        void shouldHandleEmptyPositionList() {
            // Given
            Ship ship = new Cruiser();
            ship.setPositions(new ArrayList<>());

            // When & Then
            assertFalse(ship.occupiesPositions(new Coordinate(0, 0)));
        }
    }

    @Nested
    @DisplayName("Ship Type Tests")
    class ShipTypeTests {

        @Test
        @DisplayName("Should create Carrier with correct properties")
        void shouldCreateCarrierWithCorrectProperties() {
            // When
            Ship carrier = new Carrier();

            // Then
            assertEquals("Carrier", carrier.getName());
            assertEquals(5, carrier.getLength());
            assertEquals(5, carrier.getLife());
        }

        @Test
        @DisplayName("Should create Cruiser with correct properties")
        void shouldCreateCruiserWithCorrectProperties() {
            // When
            Ship cruiser = new Cruiser();

            // Then
            assertEquals("Cruiser", cruiser.getName());
            assertEquals(4, cruiser.getLength());
            assertEquals(4, cruiser.getLife());
        }

        @Test
        @DisplayName("Should create Destroyer with correct properties")
        void shouldCreateDestroyerWithCorrectProperties() {
            // When
            Ship destroyer = new Destroyer();

            // Then
            assertEquals("Destroyer", destroyer.getName());
            assertEquals(3, destroyer.getLength());
            assertEquals(3, destroyer.getLife());
        }

        @Test
        @DisplayName("Should create Torpedo with correct properties")
        void shouldCreateTorpedoWithCorrectProperties() {
            // When
            Ship torpedo = new Torpedo();

            // Then
            assertEquals("Torpedo", torpedo.getName());
            assertEquals(2, torpedo.getLength());
            assertEquals(2, torpedo.getLife());
        }

        @Test
        @DisplayName("All ship types should behave consistently")
        void allShipTypesShouldBehaveConsistently() {
            // Given
            List<Ship> allShips = List.of(
                new Carrier(),
                new Cruiser(),
                new Torpedo(),
                new Destroyer()
            );

            // Then - All should start alive
            for (Ship ship : allShips) {
                assertFalse(ship.isDestroyed());
                assertEquals(ship.getLength(), ship.getLife());
            }
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete ship lifecycle")
        void shouldHandleCompleteShipLifecycle() {
            // Given
            Ship ship = new Destroyer(); // Length 3
            List<Coordinate> positions = List.of(
                new Coordinate(0, 0),
                new Coordinate(0, 1),
                new Coordinate(0, 2)
            );

            // When - Setup
            ship.setDirection(Direction.VERTICAL);
            ship.setPositions(positions);

            // Then
            assertEquals(Direction.VERTICAL, ship.getDirection());
            assertEquals(3, ship.getPositions().size());
            assertFalse(ship.isDestroyed());

            // When - Take damage
            ship.receiveDamage();
            assertEquals(2, ship.getLife());
            assertFalse(ship.isDestroyed());

            ship.receiveDamage();
            assertEquals(1, ship.getLife());
            assertFalse(ship.isDestroyed());

            ship.receiveDamage();
            assertEquals(0, ship.getLife());
            assertTrue(ship.isDestroyed());
        }

        @Test
        @DisplayName("Should verify position occupancy after placement")
        void shouldVerifyPositionOccupancyAfterPlacement() {
            // Given
            Ship cruiser = new Cruiser(); // Length 4
            List<Coordinate> positions = List.of(
                new Coordinate(3, 5),
                new Coordinate(4, 5),
                new Coordinate(5, 5),
                new Coordinate(6, 5)
            );

            // When
            cruiser.setDirection(Direction.HORIZONTAL);
            cruiser.setPositions(positions);

            // Then
            assertTrue(cruiser.occupiesPositions(new Coordinate(3, 5)));
            assertTrue(cruiser.occupiesPositions(new Coordinate(4, 5)));
            assertTrue(cruiser.occupiesPositions(new Coordinate(5, 5)));
            assertTrue(cruiser.occupiesPositions(new Coordinate(6, 5)));
            assertFalse(cruiser.occupiesPositions(new Coordinate(2, 5)));
            assertFalse(cruiser.occupiesPositions(new Coordinate(7, 5)));
            assertFalse(cruiser.occupiesPositions(new Coordinate(3, 4)));
        }
    }
}