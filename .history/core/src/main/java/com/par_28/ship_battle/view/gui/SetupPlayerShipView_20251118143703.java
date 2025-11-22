package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;
import com.par_28.ship_battle.controller.gui.SetupMenuController.PlacementResult;
import com.par_28.ship_battle.controller.gui.SetupMenuController.ShipStatus;
import com.par_28.ship_battle.controller.gui.SetupMenuController.ShipTemplate;
import com.par_28.ship_battle.model.Cell;
import com.par_28.ship_battle.model.Grid;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.enums.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupPlayerShipView extends GuiView<SetupMenuController> {

    private Table root;
    private Table gridTable;
    private Table shipListTable;
    private Label titleLabel;
    private Label helperLabel;
    private Label feedbackLabel;
    private TextButton orientationButton;
    private TextButton readyButton;
    private TextButton resetButton;

    private Direction currentDirection = Direction.HORIZONTAL;
    private ShipTemplate selectedTemplate;

    private final Map<ShipTemplate, TextButton> shipButtons = new EnumMap<>(ShipTemplate.class);
    private final Map<String, TextureRegionDrawable> shipDrawables = new HashMap<>();
    private TextureRegionDrawable gridCaseDrawable;

    private GridCell[][] gridCells;

    public SetupPlayerShipView(ScreenController parent, SetupMenuController controller) {
        super(parent, controller);
    }

    @Override
    public void show() {
        if (root == null) {
            buildUI();
        }
        super.show();
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        Texture gridTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.GRID_CASE);
        if (gridTexture != null) {
            gridCaseDrawable = new TextureRegionDrawable(new TextureRegion(gridTexture));
        }
        loadShipDrawable("Carrier");
        loadShipDrawable("Cruiser");
        loadShipDrawable("Destroyer");
        loadShipDrawable("Torpedo");
    }

    private void loadShipDrawable(String shipName) {
        if (shipDrawables.containsKey(shipName)) {
            return;
        }
        Texture texture = SpriteHandler.getShipByName(shipName);
        if (texture != null) {
            shipDrawables.put(shipName, new TextureRegionDrawable(new TextureRegion(texture)));
        }
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        selectedTemplate = controller.getNextAvailableTemplate();

    root = new Table();
    root.setFillParent(true);
    root.pad(20f);
        stage.addActor(root);

    Table leftColumn = new Table();
    leftColumn.defaults().pad(10f).growX();

        titleLabel = new Label("", skin);
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2f);
        leftColumn.add(titleLabel).row();

        helperLabel = new Label("", skin);
        helperLabel.setWrap(true);
        leftColumn.add(helperLabel).row();

    shipListTable = new Table();
    shipListTable.defaults().pad(4f).fillX();
    buildShipButtons();
    leftColumn.add(shipListTable).growX().row();

        orientationButton = new TextButton("Orientation: Horizontal", skin);
        orientationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleOrientation();
            }
        });
        leftColumn.add(orientationButton).row();

        resetButton = new TextButton("Reset player grid", skin);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.resetCurrentPlayerPlacements();
                feedbackLabel.setText("Grid cleared");
            }
        });
        leftColumn.add(resetButton).row();

        readyButton = new TextButton("Fleet ready", skin);
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleReadyButton();
            }
        });
        leftColumn.add(readyButton).row();

        feedbackLabel = new Label("Select a ship and tap the grid", skin);
        feedbackLabel.setWrap(true);
        leftColumn.add(feedbackLabel).growX();

        gridTable = new Table();
        gridTable.defaults().pad(2f);

        root.add(leftColumn)
            .width(400f)
            .top()
            .growY();
        root.add(gridTable)
            .grow();

        refreshState();
    }

    private void buildShipButtons() {
        shipListTable.clearChildren();
        shipButtons.clear();

        for (ShipTemplate template : ShipTemplate.values()) {
            TextButton button = new TextButton(template.getDisplayName(), skin, "toggle");
            button.getLabel().setAlignment(Align.left);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (controller.canPlaceShip(template)) {
                        selectedTemplate = template;
                        refreshShipSelections();
                    }
                }
            });
            shipButtons.put(template, button);
            shipListTable.add(button).growX().row();
        }
    }

    private void refreshShipSelections() {
        List<ShipStatus> statuses = controller.getShipStatuses();
        boolean selectionStillValid = selectedTemplate != null && controller.canPlaceShip(selectedTemplate);
        if (!selectionStillValid) {
            selectedTemplate = controller.getNextAvailableTemplate();
        }

        for (ShipStatus status : statuses) {
            TextButton button = shipButtons.get(status.template());
            if (button == null) {
                continue;
            }
            String label = String.format("%s (%d/%d)",
                status.template().getDisplayName(),
                status.placed(),
                status.required()
            );
            button.setText(label);
            button.setDisabled(!controller.canPlaceShip(status.template()));
            button.setChecked(status.template() == selectedTemplate);
        }
    }

    private void rebuildGrid() {
        if (gridTable == null) {
            return;
        }
        gridTable.clearChildren();
        Player player = controller.getCurrentPlayer();
        if (player == null) {
            return;
        }

        Grid grid = player.getGrid();
        Cell[][] cells = grid.getCells();
        int width = grid.getWidth();
        int height = grid.getHeight();
        gridCells = new GridCell[width][height];

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                GridCell cellActor = new GridCell(x, y);
                cellActor.update(cells[x][y]);
                gridCells[x][y] = cellActor;
                gridTable.add(cellActor)
                    .size(32f);
            }
            gridTable.row();
        }
    }

    private void handleCellClicked(int x, int y) {
        if (selectedTemplate == null) {
            Dialogs.showErrorDialog(stage, "Select a ship to place");
            return;
        }

        PlacementResult result = controller.tryPlaceShip(selectedTemplate, x, y, currentDirection);
        if (!result.success()) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(stage, result.message());
        } else {
            feedbackLabel.setText(result.message());
        }
    }

    private void handleReadyButton() {
        if (!controller.isFleetCompleteForCurrentPlayer()) {
            Dialogs.showErrorDialog(stage, "Place every ship before continuing");
            return;
        }

        Player currentPlayer = controller.getCurrentPlayer();
        boolean advanced = controller.confirmCurrentPlayerReady();
        if (advanced && controller.getCurrentPlayer() != null && currentPlayer != controller.getCurrentPlayer()) {
            Dialogs.showOKDialog(stage, "Next Player", "Hand the device to the next player");
        }
    }

    private void toggleOrientation() {
        currentDirection = currentDirection == Direction.HORIZONTAL ? Direction.VERTICAL : Direction.HORIZONTAL;
        orientationButton.setText(
            String.format("Orientation: %s", currentDirection == Direction.HORIZONTAL ? "Horizontal" : "Vertical")
        );
    }

    private TextureRegionDrawable getShipDrawable(String shipName) {
        return shipDrawables.get(shipName);
    }

    private void refreshHelperText() {
        List<ShipStatus> statuses = controller.getShipStatuses();
        int remaining = statuses.stream().mapToInt(ShipStatus::remaining).sum();
        if (remaining == 0) {
            helperLabel.setText("Fleet complete! Press \"Fleet ready\" to continue.");
        } else {
            helperLabel.setText(String.format("Select a ship (%d remaining) and tap the grid to place it.", remaining));
        }
    }

    private void refreshControlsState() {
        boolean fleetComplete = controller.isFleetCompleteForCurrentPlayer();
        readyButton.setDisabled(!fleetComplete);
        resetButton.setDisabled(false);
    }

    private void updatePlayerHeader() {
        Player player = controller.getCurrentPlayer();
        if (player == null) {
            titleLabel.setText("Waiting for players...");
            return;
        }
        titleLabel.setText(String.format(
            "Player %d: %s",
            controller.getCurrentPlayerIndex() + 1,
            player.getName()
        ));
    }

    /**
     * Called by the controller whenever placement state changes.
     */
    public void refreshState() {
        if (root == null) {
            return;
        }
        updatePlayerHeader();
        refreshShipSelections();
        rebuildGrid();
        refreshHelperText();
        refreshControlsState();
    }

    private class GridCell extends com.badlogic.gdx.scenes.scene2d.ui.Stack {
        private final int x;
        private final int y;
        private final Image background;
        private final Image shipOverlay;

        GridCell(int x, int y) {
            this.x = x;
            this.y = y;
            background = new Image(gridCaseDrawable);
            background.setColor(new Color(1f, 1f, 1f, 0.85f));
            shipOverlay = new Image();
            shipOverlay.setScaling(Scaling.fit);
            shipOverlay.setVisible(false);
            addActor(background);
            addActor(shipOverlay);

            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float localX, float localY) {
                    handleCellClicked(GridCell.this.x, GridCell.this.y);
                }
            });
        }

        void update(Cell cell) {
            if (cell != null && cell.hasShip()) {
                Ship ship = cell.getShip();
                TextureRegionDrawable shipDrawable = getShipDrawable(ship.getName());
                shipOverlay.setDrawable(shipDrawable);
                shipOverlay.setVisible(shipDrawable != null);
            } else {
                shipOverlay.setVisible(false);
            }
        }
    }
}
