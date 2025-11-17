package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;


/**
 * Main menu
 */
public class MainMenuView extends GuiView {

    /**
     * Title label
     */
    private Label titleLabel;

    /**
     * Version label
     */
    private Label versionLabel;

    /**
     * Initialize main menu
     * 
     * @param parent screen manager
     */
    public MainMenuView(ScreenController parent) {
        super(parent);
        buildUI();
    }

    /**
     * Build main menu
     */
    @Override
    protected void buildUI() {
        super.buildUI();
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

        addMenuButton(buttons, "Nouvelle Partie", () -> {
            System.out.println("Nouvelle Partie cliquée!");
            this.parent.changeController(GuiControllerEnum.SETUP_MENU);
        });
        addMenuButton(buttons, "Quitter", Gdx.app::exit);

        root.add(buttons).expand().fill().center().row();

        versionLabel = new Label("v1.0.0 - 2025", skin);
        versionLabel.setColor(Color.GRAY);
        root.add(versionLabel).expand().height(Value.percentHeight(0.1f)).row();

        stage.addActor(root);
    }

    /**
     * Resize elements on window resize
     * 
     * @param width new width
     * @param height new height
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 300f);
        versionLabel.setFontScale(base / 500f);
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
    }
}
