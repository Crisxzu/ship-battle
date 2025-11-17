package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

/**
 * Game menu
 */
public class GameView extends GuiView {
    /**
     * Stack for layering UI elements
     */
    private Stack stack;

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
    private Table root;

    /**
     * Pause table UI element
     */
    private Table pauseTable;
    /**
     * Header labels for each grid column
     */
    private List<Label> headerLabels = new ArrayList<>();

    /**
     * Row labels for each grid row
     */
    private List<Label> rowLabels = new ArrayList<>();

    /**
     * Loli message label
     */
    private Label loliMsg;

    /**
     * Coordinate label displaying selected grid position
     */
    private Label coordLabel;

    /**
     * Pause title label
     */
    private Label pauseTitleLabel;

    /**
     * Turn label
     */
    private Label turnLabel;

    /**
     * Player name label
     */
    private Label nameLabel;

    /**
     * Pause image overlay
     */
    private Image pauseImage;

    /**
     * Loli image UI element
     */
    private Image loliImage;

    /**
     * Grid case texture
     */
    TextureRegionDrawable gridCaseTexture;

    /**
     * Snipe texture
     */
    TextureRegionDrawable snipeTexture;

    /**
     * Carrier ship texture
     */
    TextureRegionDrawable carrierTexture;

    /**
     * Cruiser ship texture
     */
    TextureRegionDrawable cruiserTexture;

    /**
     * Destroyer ship texture
     */
    TextureRegionDrawable destroyerTexture;

    /**
     * Torpedo texture
     */
    TextureRegionDrawable torpedoTexture;

    /**
     * Miss texture
     */
    TextureRegionDrawable missTexture;

    /**
     * Hit texture
     */
    TextureRegionDrawable hitTexture;

    /**
     * Sunk texture
     */
    TextureRegionDrawable sunkTexture;

    /**
     * Loli talking texture
     */
    TextureRegionDrawable loliTexture;

    /**
     * Pause background texture
     */
    TextureRegionDrawable pauseTexture;

    /**
     * Loli talking animation
     */
    Animation<TextureRegion> loliAnimation;


    /**
     * Current player
     */
    private Player currentPlayer;

    /**
     * Attack response after shooting
     */
    private AttackResponse response;

    /**
     * Game menu controller
     */
    private GameController controller;

    /**
     * Turn played flag
     */
    private boolean turnPlayed = false;

    /**
     * Shoot flag
     */
    private boolean shoot = false;

    /**
     * Wait timer for delays
     */
    private float waitTimer = 0f;

    /**
     * Elapsed time for animation
     */
    private float elapsed = 0f;

    /**
     * Paused flag
     */
    private boolean paused = false;


    /**
     * Initialize game menu
     *
     * @param parent screen manager
     * @param controller game menu controller
     */
    public GameView(ScreenController parent, GameController controller) {
        super(parent);
        this.controller = controller;
        loadTextures();
        buildUI();
        playDialog(
            DialogHandler.DialogID.TURN_START,
            (float) (loliAnimation.getAnimationDuration() * 1.25)
        );
    }

    /**
     * Load textures
     */
    @Override
    protected void loadTextures() {
        Texture gridCaseTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.GRID_CASE);
        this.gridCaseTexture = new TextureRegionDrawable(new TextureRegion(gridCaseTexture));

