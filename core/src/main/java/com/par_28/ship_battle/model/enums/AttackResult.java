package com.par_28.ship_battle.model.enums;

/**
 * Enum representing the possible results of an attack in the ship battle game.
 */
public enum AttackResult {
    /**
     * The attack missed (no ship at the coordinate).
     */
    MISS,
    /**
     * The attack hit a ship but did not sink it.
     */
    HIT,
    /**
     * The attack hit and sank a ship.
     */
    SUNK,
    /**
     * The coordinate was already hit before.
     */
    ALREADY_HIT,

    RADAR_USED
}
