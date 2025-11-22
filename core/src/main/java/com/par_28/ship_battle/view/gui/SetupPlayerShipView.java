package com.par_28.ship_battle.view.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;
import com.par_28.ship_battle.model.Cell;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.model.Grid;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.enums.Direction;

public class SetupPlayerShipView extends GuiView<SetupMenuController> {

    private static final float BASE_SHIP_ICON_SIZE = 48f;

    private static final Color VALID_COLOR = new Color(0f, 1f, 0f, 0.35f);
    private static final Color INVALID_COLOR = new Color(1f, 0f, 0f, 0.35f);

    private Table root;
    private Table leftPanel;
    private VerticalGroup shipListGroup;
    private ScrollPane shipListScroll;
    private Container<ScrollPane> shipListContainer;
    private Table gridContainer;
    private Table gridTable;
    private com.badlogic.gdx.scenes.scene2d.ui.Cell<Table> leftPanelCell;
    private com.badlogic.gdx.scenes.scene2d.ui.Cell<Table> gridCell;
    private final List<Label> headerLabels = new ArrayList<>();
    private final List<Label> rowLabels = new ArrayList<>();
    private final List<Container<Image>> shipIconWrappers = new ArrayList<>();
    private final List<HorizontalGroup> shipRows = new ArrayList<>();
    private final Map<Ship, TextButton> shipButtons = new HashMap<>();
    private final List<Image> activeHighlights = new ArrayList<>();
    private final List<Image> placedShipImages = new ArrayList<>();

    private Stack[][] cellStacks;
    private Image[][] highlightLayers;

    private float cellPixelSize = 64f;

    private Label titleLabel;
    private Label instructionLabel;
    private Label directionLabel;
    private Label helperLabel;
    private TextButton readyButton;
    private TextButton resetButton;
    private TextButton cancelSelectionButton;

    private Direction placementDirection = Direction.HORIZONTAL;
    private Coordinate lastHoveredCell = null;
    private TextButton activeShipButton;

    private TextureRegionDrawable gridCaseDrawable;
    private TextureRegionDrawable carrierTexture;
    private TextureRegionDrawable cruiserTexture;
    private TextureRegionDrawable destroyerTexture;
    private TextureRegionDrawable torpedoTexture;
    private Texture highlightTexture;

    private Cursor customCursor;
    private Pixmap cursorPixmap;

