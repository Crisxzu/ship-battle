package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.MainMenuController;
import com.par_28.ship_battle.controller.gui.ScreenController;


/**
 * Main menu
 */
public class MainMenuView extends GuiView<MainMenuController> {
    /**
     * Title label
     */
    private Label titleLabel;

    /**
     * Version label
     */
    private Label versionLabel;

    private TextureRegionDrawable logoTexture;

    /**
     * Initialize main menu
     *
     * @param parent screen manager
     */
    public MainMenuView(ScreenController parent) {
        super(parent);
        buildUI();
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        Texture logoTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.LOGO);
        this.logoTexture = new TextureRegionDrawable(new TextureRegion(logoTexture));
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        Table root = new Table();
        root.setFillParent(true);

        titleLabel = new Label("ShipBattle", skin);
        titleLabel.setFontScale(2.5f);

        Image logo = new Image(logoTexture);

        Container<Image> logoContainer = new Container<>(logo);

        logoContainer.pad(10f);

        root.add(logoContainer)
            .width(Value.percentWidth(0.50f, root))
            .height(Value.percentHeight(0.35f, root))
            .row();


        Table buttons = new Table();
        buttons.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        addMenuButton(buttons, "New game", () -> this.parent.changeController(GuiControllerEnum.SETUP_MENU));
        addMenuButton(buttons, "Settings", () -> this.parent.changeController(GuiControllerEnum.SETTINGS));
        addMenuButton(buttons, "Quit", Gdx.app::exit);

        root.add(buttons).expand().fill().center().row();

        versionLabel = new Label("v1.0.0 - 2025", skin);
        versionLabel.setColor(Color.GRAY);
        root.add(versionLabel).expand().height(Value.percentHeight(0.1f)).row();

        stage.addActor(root);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 200f);
        versionLabel.setFontScale(base / 500f);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
