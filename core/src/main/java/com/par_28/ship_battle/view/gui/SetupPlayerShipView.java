package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.graphics.Texture;
import com.par_28.ship_battle.controller.gui.GuiController;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;

public class SetupPlayerShipView extends GuiView<SetupMenuController> {

    public SetupPlayerShipView(ScreenController parent) {
        super(parent);
        buildUI();
    }


}
