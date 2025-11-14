package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.par_28.ship_battle.controller.gui.GameController;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.Cell;
import com.par_28.ship_battle.model.enums.*;

import java.util.List;
import java.util.ArrayList;

public class GameView extends GuiView {
    public Table trackingTable;
    public Table shipTable;
    TextureRegionDrawable gridCaseDrawable;
    TextureRegionDrawable snipeDrawable;
    TextureRegionDrawable carrierDrawable;
    TextureRegionDrawable cruiserDrawable;
    TextureRegionDrawable destroyerDrawable;
    TextureRegionDrawable torpedoDrawable;
    TextureRegionDrawable missDrawable;
    TextureRegionDrawable hitDrawable;
    TextureRegionDrawable sunkDrawable;
    private Player currentPlayer;
    private AttackResponse response;
    private boolean turnPlayed = false;
    private boolean shooted = false;
    private float waitTimer = 0f;
    private GameController controller;
    private List<Label> headerLabels = new ArrayList<>();
    private List<Label> rowLabels = new ArrayList<>();

    public GameView(ScreenController parent, GameController controller) {
        super(parent);
        this.controller = controller;
        buildUI();
    }

    @Override
    protected void buildUI() {
        Texture gridCaseTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.GRID_CASE);
        gridCaseDrawable = new TextureRegionDrawable(new TextureRegion(gridCaseTexture));

        Texture snipeTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.SNIPE);
        snipeDrawable = new TextureRegionDrawable(new TextureRegion(snipeTexture));

        Texture carrierTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.CARRIER);
        carrierDrawable = new TextureRegionDrawable(new TextureRegion(carrierTexture));

        Texture cruiserTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.CRUISER);
        cruiserDrawable = new TextureRegionDrawable(new TextureRegion(cruiserTexture));

        Texture destroyerTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.DESTROYER);
        destroyerDrawable = new TextureRegionDrawable(new TextureRegion(destroyerTexture));

        Texture torpedoTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.TORPEDO);
        torpedoDrawable = new TextureRegionDrawable(new TextureRegion(torpedoTexture));

        Texture missTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.MISS);
        missDrawable = new TextureRegionDrawable(new TextureRegion(missTexture));

        Texture hitTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.HIT);
        hitDrawable = new TextureRegionDrawable(new TextureRegion(hitTexture));

        Texture sunkTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.SUNK);
        sunkDrawable = new TextureRegionDrawable(new TextureRegion(sunkTexture));

        currentPlayer = this.parent.app.game.getCurrentPlayer();

        Table root = new Table();
        root.setFillParent(true);

        shipTable = new Table();
        shipTable.defaults().expand().fill();

        trackingTable = new Table();
        trackingTable.defaults().expand().fill();

        updatePlayerTables();

        root.add(shipTable).size(Value.percentWidth(0.3f, root)).expand();
        root.add(trackingTable).size(Value.percentWidth(0.5f, root)).expand();
        stage.addActor(root);
    }

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

                Image gridCase = new Image(gridCaseDrawable);
                gridCase.setFillParent(true);
                stack.add(gridCase);

                int finalI = i;
                int finalJ = j;

                if(cell.isShot()) {
                    if(cell.hasShip()) {
                        if(cell.getShip().isDestroyed()) {
                            Image sunkImage = new Image(sunkDrawable);
                            sunkImage.setFillParent(true);
                            stack.add(sunkImage);
                        }
                        else {
                            Image hitImage = new Image(hitDrawable);
                            hitImage.setFillParent(true);
                            stack.add(hitImage);
                        }
                    }
                    else {
                        Image missImage = new Image(missDrawable);
                        missImage.setFillParent(true);
                        stack.add(missImage);
                    }
                }

                if(caseSelectable) {
                    Image snipeImage = new Image(snipeDrawable);
                    snipeImage.setFillParent(true);
                    snipeImage.setVisible(false);
                    stack.add(snipeImage);

                    final Image finalSnipeImage = snipeImage;

                    stack.addListener(new ClickListener() {
                        @Override
                        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                            System.out.printf("Enter Case %d, %d\n", finalI, finalJ);
                            if(!shooted) {
                                finalSnipeImage.setVisible(true);
                            }
                        }

                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            System.out.printf("Exit Case %d, %d\n", finalI, finalJ);
                            if(!shooted) {
                                finalSnipeImage.setVisible(false);
                            }
                        }

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if(!shooted) {
                                shooted = true;
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
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }



    @Override
    public void render(float delta) {
        if(shooted && !turnPlayed) {
            waitTimer += delta;
            if(waitTimer > 1.5f) {
                if(response.isHit()) {
                    if(response.getShip().isDestroyed()) {
                        SoundHandler.playSound(SoundHandler.SoundID.SUNK, 0.2f);
                    }
                    else {
                        SoundHandler.playSound(SoundHandler.SoundID.HIT, 0.2f);
                    }
                }
                else {
                    SoundHandler.playSound(SoundHandler.SoundID.MISS, 1f);
                }
                updatePlayerTables();
                turnPlayed = true;
                waitTimer = 0;
            }
        }

        if(turnPlayed) {
            waitTimer += delta;

            if(waitTimer > 2.5f) {
                Gdx.app.postRunnable(() -> controller.changeTurn());
            }
        }
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
    }

    /**
     * Obtient le TextureRegionDrawable correspondant au nom du bateau.
     * @param shipName le nom du bateau (Carrier, Cruiser, Destroyer, Torpedo)
     * @return le TextureRegionDrawable correspondant, ou null si non trouvé
     */
    private TextureRegionDrawable getShipDrawable(String shipName) {
        if (shipName == null) return null;

        switch (shipName) {
            case "Carrier":
                return carrierDrawable;
            case "Cruiser":
                return cruiserDrawable;
            case "Destroyer":
                return destroyerDrawable;
            case "Torpedo":
                return torpedoDrawable;
            default:
                return null;
        }
    }

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

    @Override
    public void dispose() {
        super.dispose();

        gridCaseDrawable = null;
        snipeDrawable = null;
        carrierDrawable = null;
        cruiserDrawable = null;
        destroyerDrawable = null;
        torpedoDrawable = null;
    }
}
