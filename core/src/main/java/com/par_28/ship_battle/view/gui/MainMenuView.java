package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.par_28.ship_battle.controller.gui.ScreenController;

/**
 * MainMenuView avec Scene2D - Menu principal avec boutons interactifs
 */
public class MainMenuView implements Screen {
    private ScreenController parent;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

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
        stage.addActor(root);

        // Titre
        Label title = new Label("ShipBattle", skin);
        title.setFontScale(2.5f);
        root.add(title).padBottom(80).row();

        TextButton newGameButton = new TextButton("Nouvelle Partie", skin);
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Nouvelle Partie cliquée!");
            }
        });
        root.add(newGameButton).width(300).height(60).padBottom(20).row();

        TextButton loadButton = new TextButton("Charger Partie", skin);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Charger Partie cliquée!");
            }
        });
        root.add(loadButton).width(300).height(60).padBottom(20).row();

        TextButton optionsButton = new TextButton("Options", skin);
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Options cliquée!");
            }
        });
        root.add(optionsButton).width(300).height(60).padBottom(20).row();

        TextButton quitButton = new TextButton("Quitter", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        root.add(quitButton).width(300).height(60).row();

        Label version = new Label("v1.0.0 - 2025", skin);
        version.setColor(Color.GRAY);
        root.row();
        root.add(version).padTop(40);
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
