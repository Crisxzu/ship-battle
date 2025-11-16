package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.par_28.ship_battle.controller.gui.GameController;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.*;

public class GameTurnDisplayView extends GuiView {
    private Label msg;
    private Player currentPlayer;
    private GameController controller;

    public GameTurnDisplayView(ScreenController parent, GameController controller) {
        super(parent);
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        currentPlayer = this.parent.app.game.getCurrentPlayer();

        Table root = new Table();
        root.setFillParent(true);

        msg = new Label(
            String.format(
                "Turn %d\n Player %s",
                this.parent.app.game.getNbTurns()+1,
                currentPlayer.getName()
            ),
            skin
        );
        msg.setAlignment(Align.center);
        msg.setFontScale(2.5f);
        root.add(msg).row();

        addMenuButton(root, "Continue", () -> {
            Gdx.app.postRunnable(() -> controller.startTurn());
        }).width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root));
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
