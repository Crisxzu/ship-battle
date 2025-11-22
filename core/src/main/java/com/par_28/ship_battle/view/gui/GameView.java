package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.par_28.ship_battle.controller.gui.GameController;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.Cell;
import com.par_28.ship_battle.model.enums.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Game menu
 */
public class GameView extends GuiView<GameController> {
    /**
     * Stack for layering main UI elements (game, pause overlay)
     */
    protected Stack rootStack;

    /**
     * Tracking table stack for layering UI elements (grid and animation on top)
     */
    protected Stack trackingTableStack;

    /**
     * Snipe power UI element
     */
    protected Stack snipeStack;

    /**
     * Bomb power UI element to stack label over icon
     */
    protected Stack bombStack;

    /**
     * Radar power UI element to stack label over icon
     */
    protected Stack radarStack;

    /**
     * Tracking table cells ui elements
     */
    protected Stack[][] trackingTableChildStacks;

    /**
     * Table for player's tracking grid
     */
    public Table trackingTable;

    /**
     * Table for player's ship grid
     */
    public Table shipTable;

    /**
     * Root table of the UI
     */
    protected Table root;

    /**
     * Pause table UI element
     */
    protected Table pauseTable;

    /**
     * Header labels for each grid column
     */
    protected List<Label> headerLabels = new ArrayList<>();

    /**
     * Row labels for each grid row
     */
    protected List<Label> rowLabels = new ArrayList<>();

    /**
     * Loli message label
     */
    protected Label loliMsg;

    /**
     * Coordinate label displaying selected grid position
     */
    protected Label coordLabel;

    /**
     * Pause title label
     */
    protected Label pauseTitleLabel;

    /**
     * Turn label
     */
    protected Label turnLabel;

    /**
     * Player name label
     */
    protected Label nameLabel;

    /**
     * Pause image overlay
     */
    protected Image pauseImage;

    /**
     * Loli image for dialog
     */
    protected Image loliImage;

    /**
     * Radar animation image
     */
    protected Image radarAnimImage;

    /**
     * Grid case texture
     */
    protected TextureRegionDrawable gridCaseTexture;

    /**
     * Snipe texture
     */
    protected TextureRegionDrawable snipeTexture;

    /**
     * Carrier ship texture
     */
    protected TextureRegionDrawable carrierTexture;

    /**
     * Cruiser ship texture
     */
    protected TextureRegionDrawable cruiserTexture;

    /**
     * Destroyer ship texture
     */
    protected TextureRegionDrawable destroyerTexture;

    /**
     * Torpedo texture
     */
    protected TextureRegionDrawable torpedoTexture;

    /**
     * Miss texture
     */
    protected TextureRegionDrawable missTexture;

    /**
     * Hit texture
     */
    protected TextureRegionDrawable hitTexture;

    /**
     * Sunk texture
     */
    protected TextureRegionDrawable sunkTexture;

    /**
     * Loli talking texture
     */
    protected TextureRegionDrawable loliTexture;

    /**
     * Pause background texture
     */
    protected TextureRegionDrawable pauseTexture;

    /**
     * Radar icon texture
     */
    protected TextureRegionDrawable radarTexture;

    /**
     * Bomb icon texture
     */
    protected TextureRegionDrawable bombTexture;

    /**
     * Radar animation texture
     */
    protected TextureRegionDrawable radarAnimTexture;

    /**
     * Loli talking animation
     */
    protected Animation<TextureRegion> loliAnimation;

    /**
     * Radar found something animation
     */
    protected Animation<TextureRegion> radarFoundAnimation;

    /**
     * Radar found nothing animation
     */
    protected Animation<TextureRegion> radarNothingAnimation;


    /**
     * Current player data
     */
    protected Player currentPlayer;

    /**
     * Attack response after shooting
     */
    protected AttackResponse response;

    /**
     * Turn played flag
     */
    protected boolean turnPlayed = false;

    /**
     * Shoot flag
     */
    protected boolean shoot = false;

    /**
     * Launch radar animation flag
     */
    protected boolean launchRadarAnimation = false;

    /**
     * Paused flag
     */
    protected boolean paused = false;

    /**
     * Wait timer for delays
     */
    protected float waitTimer = 0f;

