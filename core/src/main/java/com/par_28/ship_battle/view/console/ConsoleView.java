package com.par_28.ship_battle.view.console;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.model.enums.*;

import java.util.*;

/**
 * Console view to interact with the user
 *
 * @see Scanner
 * @see Player
 * @see Ship
 * @see Cell
 * @see AttackResponse
 * @see AttackResult
 */
public class ConsoleView {
    /**
     * Java Scanner to read user input
     */
    Scanner scanner = new Scanner(System.in);

    /**
     * Show game welcome message
     */
    public void displayWelcomeMessage() {
        System.out.println("Welcome to Ship Battle!");
    }

    /**
     * Show game farewell message
     */
    public void displayFarewellMessage() {
        System.out.println("See you again!");
    }

    /**
     * Display main menu and get user choice
     * <p>
     * On invalid choice, the user is prompted again until a valid choice is made.
     * For now, only two choices are supported: 1 (Launch Battle) and 2 (Quit)
     * </p>
     *
     * @return int User's selected choice
     */
    public int displayMainMenu() {
        boolean isChoiceMade = false;
        Integer[] supportedChoices = {1, 2};
        int selectedChoice = 0;

        System.out.println("Main Menu");
        System.out.println("1 - Launch Battle");
        System.out.println("2 - Quit");

        while(!isChoiceMade) {
            try {
                System.out.print("Enter your choice: ");
                selectedChoice = scanner.nextInt();

                if(!Arrays.asList(supportedChoices).contains(selectedChoice)) {
                    throw new InputMismatchException();
                }

                isChoiceMade = true;
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice and a number");
            }
            finally {
                scanner.nextLine(); // Clear buffer if not we always take last input without asking
            }
        }

        return selectedChoice;
    }

    /**
     * Display turn start message
     * <p>
     * Shows current turn number, current player name, and both player's grids.
     * First grid shows the current player's own grid with ship positions,
     * second grid shows the tracking grid of the opponent with hit/miss info.
     * </p>
     *
     * @param currentPlayer Player whose turn it is
     * @param nbTurns Number of turns played
     */
    public void displayTurnStartMessage(Player currentPlayer, int nbTurns) {
        System.out.printf("Turn %d\n", nbTurns);
        System.out.printf("Turn of %s\n", currentPlayer.getName());

        System.out.println("--------------");

        System.out.println("Here is your grids");
        System.out.println("~ : Not touched");
        System.out.println("X : Hit");
        System.out.println("O : Missed");

        System.out.println("=> Your grid");
        displayPlayerGrid(currentPlayer.getGrid().getCells(), true);

        System.out.println("=> Grid of your opponent");
        displayPlayerGrid(currentPlayer.getTrackingGrid().getCells(), false);

        System.out.println("--------------");

        System.out.println("It's your time to play ! Let's attack !");
    }

    /**
     * Display setup game message
     * <p>
     * Informs players about the ship placement phase before the game starts.
     * Explains the types and quantities of ships each player must place on their grid.
     * </p>
     */
    public void displaySetupGameMessage() {
        System.out.println("--------------");
        System.out.println("Yeah it's time to play !");
        System.out.println("But before that, every player have to place his ships on his board.");
        System.out.println("Each player will have :");
        System.out.println("- 1x Carrier (5 cells)");
        System.out.println("- 1x Cruiser (4 cells)");
        System.out.println("- 2x Destroyer (3 cells)");
        System.out.println("- 1x Torpedo (2 cells");
        System.out.println("--------------");
    }

    /**
     * Display ship placement prompt for a player
     * <p>
     * Notify the current player to place a specific ship on their grid.
     * The ship's name and length are displayed to guide the player.
     * </p>
     * @param currentPlayer Player who is placing the ship
     * @param ship Ship to be placed
     */
    public void displayShipPlacement(Player currentPlayer, Ship ship) {
        System.out.println("--------------");
        System.out.printf(
            "%s ! You have to place a %s and it takes %d cells.\n",
            currentPlayer.getName(),
            ship.getName(),
            ship.getLength()
        );
    }

    /**
     * Ask for ship direction input from the user
     * <p>
     * Prompts the user to choose a direction for placing the ship.
     * Supported directions are Horizontal (1) and Vertical (2).
     * On invalid input, the user is prompted again until a valid choice is made.
     * </p>
     *
     * @return int Direction index corresponding to the chosen direction
     */
    public int askShipDirection() {
        int direction = 0;
        boolean isChoiceMade = false;
        Integer[] supportedChoices = {1, 2};

        while(!isChoiceMade) {
            try {
                System.out.println("Choose a direction for the ship (1: Horizontal, 2: Vertical).");
                System.out.print("Direction: ");
                direction = scanner.nextInt();

                if(!Arrays.asList(supportedChoices).contains(direction)) {
                    throw new InputMismatchException();
                }

                isChoiceMade = true;
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice and a number");
            }
            finally {
                scanner.nextLine(); // Clear buffer if not we always take last input without asking
            }
        }

        return direction - 1; // to match enum index
    }

