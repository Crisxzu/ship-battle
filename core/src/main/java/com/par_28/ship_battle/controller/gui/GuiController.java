package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.view.gui.GuiView;

/**
 * Base for all GUI controllers.
 */
public abstract class GuiController {
    /**
     * The view associated with this controller.
     */
    public GuiView view;
    /**
     * Reference to the parent screen controller.
     */
    public ScreenController parent;

    /**
     * Initialize controller.
     * 
     * @param parent Reference to the parent screen controller
     */
    public GuiController(ScreenController parent) {
        this.parent = parent;
    }

    /**
     * Update controller.
     * 
     * @param dt Delta time since last update
     */
    public abstract void update(float dt);

    /**
     * Render controller view.
     * @param dt
     */
    public void render(float dt) {
        update(dt);
    }

    /**
     * Dispose controller resources.
     */
    public void dispose() {

    }

    /**
     * Handle resizing of the application window.
     * <p>
     * Delegates resize event to the view.
     * </p>
     * @param width new width
     * @param height new height
     */
    public void resize(int width, int height) {

    }

    /**
     * Reset controller to initial state.
     */
    public void reset() {

    }

    /**
     * Change the current view.
     * 
     * @param view The new view to set
     */
    protected void changeView(GuiView view) {
        if (this.view != null) {
            this.view.hide();
            this.view.dispose();
        }
        this.view = view;
        this.view.show();
    }
}
