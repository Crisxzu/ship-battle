package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.view.gui.MainMenuView;
import com.par_28.ship_battle.view.gui.SoundHandler;

/**
 * Manager of main menu.
 */
public class MainMenuController extends GuiController {
    /**
     * Initialize the menu controller.
     *
     * @param parent Reference to the parent screen controller
     */
    public MainMenuController(ScreenController parent) {
        super(parent);
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
     * Reset controller to initial state.
     */
    @Override
    public void reset() {
        super.reset();
        changeView(new MainMenuView(parent));
        SoundHandler.playTrack(
            SoundHandler.TrackID.MENU_THEME,
            0.2f * this.parent.app.settingsHandler.getMusicVolume(),
            true
        );
    }
}
