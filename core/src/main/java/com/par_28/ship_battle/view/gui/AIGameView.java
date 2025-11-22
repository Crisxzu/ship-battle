package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.par_28.ship_battle.controller.gui.GameController;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.Coordinate;

import java.util.Random;

/**
 * Game menu for AI opponent
 */
public class AIGameView extends GameView {

    /**
     * Old position of AI snipe image
     */
    protected Stack oldPos = null;

    /**
     * Timer to wait before AI makes a move
     */
    protected float waitAITimer = 0f;

    /**
     * Random number generator for AI moves
     */
    protected Random random = new Random();

    /**
     * Initializes game menu for AI opponent
     * 
     * @param parent screen manager
     * @param controller game controller
     */
    public AIGameView(ScreenController parent, GameController controller) {
        super(parent, controller);
        playDialog(
            DialogHandler.DialogID.TURN_START_AI,
            (float) (loliAnimation.getAnimationDuration() * 1.25)
        );
    }

    @Override
    protected void buildPlayerGridsUI() {
        trackingTable = new Table();
        trackingTable.defaults().expand().fill();

        updatePlayerTables();

        root.add(trackingTable)
            .width(Value.percentWidth(0.8f, root))
            .height(Value.percentHeight(0.7f, root))
            .expand();

        root.row();
    }

    @Override
    protected void updatePlayerTables() {
        trackingTableChildStacks = updateTableWithModel(
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

            if(dialogTimer < 5f) {
                if(waitAITimer > 1f) {
                    waitAITimer = 0;

                    int x = random.nextInt(this.parent.app.gridSize);
                    int y = random.nextInt(this.parent.app.gridSize);

                    if(oldPos != null) {
                        Image snipeImage = (Image) oldPos.getChild(oldPos.getChildren().size-1);

                        snipeImage.setVisible(false);
                    }

                    Stack stack = trackingTableChildStacks[x][y];
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

                    Stack stack = trackingTableChildStacks[attackCord.getX()][attackCord.getY()];
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
