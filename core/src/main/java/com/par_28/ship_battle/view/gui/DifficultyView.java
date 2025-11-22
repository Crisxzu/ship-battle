package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.par_28.ship_battle.controller.gui.*;
import com.par_28.ship_battle.model.ai.enums.AIDifficulty;


/**
 *
 */
public class DifficultyView extends GuiView<SetupMenuController> {
    /**
     * Title label
     */
    private Label titleLabel;


    /**
     * Initialize main menu
     *
     * @param parent screen manager
     */
    public DifficultyView(ScreenController parent, SetupMenuController controller) {
        super(parent, controller);
        buildUI();
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        Table root = new Table();
        root.setFillParent(true);

        titleLabel = new Label("Difficulty", skin);
        titleLabel.setFontScale(2.5f);

        root.add(titleLabel)
            .expand()
            .height(Value.percentHeight(0.2f, root))
            .row();


        Table buttons = new Table();
        buttons.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        addMenuButton(buttons, "Easy", () -> {
            gotoSetupPlayerNameMenu(AIDifficulty.EASY);
        });
        addMenuButton(buttons, "Medium", () -> {
            gotoSetupPlayerNameMenu(AIDifficulty.MEDIUM);
        });
        addMenuButton(buttons, "Hard", () -> {
            gotoSetupPlayerNameMenu(AIDifficulty.HARD);
        });
        addMenuButton(buttons, "Return", () -> this.controller.gotoGameModeMenu());

        root.add(buttons).expand().fill().center().row();

        stage.addActor(root);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void gotoSetupPlayerNameMenu(AIDifficulty difficulty) {
        this.controller.setAIDifficulty(difficulty);
        this.controller.gotoSetupPlayerNameMenu();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 200f);
    }
}
