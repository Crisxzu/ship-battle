package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.view.gui.SetupPlayerNameView;
import com.par_28.ship_battle.view.gui.SetupPlayerShipView;
import com.par_28.ship_battle.view.gui.SoundHandler;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import java.util.ArrayList;
import java.util.List;

public class SetupMenuController extends GuiController {
    List<String> names = new ArrayList<>();

    public SetupMenuController(ScreenController parent) {
        super(parent);
    }

    public void update(float dt){

    }

    public boolean addName(String name) {
        if(name.isEmpty()) {
            SoundHandler.playSound(SoundHandler.SoundID.ERROR, 0.2f);
            Dialogs.showErrorDialog(view.stage, "Please enter a name");
            return false;
        }

        if(names.contains(name)) {
            SoundHandler.playSound(SoundHandler.SoundID.ERROR, 0.2f);
            Dialogs.showErrorDialog(view.stage, String.format("%s already registered", name));
            return false;
        }

        this.names.add(name);

        if(this.names.size() >= this.parent.app.nbPlayers) {
            // Play some music maybe
            Player player1 = new Player(this.names.get(0), this.parent.app.gridSize);
            Player player2 = new Player(this.names.get(1), this.parent.app.gridSize);


            this.parent.app.player1 = player1;
            this.parent.app.player2 = player2;

            view = new SetupPlayerShipView(parent);

            //this.parent.changeController(GuiControllerEnum.GAME);
        }

        return true;
    }

    @Override
    public void render(float dt){
        super.render(dt);
        view.render(dt);
    }

    @Override
    public void reset() {
        if(names != null) {
            names.clear();
        }
        view = new SetupPlayerNameView(this.parent, this);
    }
}
