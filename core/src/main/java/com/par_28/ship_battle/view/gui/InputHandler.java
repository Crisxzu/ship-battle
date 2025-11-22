package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;

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

    static List<Integer> lastKeys = new ArrayList<>();

    static int[] konamiCode = new int[]{
        Input.Keys.UP,
        Input.Keys.UP,
        Input.Keys.DOWN,
        Input.Keys.DOWN,
        Input.Keys.LEFT,
        Input.Keys.RIGHT,
        Input.Keys.LEFT,
        Input.Keys.RIGHT,
        Input.Keys.B,
        Input.Keys.A,
    };


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

    public static void saveUserPressedKey(int keyCode) {
        System.out.println("Saving user pressed key: " + keyCode);

        lastKeys.add(keyCode);

        if(lastKeys.size() > 10) {
            lastKeys.remove(0);
        }
    }

    public static boolean konamiCodeJustPressed() {

        boolean konamiCodeJustPressed = true;

        if(lastKeys.size() != konamiCode.length) {
            konamiCodeJustPressed = false;
        }
        else {
            for(int i = 0; i < konamiCode.length; i++){
                if(lastKeys.get(i) != konamiCode[i]) {
                    // Support case where A is Q, QWERTY Keyboard
                    if (konamiCode[i] == Input.Keys.A && lastKeys.get(i) != Input.Keys.Q) {
                        konamiCodeJustPressed = false;
                        break;
                    }
                }
            }
        }

        return konamiCodeJustPressed;
    }

    public static void clearSaveKeys() {
        lastKeys.clear();
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {

    }
}