    public SetupPlayerShipView(ScreenController parent, SetupMenuController controller) {
        super(parent, controller);
        buildUI();
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        Texture gridTexture = SpriteHandler.getTexture(SpriteHandler.TextureID.GRID_CASE);
        gridCaseDrawable = new TextureRegionDrawable(new TextureRegion(gridTexture));
        if(gridCaseDrawable != null && gridCaseDrawable.getMinWidth() > 0f) {
            cellPixelSize = gridCaseDrawable.getMinWidth();
        }

        carrierTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.TextureID.CARRIER)
        ));
        cruiserTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.TextureID.CRUISER)
        ));
        destroyerTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.TextureID.DESTROYER)
        ));
        torpedoTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.TextureID.TORPEDO)
        ));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        highlightTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    protected void buildUI() {
        super.buildUI();

    root = new Table();
    root.setFillParent(true);
    root.defaults().top().left();
    stage.addActor(root);

    leftPanel = new Table();
    leftPanel.defaults().growX().padBottom(10f);

    Table leftHeader = new Table();

    titleLabel = new Label("Ship Placement", skin);
        titleLabel.setAlignment(Align.left);
    leftHeader.add(titleLabel).left().growX().row();

        instructionLabel = new Label("Left click = place, Right click = rotate", skin);
        instructionLabel.setAlignment(Align.left);
    leftHeader.add(instructionLabel).left().growX().row();

        directionLabel = new Label(directionText(), skin);
        directionLabel.setAlignment(Align.left);
    leftHeader.add(directionLabel).left().growX().row();

        helperLabel = new Label("Place every ship to continue", skin);
        helperLabel.setAlignment(Align.left);
    leftHeader.add(helperLabel).left().padBottom(6f).growX().row();

    leftPanel.add(leftHeader).growX().padBottom(6f).row();

    shipListGroup = new VerticalGroup();
    shipListGroup.align(Align.topLeft);
    shipListGroup.fill();
    shipListGroup.space(8f);
    shipListScroll = new ScrollPane(shipListGroup, skin);
    shipListScroll.setScrollingDisabled(true, false);
    shipListScroll.setFadeScrollBars(false);
    shipListScroll.setForceScroll(false, true);
    shipListContainer = new Container<>(shipListScroll);
    shipListContainer.fill();
    leftPanel.add(shipListContainer).grow().top().row();

    readyButton = new TextButton("Fleet ready", skin);
        readyButton.setDisabled(true);
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleReadyButton();
            }
        });
        leftPanel.add(readyButton)
            .growX()
            .padTop(10f)
            .row();

    Table secondaryActionsRow = new Table();
        secondaryActionsRow.defaults().growX().padRight(10f);

        resetButton = new TextButton("Reset placement", skin);
        resetButton.setDisabled(true);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleResetPlacement();
            }
        });
        secondaryActionsRow.add(resetButton).growX();

        cancelSelectionButton = new TextButton("Cancel selection", skin);
        cancelSelectionButton.setDisabled(true);
        cancelSelectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleCancelSelection();
            }
        });
        secondaryActionsRow.add(cancelSelectionButton).growX().padRight(0f);

        leftPanel.add(secondaryActionsRow)
            .growX()
            .padTop(6f)
            .row();

        leftPanelCell = root.add(leftPanel)
            .width(Value.percentWidth(0.35f, root))
            .minWidth(320f)
            .growY()
            .pad(20f)
            .padRight(20f);

        gridTable = new Table();
        gridTable.defaults().pad(2f);
        buildGrid();

        scheduleCellSizeUpdate();

        gridContainer = new Table();
        gridContainer.setFillParent(false);
        gridContainer.add(gridTable).expand().center();

        gridCell = root.add(gridContainer)
            .grow()
            .pad(20f);

        refreshShipList();
        refreshPlacedShips();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        updateControlButtonsState();

        applyResponsiveLayout((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
    }

    private void scheduleCellSizeUpdate() {
        if(stage != null) {
            stage.addAction(Actions.run(this::updateCellPixelSize));
        }
    }

    private void updateCellPixelSize() {
        if(cellStacks == null || cellStacks.length == 0 || cellStacks[0].length == 0) {
            return;
        }
        Stack firstCell = cellStacks[0][0];
        if(firstCell == null) {
            return;
        }
        float currentSize = Math.min(firstCell.getWidth(), firstCell.getHeight());
        if(currentSize > 0f) {
            cellPixelSize = currentSize;
            updateCursorForSelection(controller.getSelectedShip());
        }
    }

    private void buildGrid() {
        gridTable.clear();
        headerLabels.clear();
        rowLabels.clear();

        Player player = controller.getCurrentSetupPlayer();
        if(player == null) {
            return;
        }

        Grid grid = player.getGrid();
        int width = grid.getWidth();
        int height = grid.getHeight();

        cellStacks = new Stack[width][height];
        highlightLayers = new Image[width][height];

    gridTable.add().size(35f).expandX().left();
        for (int x = 0; x < width; x++) {
            char columnLetter = (char) ('A' + x);
            Label header = new Label(String.valueOf(columnLetter), skin);
            header.setAlignment(Align.center);
            gridTable.add(header).height(35f).expandX();
            headerLabels.add(header);
        }
        gridTable.row();

        for (int y = 0; y < height; y++) {
            Label rowLabel = new Label(String.valueOf(y + 1), skin);
            rowLabel.setAlignment(Align.center);
            gridTable.add(rowLabel).width(35f).expandY();
            rowLabels.add(rowLabel);

            for (int x = 0; x < width; x++) {
                Stack stack = new Stack();
                stack.setTouchable(Touchable.enabled);
                stack.setName(String.format("cell-%d-%d", x, y));

                Image gridCellImage = new Image(gridCaseDrawable);
                gridCellImage.setFillParent(true);
                stack.add(gridCellImage);

                Image highlight = new Image(new TextureRegionDrawable(new TextureRegion(highlightTexture)));
                highlight.setFillParent(true);
                highlight.setVisible(false);
                stack.add(highlight);
                highlightLayers[x][y] = highlight;

                final int cellX = x;
                final int cellY = y;

                stack.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        handleHover(cellX, cellY);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        if (toActor == null || !(toActor.getName() != null && toActor.getName().startsWith("cell-"))) {
                            clearHover();
                        }
                    }

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if(pointer > 0) {
                            return false;
                        }

                        if(button == Input.Buttons.RIGHT) {
                            toggleDirection();
                            handleHover(cellX, cellY);
                            return true;
                        }

                        if(button == Input.Buttons.LEFT) {
                            attemptPlacement(cellX, cellY);
                            return true;
                        }

                        return false;
                    }
                });

                gridTable.add(stack)
                    .minSize(48f)
                    .grow()
                    .uniform();

                cellStacks[x][y] = stack;
            }
            gridTable.row();
        }
    }

    private void refreshShipList() {
        shipListGroup.clearChildren();
        shipIconWrappers.clear();
        shipRows.clear();
        shipButtons.clear();
        activeShipButton = null;

        List<Ship> ships = controller.getShipsToPlace();
        updateReadyState(ships.isEmpty());
        if(ships.isEmpty()) {
            Label doneLabel = new Label("All ships placed", skin);
            doneLabel.setAlignment(Align.left);
            shipListGroup.addActor(doneLabel);
            updateCursorForSelection(null);
            Gdx.app.postRunnable(() -> controller.handlePlacementComplete());
            return;
        }

        Map<String, Integer> occurrences = new HashMap<>();
        Map<String, Integer> indexMap = new HashMap<>();

        for (Ship ship : ships) {
            occurrences.merge(ship.getName(), 1, Integer::sum);
        }

        for (Ship ship : ships) {
            int count = occurrences.getOrDefault(ship.getName(), 1);
            int index = indexMap.merge(ship.getName(), 1, Integer::sum);
            String label = count > 1 ? String.format("%s #%d", ship.getName(), index) : ship.getName();
            TextButton button = new TextButton(label, skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleShipSelection(ship, button);
                }
            });
            button.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttonCode) {
                    if(pointer > 0 || buttonCode != Input.Buttons.LEFT) {
                        return false;
                    }
                    Ship current = controller.getSelectedShip();
                    if(current != null && current != ship) {
                        return handleShipSelection(ship, button);
                    }
                    return false;
                }
            });
            HorizontalGroup row = new HorizontalGroup();
            row.align(Align.left);
            row.space(10f);
            row.fill();
            row.expand();
            shipRows.add(row);

            Container<Image> iconWrapper = new Container<>();
            iconWrapper.size(BASE_SHIP_ICON_SIZE, BASE_SHIP_ICON_SIZE);
            iconWrapper.fill();
            iconWrapper.align(Align.center);
            Image icon = createShipIcon(ship);
            if(icon != null) {
                iconWrapper.setActor(icon);
            }
            shipIconWrappers.add(iconWrapper);
            row.addActor(iconWrapper);

            Container<TextButton> buttonWrapper = new Container<>(button);
            buttonWrapper.fillX();
            buttonWrapper.align(Align.left);
            row.addActor(buttonWrapper);

            shipListGroup.addActor(row);
            shipButtons.put(ship, button);
        }

        updateControlButtonsState();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        applyResponsiveLayout(width, height);
    }

    private void applyResponsiveLayout(int width, int height) {
        float baseSize = getBaseSize();
        if(baseSize <= 0f) {
            baseSize = Math.min(Math.max(width, 1), Math.max(height, 1));
        }

        updatePanelDistribution(width, height);
        updateTextScales(baseSize);
        updateShipListPresentation(baseSize);
        updateGridPresentation(baseSize);
    }

    private void updatePanelDistribution(int width, int height) {
        if(root == null) {
            return;
        }
    boolean narrowLayout = width < 1100 || height < 700;
        float padding = MathUtils.clamp(width * 0.015f, 10f, 40f);

        root.clearChildren();
        if(narrowLayout) {
            root.pad(padding).defaults().growX().padBottom(padding);
            gridCell = root.add(gridContainer)
                .grow()
                .padBottom(padding * 0.5f);
            root.row();
            leftPanelCell = root.add(leftPanel)
                .growX();
        }
        else {
            root.pad(padding * 1.5f).defaults().top().left();
            leftPanelCell = root.add(leftPanel)
                .width(Value.percentWidth(0.32f, root))
                .minWidth(320f)
                .padRight(padding);
            gridCell = root.add(gridContainer)
                .grow();
        }

        gridContainer.pad(padding * 0.3f);

        if(leftPanelCell != null) {
            leftPanelCell.fillX();
        }
        if(gridCell != null) {
            gridCell.expand().fill();
        }
    }

    private void updateTextScales(float baseSize) {
        float titleScale = MathUtils.clamp(baseSize / 320f, 1.1f, 2.8f);
        float bodyScale = MathUtils.clamp(baseSize / 520f, 0.8f, 1.7f);
        float helperScale = MathUtils.clamp(baseSize / 600f, 0.7f, 1.4f);
        float buttonScale = MathUtils.clamp(baseSize / 620f, 0.65f, 1.4f);

        titleLabel.setFontScale(titleScale);
        instructionLabel.setFontScale(bodyScale);
        directionLabel.setFontScale(bodyScale);
        helperLabel.setFontScale(helperScale);

        setButtonLabelScale(readyButton, buttonScale);
        setButtonLabelScale(resetButton, buttonScale);
        setButtonLabelScale(cancelSelectionButton, buttonScale);

        for (TextButton button : shipButtons.values()) {
            setButtonLabelScale(button, buttonScale * 0.95f);
        }
    }

    private void setButtonLabelScale(TextButton button, float scale) {
        if(button == null) {
            return;
        }
        button.getLabel().setFontScale(scale);
    }

    private void updateShipListPresentation(float baseSize) {
        if(shipListGroup == null) {
            return;
        }
        float iconSize = MathUtils.clamp(BASE_SHIP_ICON_SIZE * (baseSize / 600f), 30f, 84f);
        float rowSpacing = MathUtils.clamp(iconSize * 0.2f, 6f, 20f);

        shipListGroup.space(rowSpacing);

        for (HorizontalGroup row : shipRows) {
            if(row == null) {
                continue;
            }
            row.space(Math.max(rowSpacing * 0.6f, 4f));
            row.padBottom(rowSpacing * 0.4f);
        }

        for (Container<Image> iconWrapper : shipIconWrappers) {
            if(iconWrapper == null) {
                continue;
            }
            iconWrapper.size(iconSize, iconSize);
            Image icon = iconWrapper.getActor();
            if(icon != null) {
                icon.setSize(iconSize, iconSize);
            }
        }

        if(shipListScroll != null) {
            shipListScroll.setSmoothScrolling(baseSize > 420f);
            shipListScroll.setFadeScrollBars(baseSize > 680f);
        }
    }

    private void updateGridPresentation(float baseSize) {
        if(gridTable == null) {
            return;
        }
        float cellPadding = MathUtils.clamp(baseSize / 120f, 2f, 8f);
        float labelScale = MathUtils.clamp(baseSize / 720f, 0.65f, 1.4f);

        for (com.badlogic.gdx.scenes.scene2d.ui.Cell<?> cell : gridTable.getCells()) {
            cell.pad(cellPadding).minSize(Math.max(40f, baseSize / 18f));
        }
        for (Label label : headerLabels) {
            label.setFontScale(labelScale);
        }
        for (Label label : rowLabels) {
            label.setFontScale(labelScale);
        }
    }

    private boolean handleShipSelection(Ship ship, TextButton button) {
        if(!controller.selectShip(ship)) {
            return false;
        }
        placementDirection = Direction.HORIZONTAL;
        updateDirectionLabel();
        highlightSelectionButton(button);
        updateCursorForSelection(ship);
        lastHoveredCell = null;
        clearHover();
        updateControlButtonsState();
        return true;
    }

    private Image createShipIcon(Ship ship) {
        if(ship == null) {
            return null;
        }

        TextureRegionDrawable drawable = getShipDrawable(ship.getName());
        if(drawable == null) {
            return null;
        }

        Image icon = new Image(drawable);
        icon.setScaling(Scaling.fit);
        icon.setAlign(Align.center);
        return icon;
    }

    private void handleReadyButton() {
        if(controller == null || readyButton == null) {
            return;
        }

        if(controller.getShipsToPlaceCount() > 0) {
            helperLabel.setText("Finish placing every ship before validating.");
            return;
        }

        readyButton.setDisabled(true);
        controller.handlePlacementComplete();
    }

    private void handleResetPlacement() {
        if(controller == null || resetButton == null || resetButton.isDisabled()) {
            return;
        }
        controller.resetCurrentPlacement();
        refreshShipList();
        refreshPlacedShips();
        clearHover();
        highlightSelectionButton(null);
        updateCursorForSelection(null);
        helperLabel.setText("Placements cleared. Select a ship to start again.");
        updateControlButtonsState();
    }

    private void handleCancelSelection() {
        if(controller == null || cancelSelectionButton == null || cancelSelectionButton.isDisabled()) {
            return;
        }
        controller.cancelSelection();
        highlightSelectionButton(null);
        clearHover();
        updateCursorForSelection(null);
        helperLabel.setText("Selection canceled. Pick a ship to continue.");
        updateControlButtonsState();
    }

    private void updateReadyState(boolean fleetComplete) {
        if(readyButton != null) {
            readyButton.setDisabled(!fleetComplete);
        }
        if(helperLabel != null) {
            helperLabel.setText(
                fleetComplete
                    ? "Fleet complete! Validate to continue."
                    : "Place every ship to continue"
            );
        }
    }

    private void updateControlButtonsState() {
        if(resetButton != null) {
            boolean canReset = controller != null && controller.hasPlacedShips();
            resetButton.setDisabled(!canReset);
        }
        if(cancelSelectionButton != null) {
            boolean hasSelection = controller != null && controller.getSelectedShip() != null;
            cancelSelectionButton.setDisabled(!hasSelection);
        }
    }

    private void highlightSelectionButton(TextButton button) {
        if(activeShipButton != null) {
            activeShipButton.setChecked(false);
        }
        activeShipButton = button;
        if(activeShipButton != null) {
            activeShipButton.setChecked(true);
        }
    }

    private void handleHover(int x, int y) {
        Ship selected = controller.getSelectedShip();
        if(selected == null) {
            clearHover();
            return;
        }

        lastHoveredCell = new Coordinate(x, y);
        clearHover();

        List<Coordinate> span = buildSpan(lastHoveredCell, selected.getLength());
        boolean outOfBounds = span.size() < selected.getLength();
        boolean canPlace = !outOfBounds && controller.canPlaceSelectedShip(lastHoveredCell, placementDirection);

        Color color = canPlace ? VALID_COLOR : INVALID_COLOR;
        for (Coordinate coord : span) {
            if(!isInsideGrid(coord)) {
                continue;
            }
            Image overlay = highlightLayers[coord.getX()][coord.getY()];
            overlay.setColor(color);
            overlay.setVisible(true);
            activeHighlights.add(overlay);
        }
    }

    private void clearHover() {
        for (Image image : activeHighlights) {
            image.setVisible(false);
        }
        activeHighlights.clear();
    }

    private void attemptPlacement(int x, int y) {
        Ship selected = controller.getSelectedShip();
        if(selected == null) {
            return;
        }

        Coordinate origin = new Coordinate(x, y);
        if(controller.placeSelectedShip(origin, placementDirection)) {
            refreshShipList();
            refreshPlacedShips();
            clearHover();
            updateCursorForSelection(null);
            updateControlButtonsState();
        }
    }

    private List<Coordinate> buildSpan(Coordinate origin, int length) {
        List<Coordinate> coords = new ArrayList<>();
        if(origin == null) {
            return coords;
        }
        int dx = placementDirection == Direction.HORIZONTAL ? 1 : 0;
        int dy = placementDirection == Direction.VERTICAL ? 1 : 0;
        for (int i = 0; i < length; i++) {
            coords.add(new Coordinate(origin.getX() + i * dx, origin.getY() + i * dy));
        }
        return coords;
    }

    private boolean isInsideGrid(Coordinate coord) {
        Player player = controller.getCurrentSetupPlayer();
        if(player == null || coord == null) {
            return false;
        }
        Grid grid = player.getGrid();
        return coord.getX() >= 0 && coord.getX() < grid.getWidth()
            && coord.getY() >= 0 && coord.getY() < grid.getHeight();
    }

    private void toggleDirection() {
        placementDirection = (placementDirection == Direction.HORIZONTAL)
            ? Direction.VERTICAL
            : Direction.HORIZONTAL;
        updateDirectionLabel();
        updateCursorForSelection(controller.getSelectedShip());
        if(lastHoveredCell != null) {
            handleHover(lastHoveredCell.getX(), lastHoveredCell.getY());
        }
    }

    private void updateDirectionLabel() {
        directionLabel.setText(directionText());
    }

    private String directionText() {
        return String.format("Direction: %s", placementDirection.name());
    }

    private void refreshPlacedShips() {
        for (Image image : placedShipImages) {
            image.remove();
        }
        placedShipImages.clear();

        Player player = controller.getCurrentSetupPlayer();
        if(player == null) {
            return;
        }

        Grid grid = player.getGrid();
        Cell[][] cells = grid.getCells();
        int width = grid.getWidth();
        int height = grid.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = cells[x][y];
                if(cell.hasShip() && cell.getShip().getPositions() != null) {
                    Ship ship = cell.getShip();
                    List<Coordinate> positions = ship.getPositions();
                    int index = positions.indexOf(new Coordinate(x, y));
                    if(index < 0) {
                        continue;
                    }

                    TextureRegionDrawable shipDrawable = getShipDrawable(ship.getName());
                    if(shipDrawable == null) {
                        continue;
                    }

                    Texture shipTexture = shipDrawable.getRegion().getTexture();
                    int shipLength = ship.getLength();
                    int partHeight = shipTexture.getHeight() / shipLength;
                    TextureRegion partRegion = new TextureRegion(
                        shipTexture,
                        0,
                        partHeight * index,
                        shipTexture.getWidth(),
                        partHeight
                    );

                    Image partImage = new Image(new TextureRegionDrawable(partRegion));
                    partImage.setFillParent(true);
                    partImage.setOrigin(Align.center);
                    partImage.setScaling(Scaling.stretch);
                    if(ship.getDirection() == Direction.HORIZONTAL) {
                        partImage.setRotation(90f);
                    }
                    cellStacks[x][y].add(partImage);
                    placedShipImages.add(partImage);
                }
            }
        }

        updateControlButtonsState();
    }

    private TextureRegionDrawable getShipDrawable(String shipName) {
        if(shipName == null) {
            return null;
        }
        return switch (shipName) {
            case "Carrier" -> carrierTexture;
            case "Cruiser" -> cruiserTexture;
            case "Destroyer" -> destroyerTexture;
            case "Torpedo" -> torpedoTexture;
            default -> null;
        };
    }

    private void updateCursorForSelection(Ship ship) {
        clearCursor();
        if(ship == null) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            return;
        }

        String spritePath = getCursorSpritePath(ship.getName());
        if(spritePath == null) {
            return;
        }

        Pixmap pixmap = new Pixmap(Gdx.files.internal(spritePath));
        pixmap = prepareCursorPixmap(pixmap, ship);
        if(pixmap == null) {
            return;
        }

        cursorPixmap = ensureCursorPixmapIsValid(pixmap);
        if(cursorPixmap == null) {
            return;
        }

        int hotspotX = Math.min(cursorPixmap.getWidth() / 2, cursorPixmap.getWidth() - 1);
        int hotspotY = Math.min(cursorPixmap.getHeight() / 2, cursorPixmap.getHeight() - 1);
        customCursor = Gdx.graphics.newCursor(cursorPixmap, hotspotX, hotspotY);
        Gdx.graphics.setCursor(customCursor);
    }

    private void clearCursor() {
        if(customCursor != null) {
            customCursor.dispose();
            customCursor = null;
        }
        if(cursorPixmap != null) {
            cursorPixmap.dispose();
            cursorPixmap = null;
        }
    }

    private String getCursorSpritePath(String shipName) {
        return switch (shipName) {
            case "Carrier" -> "ships/carrier.png";
            case "Cruiser" -> "ships/cruiser.png";
            case "Destroyer" -> "ships/destroyer.png";
            case "Torpedo" -> "ships/torpedo.png";
            default -> null;
        };
    }

    private Pixmap ensureCursorPixmapIsValid(Pixmap source) {
        if(source == null) {
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        int pow2Width = MathUtils.nextPowerOfTwo(width);
        int pow2Height = MathUtils.nextPowerOfTwo(height);
        if(pow2Width == width && pow2Height == height) {
            return source;
        }

        Pixmap resized = new Pixmap(pow2Width, pow2Height, source.getFormat());
        resized.drawPixmap(source, 0, 0);
        source.dispose();
        return resized;
    }

    private Pixmap prepareCursorPixmap(Pixmap source, Ship ship) {
        if(source == null) {
            return null;
        }

        Pixmap aligned = alignCursorPixmapToDirection(source);
        return scalePixmapToCellSize(aligned, ship);
    }

    private Pixmap alignCursorPixmapToDirection(Pixmap source) {
        if(source == null) {
            return null;
        }

        if(placementDirection != Direction.HORIZONTAL) {
            return source;
        }

        return rotatePixmap90Degrees(source);
    }

    private Pixmap rotatePixmap90Degrees(Pixmap source) {
        Pixmap rotated = new Pixmap(source.getHeight(), source.getWidth(), source.getFormat());
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                int pixel = source.getPixel(x, y);
                rotated.drawPixel(source.getHeight() - 1 - y, x, pixel);
            }
        }
        source.dispose();
        return rotated;
    }

    private Pixmap scalePixmapToCellSize(Pixmap source, Ship ship) {
        if(source == null || ship == null) {
            return source;
        }

        int cellSize = Math.max(1, Math.round(cellPixelSize));
        int shipLength = Math.max(1, ship.getLength());

        int targetWidth = cellSize;
        int targetHeight = cellSize * shipLength;

        if(placementDirection == Direction.HORIZONTAL) {
            targetWidth = cellSize * shipLength;
            targetHeight = cellSize;
        }

        if(source.getWidth() == targetWidth && source.getHeight() == targetHeight) {
            return source;
        }

        Pixmap scaled = new Pixmap(targetWidth, targetHeight, source.getFormat());
        scaled.drawPixmap(
            source,
            0,
            0,
            source.getWidth(),
            source.getHeight(),
            0,
            0,
            targetWidth,
            targetHeight
        );
        source.dispose();
        return scaled;
    }

    @Override
    public void dispose() {
        super.dispose();
        clearCursor();
        if(highlightTexture != null) {
            highlightTexture.dispose();
            highlightTexture = null;
        }
    }
}
