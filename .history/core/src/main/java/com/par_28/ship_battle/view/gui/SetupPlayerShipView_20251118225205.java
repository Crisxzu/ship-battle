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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SetupMenuController;
import com.par_28.ship_battle.model.Cell;
import com.par_28.ship_battle.model.Coordinate;
import com.par_28.ship_battle.model.Grid;
import com.par_28.ship_battle.model.Player;
import com.par_28.ship_battle.model.Ship;
import com.par_28.ship_battle.model.enums.Direction;

public final class SetupPlayerShipView extends GuiView<SetupMenuController> {

    private static final Color VALID_COLOR = new Color(0f, 1f, 0f, 0.35f);
    private static final Color INVALID_COLOR = new Color(1f, 0f, 0f, 0.35f);

    private Table root;
    private Table shipListTable;
    private Table gridTable;
    private final List<Label> headerLabels = new ArrayList<>();
    private final List<Label> rowLabels = new ArrayList<>();
    private final Map<Ship, TextButton> shipButtons = new HashMap<>();
    private final List<Image> activeHighlights = new ArrayList<>();
    private final List<Image> placedShipImages = new ArrayList<>();

    private Stack[][] cellStacks;
    private Image[][] highlightLayers;

    private Label titleLabel;
    private Label instructionLabel;
    private Label directionLabel;
    private Label helperLabel;
    private TextButton readyButton;

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
        Texture gridTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.GRID_CASE);
        gridCaseDrawable = new TextureRegionDrawable(new TextureRegion(gridTexture));

        carrierTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.SpriteID.CARRIER)
        ));
        cruiserTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.SpriteID.CRUISER)
        ));
        destroyerTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.SpriteID.DESTROYER)
        ));
        torpedoTexture = new TextureRegionDrawable(new TextureRegion(
            SpriteHandler.getTexture(SpriteHandler.SpriteID.TORPEDO)
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
        root.pad(20f);
        stage.addActor(root);

        Table leftPanel = new Table();
        leftPanel.defaults().growX().padBottom(10f);

        titleLabel = new Label("Ship Placement", skin);
        titleLabel.setAlignment(Align.left);
        leftPanel.add(titleLabel).left().row();

        instructionLabel = new Label("Left click = place, Right click = rotate", skin);
        instructionLabel.setAlignment(Align.left);
        leftPanel.add(instructionLabel).left().row();

        directionLabel = new Label(directionText(), skin);
        directionLabel.setAlignment(Align.left);
        leftPanel.add(directionLabel).left().row();

        helperLabel = new Label("Place every ship to continue", skin);
        helperLabel.setAlignment(Align.left);
        leftPanel.add(helperLabel).left().padBottom(6f).row();

        shipListTable = new Table();
        shipListTable.defaults().growX().padBottom(8f);
        leftPanel.add(shipListTable).growY().top().row();

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

        root.add(leftPanel)
            .width(Value.percentWidth(0.3f, root))
            .top()
            .left()
            .padRight(30f);

        gridTable = new Table();
        gridTable.defaults().pad(2f);
        buildGrid();

        root.add(gridTable)
            .expand()
            .fill();

        refreshShipList();
        refreshPlacedShips();
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

        gridTable.add().size(35f);
        for (int x = 0; x < width; x++) {
            char columnLetter = (char) ('A' + x);
            Label header = new Label(String.valueOf(columnLetter), skin);
            header.setAlignment(Align.center);
            gridTable.add(header).height(35f);
            headerLabels.add(header);
        }
        gridTable.row();

        for (int y = 0; y < height; y++) {
            Label rowLabel = new Label(String.valueOf(y + 1), skin);
            rowLabel.setAlignment(Align.center);
            gridTable.add(rowLabel).width(35f);
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
                    .size(Value.percentWidth(0.06f, gridTable));

                cellStacks[x][y] = stack;
            }
            gridTable.row();
        }
    }

    private void refreshShipList() {
        shipListTable.clear();
        shipButtons.clear();
        activeShipButton = null;

        List<Ship> ships = controller.getShipsToPlace();
        updateReadyState(ships.isEmpty());
        if(ships.isEmpty()) {
            Label doneLabel = new Label("All ships placed", skin);
            shipListTable.add(doneLabel).left();
            updateCursorForSelection(null);
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
                    if(controller.selectShip(ship)) {
                        placementDirection = Direction.HORIZONTAL;
                        updateDirectionLabel();
                        highlightSelectionButton(button);
                        updateCursorForSelection(ship);
                        lastHoveredCell = null;
                        clearHover();
                    }
                }
            });
            shipListTable.add(button).row();
            shipButtons.put(ship, button);
        }
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
                    if(ship.getDirection() == Direction.HORIZONTAL) {
                        partImage.setRotation(90f);
                    }
                    cellStacks[x][y].add(partImage);
                    placedShipImages.add(partImage);
                }
            }
        }
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
        int originalWidth = pixmap.getWidth();
        int originalHeight = pixmap.getHeight();
        cursorPixmap = ensureCursorPixmapIsValid(pixmap);
        int hotspotX = Math.min(originalWidth / 2, cursorPixmap.getWidth() - 1);
        int hotspotY = Math.min(originalHeight / 2, cursorPixmap.getHeight() - 1);
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

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        float scaleLarge = base / 250f;
        float scaleMedium = base / 350f;

        if(titleLabel != null) {
            titleLabel.setFontScale(scaleLarge);
        }
        if(instructionLabel != null) {
            instructionLabel.setFontScale(scaleMedium);
        }
        if(directionLabel != null) {
            directionLabel.setFontScale(scaleMedium);
        }
        if(helperLabel != null) {
            helperLabel.setFontScale(scaleMedium);
        }
        for (Label header : headerLabels) {
            header.setFontScale(base / 600f);
        }
        for (Label row : rowLabels) {
            row.setFontScale(base / 600f);
        }
        if(shipButtons != null) {
            shipButtons.values().forEach(button ->
                button.getLabel().setFontScale(base / 450f)
            );
        }
        if(readyButton != null) {
            readyButton.getLabel().setFontScale(base / 450f);
        }
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
