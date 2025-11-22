package com.par_28.ship_battle.controller.gui;

import java.util.*;

import com.par_28.ship_battle.ShipBattleApplication;
import com.par_28.ship_battle.model.Carrier;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.model.Cruiser;
import com.par_28.ship_battle.model.Destroyer;
import com.par_28.ship_battle.model.Game;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.Torpedo;
import com.par_28.ship_battle.model.ai.AIPlayer;
import com.par_28.ship_battle.model.ai.enums.AIDifficulty;
import com.par_28.ship_battle.model.enums.PowerType;
import com.par_28.ship_battle.view.gui.GameOverView;

/**
 * Manager of screens and controllers.
 */
public class ScreenController extends GuiController {
    /**
     * Reference to the main application.
     */
    public ShipBattleApplication app;

    /**
     * Map of available GUI controllers.
     */
    Map<GuiControllerEnum, GuiController> controllers;

    /**
     * Currently active GUI controller.
     */
    GuiController currentController;

    /**
     * Initialize the screen controller with all GUI controllers.
     *
     * @param app Reference to the main application
     */
    public ScreenController(ShipBattleApplication app) {
        super(null);
        this.app = app;

        controllers = new HashMap<>();

        controllers.put(GuiControllerEnum.MAIN_MENU, new MainMenuController(this));
        controllers.put(GuiControllerEnum.SETUP_MENU, new SetupMenuController(this));
        controllers.put(GuiControllerEnum.GAME, new GameController(this));
        controllers.put(GuiControllerEnum.SETTINGS, new SettingsController(this));

        changeController(GuiControllerEnum.MAIN_MENU);
    }

    /**
     * Get the currently active GUI controller.
     * 
     * @return the current controller
     */
    public GuiController getCurrentController() {
        return currentController;
    }

    /**
     * Update the current controller.
     *
     * @param dt Delta time since last update
     */
    public void update(float dt){

    }

    /**
     * Render the current controller.
     *
     * @param dt Delta time since last render
     */
    @Override
    public void render(float dt){
        super.render(dt);
        currentController.render(dt);
    }

    /**
     * Change the active GUI controller.
     *
     * @param controller The new controller to activate
     */
    public void changeController(GuiControllerEnum controller){
        currentController = controllers.get(controller);
        currentController.reset();
        currentController.view.show();
    }

    /**
     * Handle resizing of the application window.
     * <p>
     * Delegates resize event to the current controller's view.
     * </p>
     * @param width  new width
     * @param height new height
     */
    @Override
    public void resize(int width, int height) {
        if(currentController.view != null) {
            currentController.view.resize(width, height);
        }
    }

    /**
     * Dispose all controllers and their resources.
     */
    @Override
    public void dispose() {
        for (GuiController controller : controllers.values()) {
            controller.dispose();
        }
    }
}
