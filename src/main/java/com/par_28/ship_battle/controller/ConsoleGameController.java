package com.par_28.ship_battle.controller;

import com.par_28.ship_battle.view.ConsoleView;

/**
 * Game controller to manage game flow
 * 
 * @see com.par_28.ship_battle.view.ConsoleView
 */
public class ConsoleGameController {
    /**
     * Console view instance to interact with the user
     */
    ConsoleView view;

    /**
     * Constructor to initialize the game controller
     */
    public ConsoleGameController(){
        this.view = new ConsoleView();
    }

    /**
     * Launch the game loop
     */
    public void launch() {
        boolean isGameRunning = true;
        this.view.displayWelcomeMessage();

        while (isGameRunning) {
            int selectedChoice = this.view.displayMainMenu();

            switch (selectedChoice) {
                case 1:
                    break;
                case 2:
                    this.view.displayFarewellMessage();
                    isGameRunning = false;
                    break;
            }
        }
    }
}
