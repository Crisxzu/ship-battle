package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;

/**
 * Setup player name menu
 */
public class SetupPlayerNameView extends GuiView {
    /**
     * Title label
     */
    private Label titleLabel;

    /**
     * Name input field
     */
    private TextField nameField;

    /**
     * Current player index
     */
    private int iPlayer = 0;

    /**
     * Controller for setup menu
     */
    private SetupMenuController controller;

    /**
     * Initialize menu
     *
     * @param parent parent controller
     * @param controller setup menu controller
     */
    public SetupPlayerNameView(ScreenController parent, SetupMenuController controller) {
        super(parent);
        this.controller = controller;
        buildUI();
    }

    /**
     * Build UI
     */
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

        addMenuButton(root, "OK", this::addName);

        root.add().expand().row();

        stage.addActor(root);
    }

    /**
     * Update UI elements
     */
    void updateUI() {
        titleLabel.setText(String.format("Player %d", iPlayer+1));
        nameField.setText("");
    }

    /**
     * Update the screen
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if(InputHandler.isKeyJustPressed(Input.Keys.ENTER)) {
            addName();
        }
    }

    /**
     * Add player name
     */
    private void addName() {
        if(controller.addName(nameField.getText())) {
            iPlayer++;
            updateUI();
        }
    }

    /**
     * Resize elements on window resize
     *
     * @param width new width
     * @param height new height
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 300f);
        TextField.TextFieldStyle style = nameField.getStyle();
        style.font.getData().setScale(base / 600f);
        nameField.setStyle(style);
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
    }
}
