package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.par_28.ship_battle.controller.gui.GameController;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.Coordinate;

import java.util.Random;

public class AIGameView extends GameView {

    protected Stack oldPos = null;
    protected float waitAITimer = 0f;
    protected Random random = new Random();

    public AIGameView(ScreenController parent, GameController controller) {
        super(parent, controller);
        playDialog(
            DialogHandler.DialogID.TURN_START_AI,
            (float) (loliAnimation.getAnimationDuration() * 1.25)
        );
    }

    @Override
    protected void buildUI() {
        loadTextures();

        currentPlayer = this.parent.app.game.getCurrentPlayer();

        stack = new Stack();
        stack.setFillParent(true);

        root = new Table();
        root.setFillParent(true);

        HorizontalGroup statusGroup = new HorizontalGroup();
        statusGroup.space(10f);

        turnLabel = new Label(
            String.format("Turn %d", this.parent.app.game.getNbTurns()+1),
            skin
        );

        statusGroup.addActor(turnLabel);

        nameLabel = new Label(
            String.format("%s", currentPlayer.getName()),
            skin
        );

        statusGroup.addActor(nameLabel);

        root.add(statusGroup)
            .colspan(2);

        com.badlogic.gdx.scenes.scene2d.ui.Cell<TextButton> pauseBtn = addMenuButton(root, "Pause", this::togglePause);
        pauseBtn.getActor().pad(Value.percentHeight(0.02f, root));
        pauseBtn.expandY().top().padTop(Value.percentHeight(0.02f, root));

        root.row();

        trackingTable = new Table();
        trackingTable.defaults().expand().fill();

        updatePlayerTables();

        root.add(trackingTable)
            .width(Value.percentWidth(0.8f, root))
            .height(Value.percentHeight(0.7f, root))
            .expand();

        root.row();

        HorizontalGroup dialogGroup = new HorizontalGroup();
        dialogGroup.space(10f);
        dialogGroup.expand().fill();

        loliImage = new Image(loliAnimation.getKeyFrame(elapsed));
        Container<Image> loliImageContainer = new Container<>(loliImage);

        loliImageContainer.pad(10f);

        dialogGroup.addActor(loliImageContainer);

        loliMsg = new Label("Test", skin);
        loliMsg.setAlignment(Align.left);
        loliMsg.setFontScale(1.2f);

        dialogGroup.addActor(loliMsg);

        coordLabel = new Label("XX", skin);
        coordLabel.setAlignment(Align.center);
        coordLabel.setFontScale(1.2f);

        root.add(dialogGroup)
            .expandX()
            .fill()
            .height(Value.percentHeight(0.2f, root))
            .colspan(2);

        root
            .add(coordLabel)
            .width(Value.percentWidth(0.1f, root));

        root.row();

        stack.add(root);

        pauseImage = new Image(pauseTexture);
        pauseImage.setFillParent(true);
        pauseImage.setVisible(paused);

        stack.add(pauseImage);

        pauseTable = new Table();
        pauseTable.setFillParent(true);

        stack.add(pauseTable);

        stage.addActor(stack);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void updatePlayerTables() {
        trackingTableStack = updateTableWithModel(
            trackingTable,
            currentPlayer.getTrackingGrid(),
            true,
            false
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(!shoot && !paused) {
            waitAITimer += delta;

            if(elapsed < 5f) {
                if(waitAITimer > 1f) {
                    waitAITimer = 0;

                    int x = random.nextInt(this.parent.app.gridSize);
                    int y = random.nextInt(this.parent.app.gridSize);

                    if(oldPos != null) {
                        Image snipeImage = (Image) oldPos.getChild(oldPos.getChildren().size-1);

                        snipeImage.setVisible(false);
                    }

                    Stack stack = trackingTableStack[x][y];
                    Image snipeImage = (Image) stack.getChild(stack.getChildren().size-1);

                    snipeImage.setVisible(true);
                    oldPos = stack;

                    coordLabel.setText(new Coordinate(x, y).toLetterFormat());
                }
            }
            else {
                shoot = true;

                Coordinate attackCord = this.controller.getAIShot();

                if(attackCord != null) {
                    if(oldPos != null) {
                        Image snipeImage = (Image) oldPos.getChild(oldPos.getChildren().size-1);

                        snipeImage.setVisible(false);
                    }

                    Stack stack = trackingTableStack[attackCord.getX()][attackCord.getY()];
                    Image snipeImage = (Image) stack.getChild(stack.getChildren().size-1);

                    snipeImage.setVisible(true);
                    oldPos = stack;

                    coordLabel.setText(new Coordinate(attackCord.getX(), attackCord.getY()).toLetterFormat());

                    response = controller.playTurn(attackCord);
                }
                else {
                    System.out.println("Unexpected error Oo, ai tries to attack but nothing was chosen ! Seems that player was not AI");
                    Gdx.app.exit();
                }
            }
        }
    }
}
