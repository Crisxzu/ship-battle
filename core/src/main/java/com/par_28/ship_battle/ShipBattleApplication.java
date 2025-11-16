package com.par_28.ship_battle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.ui.VisUI;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.Game;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.view.gui.DialogHandler;
import com.par_28.ship_battle.view.gui.InputHandler;
import com.par_28.ship_battle.view.gui.SoundHandler;
import com.par_28.ship_battle.view.gui.SpriteHandler;

/**
 * Ship Battle game.
 */
public class ShipBattleApplication extends ApplicationAdapter {
    /**
     * Screen controller managing different views.
     */
    private ScreenController screenController;

    /**
     * Number of players in the game.
     */
    public int nbPlayers = 2;
    
    /**
     * Size of the game grid (gridSize x gridSize).
     */
    public final int gridSize = 10;
    
    /**
     * Player 1 instance.
     */
    public Player player1;
    /**
     * Player 2 instance.
     */
    public Player player2;

    /**
     * Game logic instance.
     */
    public Game game;

    /**
     * Sound handler for managing game sounds.
     */
    private SoundHandler soundHandler;
    
    /**
     * Input handler for managing user inputs.
     */
    private InputHandler inputHandler;

    /**
     * Sprite handler for managing game sprites and textures.
     */
    private SpriteHandler spriteHandler;
    
    /**
     * Dialog handler for managing in-game dialogs.
     */
    private DialogHandler dialogHandler;

    /**
     * Initialize the application.
     */
    @Override
    public void create() {
        soundHandler = new SoundHandler();
        inputHandler = new InputHandler();
        spriteHandler = new SpriteHandler();
        dialogHandler = new DialogHandler();

        screenController = new ScreenController(this);



        VisUI.load();
    }

    /**
     * Main render loop.
     */
    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        screenController.render(dt);
    }

    /**
     * Handle resizing of the application window.
     * <p>
     * Prevents resizing to non-positive dimensions.
     * Applies new size to the screen controller.
     * </p>
     * @param width  new width
     * @param height new height
     */
    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;

        screenController.resize(width, height);
    }

    /**
     * Dispose resources on application exit.
     */
    @Override
    public void dispose() {
        screenController.dispose();
        soundHandler.dispose();
        inputHandler.dispose();
        spriteHandler.dispose();
        dialogHandler.dispose();
        VisUI.dispose();
    }
}
