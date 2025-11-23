package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.view.gui.MainMenuView;
import com.par_28.ship_battle.view.gui.SettingsHandler;
import com.par_28.ship_battle.view.gui.SettingsView;

/**
 * Manager of settings menu.
 */
public class SettingsController extends GuiController {
    
    /**
     * Initialize the menu controller.
     *
     * @param parent Reference to the screen manager
     */
    public SettingsController(ScreenController parent) {
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
     * Change sound volume
     *
     * @param soundVolume new sound volume
     */
    public void changeSoundVolume(float soundVolume){
        this.parent.app.settingsHandler.setSoundVolume(soundVolume);
    }

    /**
     * Change music volume
     * 
     * @param musicVolume new music volume
     */
    public void changeMusicVolume(float musicVolume){
        this.parent.app.settingsHandler.setMusicVolume(musicVolume);
    }

    /**
     * Save settings to preferences
     */
    public void saveSettings() {
        this.parent.app.settingsHandler.saveSettings();
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
        changeView(new SettingsView(parent, this));
    }
}
