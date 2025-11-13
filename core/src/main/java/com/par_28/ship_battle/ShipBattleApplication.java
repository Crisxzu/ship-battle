package com.par_28.ship_battle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.ui.VisUI;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.Game;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.view.gui.InputHandler;
import com.par_28.ship_battle.view.gui.SoundHandler;
import com.par_28.ship_battle.view.gui.SpriteHandler;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ShipBattleApplication extends ApplicationAdapter {
    public SpriteBatch batch;
    private ScreenController screenController;
    public int nbPlayers = 2;
    public final int gridSize = 10;
    public Player player1;
    public Player player2;
    public Game game;
    private SoundHandler soundHandler;
    private InputHandler inputHandler;
    private SpriteHandler spriteHandler;

    @Override
    public void create() {
        batch = new SpriteBatch();

        soundHandler = new SoundHandler();
        inputHandler = new InputHandler();
        spriteHandler = new SpriteHandler();

        screenController = new ScreenController(this);



        VisUI.load();
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
        screenController.dispose();
        soundHandler.dispose();
        inputHandler.dispose();
        spriteHandler.dispose();
        VisUI.dispose();
    }
}
