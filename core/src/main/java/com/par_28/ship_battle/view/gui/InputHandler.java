package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Handler for input in the GUI.
 */
public class InputHandler implements Handler{
    /**
     * Touch position
     */
    static Vector2 touchPos;

    /**
     * Initialize input handler
     */
    public InputHandler() {
        touchPos = new Vector2();
    }

    /**
     * Check if user just touched the screen
     * 
     * @return true if user just touched, false otherwise
     */
    public static boolean userJustTouched(){
        return Gdx.input.justTouched();
    }

    /**
     * Get touch position
     * 
     * @param viewport viewport to unproject
     * @return touch position
     */
    public static Vector2 getTouchPos(Viewport viewport) {
        Vector2 touchPos = new Vector2();

        touchPos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touchPos);

        return touchPos;
    }

    /**
     * Check if a key was just pressed
     * 
     * @param keyCode key code to check
     * @return true if key was just pressed, false otherwise
     */
    public static boolean isKeyJustPressed(int keyCode) {
        return Gdx.input.isKeyJustPressed(keyCode);
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {

    }
}
