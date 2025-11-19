package com.par_28.ship_battle.controller.gui;

import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.ai.AIPlayer;
import com.par_28.ship_battle.model.ai.enums.AIDifficulty;
import com.par_28.ship_battle.model.enums.Direction;
import com.par_28.ship_battle.model.exceptions.IllegalGameStateException;
import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import com.par_28.ship_battle.model.exceptions.ShipPlacementException;
import com.par_28.ship_battle.view.gui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Manager of setup menu where players enter their names and set up ships.
 */
public class SetupMenuController extends GuiController {
    /**
     * List of player names entered.
     */
    private final List<String> names = new ArrayList<>();

    /**
     * Ordered list of players participating in the game.
     */
    private final List<Player> players = new ArrayList<>();

    private AIDifficulty aiDifficulty = null;

    private List<ShipTemplate> currentTemplates = Collections.emptyList();

    private ShipTemplate selectedTemplate;

    private Direction currentDirection = Direction.HORIZONTAL;

    private int currentPlayerIndex = -1;

    /**
     * Initialize the menu controller.
     *
     * @param parent Reference to the parent screen controller
     */
    public SetupMenuController(ScreenController parent) {
        super(parent);
    }

    /**
     * Update menu controller.
     *
     * @param dt Delta time since last update
     */
    public void update(float dt){

    }

    /**
     * Add a player name to the setup.
     *
     * @param name Player name to add
     * @return true if another name is required, false when setup can continue
     */
    public boolean addName(String name) {
        if(name == null || name.isEmpty()) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * this.parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(view.stage, "Please enter a name");
            return true;
        }

        if(names.contains(name)) {
            SoundHandler.playSound(
                SoundHandler.SoundID.ERROR,
                0.2f * this.parent.app.settingsHandler.getSoundVolume()
            );
            Dialogs.showErrorDialog(view.stage, String.format("%s already registered", name));
            return true;
        }

        this.names.add(name);

        if(isAIMode()) {
            this.names.add(AIPlayer.getRandomName());
        }

        if(this.names.size() >= this.parent.app.nbPlayers) {
            startShipSetupFlow();
            return false;
        }

