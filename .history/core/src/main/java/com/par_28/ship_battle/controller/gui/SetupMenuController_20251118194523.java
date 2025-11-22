package com.par_28.ship_battle.controller.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.par_28.ship_battle.model.Carrier;
import com.par_28.ship_battle.model.Cruiser;
import com.par_28.ship_battle.model.Destroyer;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.Torpedo;
import com.par_28.ship_battle.model.ai.AIPlayer;
import com.par_28.ship_battle.model.ai.enums.AIDifficulty;
import com.par_28.ship_battle.model.enums.Direction;
import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import com.par_28.ship_battle.model.exceptions.ShipPlacementException;
import com.par_28.ship_battle.view.gui.DifficultyView;
import com.par_28.ship_battle.view.gui.GameModeView;
import com.par_28.ship_battle.view.gui.SetupPlayerNameView;
import com.par_28.ship_battle.view.gui.SetupPlayerShipView;
import com.par_28.ship_battle.view.gui.SoundHandler;

/**
 * Manager of setup menu where players enter their names and set up ships.
 */
public class SetupMenuController extends GuiController {
    /**
     * List of player names entered.
     */
    List<String> names = new ArrayList<>();

    AIDifficulty aiDifficulty = null;

    /**
     * List of ships waiting to be placed by the current player.
     */
    private final List<Ship> shipsToPlace = new ArrayList<>();

    /**
     * Ship currently selected by the player.
     */
    private Ship selectedShip;

    /**
     * Initialize the menu controller.
     *
     * @param parent Reference to the parent screen controller
     */
    public SetupMenuController(ScreenController parent) {
        super(parent);
        initializeShipsToPlace();
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
        if(name.isEmpty()) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * this.parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(view.stage, "Please enter a name");
            return false;
        }

        if(names.contains(name)) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * this.parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(view.stage, String.format("%s already registered", name));
            return false;
        }

        this.names.add(name);

        if(isAIMode()) {
            this.names.add(AIPlayer.getRandomName());
        }

        if(this.names.size() >= this.parent.app.nbPlayers) {
            Player player1;
            Player player2;

            if(isAIMode()) {
                player1 = new Player(this.names.get(0), this.parent.app.gridSize);
                player2 = new AIPlayer(this.names.get(1), this.parent.app.gridSize, aiDifficulty);
            }
            else {
                player1 = new Player(this.names.get(0), this.parent.app.gridSize);
                player2 = new Player(this.names.get(1), this.parent.app.gridSize);
            }

            this.parent.app.player1 = player1;
            this.parent.app.player2 = player2;

            changeView(new SetupPlayerShipView(parent, this));
        }

        return true;
    }

    public boolean isAIMode() {
        return aiDifficulty != null;
    }

    public void setAIDifficulty(AIDifficulty aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public void goToDifficultyMenu() {
        changeView(new DifficultyView(this.parent, this));
    }

    public void gotoSetupPlayerNameMenu() {
        changeView(new SetupPlayerNameView(this.parent, this));
    }

    public void gotoGameModeMenu() {
        changeView(new GameModeView(this.parent, this));
    }

    /**
     * Render menu view.
     *
     * @param dt Delta time since last render
     */
    @Override
    public void render(float dt){
        super.render(dt);
        view.render(dt);
    }

    /**
     * Reset menu to initial state.
     */
    @Override
    public void reset() {
        if(names != null) {
            names.clear();
        }
        initializeShipsToPlace();
        changeView(new GameModeView(this.parent, this));
    }

    private Player getSetupPlayer() {
        return this.parent != null ? this.parent.app.player1 : null;
    }

    public Player getCurrentSetupPlayer() {
        return getSetupPlayer();
    }

    /**
     * Check if the currently selected ship can be placed at the given coordinate.
     *
     * @param coord starting coordinate on the grid
     * @param direction placement direction
     * @return true if placement would be valid
     */
    public boolean canPlaceSelectedShip(Coordinate coord, Direction direction) {
        Player player = getSetupPlayer();
        if(selectedShip == null || player == null || coord == null || direction == null) {
            return false;
        }

        return player.getGrid().previewPlacement(selectedShip, coord, direction).size() == selectedShip.getLength();
    }

    /**
     * Place the selected ship on the player's grid.
     *
     * @param coord starting coordinate
     * @param direction placement direction
     * @return true if placement succeeded
     */
    public boolean placeSelectedShip(Coordinate coord, Direction direction) {
        Player player = getSetupPlayer();
        if(selectedShip == null || player == null || coord == null || direction == null) {
            return false;
        }

        try {
            player.placeShipOnGrid(selectedShip, coord, direction);
            player.addShip(selectedShip);
            shipsToPlace.remove(selectedShip);
            selectedShip = null;
            return true;
        }
        catch (InvalidCoordinateException | ShipPlacementException ex) {
            return false;
        }
    }

    /**
     * Get ships already placed on the grid.
     *
     * @return immutable list of placed ships
     */
    public List<Ship> getPlacedShips() {
        Player player = getSetupPlayer();
        if(player == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(player.getShips());
    }

    /**
     * Get an immutable view of ships waiting for placement.
     *
     * @return list of ships to place
     */
    public List<Ship> getShipsToPlace() {
        return Collections.unmodifiableList(shipsToPlace);
    }

    /**
     * Select a ship to place on the grid.
     *
     * @param ship ship reference from the available list
     * @return true if the selection is valid
     */
    public boolean selectShip(Ship ship) {
        if(ship == null || !shipsToPlace.contains(ship)) {
            return false;
        }
        this.selectedShip = ship;
        return true;
    }

    /**
     * Get the currently selected ship.
     *
     * @return selected ship or null when nothing is selected
     */
    public Ship getSelectedShip() {
        return selectedShip;
    }

    /**
     * Reset available ships and clear the current selection.
     */
    private void initializeShipsToPlace() {
        shipsToPlace.clear();
        shipsToPlace.add(new Carrier());
        shipsToPlace.add(new Cruiser());
        shipsToPlace.add(new Destroyer());
        shipsToPlace.add(new Destroyer());
        shipsToPlace.add(new Torpedo());
        selectedShip = null;
    }
}
