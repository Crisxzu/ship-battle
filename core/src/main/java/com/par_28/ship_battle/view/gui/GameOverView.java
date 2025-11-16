package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.par_28.ship_battle.controller.gui.GameController;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.Player;

public class GameOverView extends GuiView {
    private Label msg;
    private Player winner;
    private GameController controller;

    public GameOverView(ScreenController parent, GameController controller) {
        super(parent);
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        winner = this.parent.app.game.getWinner();

        Table root = new Table();
        root.setFillParent(true);

        msg = new Label(
            String.format(
                """
                    GAME OVER !
                    The winner is %s !
                    Glory to him and shame to the looser !""",
                winner.getName()
            ),
            skin
        );
        msg.setAlignment(Align.center);
        msg.setFontScale(2.5f);
        root.add(msg).row();

        root.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        addMenuButton(root, "Retry", () -> {
            Gdx.app.postRunnable(() -> controller.startTurn());
        });
        addMenuButton(root, "Retry without same placement", () -> {

        });
        addMenuButton(root, "Return to Title", () -> this.parent.changeController(GuiControllerEnum.MAIN_MENU));

        stage.addActor(root);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if(msg != null) {
            msg.setFontScale(base / 200f);
        }
    }
}