        return true;
    }

    public boolean isAIMode() {
        return aiDifficulty != null;
    }

    public void setAIDifficulty(AIDifficulty aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public void goToDifficultyMenu() {
        changeView(new DifficultyView(this.parent, this));
    }

    public void gotoSetupPlayerNameMenu() {
        changeView(new SetupPlayerNameView(this.parent, this));
    }

    public void gotoGameModeMenu() {
        changeView(new GameModeView(this.parent, this));
    }

    /**
     * Render menu view.
     *
     * @param dt Delta time since last render
     */
    @Override
    public void render(float dt){
        super.render(dt);
        if(view != null) {
            view.render(dt);
        }
    }

    /**
     * Reset menu to initial state.
     */
    @Override
    public void reset() {
        names.clear();
        players.clear();
        currentTemplates = Collections.emptyList();
        selectedTemplate = null;
        currentDirection = Direction.HORIZONTAL;
        currentPlayerIndex = -1;
        aiDifficulty = null;
        changeView(new GameModeView(this.parent, this));
    }

    /**
     * Templates that describe the ships to place.
     */
    public static class ShipTemplate {
        private final String id;
        private final String displayName;
        private final int length;
        private final Supplier<Ship> factory;
        private ShipStatus status = ShipStatus.PENDING;
        private List<Coordinate> placedCoordinates = Collections.emptyList();
        private Direction placedDirection;

        ShipTemplate(String id, String displayName, int length, Supplier<Ship> factory) {
            this.id = id;
            this.displayName = displayName;
            this.length = length;
            this.factory = factory;
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getLength() {
            return length;
        }

        public ShipStatus getStatus() {
            return status;
        }

        void setStatus(ShipStatus status) {
            this.status = status;
        }

        List<Coordinate> getPlacedCoordinates() {
            return placedCoordinates;
        }

        void setPlacedCoordinates(List<Coordinate> placedCoordinates) {
            this.placedCoordinates = placedCoordinates;
        }

        Direction getPlacedDirection() {
            return placedDirection;
        }

        void setPlacedDirection(Direction placedDirection) {
            this.placedDirection = placedDirection;
        }

        Ship createShipInstance() {
            return factory.get();
        }
    }

    public enum ShipStatus {
        PENDING,
        PLACED
    }

    /**
     * Result of a placement-related action.
     */
    public static class PlacementResult {
        private final boolean success;
        private final String message;
        private final boolean gameStarted;

        private PlacementResult(boolean success, String message, boolean gameStarted) {
            this.success = success;
            this.message = message;
            this.gameStarted = gameStarted;
        }

        public static PlacementResult success(String message) {
            return new PlacementResult(true, message, false);
        }

        public static PlacementResult success(String message, boolean gameStarted) {
            return new PlacementResult(true, message, gameStarted);
        }

        public static PlacementResult error(String message) {
            return new PlacementResult(false, message, false);
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean hasStartedGame() {
            return gameStarted;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Preview value for a potential placement.
     */
    public static class PlacementPreview {
        private final boolean valid;
        private final List<Coordinate> coordinates;
        private final String message;

        private PlacementPreview(boolean valid, List<Coordinate> coordinates, String message) {
            this.valid = valid;
            this.coordinates = coordinates;
            this.message = message;
        }

        public static PlacementPreview invalid(String message) {
            return new PlacementPreview(false, Collections.emptyList(), message);
        }

        public static PlacementPreview valid(List<Coordinate> coordinates) {
            return new PlacementPreview(true, coordinates, "");
        }

        public boolean isValid() {
            return valid;
        }

        public List<Coordinate> getCoordinates() {
            return coordinates;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * List of ship templates available for the current player.
     */
    public List<ShipTemplate> getCurrentShipTemplates() {
        return Collections.unmodifiableList(currentTemplates);
    }

    public ShipTemplate getSelectedTemplate() {
        return selectedTemplate;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void toggleDirection() {
        currentDirection = currentDirection == Direction.HORIZONTAL ? Direction.VERTICAL : Direction.HORIZONTAL;
        notifyViewRefresh();
    }

    public boolean selectShip(String templateId) {
        if(templateId == null) {
            return false;
        }
        ShipTemplate template = currentTemplates.stream()
            .filter(t -> Objects.equals(t.getId(), templateId) && t.getStatus() == ShipStatus.PENDING)
            .findFirst()
            .orElse(null);
        if(template == null) {
            return false;
        }
        selectedTemplate = template;
        notifyViewRefresh();
        return true;
    }

    public PlacementPreview getPlacementPreview(int x, int y) {
        if(selectedTemplate == null || getCurrentPlayer() == null) {
            return PlacementPreview.invalid("Select a ship first");
        }
        List<Coordinate> coords = computePlacementCoordinates(
            getCurrentPlayer().getGrid(),
            selectedTemplate.getLength(),
            new Coordinate(x, y),
            currentDirection
        );
        if(coords.isEmpty()) {
            return PlacementPreview.invalid("Invalid placement");
        }
        return PlacementPreview.valid(coords);
    }

    public PlacementResult placeShipAt(int x, int y) {
        Player current = getCurrentPlayer();
        if(current == null) {
            return PlacementResult.error("No player available");
        }
        if(selectedTemplate == null) {
            return PlacementResult.error("Select a ship before placing it");
        }

        Coordinate coord = new Coordinate(x, y);
        List<Coordinate> preview = computePlacementCoordinates(current.getGrid(), selectedTemplate.getLength(), coord, currentDirection);
        if(preview.isEmpty()) {
            return PlacementResult.error("Cannot place ship here");
        }

        Ship ship = selectedTemplate.createShipInstance();
        try {
            current.placeShipOnGrid(ship, coord, currentDirection);
            current.addShip(ship);
        } catch (InvalidCoordinateException | ShipPlacementException e) {
            return PlacementResult.error(e.getMessage());
        }

        selectedTemplate.setStatus(ShipStatus.PLACED);
        selectedTemplate.setPlacedCoordinates(Collections.unmodifiableList(new ArrayList<>(ship.getPositions())));
        selectedTemplate.setPlacedDirection(currentDirection);
        selectedTemplate = currentTemplates.stream()
            .filter(t -> t.getStatus() == ShipStatus.PENDING)
            .findFirst()
            .orElse(null);
        notifyViewRefresh();
        String letter = String.valueOf((char)('A' + x));
        return PlacementResult.success(String.format("%s placed at %s%d", ship.getName(), letter, y + 1));
    }

    public PlacementResult resetCurrentPlayerSetup() {
        Player current = getCurrentPlayer();
        if(current == null || current.isAI()) {
            return PlacementResult.error("No human player to reset");
        }
        Player refreshed = new Player(current.getName(), parent.app.gridSize);
        replacePlayerInstance(currentPlayerIndex, refreshed);
        currentTemplates = createShipTemplates();
        selectedTemplate = currentTemplates.isEmpty() ? null : currentTemplates.get(0);
        currentDirection = Direction.HORIZONTAL;
        notifyViewRefresh();
        return PlacementResult.success(String.format("%s's grid has been cleared", refreshed.getName()));
    }

    public boolean areAllShipsPlaced() {
        return !currentTemplates.isEmpty() && currentTemplates.stream().allMatch(t -> t.getStatus() == ShipStatus.PLACED);
    }

    public int getPlacedShipCount() {
        return (int) currentTemplates.stream().filter(t -> t.getStatus() == ShipStatus.PLACED).count();
    }

    public int getTotalShipsToPlace() {
        return currentTemplates.size();
    }

    public Player getCurrentPlayer() {
        if(currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerIndex + 1;
    }

    public int getTotalPlayers() {
        return players.size();
    }

    public int getGridSize() {
        return parent.app.gridSize;
    }

    public PlacementResult finalizeCurrentPlayerPlacement() {
        if(!areAllShipsPlaced()) {
            return PlacementResult.error("Place all ships before continuing");
        }
        return moveToNextPlayablePlayer(currentPlayerIndex + 1);
    }

    private void startShipSetupFlow() {
        initializePlayers();
        PlacementResult result = moveToNextPlayablePlayer(0);
        if(!result.isSuccess() && view != null) {
            Dialogs.showErrorDialog(view.stage, result.getMessage());
        }
    }

    private void initializePlayers() {
        players.clear();
        if(names.isEmpty()) {
            return;
        }

        Player first = new Player(names.get(0), parent.app.gridSize);
        players.add(first);

        if(names.size() > 1) {
            Player second = isAIMode()
                ? new AIPlayer(names.get(1), parent.app.gridSize, aiDifficulty)
                : new Player(names.get(1), parent.app.gridSize);
            players.add(second);
        }

        parent.app.player1 = players.get(0);
        parent.app.player2 = players.size() > 1 ? players.get(1) : null;
    }

    private PlacementResult moveToNextPlayablePlayer(int startIndex) {
        for(int idx = startIndex; idx < players.size(); idx++) {
            Player candidate = players.get(idx);
            if(candidate.isAI() && candidate instanceof AIPlayer aiPlayer) {
                autoPlaceAIShips(aiPlayer);
                continue;
            }

            currentPlayerIndex = idx;
            currentTemplates = createShipTemplates();
            selectedTemplate = currentTemplates.isEmpty() ? null : currentTemplates.get(0);
            currentDirection = Direction.HORIZONTAL;
            showSetupView();
            return PlacementResult.success(String.format("Prepare %s's fleet", candidate.getName()));
        }
        return startGame();
    }

    private void showSetupView() {
        if(view instanceof SetupPlayerShipView shipView) {
            shipView.refreshFromModel();
        }
        else {
            changeView(new SetupPlayerShipView(parent, this));
        }
    }

    private PlacementResult startGame() {
        if(players.size() < 2 || parent.app.player1 == null || parent.app.player2 == null) {
            return PlacementResult.error("Both players must be defined before starting");
        }

        parent.app.game = new Game(parent.app.player1, parent.app.player2);
        try {
            parent.app.game.start();
        }
        catch (IllegalGameStateException e) {
            return PlacementResult.error(e.getMessage());
        }

        parent.changeController(GuiControllerEnum.GAME);
        return PlacementResult.success("Battle started", true);
    }

    private void autoPlaceAIShips(AIPlayer aiPlayer) {
        if(aiPlayer.getShips().isEmpty()) {
            aiPlayer.placeShipsRandomly(List.of(
                new Carrier(),
                new Cruiser(),
                new Destroyer(),
                new Torpedo()
            ));
        }
    }

    private void replacePlayerInstance(int index, Player replacement) {
        players.set(index, replacement);
        if(index == 0) {
            parent.app.player1 = replacement;
        }
        else if(index == 1) {
            parent.app.player2 = replacement;
        }
    }

    private List<ShipTemplate> createShipTemplates() {
        List<ShipTemplate> templates = new ArrayList<>();
        templates.add(new ShipTemplate("carrier", "Carrier", 5, Carrier::new));
        templates.add(new ShipTemplate("cruiser", "Cruiser", 4, Cruiser::new));
        templates.add(new ShipTemplate("destroyer", "Destroyer", 3, Destroyer::new));
        templates.add(new ShipTemplate("torpedo", "Torpedo", 2, Torpedo::new));
        return templates;
    }

    private List<Coordinate> computePlacementCoordinates(Grid grid, int length, Coordinate start, Direction direction) {
        if(grid == null || start == null || direction == null) {
            return Collections.emptyList();
        }
        List<Coordinate> coordinates = new ArrayList<>();
        int dx = direction == Direction.HORIZONTAL ? 1 : 0;
        int dy = direction == Direction.VERTICAL ? 1 : 0;

        for(int i = 0; i < length; i++) {
            int x = start.getX() + (dx * i);
            int y = start.getY() + (dy * i);
            if(x < 0 || y < 0 || x >= grid.getWidth() || y >= grid.getHeight()) {
                return Collections.emptyList();
            }
            coordinates.add(new Coordinate(x, y));
        }

        Cell[][] cells = grid.getCells();
        for(Coordinate coordinate : coordinates) {
            Cell cell = cells[coordinate.getX()][coordinate.getY()];
            if(cell.hasShip()) {
                return Collections.emptyList();
            }

            for(int nx = coordinate.getX() - 1; nx <= coordinate.getX() + 1; nx++) {
                for(int ny = coordinate.getY() - 1; ny <= coordinate.getY() + 1; ny++) {
                    if(nx == coordinate.getX() && ny == coordinate.getY()) {
                        continue;
                    }
                    if(nx < 0 || ny < 0 || nx >= grid.getWidth() || ny >= grid.getHeight()) {
                        continue;
                    }
                    if(cells[nx][ny].hasShip()) {
                        return Collections.emptyList();
                    }
                }
            }
        }

        return coordinates;
    }

    private void notifyViewRefresh() {
        if(view instanceof SetupPlayerShipView shipView) {
            shipView.refreshFromModel();
        }
    }
}
