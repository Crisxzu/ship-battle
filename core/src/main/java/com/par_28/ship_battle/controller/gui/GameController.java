package com.par_28.ship_battle.controller.gui;

import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.ai.AIPlayer;
import com.par_28.ship_battle.model.enums.PowerType;
import com.par_28.ship_battle.model.exceptions.IllegalGameStateException;
import com.par_28.ship_battle.view.gui.*;

/**
 * Manager of the game menu.
 */
public class GameController extends GuiController {
    /**
     * Current power type selected by the player
     */
    private PowerType currentPowerType;

    /**
     * Initialize the menu controller.
     *
     * @param parent Reference to the parent screen controller
     */
    public GameController(ScreenController parent) {
        super(parent);
        this.currentPowerType = PowerType.NORMAL;
    }

    /**
     * Update menu controller.
     *
     * @param dt Delta time since last update
     */
    public void update(float dt){

    }

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
     * Start a player's turn.
     */
    public void startTurn() {
        Player current = this.parent.app.game.getCurrentPlayer();

        if(current.isAI()) {
            changeView(new AIGameView(parent, this));
        }
        else {
            changeView(new GameView(parent, this));
        }
    }

    /**
     * Update the view to reflect turn change.
     * <p>
     * If the game is over, switch to the game over view.
     * Otherwise, display the turn change view.
     * </p>
     */
    public void changeTurn() {
        if(this.parent.app.game.isGameOver()) {
            SoundHandler.playTrack(
                SoundHandler.TrackID.MENU_THEME,
                0.2f * this.parent.app.settingsHandler.getMusicVolume(),
                true
            );
            changeView(new GameOverView(parent, this));
        }
        else {
            changeView(new GameTurnDisplayView(parent, this));
        }
    }

    /**
     * Play a turn by attacking a coordinate using the currently selected power type.
     *
     * @param attackCord Coordinate to attack
     * @return Attack response
     */
    public AttackResponse playTurn(Coordinate attackCord) {
        Player current = this.parent.app.game.getCurrentPlayer();

        AttackResponse response = this.parent.app.game.playTurn(attackCord, currentPowerType);

        if(current instanceof AIPlayer aiPlayer) {
            aiPlayer.notifyAttackResult(attackCord, response);
        }

        // Play appropriate sound based on power type
        switch (currentPowerType) {
            case BOMB:
                SoundHandler.playSound(
                    SoundHandler.SoundID.BOMB_SHOT,
                    0.5f * this.parent.app.settingsHandler.getSoundVolume()
                );
                break;
            case RADAR:
                if(response.isRadarDetection()) {
                    SoundHandler.playSound(
                        SoundHandler.SoundID.RADAR_FOUND,
                        0.2f * this.parent.app.settingsHandler.getSoundVolume()
                    );
                }
                else {
                    SoundHandler.playSound(
                        SoundHandler.SoundID.RADAR_NOTHING,
                        0.2f * this.parent.app.settingsHandler.getSoundVolume()
                    );
                }
                break;
            case NORMAL:
            default:
                SoundHandler.playSound(
                    SoundHandler.SoundID.CANNON_SHOT,
                    0.2f * this.parent.app.settingsHandler.getSoundVolume()
                );
                break;
        }

        // Reset to normal attack after using a power
        if (currentPowerType != PowerType.NORMAL) {
            currentPowerType = PowerType.NORMAL;
        }

        return response;
    }

    /**
     * Get the next shot coordinate chosen by the AI player.
     *
     * @return Coordinate chosen by AI
     */
    public Coordinate getAIShot() {
        Player current = this.parent.app.game.getCurrentPlayer();

        if(current instanceof AIPlayer aiPlayer) {
            return aiPlayer.chooseShot(this.parent.app.game.getOpponent().getShips());
        }

        return null;
    }

    /**
     * Set the current power type to be used in next attack.
     *
     * @param powerType Power type to set
     */
    public void setCurrentPowerType(PowerType powerType) {
        this.currentPowerType = powerType;
    }

