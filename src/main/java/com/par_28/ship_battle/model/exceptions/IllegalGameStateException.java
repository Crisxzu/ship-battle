package com.par_28.ship_battle.model.exceptions;

public class IllegalGameStateException extends RuntimeException {
    public IllegalGameStateException(String message) {
        super(message);
    }
}
