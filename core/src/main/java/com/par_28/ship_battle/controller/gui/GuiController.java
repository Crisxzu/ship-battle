package com.par_28.ship_battle.controller.gui;

import com.badlogic.gdx.Screen;

public abstract class GuiController {
    public Screen view;

    public abstract void update(float dt);

    public void render(float dt) {
        update(dt);
    }

    public void dispose() {

    }

    public void resize(int width, int height) {

    }
}
