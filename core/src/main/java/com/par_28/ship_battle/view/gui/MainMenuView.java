package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.par_28.ship_battle.controller.gui.ScreenController;

import javax.swing.*;

/**
 * MainMenuView avec Scene2D - Menu principal avec boutons interactifs
 */
public class MainMenuView implements Screen {
    private ScreenController parent;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private Label titleLabel;
    private Label versionLabel;
    private Array<TextButton> menuButtons = new Array<>();

    public MainMenuView(ScreenController parent) {
        this.parent = parent;

        stage = new Stage(new ScreenViewport());

        backgroundTexture = new Texture("background.png");
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        buildUI();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);

        titleLabel = new Label("ShipBattle", skin);
        titleLabel.setFontScale(2.5f);

        root.add(titleLabel).expand().height(Value.percentHeight(0.2f)).row();


        Table buttons = new Table();
        buttons.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        addMenuButton(buttons, "Nouvelle Partie", () -> System.out.println("Nouvelle Partie cliquée!"));
        addMenuButton(buttons, "Quitter", Gdx.app::exit);

        root.add(buttons).expand().fill().center().row();

        versionLabel = new Label("v1.0.0 - 2025", skin);
        versionLabel.setColor(Color.GRAY);
        root.add(versionLabel).expand().height(Value.percentHeight(0.1f)).row();

        stage.addActor(root);
    }

    private void addMenuButton(Table table, String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        menuButtons.add(button);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        table.add(button).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float dt) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        stage.getBatch().begin();
        stage.getBatch().draw(
            backgroundTexture,
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        stage.getBatch().end();

        stage.act(dt);
        stage.draw();
    }

    public void update(float dt) {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        float base = Math.min(width, height);
        titleLabel.setFontScale(base / 300f);
        versionLabel.setFontScale(base / 500f);
        float buttonScale = base / 600f;
        for (TextButton button : menuButtons) {
            button.getLabel().setFontScale(buttonScale);
        }
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
        backgroundTexture.dispose();
    }
}
