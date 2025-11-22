package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.par_28.ship_battle.controller.gui.*;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.ai.AIPlayer;

/**
 * Game over menu
 */
public class GameOverView extends GuiView<GameController> {
    /**
     * Message label
     */
    private Label msg;

    /**
     * Winner player
     */
    private Player winner;


    /**
     * Initialize game over menu
     *
     * @param parent screen manager
     * @param controller game controller
     */
    public GameOverView(ScreenController parent, GameController controller) {
        super(parent, controller);
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
            .width(Value.percentWidth(0.40f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        addMenuButton(root, "Retry", () -> {
            Gdx.app.postRunnable(() -> {
                controller.resetGame();
            });
        });
        addMenuButton(root, "Retry without same placement", () -> {
            Gdx.app.postRunnable(() -> {
                this.controller.resetGameWithoutSamePlacement();
            });
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
