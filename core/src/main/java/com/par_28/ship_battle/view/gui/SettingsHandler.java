package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Handler for managing settings data.
 */
public class SettingsHandler implements Handler {
    /**
     * Sound volume
     */
    private float soundVolume;
    /**
     * Music volume
     */
    private float musicVolume;

    /**
     * Initialize settings handler
     */
    public SettingsHandler() {
        loadSettings();
    }

    /**
     * Get sound volume
     * 
     * @return sound volume
     */
    public float getSoundVolume() {
        return soundVolume;
    }

    /**
     * Set sound volume
     * 
     * @param soundVolume sound volume
     */
    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    /**
     * Get music volume
     * 
     * @return music volume
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Set music volume
     * 
     * @param musicVolume music volume
     */
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    /**
     * Load settings from preferences
     */
    public void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences("Settings");

        setSoundVolume(prefs.getFloat("soundVolume", 1.0f));
        setMusicVolume(prefs.getFloat("musicVolume", 1.0f));
    }

    /**
     * Save settings to preferences
     */
    public void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("Settings");

        prefs.putFloat("soundVolume", getSoundVolume());
        prefs.putFloat("musicVolume", getMusicVolume());

        prefs.flush();
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {

    }
}
