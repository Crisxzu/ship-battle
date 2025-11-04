package com.par_28.ship_battle;

import com.par_28.ship_battle.controller.ConsoleGameController;

/**
 * Main entry point of our game ShipBattle
 */
public class Main {
    /**
     * Entry point of the game.
     * <p>
     * Create player, initialize game and launch it.
     *
     * @param args inline arguments (not used though)
     * @see com.par_28.ship_battle.controller.ConsoleGameController
     */
    public static void main(String[] args) {
        ConsoleGameController consoleGameController = new ConsoleGameController();

        consoleGameController.launch();
    }
}