    /**
     * Elapsed time for dialog animation
     */
    protected float dialogTimer = 0f;

    /**
     * Radar animation timer
     */
    protected float radarTimer = 0f;

    /**
     * X coordinate of radar animation target
     */
    protected int radarGridX = 0;

    /**
     * Y coordinate of radar animation target
     */
    protected int radarGridY = 0;

    /**
     * Initialize game menu
     *
     * @param parent screen manager
     * @param controller game menu controller
     */
    public GameView(ScreenController parent, GameController controller) {
        super(parent, controller);
        buildUI();
        playDialog(
            DialogHandler.DialogID.TURN_START,
            (float) (loliAnimation.getAnimationDuration() * 1.25)
        );
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        Texture gridCaseTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.GRID_CASE);
        this.gridCaseTexture = new TextureRegionDrawable(new TextureRegion(gridCaseTexture));

        Texture snipeTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.SNIPE);
        this.snipeTexture = new TextureRegionDrawable(new TextureRegion(snipeTexture));

        Texture carrierTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.CARRIER);
        this.carrierTexture = new TextureRegionDrawable(new TextureRegion(carrierTexture));

        Texture cruiserTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.CRUISER);
        this.cruiserTexture = new TextureRegionDrawable(new TextureRegion(cruiserTexture));

        Texture destroyerTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.DESTROYER);
        this.destroyerTexture = new TextureRegionDrawable(new TextureRegion(destroyerTexture));

        Texture torpedoTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.TORPEDO);
        this.torpedoTexture = new TextureRegionDrawable(new TextureRegion(torpedoTexture));

        Texture missTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.MISS);
        this.missTexture = new TextureRegionDrawable(new TextureRegion(missTexture));

        Texture hitTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.HIT);
        this.hitTexture = new TextureRegionDrawable(new TextureRegion(hitTexture));

        Texture sunkTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.SUNK);
        this.sunkTexture = new TextureRegionDrawable(new TextureRegion(sunkTexture));

        Texture pauseTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.PAUSE_BACKGROUND);
        this.pauseTexture = new TextureRegionDrawable(new TextureRegion(pauseTexture));

        Texture radarTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.RADAR);
        this.radarTexture = new TextureRegionDrawable(new TextureRegion(radarTexture));

        Texture bombTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.BOMB);
        this.bombTexture = new TextureRegionDrawable(new TextureRegion(bombTexture));

        loliAnimation = SpriteHandler.getAnimation(SpriteHandler.AnimationID.LOLI);
        loliAnimation.setPlayMode(Animation.PlayMode.LOOP);

        loliTexture = new TextureRegionDrawable(new TextureRegion(loliAnimation.getKeyFrame(dialogTimer)));

        radarNothingAnimation = SpriteHandler.getAnimation(SpriteHandler.AnimationID.RADAR_NOTHING);

        radarFoundAnimation = SpriteHandler.getAnimation(SpriteHandler.AnimationID.RADAR_FOUND);

        radarAnimTexture = new TextureRegionDrawable(new TextureRegion(radarNothingAnimation.getKeyFrame(radarTimer)));
    }

    @Override
    protected void buildUI() {
        super.buildUI();

        currentPlayer = this.parent.app.game.getCurrentPlayer();

        rootStack = new Stack();
        rootStack.setFillParent(true);

        root = new Table();
        root.setFillParent(true);

        buildStatusGroup();

        buildPlayerGridsUI();

        buildFooter();

        rootStack.add(root);

        pauseImage = new Image(pauseTexture);
        pauseImage.setFillParent(true);
        pauseImage.setVisible(paused);

        rootStack.add(pauseImage);

        pauseTable = new Table();
        pauseTable.setFillParent(true);

        rootStack.add(pauseTable);

        stage.addActor(rootStack);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(!currentPlayer.isAI()) {
                    InputHandler.saveUserPressedKey(keycode);

                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Build status group UI
     */
    protected void buildStatusGroup() {
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

        HorizontalGroup powerGroup = new HorizontalGroup();
        powerGroup.space(10f);
        powerGroup.padLeft(2f);

        buildPowerGroup(powerGroup);

        statusGroup.addActor(powerGroup);

        root.add(statusGroup)
            .colspan(2);

        com.badlogic.gdx.scenes.scene2d.ui.Cell<TextButton> pauseBtn = addMenuButton(root, "Pause", this::togglePause);
        pauseBtn.getActor().pad(Value.percentHeight(0.02f, root));
        pauseBtn.expandY().top().padTop(Value.percentHeight(0.02f, root));
        pauseBtn.padRight(Value.percentHeight(0.02f, root));

        root.row();
    }
    /**
     * Build power group UI for status bar
     *
     * @param powerGroup power group to build
     */
    protected void buildPowerGroup(HorizontalGroup powerGroup) {
        final GameController gameController = this.controller;

        snipeStack = createPowerStack(
            snipeTexture,
            -1
        );

        snipeStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!currentPlayer.isAI()) {
                    gameController.setCurrentPowerType(PowerType.NORMAL);
                    playDialog(
                        DialogHandler.DialogID.TURN_START,
                        (float) (loliAnimation.getAnimationDuration() * 0.60)
                    );
                }
            }
        });

        powerGroup.addActor(snipeStack);

        radarStack = createPowerStack(
            radarTexture,
            controller.getCurrentPlayerRadarCharges()
        );

        radarStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!currentPlayer.isAI()) {
                    if(gameController.getCurrentPlayerRadarCharges() > 0) {
                        gameController.setCurrentPowerType(PowerType.RADAR);
                        playDialog(
                            DialogHandler.DialogID.RADAR_SELECTED,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                    else {
                        playDialog(
                            DialogHandler.DialogID.UNAVAILABLE_POWER,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                }
            }
        });

        powerGroup.addActor(radarStack);

        bombStack = createPowerStack(
            bombTexture,
            controller.getCurrentPlayerBombCharges()
        );

        bombStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!currentPlayer.isAI()) {
                    if(gameController.getCurrentPlayerBombCharges() > 0) {
                        gameController.setCurrentPowerType(PowerType.BOMB);
                        playDialog(
                            DialogHandler.DialogID.BOMB_SELECTED,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                    else {
                        playDialog(
                            DialogHandler.DialogID.UNAVAILABLE_POWER,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                }
            }
        });

        powerGroup.addActor(bombStack);
    }

    /**
     * Create power stack UI element
     *
     * @param texture power icon texture
     * @param nb number of charges
     * @return power stack UI element
     */
    protected Stack createPowerStack(TextureRegionDrawable texture, int nb) {
        Stack stack = new Stack();
        Image image = new Image(texture);
        Container<Image> container = new Container<>(image);

        container.padTop(10f);

        stack.add(container);

        if(nb >= 0) {
            container.padRight(5f);

            Label label = new Label(String.format("%d", nb), skin);
            label.setFontScale(2f);

            label.setAlignment(Align.bottomRight);
            stack.add(label);
        }

        stack.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Color imageColor = image.getColor();
                imageColor.set(imageColor.r, imageColor.g, imageColor.b, 0.4f);
                image.setColor(imageColor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Color imageColor = image.getColor();
                imageColor.set(imageColor.r, imageColor.g, imageColor.b, 1f);
                image.setColor(imageColor);
            }
        });

        return stack;
    }

    /**
     * Build player grids UI
     */
    protected void buildPlayerGridsUI() {
        shipTable = new Table();
        shipTable.defaults().expand().fill();

        trackingTable = new Table();
        trackingTable.defaults().expand().fill();

        updatePlayerTables();

        trackingTableStack = new Stack();
        trackingTable.setFillParent(true);

        radarAnimImage = new Image(radarAnimTexture);
        radarAnimImage.setScale(0.27f);
        radarAnimImage.setVisible(false);
        radarAnimImage.setOrigin(Align.center);

        trackingTableStack.add(trackingTable);
        trackingTableStack.add(radarAnimImage);

        root.add(shipTable)
            .width(Value.percentWidth(0.3f, root))
            .height(Value.percentHeight(0.5f, root))
            .expand();
        root.add(trackingTableStack)
            .width(Value.percentWidth(0.5f, root))
            .height(Value.percentHeight(0.7f, root))
            .expand();

        root.row();
    }

    /**
     * Build footer UI
     */
    protected void buildFooter() {
        HorizontalGroup dialogGroup = new HorizontalGroup();
        dialogGroup.space(10f);
        dialogGroup.expand().fill();

        loliImage = new Image(loliAnimation.getKeyFrame(dialogTimer));
        Container<Image> loliImageContainer = new Container<>(loliImage);

        loliImageContainer.pad(15f);

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
    }

    /**
     * Update table UI with model data
     *
     * @param table table to update
     * @param playerGrid player's grid data
     * @param caseSelectable if cases are selectable
     * @param showShips if ships should be shown
     * @return stacks of table cells
     */
    Stack[][] updateTableWithModel(Table table, Grid playerGrid, boolean caseSelectable, boolean showShips) {
        Stack[][] stacks;
        Cell[][] cells = playerGrid.getCells();
        int width = cells.length;
        int height = (width > 0) ? cells[0].length : 0;

        table.clearChildren();

        // -1 = no ship, 0+ = index in ship positions
        int[][] shipPartIndex = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                shipPartIndex[i][j] = -1;
            }
        }

        if (showShips) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Cell cell = cells[i][j];
                    if (cell.hasShip()) {
                        Ship ship = cell.getShip();
                        if (ship.getPositions() != null) {
                            for (int idx = 0; idx < ship.getPositions().size(); idx++) {
                                Coordinate pos = ship.getPositions().get(idx);
                                if (pos.getX() == i && pos.getY() == j) {
                                    shipPartIndex[i][j] = idx;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        stacks = new Stack[width][height];

        // Deactivate default for header
        table.defaults().reset();

        // Empty cell at top left
        table.add().size(30f);

        // Add letter header
        for (int i = 0; i < width; i++) {
            char columnLetter = (char) ('A' + i);
            Label headerLabel = new Label(String.valueOf(columnLetter), skin);
            headerLabel.setAlignment(Align.center);
            headerLabel.setFontScale(1f);
            headerLabels.add(headerLabel);
            table.add(headerLabel).center().expandX().fillX().height(30f);
        }
        table.row();


        // Create base grid
        for (int j = 0; j < height; j++) {
            // Add number header
            Label rowLabel = new Label(String.valueOf(j + 1), skin);
            rowLabel.setAlignment(Align.center);
            rowLabel.setFontScale(1f);
            rowLabels.add(rowLabel);
            table.add(rowLabel).center().expandY().fillY().width(30f);

            // Activate defaults
            table.defaults().expand().fill();

            for (int i = 0; i < width; i++) {
                Cell cell = cells[i][j];
                Stack stack = new Stack();
                stacks[i][j] = stack;

                Image gridCase = new Image(gridCaseTexture);
                gridCase.setFillParent(true);
                stack.add(gridCase);

                int finalI = i;
                int finalJ = j;

                if(cell.isShot()) {
                    if(cell.hasShip()) {
                        if(cell.getShip().isDestroyed()) {
                            Image sunkImage = new Image(sunkTexture);
                            sunkImage.setFillParent(true);
                            stack.add(sunkImage);
                        }
                        else {
                            Image hitImage = new Image(hitTexture);
                            hitImage.setFillParent(true);
                            stack.add(hitImage);
                        }
                    }
                    else {
                        Image missImage = new Image(missTexture);
                        missImage.setFillParent(true);
                        stack.add(missImage);
                    }
                }

                if(caseSelectable) {
                    Image snipeImage = new Image(snipeTexture);
                    snipeImage.setFillParent(true);
                    snipeImage.setVisible(false);
                    stack.add(snipeImage);

                    final Image finalSnipeImage = snipeImage;

                    stack.addListener(new ClickListener() {
                        @Override
                        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                            System.out.printf("Enter Case %d, %d\n", finalI, finalJ);
                            if(!shoot && !paused && !currentPlayer.isAI()) {
                                finalSnipeImage.setVisible(true);
                                coordLabel.setText(new Coordinate(finalI, finalJ).toLetterFormat());
                            }
                        }

                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            System.out.printf("Exit Case %d, %d\n", finalI, finalJ);
                            if(!shoot && !currentPlayer.isAI()) {
                                finalSnipeImage.setVisible(false);
                            }
                        }

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if(!shoot && !paused && !currentPlayer.isAI()) {
                                shoot = true;
                                Coordinate coord = new Coordinate(finalI, finalJ);

                                response = controller.playTurn(coord);

                                if(response.getResult() == AttackResult.RADAR_USED) {
                                    radarGridX = finalI;
                                    radarGridY = finalJ;
                                    launchRadarAnimation = true;
                                    radarTimer = 0f;

                                    updatePowerLabels();

                                    finalSnipeImage.setVisible(false);
                                }
                                else if(response.isBombAttack()) {
                                    updatePowerLabels();
                                }
                            }
                        }
                    });
                }

                table.add(stack);
            }
            table.row();
        }

        if (showShips) {
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    Cell cell = cells[i][j];
                    if (!cell.isShot() &&cell.hasShip() && shipPartIndex[i][j] >= 0) {
                        Ship ship = cell.getShip();
                        TextureRegionDrawable shipDrawable = getShipDrawable(ship.getName());

                        if (shipDrawable != null) {
                            int partIdx = shipPartIndex[i][j];
                            int shipLength = ship.getLength();

                            Texture shipTexture = shipDrawable.getRegion().getTexture();
                            int textureWidth = shipTexture.getWidth();
                            int textureHeight = shipTexture.getHeight();

                            TextureRegion partRegion;

                            if (ship.getDirection() == Direction.HORIZONTAL) {
                                // Verticul cut of texture
                                int partHeight = textureHeight / shipLength;
                                int yOffset = partIdx * partHeight;
                                partRegion = new TextureRegion(shipTexture, 0, yOffset, textureWidth, partHeight);

                                // Add image with an offset because of rotation (not for last case)
                                if (i + 1 < width) {
                                    Image shipPartImage = new Image(new TextureRegionDrawable(partRegion));
                                    shipPartImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
                                    shipPartImage.setRotation(90);
                                    shipPartImage.setFillParent(true);
                                    stacks[i + 1][j].add(shipPartImage);
                                }
                            } else {
                                // Vertical cut of texture
                                int partHeight = textureHeight / shipLength;
                                int yOffset = partIdx * partHeight;
                                partRegion = new TextureRegion(shipTexture, 0, yOffset, textureWidth, partHeight);

                                Image shipPartImage = new Image(new TextureRegionDrawable(partRegion));
                                shipPartImage.setFillParent(true);
                                stacks[i][j].add(shipPartImage);
                            }
                        }
                    }
                }
            }
        }

        return stacks;
    }

    /**
     * Update power labels UI
     */
    private void updatePowerLabels() {
        Label radarNbLabel = (Label) radarStack.getChild(1);

        radarNbLabel.setText(String.format("%d", currentPlayer.getRadarCharges()));

        Label bombNbLabel = (Label) bombStack.getChild(1);

        bombNbLabel.setText(String.format("%d", currentPlayer.getBombCharges()));
    }

    /**
     * Play dialog
     *
     * @param dialogID dialog identifier
     * @param duration duration of dialog
     */
    void playDialog(DialogHandler.DialogID dialogID, float duration) {
        loliMsg.setText("");
        dialogTimer = 0;

        loliAnimation.setPlayMode(Animation.PlayMode.LOOP);

        DialogHandler.playDialog(
            dialogID,
            duration
        );
    }

    @Override
    public void update(float delta) {
        if(InputHandler.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }

        if(paused) {
            return;
        }

        super.update(delta);

        if(InputHandler.konamiCodeJustPressed()) {
            applyKonamiCode();
        }

        if(launchRadarAnimation) {
            radarTimer += delta;

            updateRadarAnimation();
        }

        dialogTimer += delta;

        updateDialogAnimation();

        if(shoot && !turnPlayed) {
            waitTimer += delta;

            updateUIOnAttack();
        }

        if(turnPlayed) {
            waitTimer += delta;

            if(waitTimer > 3f) {
                Gdx.app.postRunnable(() -> controller.changeTurn());
            }
        }
    }

    /**
     * Update radar animation
     */
    protected void updateRadarAnimation() {
        // Recalculate position every frame for responsiveness
        if(trackingTableChildStacks != null && radarGridX < trackingTableChildStacks.length && radarGridY < trackingTableChildStacks[0].length) {
            Stack cellStack = trackingTableChildStacks[radarGridX][radarGridY];

            // Get the center position of the target cell
            Vector2 cellCenter = cellStack.localToStageCoordinates(new Vector2(cellStack.getWidth() / 8f, cellStack.getHeight() / 5f));
            Vector2 localPos = trackingTableStack.stageToLocalCoordinates(cellCenter);

            // Get the actual texture size
            TextureRegion region = radarAnimTexture.getRegion();
            float textureWidth = region.getRegionWidth();
            float textureHeight = region.getRegionHeight();

            // Set origin to center for proper scaling
            radarAnimImage.setOrigin(textureWidth / 2, textureHeight / 2);

            // Position so that the origin (center) of the image is at the cell center
            // We position the bottom-left corner, accounting for the origin offset
            radarAnimImage.setPosition(
                localPos.x - textureWidth / 2,
                localPos.y - textureHeight / 2
            );
        }

        if(!radarAnimImage.isVisible()) {
            // Start with scale 0
            radarAnimImage.setScale(0f);
            radarAnimImage.setVisible(true);
        }

        if(response.isRadarDetection()) {
            radarAnimTexture.setRegion(new TextureRegion(radarFoundAnimation.getKeyFrame(radarTimer)));
        }
        else {
            radarAnimTexture.setRegion(new TextureRegion(radarNothingAnimation.getKeyFrame(radarTimer)));
        }
        radarAnimImage.setDrawable(radarAnimTexture);


        if(radarTimer <= 2.5f) {
            float scale = (radarTimer / 2.5f) * 0.27f;
            radarAnimImage.setScale(scale);
        } else if (radarTimer <= 4f) {
            radarAnimImage.setScale(0.27f);
        } else if(radarTimer <= 5f) {
            float shrinkProgress = (radarTimer - 4f);
            float scale = 0.27f * (1f - shrinkProgress);
            radarAnimImage.setScale(scale);
        }
        else {
            radarAnimImage.setVisible(false);
            launchRadarAnimation = false;
            radarTimer = 0f;
        }
    }

    /**
     * Update dialog animation
     */
    protected void updateDialogAnimation() {
        if(dialogTimer <= DialogHandler.getDialogDuration()) {
            int charCount = DialogHandler.getCharCountThisFrame(dialogTimer);

            if(charCount > 0) {
                String text = DialogHandler.getDialogText().substring(0, charCount-1);

                loliMsg.setText(text);
            }
        }
        else {
            if(!Objects.equals(loliMsg.getText().substring(0), DialogHandler.getDialogText())) {
                loliMsg.setText(DialogHandler.getDialogText());
            }
            loliAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        }

        loliTexture.setRegion(new TextureRegion(loliAnimation.getKeyFrame(dialogTimer)));
        loliImage.setDrawable(new TextureRegionDrawable(loliTexture));
    }

    /**
     * Update UI on attack result
     */
    protected void updateUIOnAttack() {
        if(response.getResult() == AttackResult.RADAR_USED) {
            if(waitTimer > 3f) {
                if(response.isRadarDetection()) {
                    playDialog(
                        DialogHandler.DialogID.RADAR_FOUND,
                        (float) (loliAnimation.getAnimationDuration() * 0.60)
                    );
                }
                else {
                    playDialog(
                        DialogHandler.DialogID.RADAR_NOTHING,
                        (float) (loliAnimation.getAnimationDuration() * 0.60)
                    );
                }
                shoot = false;
                waitTimer = 0;
            }

        }
        else {
            if(waitTimer > 1.5f) {
                AttackResult result = response.getResult();
                if(response.isHit()) {
                    if(result == AttackResult.SUNK) {
                        SoundHandler.playSound(
                            SoundHandler.SoundID.SUNK,
                            0.05f * this.parent.app.settingsHandler.getSoundVolume()
                        );
                        playDialog(
                            DialogHandler.DialogID.SUNK,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                    else {
                        SoundHandler.playSound(
                            SoundHandler.SoundID.HIT,
                            0.05f * this.parent.app.settingsHandler.getSoundVolume()
                        );
                        playDialog(
                            DialogHandler.DialogID.HIT,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                }
                else {
                    if(result == AttackResult.ALREADY_HIT) {
                        SoundHandler.playSound(
                            SoundHandler.SoundID.ALREADY_HIT,
                            0.05f * this.parent.app.settingsHandler.getSoundVolume()
                        );
                        playDialog(
                            DialogHandler.DialogID.ALREADY_HIT,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                    else {
                        SoundHandler.playSound(
                            SoundHandler.SoundID.MISS,
                            0.05f * this.parent.app.settingsHandler.getSoundVolume()
                        );
                        playDialog(
                            DialogHandler.DialogID.MISS,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                }
                updatePlayerTables();
                resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                turnPlayed = true;
                waitTimer = 0;
            }
        }
    }

    /**
     * Apply Konami code effects
     */
    protected void applyKonamiCode() {
        controller.applyKonamiCode();
        updatePowerLabels();

        playDialog(
            DialogHandler.DialogID.KONAMI_CODE,
            loliAnimation.getAnimationDuration()
        );

        SoundHandler.playSound(
            SoundHandler.SoundID.CHEAT_CODE,
            0.1f * this.parent.app.settingsHandler.getSoundVolume()
        );

        InputHandler.clearSaveKeys();
    }

    /**
     * Render game menu
     */
    @Override
    public void render(float delta) {
        super.render(delta);
    }


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        for(Label headerLabel : headerLabels) {
            headerLabel.setFontScale(base / 550f);
        }

        for(Label rowLabel : rowLabels) {
            rowLabel.setFontScale(base / 550f);
        }

        if(loliMsg != null) {
            loliMsg.setFontScale(base / 425f);
        }

        if(coordLabel != null) {
            coordLabel.setFontScale(base / 425f);
        }

        if(pauseTitleLabel != null) {
            pauseTitleLabel.setFontScale(base / 200f);
        }

        if(turnLabel != null) {
            turnLabel.setFontScale(base / 300f);
        }

        if(nameLabel != null) {
            nameLabel.setFontScale(base / 300f);
        }
    }

    /**
     * Get ship drawable by name
     *
     * @param shipName name of ship
     * @return drawable of ship
     */
    protected TextureRegionDrawable getShipDrawable(String shipName) {
        if (shipName == null) return null;

        switch (shipName) {
            case "Carrier":
                return carrierTexture;
            case "Cruiser":
                return cruiserTexture;
            case "Destroyer":
                return destroyerTexture;
            case "Torpedo":
                return torpedoTexture;
            default:
                return null;
        }
    }

    /**
     * Update player ui tables
     */
    protected void updatePlayerTables() {
        updateTableWithModel(
            shipTable,
            currentPlayer.getGrid(),
            false,
            true
        );

        trackingTableChildStacks = updateTableWithModel(
            trackingTable,
            currentPlayer.getTrackingGrid(),
            true,
            false
        );
    }

    /**
     * Toggle pause menu
     */
    protected void togglePause() {
        paused = !paused;

        pauseImage.setVisible(paused);
        Color pauseColor = pauseImage.getColor();
        pauseColor.set(pauseColor.r, pauseColor.g, pauseColor.b, 0.2f);
        pauseImage.setColor(pauseColor);

        if(paused) {
            pauseTable.defaults()
                .width(Value.percentWidth(0.35f, root))
                .height(Value.percentHeight(0.1f, root))
                .pad(Value.percentHeight(0.02f, root));

            pauseTitleLabel = new Label("Pause", skin);
            pauseTitleLabel.setFontScale(2.5f);
            pauseTitleLabel.setAlignment(Align.center);

            pauseTable.add(pauseTitleLabel).row();;

            addMenuButton(pauseTable, "Continue", this::togglePause);
            addMenuButton(pauseTable,  "Return to Title", () -> this.parent.changeController(GuiControllerEnum.MAIN_MENU));
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        else {
            pauseTable.clear();
            pauseTitleLabel = null;
        }
    }


    @Override
    public void dispose() {
        super.dispose();

        gridCaseTexture = null;
        snipeTexture = null;
        carrierTexture = null;
        cruiserTexture = null;
        destroyerTexture = null;
        torpedoTexture = null;
    }
}
