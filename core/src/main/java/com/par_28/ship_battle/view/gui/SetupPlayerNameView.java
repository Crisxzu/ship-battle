package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;

public class SetupPlayerNameView extends GuiView {
    private Label titleLabel;
    private TextField nameField;
    private int iPlayer = 0;
    private SetupMenuController controller;

    public SetupPlayerNameView(ScreenController parent, SetupMenuController controller) {
        super(parent);
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        Table root = new Table();
        root.setFillParent(true);

        root.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        Cell<TextButton> backBtn = addMenuButton(root, "Back to Title", () -> {
            parent.changeController(GuiControllerEnum.MAIN_MENU);
        });
        backBtn.expandX().left().width(Value.percentWidth(0.15f, root));

        root.add().height(Value.percentHeight(0.1f, root)).row();

        titleLabel = new Label("", skin);
        titleLabel.setFontScale(2.5f);

        root.add(titleLabel).row();

        nameField = new TextField("", skin);
        nameField.setMessageText("Enter your name");

        updateUI();

        root.add(nameField).row();

        addMenuButton(root, "OK", () -> {
            if(controller.addName(nameField.getText())) {
                iPlayer++;
                updateUI();
            }
        });

        root.add().expand().row();

        stage.addActor(root);
    }

    void updateUI() {
        titleLabel.setText(String.format("Player %d", iPlayer+1));
        nameField.setText("");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 300f);
        TextField.TextFieldStyle style = nameField.getStyle();
        style.font.getData().setScale(base / 600f);
        nameField.setStyle(style);
    }

    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
    }
}
