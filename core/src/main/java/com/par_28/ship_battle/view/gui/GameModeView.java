package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;


/**
 *
 */
public class GameModeView extends GuiView<SetupMenuController> {
    /**
     *
     */
    private Label titleLabel;


    /**
     *
     *
     * @param parent screen manager
     */
    public GameModeView(ScreenController parent, SetupMenuController controller) {
        super(parent, controller);
        buildUI();
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        Table root = new Table();
        root.setFillParent(true);

        titleLabel = new Label("Game Mode", skin);
        titleLabel.setFontScale(2.5f);

        root.add(titleLabel)
            .height(Value.percentHeight(0.2f, root))
            .row();


        Table buttons = new Table();
        buttons.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        addMenuButton(buttons, "Player VS AI", () -> {
            this.controller.goToDifficultyMenu();
        });
        addMenuButton(buttons, "Player VS Player", () -> {
            this.controller.gotoSetupPlayerNameMenu();
        });
        addMenuButton(buttons, "Return to title", () -> this.parent.changeController(GuiControllerEnum.MAIN_MENU));

        root.add(buttons).expand().fill().center().row();

        stage.addActor(root);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 200f);
    }
}