        Texture snipeTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.SNIPE);
        this.snipeTexture = new TextureRegionDrawable(new TextureRegion(snipeTexture));

        Texture carrierTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.CARRIER);
        this.carrierTexture = new TextureRegionDrawable(new TextureRegion(carrierTexture));

        Texture cruiserTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.CRUISER);
        this.cruiserTexture = new TextureRegionDrawable(new TextureRegion(cruiserTexture));

        Texture destroyerTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.DESTROYER);
        this.destroyerTexture = new TextureRegionDrawable(new TextureRegion(destroyerTexture));

        Texture torpedoTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.TORPEDO);
        this.torpedoTexture = new TextureRegionDrawable(new TextureRegion(torpedoTexture));

        Texture missTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.MISS);
        this.missTexture = new TextureRegionDrawable(new TextureRegion(missTexture));

        Texture hitTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.HIT);
        this.hitTexture = new TextureRegionDrawable(new TextureRegion(hitTexture));

        Texture sunkTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.SUNK);
        this.sunkTexture = new TextureRegionDrawable(new TextureRegion(sunkTexture));

        Texture pauseTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.PAUSE_BACKGROUND);
        this.pauseTexture = new TextureRegionDrawable(new TextureRegion(pauseTexture));

        loliAnimation = GifDecoder.loadGIFAnimation(
            Animation.PlayMode.LOOP,
            Gdx.files.internal("loli_talking.gif").read()
        );

        loliTexture = new TextureRegionDrawable(new TextureRegion(loliAnimation.getKeyFrame(elapsed)));
    }

    /**
     * Build game menu UI
     */
    @Override
    protected void buildUI() {
        super.buildUI();

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

        shipTable = new Table();
        shipTable.defaults().expand().fill();

        trackingTable = new Table();
        trackingTable.defaults().expand().fill();

        updatePlayerTables();

        root.add(shipTable)
            .width(Value.percentWidth(0.3f, root))
            .height(Value.percentHeight(0.5f, root))
            .expand();
        root.add(trackingTable)
            .width(Value.percentWidth(0.5f, root))
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

        root.debug();

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

    /**
     * Update table with model data
     *
     * @param table table to update
     * @param playerGrid player's grid data
     * @param caseSelectable if cases are selectable
     * @param showShips if ships should be shown
     */
    void updateTableWithModel(Table table, Grid playerGrid, boolean caseSelectable, boolean showShips) {
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

        Stack[][] stacks = new Stack[width][height];

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
                            if(!shoot && !paused) {
                                finalSnipeImage.setVisible(true);
                                coordLabel.setText(new Coordinate(finalI, finalJ).toLetterFormat());
                            }
                        }

                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            System.out.printf("Exit Case %d, %d\n", finalI, finalJ);
                            if(!shoot) {
                                finalSnipeImage.setVisible(false);
                            }
                        }

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if(!shoot && !paused) {
                                shoot = true;
                                Coordinate coord = new Coordinate(finalI, finalJ);

                                response = controller.playTurn(coord);
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
                                // Verticul cut of texture
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
    }

    /**
     * Play dialog
     *
     * @param dialogID dialog identifier
     * @param duration duration of dialog
     */
    void playDialog(DialogHandler.DialogID dialogID, float duration) {
        loliMsg.setText("");
        elapsed = 0;

        loliAnimation.setPlayMode(Animation.PlayMode.LOOP);

        DialogHandler.playDialog(
            dialogID,
            duration
        );
    }

    /**
     * Update game menu
     *
     * @param delta time since last frame
     */
    @Override
    public void update(float delta) {
        if(InputHandler.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }

        if(paused) {
            return;
        }

        super.update(delta);

        elapsed += delta;

        if(elapsed <= DialogHandler.getDialogDuration()) {
            int charCount = DialogHandler.getCharCountThisFrame(elapsed);

            if(charCount > 0) {
                String text = DialogHandler.getDialogText().substring(0, charCount-1);

                loliMsg.setText(text);
            }
        }
        else {
            loliAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        }

        loliTexture.setRegion(new TextureRegion(loliAnimation.getKeyFrame(elapsed)));
        loliImage.setDrawable(new TextureRegionDrawable(loliTexture));

        if(shoot && !turnPlayed) {
            waitTimer += delta;
            if(waitTimer > 1.5f) {
                AttackResult result = response.getResult();
                if(response.isHit()) {
                    if(result == AttackResult.SUNK) {
                        SoundHandler.playSound(SoundHandler.SoundID.SUNK, 0.2f);
                        playDialog(
                            DialogHandler.DialogID.SUNK,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                    else {
                        SoundHandler.playSound(SoundHandler.SoundID.HIT, 0.2f);
                        playDialog(
                            DialogHandler.DialogID.HIT,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                }
                else {
                    if(response.getResult() == AttackResult.ALREADY_HIT) {
                        SoundHandler.playSound(SoundHandler.SoundID.ALREADY_HIT, 0.2f);
                        playDialog(
                            DialogHandler.DialogID.ALREADY_HIT,
                            (float) (loliAnimation.getAnimationDuration() * 0.60)
                        );
                    }
                    else {
                        SoundHandler.playSound(SoundHandler.SoundID.MISS, 1f);
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

        if(turnPlayed) {
            waitTimer += delta;

            if(waitTimer > 3f) {
                Gdx.app.postRunnable(() -> controller.changeTurn());
            }
        }
    }

    /**
     * Render game menu
     */
    @Override
    public void render(float delta) {
        super.render(delta);
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
    private TextureRegionDrawable getShipDrawable(String shipName) {
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
    private void updatePlayerTables() {
        updateTableWithModel(
            shipTable,
            currentPlayer.getGrid(),
            false,
            true
        );

        updateTableWithModel(
            trackingTable,
            currentPlayer.getTrackingGrid(),
            true,
            false
        );
    }

    /**
     * Toggle pause menu
     */
    private void togglePause() {
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

        }
        else {
            pauseTable.clear();
            pauseTitleLabel = null;
        }
    }

    /**
     * Dispose resources
     */
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
