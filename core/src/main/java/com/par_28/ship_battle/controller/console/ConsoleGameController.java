package com.par_28.ship_battle.controller.console;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.*;
import com.par_28.ship_battle.model.exceptions.*;
import com.par_28.ship_battle.view.console.ConsoleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Game controller to manage game flow
 *
 * @see ConsoleView
 */
public class ConsoleGameController {
    /**
     * Console view instance to interact with the user
     */
    private final ConsoleView view;

    /**
     * Default grid size for players
     */
    private final int gridSize = 10;

    /**
     * Constructor to initialize the game controller
     */
    public ConsoleGameController(){
        this.view = new ConsoleView();
    }

    /**
     * Launch the game loop
     * <p>
     * Player has the choice to start a new game or quit
     * </p>
     */
    public void launch() {
        boolean isGameRunning = true;
        this.view.displayWelcomeMessage();

        while (isGameRunning) {
            int selectedChoice = this.view.displayMainMenu();

            switch (selectedChoice) {
                case 1:
                    play();
                    break;
                case 2:
                    this.view.displayFarewellMessage();
                    isGameRunning = false;
                    break;
            }
        }
    }

    /**
     * Play a full game between two players
     * <p>
     * A game starts by asking players' names, then placing ships on the grid,
     * and finally players take turns attacking each other until one player wins.
     * </p>
     */
    public void play() {
        List<String> names = view.askPlayersName();

        Player player1 = new Player(names.get(0), gridSize);
        Player player2 = new Player(names.get(1), gridSize);
        Game game = new Game(player1, player2);

        view.displaySetupGameMessage();

        setupPlayerShip(player1);
        setupPlayerShip(player2);

        try {
            game.start();
        }
        catch (IllegalGameStateException e) {
            System.out.println("Ouch, bad thing happened : " + e.getMessage());
            return;
        }

        while (!game.isGameOver()) {
            Player currentPlayer = game.getCurrentPlayer();
            view.displayTurnStartMessage(currentPlayer, game.getNbTurns() + 1);
            try {
                Coordinate attackCoord = Coordinate.fromString(view.askCoordinate());

                AttackResponse response = game.playTurn(attackCoord);
                view.displayAttackResponse(currentPlayer, response);
            }
            catch (IllegalGameStateException | InvalidCoordinateException e) {
                System.out.println("Ouch, bad thing happened : " + e.getMessage());
            }
        }

        Player winner = game.getWinner();
        view.displayGameOverMessage(winner);
    }

    /**
     * Get the default list of ships to be placed by players
     *
     * <p>
     * For now, the default ships are:
     * - 1x Carrier
     * - 1x Cruiser
     * - 1x Torpedo
     * - 2x Destroyer
     * </p>
     *
     * @return List of default ships
     */
    private List<Ship> getDefaultShips() {
        List<Ship> ships = new ArrayList<>();

        ships.add(new Torpedo());
        ships.add(new Carrier());
        ships.add(new Cruiser());
        ships.add(new Destroyer());
        ships.add(new Destroyer());

        return ships;
    }

    /**
     * Setup ship placement for a player
     * <p>
     * The player is prompted to place each ship on their grid until all ships are placed.
     * Ships are gotten from the default ship list.
     * </p>
     *
     * @param currentPlayer Player who is placing ships
     */
    private void setupPlayerShip(Player currentPlayer) {
        List<Ship> ships = getDefaultShips();

        while (currentPlayer.getShips().size() < ships.size()) {
            Ship shipToPlace = ships.get(currentPlayer.getShips().size());

            view.displayShipPlacement(currentPlayer, shipToPlace);

            try
            {
                Coordinate coord = Coordinate.fromString(view.askShipCoordinate());
                Direction direction = Direction.values()[view.askShipDirection()];

                currentPlayer.placeShipOnGrid(shipToPlace, coord, direction);
                currentPlayer.addShip(shipToPlace);
                view.displayShipPlacementSuccessMessage(currentPlayer);
            }
            catch (InvalidCoordinateException | ShipPlacementException e) {
                System.out.println("Ouch, bad thing happened : " + e.getMessage());
            }
        }
    }

}
