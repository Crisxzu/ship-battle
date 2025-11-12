package com.par_28.ship_battle.controller.gui;

import com.badlogic.gdx.Screen;
import com.par_28.ship_battle.view.gui.GuiView;

public abstract class GuiController {
    public GuiView view;
    public ScreenController parent;

    public GuiController(ScreenController parent) {
        this.parent = parent;
        reset();
    }

    public abstract void update(float dt);

    public void render(float dt) {
        update(dt);
    }

    public void dispose() {

    }

    public void resize(int width, int height) {

    }

    public void reset() {

    }
}
