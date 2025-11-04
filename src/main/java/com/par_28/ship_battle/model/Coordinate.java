package com.par_28.ship_battle.model;
import java.util.Objects;

public class Coordinate {
    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** 
     * Accepts "x,y" (e.g. "3,5") OR "A1" style (letters then digits).
     * For "A1": column letter(s) -> x (A=0), number -> y (1 -> 0).
     */
    public static Coordinate fromString(String coord) {
        if (coord == null) throw new IllegalArgumentException("coord is null");
        coord = coord.trim().toUpperCase();

        // x,y format
        if (coord.contains(",")) {
            String[] parts = coord.split(",");
            if (parts.length != 2) throw new IllegalArgumentException("Invalid coordinate format");
            try {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                return new Coordinate(x, y);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer in coordinate", e);
            }
        }

        // Letter(s)+digits format e.g. "A1", "BC12"
        int i = 0;
        while (i < coord.length() && Character.isLetter(coord.charAt(i))) i++;
        if (i == 0 || i == coord.length())
            throw new IllegalArgumentException("Invalid coordinate format");

        String letters = coord.substring(0, i);
        String numbers = coord.substring(i);
        // convert letters to number (A=0, B=1, ...). Support multi-letter (e.g. "AA").
        int x = 0;
        for (char c : letters.toCharArray()) {
            x = x * 26 + (c - 'A' + 1);
        }
        x = x - 1; // make zero-based

        try {
            int y = Integer.parseInt(numbers) - 1; // "1" -> 0
            return new Coordinate(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in coordinate", e);
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
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}