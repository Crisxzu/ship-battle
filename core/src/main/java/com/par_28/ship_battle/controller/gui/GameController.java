package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.model.AttackResponse;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.view.gui.*;

public class GameController extends GuiController {
    public GameController(ScreenController parent) {
        super(parent);
    }

    public void update(float dt){

    }

    @Override
    public void render(float dt){
        super.render(dt);
        if(view != null) {
            view.render(dt);
        }
    }

    public void startTurn() {
        changeView(new GameView(parent, this));
    }

    public void changeTurn() {
        if(this.parent.app.game.isGameOver()) {
            SoundHandler.playTrack(SoundHandler.TrackID.MENU_THEME, 0.2f, true);
            changeView(new GameOverView(parent, this));
        }
        else {
            changeView(new GameTurnDisplayView(parent, this));
        }
    }

    public AttackResponse playTurn(Coordinate attackCord) {
        AttackResponse response = this.parent.app.game.playTurn(attackCord);

        SoundHandler.playSound(SoundHandler.SoundID.CANNON_SHOT, 0.3f);

        return response;
    }

    @Override
    public void reset() {
        super.reset();
        changeView(new GameTurnDisplayView(parent, this));
        System.out.println(this.parent.app.player1);
        System.out.println(this.parent.app.player2);
        SoundHandler.playTrack(SoundHandler.TrackID.GAME_THEME, 0.2f, true);
    }
}
