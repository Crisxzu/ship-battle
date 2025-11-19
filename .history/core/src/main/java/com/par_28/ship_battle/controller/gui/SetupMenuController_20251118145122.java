package com.par_28.ship_battle.controller.gui;

import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.par_28.ship_battle.model.Carrier;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.model.Cruiser;
import com.par_28.ship_battle.model.Destroyer;
import com.par_28.ship_battle.model.Game;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.Torpedo;
import com.par_28.ship_battle.model.enums.Direction;
import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import com.par_28.ship_battle.model.exceptions.ShipPlacementException;
import com.par_28.ship_battle.view.gui.SetupPlayerNameView;
import com.par_28.ship_battle.view.gui.SetupPlayerShipView;
import com.par_28.ship_battle.view.gui.SoundHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Manager of the setup flow: collects player names and drives ship placement.
 */
public class SetupMenuController extends GuiController {
    /**
     * Entered player names.
     */
    private final List<String> names = new ArrayList<>();

    /**
     * Created players matching {@link #names}.
     */
    private final List<Player> players = new ArrayList<>();

    /**
     * Counter of placed ships for the active player.
     */
    private final Map<ShipTemplate, Integer> placedShips = new EnumMap<>(ShipTemplate.class);

    /**
     * Index of the player currently placing ships.
     */
    private int currentPlayerIndex = 0;

    /**
     * Whether ship placement has started.
     */
    private boolean placementPhaseStarted = false;

    /**
     * Initialize the menu controller.
     *
     * @param parent Reference to the parent screen controller
     */
    public SetupMenuController(ScreenController parent) {
        super(parent);
        resetPlacedShips();
    }

    /**
     * Update menu controller.
     *
     * @param dt Delta time since last update
     */
    @Override
    public void update(float dt){

    }

    /**
     * Add a player name to the setup.
     *
     * @param name Player name to add
     * @return true if name added successfully, false otherwise
     */
    public boolean addName(String name) {
        String trimmed = name == null ? "" : name.trim();
        if(trimmed.isEmpty()) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * this.parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(view.stage, "Please enter a name");
            return false;
        }

