package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.view.gui.GuiView;

public abstract class GuiController {
    public GuiView view;
    public ScreenController parent;

    public GuiController(ScreenController parent) {
        this.parent = parent;
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

    protected void changeView(GuiView view) {
        if (this.view != null) {
            this.view.hide();
            this.view.dispose();
        }
        this.view = view;
        this.view.show();
    }
}
