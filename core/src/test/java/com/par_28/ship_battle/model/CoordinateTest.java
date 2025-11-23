package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Coordinate class
 * Tests coordinate creation, parsing, equality, and string conversion
 */
@DisplayName("Coordinate Tests")
class CoordinateTest {

    @Nested
    @DisplayName("Coordinate Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create coordinate with valid x and y")
        void shouldCreateCoordinateWithValidXAndY() {
            // When
            Coordinate coord = new Coordinate(3, 5);

            // Then
            assertEquals(3, coord.getX());
            assertEquals(5, coord.getY());
        }

        @Test
        @DisplayName("Should create coordinate with zero values")
        void shouldCreateCoordinateWithZeroValues() {
            // When
            Coordinate coord = new Coordinate(0, 0);

            // Then
            assertEquals(0, coord.getX());
            assertEquals(0, coord.getY());
        }

        @Test
        @DisplayName("Should create coordinate with negative values")
        void shouldCreateCoordinateWithNegativeValues() {
            // When
            Coordinate coord = new Coordinate(-1, -5);

            // Then
            assertEquals(-1, coord.getX());
            assertEquals(-5, coord.getY());
        }

        @Test
        @DisplayName("Should create coordinate with large values")
        void shouldCreateCoordinateWithLargeValues() {
            // When
            Coordinate coord = new Coordinate(100, 200);

            // Then
            assertEquals(100, coord.getX());
            assertEquals(200, coord.getY());
        }
    }

    @Nested
    @DisplayName("Coordinate Parsing Tests - Comma Format")
    class CommaFormatParsingTests {

        @Test
        @DisplayName("Should parse simple comma-separated format")
        void shouldParseSimpleCommaFormat() {
            // When
            Coordinate coord = Coordinate.fromString("3,5");

            // Then
            assertEquals(3, coord.getX());
            assertEquals(5, coord.getY());
        }

        @Test
        @DisplayName("Should parse comma format with spaces")
        void shouldParseCommaFormatWithSpaces() {
            // When
            Coordinate coord = Coordinate.fromString("  7 , 9  ");

            // Then
            assertEquals(7, coord.getX());
            assertEquals(9, coord.getY());
        }

        @Test
        @DisplayName("Should parse comma format with zero values")
        void shouldParseCommaFormatWithZero() {
            // When
            Coordinate coord = Coordinate.fromString("0,0");

            // Then
            assertEquals(0, coord.getX());
            assertEquals(0, coord.getY());
        }

        @Test
        @DisplayName("Should throw exception for invalid comma format with too many parts")
        void shouldThrowExceptionForTooManyParts() {
            // When & Then
            InvalidCoordinateException exception = assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("1,2,3");
            });

            assertTrue(exception.getMessage().contains("invalid"));
        }

        @Test
        @DisplayName("Should throw exception for invalid comma format with non-numeric values")
        void shouldThrowExceptionForNonNumericCommaFormat() {
            // When & Then
            InvalidCoordinateException exception = assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("a,b");
            });

            assertTrue(exception.getMessage().contains("invalid"));
        }

        @Test
        @DisplayName("Should throw exception for single comma without values")
        void shouldThrowExceptionForSingleComma() {
            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString(",");
            });
        }
    }

    @Nested
    @DisplayName("Coordinate Parsing Tests - Letter Format")
    class LetterFormatParsingTests {

        @Test
        @DisplayName("Should parse A1 format")
        void shouldParseA1Format() {
            // When
            Coordinate coord = Coordinate.fromString("A1");

            // Then
            assertEquals(0, coord.getX()); // A = 0
            assertEquals(0, coord.getY()); // 1 -> 0
        }

        @Test
        @DisplayName("Should parse single letter with number")
        void shouldParseSingleLetterFormat() {
            // When
            Coordinate coord1 = Coordinate.fromString("B5");
            Coordinate coord2 = Coordinate.fromString("C10");

            // Then
            assertEquals(1, coord1.getX()); // B = 1
            assertEquals(4, coord1.getY()); // 5 -> 4

            assertEquals(2, coord2.getX()); // C = 2
            assertEquals(9, coord2.getY()); // 10 -> 9
        }

        @Test
        @DisplayName("Should parse letter format case insensitive")
        void shouldParseLetterFormatCaseInsensitive() {
            // When
            Coordinate coord1 = Coordinate.fromString("a1");
            Coordinate coord2 = Coordinate.fromString("A1");
            Coordinate coord3 = Coordinate.fromString("z7");

            // Then
            assertEquals(0, coord1.getX());
            assertEquals(0, coord1.getY());

            assertEquals(0, coord2.getX());
            assertEquals(0, coord2.getY());

            assertEquals(25, coord3.getX()); // D = 3
            assertEquals(6, coord3.getY()); // 7 -> 6
        }

        @Test
        @DisplayName("Should parse letter format with spaces")
        void shouldParseLetterFormatWithSpaces() {
            // When
            Coordinate coord = Coordinate.fromString("  E3  ");

            // Then
            assertEquals(4, coord.getX()); // E = 4
            assertEquals(2, coord.getY()); // 3 -> 2
        }

        @Test
        @DisplayName("Should parse multi-letter column format")
        void shouldParseMultiLetterFormat() {
            // When
            Coordinate coord = Coordinate.fromString("AA1");

            // Then
            // AA = 26 (A=1, so A*26 + A = 26 + 1 = 27, then -1 = 26)
            assertEquals(26, coord.getX());
            assertEquals(0, coord.getY());
        }

        @Test
        @DisplayName("Should throw exception for letters only")
        void shouldThrowExceptionForLettersOnly() {
            // When & Then
            InvalidCoordinateException exception = assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("ABC");
            });

            assertTrue(exception.getMessage().contains("invalid"));
        }

        @Test
        @DisplayName("Should throw exception for numbers only without comma")
        void shouldThrowExceptionForNumbersOnly() {
            // When & Then
            InvalidCoordinateException exception = assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("123");
            });

            assertTrue(exception.getMessage().contains("invalid"));
        }

        @Test
        @DisplayName("Should throw exception for invalid number in letter format")
        void shouldThrowExceptionForInvalidNumberInLetterFormat() {
            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("A1.2");
            });
        }

        @Test
        @DisplayName("Should parse large row numbers")
        void shouldParseLargeRowNumbers() {
            // When
            Coordinate coord = Coordinate.fromString("A100");

            // Then
            assertEquals(0, coord.getX()); // A = 0
            assertEquals(99, coord.getY()); // 100 -> 99
        }
    }

    @Nested
    @DisplayName("Coordinate Parsing Error Handling Tests")
    class ParsingErrorTests {

        @Test
        @DisplayName("Should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            // When & Then
            InvalidCoordinateException exception = assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString(null);
            });

            assertTrue(exception.getMessage().contains("invalid") || exception.getMessage().contains("null"));
        }

        @Test
        @DisplayName("Should throw exception for empty string")
        void shouldThrowExceptionForEmptyString() {
            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("");
            });
        }

        @Test
        @DisplayName("Should throw exception for whitespace only")
        void shouldThrowExceptionForWhitespaceOnly() {
            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("   ");
            });
        }

        @Test
        @DisplayName("Should throw exception for special characters")
        void shouldThrowExceptionForSpecialCharacters() {
            // When & Then
            assertThrows(InvalidCoordinateException.class, () -> {
                Coordinate.fromString("@#$");
            });
        }
    }

    @Nested
    @DisplayName("Coordinate Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            Coordinate coord = new Coordinate(3, 5);

            // When & Then
            assertEquals(coord, coord);
        }

        @Test
        @DisplayName("Should be equal to coordinate with same x and y")
        void shouldBeEqualToSameCoordinate() {
            // Given
            Coordinate coord1 = new Coordinate(3, 5);
            Coordinate coord2 = new Coordinate(3, 5);

            // When & Then
            assertEquals(coord1, coord2);
            assertEquals(coord2, coord1);
        }

        @Test
        @DisplayName("Should not be equal to coordinate with different x")
        void shouldNotBeEqualToDifferentX() {
            // Given
            Coordinate coord1 = new Coordinate(3, 5);
            Coordinate coord2 = new Coordinate(4, 5);

            // When & Then
            assertNotEquals(coord1, coord2);
        }

        @Test
        @DisplayName("Should not be equal to coordinate with different y")
        void shouldNotBeEqualToDifferentY() {
            // Given
            Coordinate coord1 = new Coordinate(3, 5);
            Coordinate coord2 = new Coordinate(3, 6);

            // When & Then
            assertNotEquals(coord1, coord2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Coordinate coord = new Coordinate(3, 5);

            // When & Then
            assertNotEquals(coord, null);
        }

        @Test
        @DisplayName("Should not be equal to different object type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            Coordinate coord = new Coordinate(3, 5);
            String notACoordinate = "3,5";

            // When & Then
            assertNotEquals(coord, notACoordinate);
        }

        @Test
        @DisplayName("Should handle equality with zero coordinates")
        void shouldHandleEqualityWithZeroCoordinates() {
            // Given
            Coordinate coord1 = new Coordinate(0, 0);
            Coordinate coord2 = new Coordinate(0, 0);

            // When & Then
            assertEquals(coord1, coord2);
        }

        @Test
        @DisplayName("Should handle equality with negative coordinates")
        void shouldHandleEqualityWithNegativeCoordinates() {
            // Given
            Coordinate coord1 = new Coordinate(-1, -5);
            Coordinate coord2 = new Coordinate(-1, -5);

            // When & Then
            assertEquals(coord1, coord2);
        }
    }

    @Nested
    @DisplayName("Coordinate HashCode Tests")
    class HashCodeTests {

        @Test
        @DisplayName("Should have same hash code for equal coordinates")
        void shouldHaveSameHashCodeForEqualCoordinates() {
            // Given
            Coordinate coord1 = new Coordinate(3, 5);
            Coordinate coord2 = new Coordinate(3, 5);

            // When & Then
            assertEquals(coord1.hashCode(), coord2.hashCode());
        }

        @Test
        @DisplayName("Should be consistent across multiple calls")
        void shouldBeConsistentAcrossMultipleCalls() {
            // Given
            Coordinate coord = new Coordinate(7, 9);

            // When
            int hash1 = coord.hashCode();
            int hash2 = coord.hashCode();

            // Then
            assertEquals(hash1, hash2);
        }

        @Test
        @DisplayName("Should handle zero coordinates hash code")
        void shouldHandleZeroCoordinatesHashCode() {
            // Given
            Coordinate coord1 = new Coordinate(0, 0);
            Coordinate coord2 = new Coordinate(0, 0);

            // When & Then
            assertEquals(coord1.hashCode(), coord2.hashCode());
        }

        @Test
        @DisplayName("Different coordinates likely have different hash codes")
        void differentCoordinatesLikelyHaveDifferentHashCodes() {
            // Given
            Coordinate coord1 = new Coordinate(3, 5);
            Coordinate coord2 = new Coordinate(5, 3);
            Coordinate coord3 = new Coordinate(0, 0);

            // When
            int hash1 = coord1.hashCode();
            int hash2 = coord2.hashCode();
            int hash3 = coord3.hashCode();

            // Then - Different coordinates should likely have different hashes
            // (Not guaranteed by contract, but good implementation does this)
            assertTrue(hash1 != hash2 || hash1 != hash3 || hash2 != hash3,
                    "Different coordinates should likely have different hash codes");
        }
    }

    @Nested
    @DisplayName("Coordinate ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should convert to string in comma format")
        void shouldConvertToCommaFormat() {
            // Given
            Coordinate coord = new Coordinate(3, 5);

            // When
            String result = coord.toString();

            // Then
            assertEquals("3,5", result);
        }

        @Test
        @DisplayName("Should convert zero coordinates to string")
        void shouldConvertZeroCoordinatesToString() {
            // Given
            Coordinate coord = new Coordinate(0, 0);

            // When
            String result = coord.toString();

            // Then
            assertEquals("0,0", result);
        }

        @Test
        @DisplayName("Should convert negative coordinates to string")
        void shouldConvertNegativeCoordinatesToString() {
            // Given
            Coordinate coord = new Coordinate(-1, -5);

            // When
            String result = coord.toString();

            // Then
            assertEquals("-1,-5", result);
        }

        @Test
        @DisplayName("Should convert large coordinates to string")
        void shouldConvertLargeCoordinatesToString() {
            // Given
            Coordinate coord = new Coordinate(100, 200);

            // When
            String result = coord.toString();

            // Then
            assertEquals("100,200", result);
        }
    }

    @Nested
    @DisplayName("Coordinate ToLetterFormat Tests")
    class ToLetterFormatTests {

        @Test
        @DisplayName("Should convert (0,0) to A1")
        void shouldConvertOriginToA1() {
            // Given
            Coordinate coord = new Coordinate(0, 0);

            // When
            String result = coord.toLetterFormat();

            // Then
            assertEquals("A1", result);
        }

        @Test
        @DisplayName("Should convert single letter coordinates")
        void shouldConvertSingleLetterCoordinates() {
            // Given & When & Then
            assertEquals("B5", new Coordinate(1, 4).toLetterFormat());
            assertEquals("C10", new Coordinate(2, 9).toLetterFormat());
            assertEquals("Z1", new Coordinate(25, 0).toLetterFormat());
        }

        @Test
        @DisplayName("Should convert multi-letter columns")
        void shouldConvertMultiLetterColumns() {
            // Given
            Coordinate coord = new Coordinate(26, 0);

            // When
            String result = coord.toLetterFormat();

            // Then
            assertEquals("AA1", result);
        }

        @Test
        @DisplayName("Should convert to letter format with large row numbers")
        void shouldConvertLargeRowNumbers() {
            // Given
            Coordinate coord = new Coordinate(0, 99);

            // When
            String result = coord.toLetterFormat();

            // Then
            assertEquals("A100", result);
        }

        @Test
        @DisplayName("Should round-trip letter format correctly")
        void shouldRoundTripLetterFormat() {
            // Given
            Coordinate original = new Coordinate(2, 5);

            // When
            String letterFormat = original.toLetterFormat();
            Coordinate parsed = Coordinate.fromString(letterFormat);

            // Then
            assertEquals(original, parsed);
        }
    }

    @Nested
    @DisplayName("Coordinate Round-trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should round-trip comma format correctly")
        void shouldRoundTripCommaFormat() {
            // Given
            String original = "3,5";

            // When
            Coordinate coord = Coordinate.fromString(original);
            String result = coord.toString();

            // Then
            assertEquals(original, result);
        }

        @Test
        @DisplayName("Should parse and toString consistently")
        void shouldParseAndToStringConsistently() {
            // Given
            Coordinate originalCoord = new Coordinate(7, 9);

            // When
            String stringForm = originalCoord.toString();
            Coordinate parsedCoord = Coordinate.fromString(stringForm);

            // Then
            assertEquals(originalCoord, parsedCoord);
        }

        @Test
        @DisplayName("Should handle letter format to comma format conversion")
        void shouldHandleLetterToCommaConversion() {
            // Given
            String letterFormat = "B5";

            // When
            Coordinate coord = Coordinate.fromString(letterFormat);
            String commaFormat = coord.toString();

            // Then
            assertEquals("1,4", commaFormat); // B=1, 5->4
        }

        @Test
        @DisplayName("Coordinates parsed from different formats with same values should be equal")
        void coordinatesFromDifferentFormatsWithSameValuesShouldBeEqual() {
            // Given
            Coordinate fromComma = Coordinate.fromString("2,3");
            Coordinate fromLetter = Coordinate.fromString("C4"); // C=2, 4->3

            // When & Then
            assertEquals(fromComma, fromLetter);
            assertEquals(fromComma.hashCode(), fromLetter.hashCode());
        }
    }
}