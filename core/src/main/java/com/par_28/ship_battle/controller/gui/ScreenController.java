package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.ShipBattleApplication;
import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.Direction;
import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import com.par_28.ship_battle.model.exceptions.ShipPlacementException;

import java.util.*;

public class ScreenController extends GuiController {
    public ShipBattleApplication app;
    Map<GuiControllerEnum, GuiController> controllers;
    GuiController currentController;


    public ScreenController(ShipBattleApplication app) {
        super(null);
        this.app = app;

        controllers = new HashMap<>();

        controllers.put(GuiControllerEnum.MAIN_MENU, new MainMenuController(this));
        controllers.put(GuiControllerEnum.SETUP_MENU, new SetupMenuController(this));
        controllers.put(GuiControllerEnum.GAME, new GameController(this));
        // TODO Return to normal after game testing
        //changeController(GuiControllerEnum.MAIN_MENU);
        this.app.player1 = new Player("Dazu", this.app.gridSize);
        this.app.player2 = new Player("Daouda", this.app.gridSize);

        // Initialize ships with default positions
        initializeDefaultShips(this.app.player1);
        initializeDefaultShips(this.app.player2);

        this.app.game = new Game(this.app.player1, this.app.player2);
        this.app.game.start();
        changeController(GuiControllerEnum.GAME);
    }

    public void update(float dt){

    }

    @Override
    public void render(float dt){
        super.render(dt);
        currentController.render(dt);
    }

    public void changeController(GuiControllerEnum controller){
        currentController = controllers.get(controller);
        System.out.println(currentController);
        currentController.reset();
        currentController.view.show();
    }

    @Override
    public void resize(int width, int height) {
        currentController.view.resize(width, height);
    }

    @Override
    public void dispose() {
        for (GuiController controller : controllers.values()) {
            controller.dispose();
        }
    }

    /**
     * Initialize default ships for a player with predefined positions
     *
     * @param player The player to initialize ships for
     */
    private void initializeDefaultShips(Player player) {
        try {
            // Create ships
            Ship carrier = new Carrier();
            Ship cruiser = new Cruiser();
            Ship destroyer = new Destroyer();
            Ship torpedo = new Torpedo();

            // Place ships with default positions
            // Carrier (length 5) at (0,0) horizontally
            player.addShip(carrier);
            player.placeShipOnGrid(carrier, new Coordinate(6, 1), Direction.VERTICAL);

            // Cruiser (length 4) at (0,2) horizontally
            player.addShip(cruiser);
            player.placeShipOnGrid(cruiser, new Coordinate(0, 2), Direction.HORIZONTAL);

            // Destroyer (length 3) at (0,4) horizontally
            player.addShip(destroyer);
            player.placeShipOnGrid(destroyer, new Coordinate(0, 4), Direction.HORIZONTAL);

            // Torpedo (length 2) at (0,6) horizontally
            player.addShip(torpedo);
            player.placeShipOnGrid(torpedo, new Coordinate(0, 6), Direction.HORIZONTAL);

            System.out.println("Ships initialized for player: " + player.getName());
        } catch (InvalidCoordinateException | ShipPlacementException e) {
            System.err.println("Error initializing ships for player " + player.getName() + ": " + e.getMessage());
        }
    }
}
