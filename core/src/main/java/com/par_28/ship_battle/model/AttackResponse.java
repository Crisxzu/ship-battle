package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.AttackResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the response to an attack on a ship.
 * <p>
 * Contains the result of the attack and the targeted ship.
 * For multi-cell attacks (like bombs), contains additional affected cells.
 * For radar scans, contains detection information.
 * </p>
 * @author James
 * @version 1.0
 * @since 1.0
 */
public class AttackResponse {

    /**
     * The result of the attack (MISS, HIT, SUNK, etc.)
     */
    private final AttackResult result;

    /**
     * The ship that was targeted by the attack
     */
    private final Ship ship;

    /**
     * List of additional attack responses for multi-cell attacks (e.g., bomb)
     */
    private final List<AttackResponse>  additionalHits;

    /**
     * Radar detection result (true if ships detected in area)
     */
    private final boolean radarDetection;

    /**
     * Coordinate of the attack
     */
    private final Coordinate coordinate;

    /**
     * Constructor for AttackResponse.
     *
     * @param result the result of the attack
     * @param ship the ship that was targeted
     */
    public AttackResponse(AttackResult result, Ship ship) {
        this(result, ship, null, new ArrayList<>(), false);
    }

    /**
     * Constructor for AttackResponse with coordinate.
     *
     * @param result the result of the attack
     * @param ship the ship that was targeted
     * @param coordinate the coordinate of the attack
     */
    public AttackResponse(AttackResult result, Ship ship, Coordinate coordinate) {
        this(result, ship, coordinate, new ArrayList<>(), false);
    }

    /**
     * Constructor for bomb attack response with multiple hits.
     *
     * @param result the result of the primary attack
     * @param ship the ship that was targeted
     * @param coordinate the coordinate of the attack
     * @param additionalHits list of responses from adjacent cells
     */
    public AttackResponse(AttackResult result, Ship ship, Coordinate coordinate, List<AttackResponse> additionalHits) {
        this(result, ship, coordinate, additionalHits, false);
    }

    /**
     * Constructor for radar scan response.
     *
     * @param coordinate the coordinate of the radar scan
     * @param radarDetection true if ships were detected
     */
    public AttackResponse(Coordinate coordinate, boolean radarDetection) {
        this(AttackResult.RADAR_USED, null, coordinate, new ArrayList<>(), radarDetection);
    }

    /**
     * Full constructor for AttackResponse.
     *
     * @param result the result of the attack
     * @param ship the ship that was targeted
     * @param coordinate the coordinate of the attack
     * @param additionalHits list of responses from adjacent cells
     * @param radarDetection radar detection result
     */
    private AttackResponse(AttackResult result, Ship ship, Coordinate coordinate, List<AttackResponse> additionalHits, boolean radarDetection) {
        this.result = result;
        this.ship = ship;
        this.coordinate = coordinate;
        this.additionalHits = additionalHits != null ? new ArrayList<>(additionalHits) : new ArrayList<>();
        this.radarDetection = radarDetection;
    }

    /**
     * Get the result of the attack.
     *
     * @return the attack result
     */
    public AttackResult getResult() {
        return result;
    }

    /**
     * Get the ship that was targeted by the attack.
     *
     * @return the targeted ship
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Get the coordinate of the attack.
     *
     * @return the coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Get additional hits from multi-cell attacks.
     *
     * @return unmodifiable list of additional attack responses
     */
    public List<AttackResponse> getAdditionalHits() {
        return Collections.unmodifiableList(additionalHits);
    }

    /**
     * Check if radar detected ships in the scanned area.
     *
     * @return true if ships were detected
     */
    public boolean isRadarDetection() {
        return radarDetection;
    }

    /**
     * Check if the attack was a hit.
     *
     * @return true if the attack hit a ship, false otherwise
     */
    public boolean isHit() {
        return result == AttackResult.HIT || result == AttackResult.SUNK;
    }

    /**
     * Check if this is a bomb attack (has additional hits).
     *
     * @return true if this is a bomb attack
     */
    public boolean isBombAttack() {
        return !additionalHits.isEmpty();
    }
}
