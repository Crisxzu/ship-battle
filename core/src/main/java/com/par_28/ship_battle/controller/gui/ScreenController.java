package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.ShipBattleApplication;

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

        changeController(GuiControllerEnum.MAIN_MENU);
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
}
