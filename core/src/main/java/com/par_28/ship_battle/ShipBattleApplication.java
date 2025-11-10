package com.par_28.ship_battle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.*;
import com.par_28.ship_battle.controller.gui.ScreenController;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ShipBattleApplication extends ApplicationAdapter {
    public SpriteBatch batch;
    public BitmapFont font;
    public Viewport viewport;
    private ScreenController screenController;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Créer viewport (8x5 comme SimpleGame)
        viewport = new FitViewport(8, 5);

        // Font avec échelle adaptée au viewport
        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
        font.setColor(Color.WHITE);

        screenController = new ScreenController(this);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        screenController.render(dt);
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;

        screenController.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        screenController.dispose();
    }
}
