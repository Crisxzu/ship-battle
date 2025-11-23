package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.AttackResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AttackResponse class
 * Tests all constructors, getters, and utility methods
 */
@DisplayName("AttackResponse Tests")
class AttackResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create basic AttackResponse with result and ship")
        void shouldCreateBasicAttackResponse() {
            // Given
            Ship ship = new Destroyer();

            // When
            AttackResponse response = new AttackResponse(AttackResult.HIT, ship);

            // Then
            assertEquals(AttackResult.HIT, response.getResult());
            assertEquals(ship, response.getShip());
            assertNull(response.getCoordinate());
            assertTrue(response.getAdditionalHits().isEmpty());
            assertFalse(response.isRadarDetection());
        }

        @Test
        @DisplayName("Should create AttackResponse with coordinate")
        void shouldCreateAttackResponseWithCoordinate() {
            // Given
            Ship ship = new Cruiser();
            Coordinate coord = new Coordinate(3, 4);

            // When
            AttackResponse response = new AttackResponse(AttackResult.SUNK, ship, coord);

            // Then
            assertEquals(AttackResult.SUNK, response.getResult());
            assertEquals(ship, response.getShip());
            assertEquals(coord, response.getCoordinate());
            assertTrue(response.getAdditionalHits().isEmpty());
        }

        @Test
        @DisplayName("Should create bomb attack response with additional hits")
        void shouldCreateBombAttackResponse() {
            // Given
            Ship ship = new Carrier();
            Coordinate coord = new Coordinate(5, 5);
            List<AttackResponse> additionalHits = new ArrayList<>();
            additionalHits.add(new AttackResponse(AttackResult.MISS, null, new Coordinate(4, 5)));
            additionalHits.add(new AttackResponse(AttackResult.HIT, ship, new Coordinate(6, 5)));

            // When
            AttackResponse response = new AttackResponse(AttackResult.HIT, ship, coord, additionalHits);

            // Then
            assertEquals(AttackResult.HIT, response.getResult());
            assertEquals(ship, response.getShip());
            assertEquals(coord, response.getCoordinate());
            assertEquals(2, response.getAdditionalHits().size());
            assertTrue(response.isBombAttack());
        }

        @Test
        @DisplayName("Should create radar scan response")
        void shouldCreateRadarScanResponse() {
            // Given
            Coordinate coord = new Coordinate(7, 8);

            // When
            AttackResponse response = new AttackResponse(coord, true);

            // Then
            assertEquals(AttackResult.RADAR_USED, response.getResult());
            assertNull(response.getShip());
            assertEquals(coord, response.getCoordinate());
            assertTrue(response.isRadarDetection());
        }

        @Test
        @DisplayName("Should create radar scan response with no detection")
        void shouldCreateRadarScanResponseNoDetection() {
            // Given
            Coordinate coord = new Coordinate(2, 3);

            // When
            AttackResponse response = new AttackResponse(coord, false);

            // Then
            assertEquals(AttackResult.RADAR_USED, response.getResult());
            assertFalse(response.isRadarDetection());
        }

        @Test
        @DisplayName("Should handle null ship in constructor")
        void shouldHandleNullShip() {
            // When
            AttackResponse response = new AttackResponse(AttackResult.MISS, null);

            // Then
            assertEquals(AttackResult.MISS, response.getResult());
            assertNull(response.getShip());
        }

        @Test
        @DisplayName("Should handle null additional hits list")
        void shouldHandleNullAdditionalHits() {
            // Given
            Ship ship = new Torpedo();
            Coordinate coord = new Coordinate(1, 1);

            // When
            AttackResponse response = new AttackResponse(AttackResult.HIT, ship, coord, null);

            // Then
            assertNotNull(response.getAdditionalHits());
            assertTrue(response.getAdditionalHits().isEmpty());
        }
    }

    @Nested
    @DisplayName("isHit Tests")
    class IsHitTests {

        @Test
        @DisplayName("Should return true for HIT result")
        void shouldReturnTrueForHit() {
            // Given
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Destroyer());

            // When & Then
            assertTrue(response.isHit());
        }

        @Test
        @DisplayName("Should return true for SUNK result")
        void shouldReturnTrueForSunk() {
            // Given
            AttackResponse response = new AttackResponse(AttackResult.SUNK, new Torpedo());

            // When & Then
            assertTrue(response.isHit());
        }

        @Test
        @DisplayName("Should return false for MISS result")
        void shouldReturnFalseForMiss() {
            // Given
            AttackResponse response = new AttackResponse(AttackResult.MISS, null);

            // When & Then
            assertFalse(response.isHit());
        }

        @Test
        @DisplayName("Should return false for ALREADY_HIT result")
        void shouldReturnFalseForAlreadyHit() {
            // Given
            AttackResponse response = new AttackResponse(AttackResult.ALREADY_HIT, new Cruiser());

            // When & Then
            assertFalse(response.isHit());
        }

        @Test
        @DisplayName("Should return false for RADAR_USED result")
        void shouldReturnFalseForRadarUsed() {
            // Given
            AttackResponse response = new AttackResponse(new Coordinate(0, 0), true);

            // When & Then
            assertFalse(response.isHit());
        }
    }

    @Nested
    @DisplayName("isBombAttack Tests")
    class IsBombAttackTests {

        @Test
        @DisplayName("Should return true when additional hits exist")
        void shouldReturnTrueWhenAdditionalHitsExist() {
            // Given
            List<AttackResponse> additionalHits = new ArrayList<>();
            additionalHits.add(new AttackResponse(AttackResult.MISS, null));
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Destroyer(), new Coordinate(5, 5), additionalHits);

            // When & Then
            assertTrue(response.isBombAttack());
        }

        @Test
        @DisplayName("Should return false when no additional hits")
        void shouldReturnFalseWhenNoAdditionalHits() {
            // Given
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Destroyer());

            // When & Then
            assertFalse(response.isBombAttack());
        }

        @Test
        @DisplayName("Should return false for empty additional hits list")
        void shouldReturnFalseForEmptyAdditionalHits() {
            // Given
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Carrier(), new Coordinate(3, 3), new ArrayList<>());

            // When & Then
            assertFalse(response.isBombAttack());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return unmodifiable additional hits list")
        void shouldReturnUnmodifiableAdditionalHitsList() {
            // Given
            List<AttackResponse> additionalHits = new ArrayList<>();
            additionalHits.add(new AttackResponse(AttackResult.MISS, null));
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Destroyer(), new Coordinate(5, 5), additionalHits);

            // When & Then
            assertThrows(UnsupportedOperationException.class, () -> {
                response.getAdditionalHits().add(new AttackResponse(AttackResult.MISS, null));
            });
        }

        @Test
        @DisplayName("Should not modify original list when external list is modified")
        void shouldNotModifyOriginalList() {
            // Given
            List<AttackResponse> additionalHits = new ArrayList<>();
            additionalHits.add(new AttackResponse(AttackResult.MISS, null));
            AttackResponse response = new AttackResponse(AttackResult.HIT, new Destroyer(), new Coordinate(5, 5), additionalHits);

            // When - Modify external list
            additionalHits.add(new AttackResponse(AttackResult.HIT, new Carrier()));

            // Then - Internal list should not be affected
            assertEquals(1, response.getAdditionalHits().size());
        }
    }
}