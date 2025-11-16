package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;


public class InputHandler implements Handler{
    static Vector2 touchPos;

    public InputHandler() {
        touchPos = new Vector2();
    }

    public static boolean userJustTouched(){
        return Gdx.input.justTouched();
    }

    public static Vector2 getTouchPos(Viewport viewport) {
        Vector2 touchPos = new Vector2();

        touchPos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touchPos);

        return touchPos;
    }

    public static boolean isKeyJustPressed(int keyCode) {
        return Gdx.input.isKeyJustPressed(keyCode);
    }

    public void dispose() {

    }
}
