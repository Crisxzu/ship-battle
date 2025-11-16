package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.model.AttackResponse;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.view.gui.*;

/**
 * Manager of the game menu.
 */
public class GameController extends GuiController {
    /**
     * Initialize the menu controller.
     * 
     * @param parent Reference to the parent screen controller
     */
    public GameController(ScreenController parent) {
        super(parent);
    }

    /**
     * Update menu controller.
     * 
     * @param dt Delta time since last update
     */
    public void update(float dt){

    }

    /**
     * Render menu view.
     * 
     * @param dt Delta time since last render
     */
    @Override
    public void render(float dt){
        super.render(dt);
        if(view != null) {
            view.render(dt);
        }
    }

    /**
     * Start a player's turn.
     */
    public void startTurn() {
        changeView(new GameView(parent, this));
    }

    /**
     * Update the view to reflect turn change.
     * <p>
     * If the game is over, switch to the game over view.
     * Otherwise, display the turn change view.
     * </p>
     */
    public void changeTurn() {
        if(this.parent.app.game.isGameOver()) {
            SoundHandler.playTrack(SoundHandler.TrackID.MENU_THEME, 0.2f, true);
            changeView(new GameOverView(parent, this));
        }
        else {
            changeView(new GameTurnDisplayView(parent, this));
        }
    }

    /**
     * Play a turn by attacking a coordinate.
     * 
     * @param attackCord Coordinate to attack
     * @return Attack response
     */
    public AttackResponse playTurn(Coordinate attackCord) {
        AttackResponse response = this.parent.app.game.playTurn(attackCord);

        SoundHandler.playSound(SoundHandler.SoundID.CANNON_SHOT, 0.3f);

        return response;
    }
    
    /**
     * Reset controller to initial state.
     */
    @Override
    public void reset() {
        super.reset();
        changeView(new GameTurnDisplayView(parent, this));
        System.out.println(this.parent.app.player1);
        System.out.println(this.parent.app.player2);
        SoundHandler.playTrack(SoundHandler.TrackID.GAME_THEME, 0.2f, true);
    }
}
