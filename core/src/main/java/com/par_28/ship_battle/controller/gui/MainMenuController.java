package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.view.gui.MainMenuView;

public class MainMenuController extends GuiController {
    public ScreenController parent;

    public MainMenuController(ScreenController parent) {
        this.parent = parent;
        view = new MainMenuView(parent);
    }

    public void update(float dt){

    }

    @Override
    public void render(float dt){
        super.render(dt);
        view.render(dt);
    }
}
