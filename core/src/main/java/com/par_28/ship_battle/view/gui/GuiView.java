package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.par_28.ship_battle.controller.gui.GuiController;
import com.par_28.ship_battle.controller.gui.ScreenController;

/**
 * Base for all GUI views
 *
 * @param <T> type of controller associated with the view
 */
public abstract class GuiView<T extends GuiController> implements Screen {
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
     * Controller associated with the view
     */
    protected T controller;

    /**
     * Initialize menu
     *
     * @param parent screen manager
     */
    public GuiView(ScreenController parent) {
        this.parent = parent;
        initUI();
    }

    /**
     * Initialize menu with controller
     *
     * @param parent screen manager
     * @param controller menu controller
     */
    public GuiView(ScreenController parent, T controller) {
        this.parent = parent;
        this.controller = controller;
        initUI();
    }

    /**
     * Initialize base things for UI
     */
    protected void initUI() {
        this.stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        menuButtons = new Array<>();
    }

    /**
     * Load necessary textures
     */
    protected void loadTextures() {
        backgroundTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.BACKGROUND);
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
        float buttonScale = base / 400f;
        if(menuButtons != null) {
            for (TextButton button : menuButtons) {
                button.getLabel().setFontScale(buttonScale);
            }
        }
    }

    /**
     * Provides the latest base length used for responsive calculations.
     *
     * @return base side length derived from the viewport, 0 when unknown
     */
    protected float getBaseSize() {
        return base;
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