    /**
     * Get the currently selected power type.
     *
     * @return Current power type
     */
    public PowerType getCurrentPowerType() {
        return currentPowerType;
    }

    /**
     * Get the number of bomb charges available for the current player.
     *
     * @return Number of bomb charges
     */
    public int getCurrentPlayerBombCharges() {
        Player current = this.parent.app.game.getCurrentPlayer();
        return current.getBombCharges();
    }

    /**
     * Get the number of radar charges available for the current player.
     *
     * @return Number of radar charges
     */
    public int getCurrentPlayerRadarCharges() {
        Player current = this.parent.app.game.getCurrentPlayer();
        return current.getRadarCharges();
    }

    /**
     * Check if the current player can use the specified power type.
     *
     * @param powerType Power type to check
     * @return true if the player has charges available for this power
     */
    public boolean canUsePower(PowerType powerType) {
        Player current = this.parent.app.game.getCurrentPlayer();
        switch (powerType) {
            case BOMB:
                return current.hasBombCharges();
            case RADAR:
                return current.hasRadarCharges();
            case NORMAL:
            default:
                return true;
        }
    }

    /**
     * Apply the Konami code effect: refill all power charges for the current player.
     */
    public void applyKonamiCode() {
        Player current = this.parent.app.game.getCurrentPlayer();

        current.refillPowers();
    }

    /**
     * Reset the game to its initial state with the same ship placements.
     */
    public void resetGame() {
        Player player1 = new Player(this.parent.app.player1.getName(), this.parent.app.gridSize);
        Player player2;

        if(this.parent.app.player2 instanceof AIPlayer aiPlayer) {
            player2 = new AIPlayer(aiPlayer.getName(), this.parent.app.gridSize, aiPlayer.getDifficulty());
        }
        else {
            player2 = new Player(this.parent.app.player2.getName(), this.parent.app.gridSize);
        }

        for(Ship ship : this.parent.app.player1.getShips()) {
            ship.refillLife();

            player1.placeShipOnGrid(ship, ship.getPositions().get(0), ship.getDirection());
            player1.addShip(ship);
        }

        for(Ship ship : this.parent.app.player2.getShips()) {
            ship.refillLife();

            player2.placeShipOnGrid(ship, ship.getPositions().get(0), ship.getDirection());
            player2.addShip(ship);
        }

        this.parent.app.player1 = player1;
        this.parent.app.player2 = player2;

        this.parent.app.game = new Game(player1, player2);
        try {
            this.parent.app.game.start();
        }
        catch (IllegalGameStateException e) {
            Dialogs.showErrorDialog(view.stage, "Unable to start game: " + e.getMessage());
            return;
        }

        startTurn();
    }

    /**
     * Reset the game to initial state without preserving ship placements.
     */
    public void resetGameWithoutSamePlacement() {
        this.parent.changeController(GuiControllerEnum.SETUP_MENU);
        GuiController currentController = this.parent.getCurrentController();

        if(currentController instanceof SetupMenuController setupMenuController) {
            if(this.parent.app.player2 instanceof AIPlayer aiPlayer) {
                setupMenuController.setAIDifficulty(aiPlayer.getDifficulty());
            }

            setupMenuController.addName(this.parent.app.player1.getName());

            if(!setupMenuController.isAIMode()) {
                setupMenuController.addName(this.parent.app.player2.getName());
            }
        }
        else {
            Dialogs.showErrorDialog(view.stage, "Unable to retry without same placement");
        }
    }

    /**
     * Reset controller to initial state.
     */
    @Override
    public void reset() {
        super.reset();
        this.currentPowerType = PowerType.NORMAL;
        changeView(new GameTurnDisplayView(parent, this));
        System.out.println(this.parent.app.player1);
        System.out.println(this.parent.app.player2);
        SoundHandler.playTrack(
            SoundHandler.TrackID.GAME_THEME,
            0.2f * this.parent.app.settingsHandler.getMusicVolume(),
            true
        );
    }
}
