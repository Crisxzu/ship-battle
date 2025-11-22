package com.par_28.ship_battle.model.enums;

/**
 * Different types of attack powers available in the game.
 */
public enum PowerType {
    /**
     * Normal single-cell attack.
     */
    NORMAL,

    /**
     * Bomb attack that hits the target and adjacent cells (3x3 area).
     */
    BOMB,

    /**
     * Radar scan that detects if ships are present in an area without revealing exact positions.
     */
    RADAR
}