    /**
     * Ask for ship starting coordinate input from the user
     * <p>
     * Prompts the user to enter the starting coordinate for placing the ship.
     * The coordinate can be in formats like "A5" or "1,5".
     * </p>
     *
     * @return String Entered coordinate string
     */
    public String askShipCoordinate() {
        System.out.println("Please enter starting coordinate for the ship. (E.g., A5 or 1,5 1 for x and 5 for y)");
        return askCoordinate();
    }

    /**
     * Display player's grid
     * <p>
     * Displays the grid cells with ship positions and hit/miss status.
     * If showShips is true, ship positions are revealed; otherwise, only hit/miss info is shown.
     * </p>
     *
     * @param cells 2D array of Cell objects representing the grid
     * @param showShips boolean whether to show ship positions or not
     */
    private void displayPlayerGrid(Cell[][] cells, boolean showShips) {
        int width = cells.length;
        int height = (width > 0) ? cells[0].length : 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = cells[x][y];
                String cellStr;

                if (cell.isShot()) {
                    cellStr = cell.hasShip() ? "X" : "O";
                } else {
                    if (cell.hasShip() && showShips) {
                    cellStr = "◼︎";
                    } else {
                    cellStr = "~";
                    }
                }

                System.out.print(cellStr);
            }
            System.out.println();
        }
    }

    /**
     * Ask for attack coordinate input from the user
     * <p>
     * Prompts the user to enter the coordinate for their attack.
     * The coordinate can be in formats like "A5" or "1,5".
     * </p>
     *
     * @return String Entered coordinate string
     */
    public String askCoordinate() {
        System.out.print("Please enter coordinate: ");
        return scanner.nextLine();
    }

    /**
     * Ask for players' names
     * <p>
     * Prompts each player to enter their name.
     * </p>
     *
     * @return List of entered player names
     */
    public List<String> askPlayersName() {
        List<String> names = new ArrayList<>();

        System.out.println("Player 1");
        String name = askName();
        names.add(name);

        System.out.println("Player 2");
        names.add(askName());

        return names;
    }

    /**
     * Ask for a single player's name
     * @return String Entered player name
     */
    private String askName() {
        System.out.print("Please enter your name: ");
        return scanner.nextLine();
    }

    /**
     * Display ship placement success message
     * <p>
     * Notifies the player that their ship has been successfully placed on the grid.
     * Displays the updated grid with the newly placed ship.
     * </p>
     *
     * @param currentPlayer Player who placed the ship
     */
    public void displayShipPlacementSuccessMessage(Player currentPlayer) {
        System.out.println("Ship placed successfully !");
        System.out.println("--------------");
        displayPlayerGrid(currentPlayer.getGrid().getCells(), true);

    }

    /**
     * Display attack response message
     * <p>
     * Shows the result of the current player's attack on the opponent.
     * Displays whether the attack was a hit, miss, sunk a ship, or was an already hit cell.
     * Also shows the updated tracking grid of the opponent after the attack.
     * </p>
     *
     * @param currentPlayer Player who made the attack
     * @param response Response of the attack with hit/miss and ship info
     */
    public void displayAttackResponse(Player currentPlayer, AttackResponse response) {
        System.out.println("--------------");

        AttackResult attackResult = response.getResult();
        String attackResultStr = getAttackResultStr(attackResult);

        System.out.println(attackResultStr);

        System.out.println("=> Grid of your opponent");
        displayPlayerGrid(currentPlayer.getTrackingGrid().getCells(), false);

        System.out.println("--------------");

        System.out.println("It's the end of your turn !");
    }

    /**
     * Get attack result string for display
     * <p>
     * Converts the AttackResult enum to a user-friendly string message.
     * </p>
     *
     * @param attackResult AttackResult enum value
     * @return String User-friendly attack result message
     */
    private String getAttackResultStr(AttackResult attackResult) {
        String attackResultStr;

        if(attackResult == AttackResult.MISS) {
            attackResultStr = "YOU MISSED ! TOO BAD >< !";
        }
        else if (attackResult == AttackResult.HIT) {
            attackResultStr = "YOU HIT YOUR OPPONENT BOAT !";
        }
        else if (attackResult == AttackResult.SUNK) {
            attackResultStr = "YOU SUNK YOUR OPPONENT BOAT !";
        }
        else {
            attackResultStr = "YOU HAVE ALREADY HIT THIS CELL! YOU WASTE YOUR SHOOT, ARE YOU DUMB ? Oo";
        }
        return attackResultStr;
    }

    /**
     * Display game over message
     * <p>
     * Announces the end of the game and congratulates the winning player.
     * </p>
     *
     * @param winner Player who won the game
     */
    public void displayGameOverMessage(Player winner) {
        System.out.println("--------------");
        System.out.printf("GAME OVER ! The winner is %s ! Congratulations !\n", winner.getName());
        System.out.println("--------------");
    }
}
