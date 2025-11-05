package com.par_28.ship_battle.model;

import java.util.Objects;

import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;

/**
 * Coordinate model representing a position on the grid.
 * 
 * Supports construction from integers and from string formats such as:
 * - "x,y" (e.g. "3,5") where x and y are zero-based integers
 * - "A1" style (letters for column, numbers for row; "A1" => (0,0))
 * 
 * Throws {@link com.par_28.ship_battle.model.exceptions.InvalidCoordinateException}
 * when parsing a malformed coordinate string.
 */
public class Coordinate {
    private final int x;
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
     *
     * @param coord input string
     * @return parsed Coordinate
     * @throws InvalidCoordinateException if input invalid
     */
    public static Coordinate fromString(String coord) throws InvalidCoordinateException {
        if (coord == null) {
            throw new InvalidCoordinateException(new Coordinate(-1, -1));
        }

        String s = coord.trim().toUpperCase();
        try {
            if (s.contains(",")) {
                String[] parts = s.split(",");
                if (parts.length != 2) throw new IllegalArgumentException("Expected two integers separated by comma");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                return new Coordinate(x, y);
            } else {
                // Letters then digits: "A1", "BC12"
                int i = 0;
                while (i < s.length() && Character.isLetter(s.charAt(i))) i++;
                if (i == 0 || i == s.length()) throw new IllegalArgumentException("Invalid letter/number coordinate");
                String letters = s.substring(0, i);
                String numbers = s.substring(i);
                int x = 0;
                for (char c : letters.toCharArray()) {
                    x = x * 26 + (c - 'A' + 1);
                }
                x = x - 1; // zero-based
                int y = Integer.parseInt(numbers) - 1; // "1" -> 0
                return new Coordinate(x, y);
            }
        } catch (Exception e) {
            // Wrap any parsing error into your InvalidCoordinateException using a placeholder coordinate
            throw new InvalidCoordinateException(new Coordinate(-1, -1));
        }
    }

    public int getX() { 
        return x; 
    }
    public int getY() { 
        return y; 
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}