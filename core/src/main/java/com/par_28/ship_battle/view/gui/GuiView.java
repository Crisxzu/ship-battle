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

public abstract class GuiView implements Screen {
    protected Texture backgroundTexture;
    protected ScreenController parent;
    public Stage stage;
    protected Skin skin;
    protected Array<TextButton> menuButtons;
    float base;

    public GuiView(ScreenController parent) {
        this.parent = parent;
        this.stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        menuButtons = new Array<>();
        backgroundTexture = new Texture("background.png");
    }

    protected void buildUI() {

    }

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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

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

    @Override
    public void render(float delta) {
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

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
