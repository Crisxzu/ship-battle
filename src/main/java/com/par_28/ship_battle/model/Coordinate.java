package com.par_28.ship_battle.model;

import java.util.Objects;

import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;

/**
 * Coordinate model representing a position on the grid.
 * <p>
 * Supports construction from integers and from string formats such as:
 * - "x,y" (e.g. "3,5") where x and y are zero-based integers
 * - "A1" style (letters for column, numbers for row; "A1" => (0,0))
 * <p>
 * Throws {@link com.par_28.ship_battle.model.exceptions.InvalidCoordinateException}
 * when parsing a malformed coordinate string.
 */
public class Coordinate {
    /**
     * Zero-based x value.
     */
    private final int x;
    /**
     * Zero-based y value.
     */
    private final int y;

    /**
     * Create a coordinate with zero-based x and y.
     *
     * @param x column index (0-based)
     * @param y row index (0-based)
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Parse a coordinate from a string. Accepts "x,y" or "A1" formats.
     * <p>
     * We support both formats to make it easier for users to input coordinates.
     * And user can use uppercase or lowercase letters and multiple letters for columns.
     * </p>
     *
     * @param coord input string
     * @return parsed Coordinate
     * @throws InvalidCoordinateException if input invalid
     */
    public static Coordinate fromString(String coord) throws InvalidCoordinateException {
        if (coord == null) {
            throw new InvalidCoordinateException(coord);
        }

        String s = coord.trim().toUpperCase();

        try {
            if (s.contains(",")) {
                String[] parts = s.split(",");

                if (parts.length != 2) 
                    throw new InvalidCoordinateException(coord);

                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());

                return new Coordinate(x, y);
            } 
            else {
                // Letters then digits: "A1", "BC12"
                int i = 0;

                // Get letters length
                while (i < s.length() && Character.isLetter(s.charAt(i))) 
                    i++;
                
                // if no letters or no digits found
                if (i == 0 || i == s.length()) 
                    throw new InvalidCoordinateException(coord);
                
                String letters = s.substring(0, i);
                String numbers = s.substring(i);

                int x = 0;

                // Here it is a bit technical, we found it on the web actually...
                // To do arithmetic operation on char, each char are converted to int using its ASCII value
                // So A = 65, B = 66, ..., Z = 90 (we use uppercase earlier)
                // And with that if we suppress 65 to each char we get A = 0, B = 1, ..., Z = 25
                // So his position in the alphabet
                // And if we support multiple letters like "AA" for 26, "AB" for 27, ..., "AZ" for 51, "BA" for 52, ...
                // At each new letter we have to multiply the previous result by 26 (number of letters in the alphabet)
                // So with that we can calculate the column index
                for (char c : letters.toCharArray()) {
                    x = x * 26 + (c - 'A' + 1);
                }

                x = x - 1; // zero-based
                int y = Integer.parseInt(numbers) - 1; // "1" -> 0
                return new Coordinate(x, y);
            }
        } catch (Exception e) {            
            throw new InvalidCoordinateException(coord);
        }
    }

    /**
     * Get the zero-based x value.
     * 
     * @return x value
     */
    public int getX() { 
        return x; 
    }

    /**
     * Get the zero-based y value.
     * 
     * @return y value
     */
    public int getY() { 
        return y; 
    }

    /**
     * String representation of coordinate.
     * 
     * @return "x,y" format
     */
    @Override
    public String toString() {
        return x + "," + y;
    }

    /**
     * Equality check based on x and y values.
     * 
     * @param o other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } 
        if (!(o instanceof Coordinate)) {
            return false;
        } 
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    /**
     * Hash code based on x and y.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}