        if(names.stream().anyMatch(existing -> existing.equalsIgnoreCase(trimmed))) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * this.parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(
                view.stage,
                String.format("%s already registered", trimmed)
            );
            return false;
        }

        this.names.add(trimmed);

        if(this.names.size() >= this.parent.app.nbPlayers) {
            preparePlayers();
            startPlacementPhase();
        }

        return true;
    }

    private void preparePlayers() {
        players.clear();
        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(names.get(i), this.parent.app.gridSize);
            players.add(player);
            if (i == 0) {
                this.parent.app.player1 = player;
            } else if (i == 1) {
                this.parent.app.player2 = player;
            }
        }
    }

    private void startPlacementPhase() {
        placementPhaseStarted = true;
        currentPlayerIndex = 0;
        resetPlacedShips();
        changeView(new SetupPlayerShipView(parent, this));
        notifyPlacementChanged();
    }

    private void resetPlacedShips() {
        placedShips.clear();
        for (ShipTemplate template : ShipTemplate.values()) {
            placedShips.put(template, 0);
        }
    }

    /**
     * Attempt to place a ship on the current player's grid.
     *
     * @param template ship template to place
     * @param x grid x coordinate
     * @param y grid y coordinate
     * @param direction placement direction
     * @return placement result with success flag and message
     */
    public PlacementResult tryPlaceShip(ShipTemplate template, int x, int y, Direction direction) {
        if(!placementPhaseStarted) {
            return new PlacementResult(false, "Placement phase not started yet");
        }

        if(template == null) {
            return new PlacementResult(false, "Select a ship to place");
        }

        if(!canPlaceShip(template)) {
            return new PlacementResult(false, "All ships of this type are already placed");
        }

        Player currentPlayer = getCurrentPlayer();
        if(currentPlayer == null) {
            return new PlacementResult(false, "No player available for placement");
        }

        Coordinate coordinate = new Coordinate(x, y);
        Ship ship = template.createShip();
        try {
            currentPlayer.placeShipOnGrid(ship, coordinate, direction);
            currentPlayer.addShip(ship);
            placedShips.computeIfPresent(template, (key, value) -> value + 1);
            notifyPlacementChanged();
            return new PlacementResult(true, String.format("%s placed", template.getDisplayName()));
        } catch (InvalidCoordinateException | ShipPlacementException e) {
            return new PlacementResult(false, e.getMessage());
        }
    }

    /**
     * Reset ships for the current player and clear their grid.
     */
    public void resetCurrentPlayerPlacements() {
        if(!placementPhaseStarted) {
            return;
        }

        Player currentPlayer = getCurrentPlayer();
        if(currentPlayer == null) {
            return;
        }

        Player freshPlayer = new Player(currentPlayer.getName(), this.parent.app.gridSize);
        players.set(currentPlayerIndex, freshPlayer);
        if(currentPlayerIndex == 0) {
            this.parent.app.player1 = freshPlayer;
        } else {
            this.parent.app.player2 = freshPlayer;
        }
        resetPlacedShips();
        notifyPlacementChanged();
    }

    /**
     * Indicates whether the active player has placed all ships.
     *
     * @return true if fleet is complete
     */
    public boolean isFleetCompleteForCurrentPlayer() {
        return placedShips.entrySet().stream()
            .allMatch(entry -> entry.getValue() >= entry.getKey().getRequiredAmount());
    }

    /**
     * Confirm that the current player finished placements and progress to the next stage.
     *
     * @return true if the confirmation is valid, false otherwise
     */
    public boolean confirmCurrentPlayerReady() {
        if(!placementPhaseStarted || !isFleetCompleteForCurrentPlayer()) {
            return false;
        }

        if(currentPlayerIndex + 1 < players.size()) {
            currentPlayerIndex++;
            resetPlacedShips();
            notifyPlacementChanged();
        } else {
            finalizeSetup();
        }
        return true;
    }

    private void finalizeSetup() {
        if(players.size() < 2) {
            return;
        }

        this.parent.app.player1 = players.get(0);
        this.parent.app.player2 = players.get(1);
        this.parent.app.game = new Game(this.parent.app.player1, this.parent.app.player2);
        this.parent.app.game.start();
        placementPhaseStarted = false;
        parent.changeController(GuiControllerEnum.GAME);
    }

    /**
     * Get the player currently placing ships.
     *
     * @return active player or {@code null}
     */
    public Player getCurrentPlayer() {
        if(!placementPhaseStarted || players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Get the active player's index (0-based).
     *
     * @return index of active player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Number of players taking part in the setup.
     *
     * @return number of players
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Provide the number of ships placed/required for each template.
     *
     * @return list of ship status models
     */
    public List<ShipStatus> getShipStatuses() {
        List<ShipStatus> statuses = new ArrayList<>();
        for (ShipTemplate template : ShipTemplate.values()) {
            int placed = placedShips.getOrDefault(template, 0);
            statuses.add(new ShipStatus(template, placed, template.getRequiredAmount()));
        }
        return statuses;
    }

    /**
     * Suggest the next ship template with remaining units.
     *
     * @return template with remaining ships or null if all placed
     */
    public ShipTemplate getNextAvailableTemplate() {
        for (ShipTemplate template : ShipTemplate.values()) {
            if(canPlaceShip(template)) {
                return template;
            }
        }
        return null;
    }

    /**
     * Check if at least one ship of this template can still be placed.
     *
     * @param template ship template
     * @return true if placement is still allowed
     */
    public boolean canPlaceShip(ShipTemplate template) {
        if(template == null) {
            return false;
        }
        return placedShips.getOrDefault(template, 0) < template.getRequiredAmount();
    }

    private void notifyPlacementChanged() {
        if(this.view instanceof SetupPlayerShipView shipView) {
            shipView.refreshState();
        }
    }

    /**
     * Ship template definition used by the setup view.
     */
    public enum ShipTemplate {
        CARRIER("Carrier", 5, 1) {
            @Override
            public Ship createShip() {
                return new Carrier();
            }
        },
        CRUISER("Cruiser", 4, 1) {
            @Override
            public Ship createShip() {
                return new Cruiser();
            }
        },
        DESTROYER("Destroyer", 3, 2) {
            @Override
            public Ship createShip() {
                return new Destroyer();
            }
        },
        TORPEDO("Torpedo", 2, 1) {
            @Override
            public Ship createShip() {
                return new Torpedo();
            }
        };

        private final String displayName;
        private final int length;
        private final int requiredAmount;

        ShipTemplate(String displayName, int length, int requiredAmount) {
            this.displayName = displayName;
            this.length = length;
            this.requiredAmount = requiredAmount;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getLength() {
            return length;
        }

        public int getRequiredAmount() {
            return requiredAmount;
        }

        public abstract Ship createShip();
    }

    /**
     * View-facing representation of how many ships were placed.
     *
     * @param template ship template
     * @param placed number already placed
     * @param required required number
     */
    public record ShipStatus(ShipTemplate template, int placed, int required) {
        public int remaining() {
            return Math.max(0, required - placed);
        }
    }

    /**
     * Result of a placement attempt.
     *
     * @param success placement success flag
     * @param message details for the UI
     */
    public record PlacementResult(boolean success, String message) { }

    /**
     * Render menu view.
     *
     * @param dt Delta time since last render
     */
    @Override
    public void render(float dt){
        super.render(dt);
        if(view != null) {
            view.render(dt);
        }
    }

    /**
     * Reset menu to initial state.
     */
    @Override
    public void reset() {
        names.clear();
        players.clear();
        placementPhaseStarted = false;
        currentPlayerIndex = 0;
        resetPlacedShips();
        changeView(new SetupPlayerNameView(this.parent, this));
    }
}
