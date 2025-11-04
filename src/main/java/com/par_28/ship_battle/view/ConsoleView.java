package com.par_28.ship_battle.view;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Console view to interact with the user
 * 
 * @see java.util.Scanner
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
                scanner.nextLine(); // Clear buffer if not we always take last input without asking
            }
        }

        return selectedChoice;
    }
}
