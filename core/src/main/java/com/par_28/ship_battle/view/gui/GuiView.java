package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;

/**
 * Base for all GUI views
 */
public abstract class GuiView implements Screen {
    /**
     * Background texture
     */
    protected Texture backgroundTexture;

    /**
     * Screen manager
     */
    protected ScreenController parent;

    /**
     * Stage for UI elements
     */
    public Stage stage;

    /**
     * UI skin
     */
    protected Skin skin;

    /**
     * Menu buttons
     */
    protected Array<TextButton> menuButtons;

    /**
     * Base size for scaling
     */
    float base;

    /**
     * Initialize GUI view
     *
     * @param parent screen manager
     */
    public GuiView(ScreenController parent) {
        this.parent = parent;
        this.stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        menuButtons = new Array<>();
        backgroundTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.BACKGROUND);
    }

    /**
     * Load necessary textures
     */
    protected void loadTextures() {

    }

    /**
     * Build UI
     */
    protected void buildUI() {
        loadTextures();
    }

    /**
     * Add a button to the menu
     *
     * @param table table to add the button to
     * @param text button text
     * @param action button action
     * @return the created button cell
     */
    protected Cell<TextButton> addMenuButton(Table table, String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        menuButtons.add(button);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        Cell<TextButton> cell =  table.add(button);
        cell.row();

        return cell;
    }

    /**
     * Show the screen
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Resize the screen
     *
     * @param width new width
     * @param height new height
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        base = Math.min(width, height);
        float buttonScale = base / 600f;
        if(menuButtons != null) {
            for (TextButton button : menuButtons) {
                button.getLabel().setFontScale(buttonScale);
            }
        }
    }

    /**
     * Update the screen
     *
     * @param delta time since last update
     */
    public void update(float delta) {

    }

    /**
     * Render the screen
     *
     * @param delta time since last render
     */
    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        stage.getBatch().begin();
        if(backgroundTexture != null) {
            stage.getBatch().draw(
                backgroundTexture,
                0,
                0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            );
        }
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Pause the screen
     */
    @Override
    public void pause() {

    }

    /**
     * Resume the screen
     */
    @Override
    public void resume() {
    }

    /**
     * Hide the screen
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
