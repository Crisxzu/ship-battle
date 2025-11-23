package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.*;
import com.par_28.ship_battle.model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Game class
 * Tests the core game logic including turn management, game state, and win conditions
 */
@DisplayName("Game Logic Tests")
class GameTest {

    private Player player1;
    private Player player2;
    private Game game;

    @BeforeEach
    void setUp() {
        player1 = new Player("Player 1", 10);
        player2 = new Player("Player 2", 10);
        game = new Game(player1, player2);
    }

    @Nested
    @DisplayName("Game Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize game in SETUP state")
        void shouldInitializeInSetupState() {
            // When
            GameState state = game.getGameState();

            // Then
            assertEquals(GameState.SETUP, state);
        }

        @Test
        @DisplayName("Should initialize with zero turns")
        void shouldInitializeWithZeroTurns() {
            // When
            int turns = game.getNbTurns();

            // Then
            assertEquals(0, turns);
        }

        @Test
        @DisplayName("Should not have a current player before start")
        void shouldNotHaveCurrentPlayerBeforeStart() {
            // When
            Player currentPlayer = game.getCurrentPlayer();

            // Then
            assertNull(currentPlayer);
        }
    }

    @Nested
    @DisplayName("Game Start Tests")
    class StartTests {

        @Test
        @DisplayName("Should start game when both players have ships")
        void shouldStartGameWhenBothPlayersHaveShips() {
            // Given
            player1.addShip(new Carrier());
            player2.addShip(new Cruiser());

            // When
            game.start();

            // Then
            assertEquals(GameState.PLAYER1_TURN, game.getGameState());
            assertEquals(player1, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should throw exception when starting game without player1 ships")
        void shouldThrowExceptionWhenPlayer1HasNoShips() {
            // Given
            player2.addShip(new Carrier());

            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                game.start();
            });

            assertEquals("Both players must have ships to start the game", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when starting game without player2 ships")
        void shouldThrowExceptionWhenPlayer2HasNoShips() {
            // Given
            player1.addShip(new Carrier());

            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                game.start();
            });

            assertEquals("Both players must have ships to start the game", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when starting game without ships for both players")
        void shouldThrowExceptionWhenPlayersHasNoShips() {
            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                game.start();
            });

            assertEquals("Both players must have ships to start the game", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when starting already started game")
        void shouldThrowExceptionWhenGameAlreadyStarted() {
            // Given
            player1.addShip(new Carrier());
            player2.addShip(new Cruiser());
            game.start();

            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                game.start();
            });

            assertEquals("Game is already started", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Turn Management Tests")
    class TurnManagementTests {

        @BeforeEach
        void startGame() throws Exception {
            Ship ship1 = new Destroyer();
            Ship ship2 = new Cruiser();
            player1.addShip(ship1);
            player2.addShip(ship2);
            player1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            player2.placeShipOnGrid(ship2, new Coordinate(0, 0), Direction.HORIZONTAL);
            game.start();
        }

        @Test
        @DisplayName("Should play turn and attack opponent")
        void shouldPlayTurnAndAttackOpponent() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(0, 0);

            // When
            AttackResponse response = game.playTurn(coord);

            // Then
            assertNotNull(response);
            assertEquals(AttackResult.HIT, response.getResult());
        }

        @Test
        @DisplayName("Should switch player after turn")
        void shouldSwitchPlayerAfterTurn() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(9, 9); // Miss

            // When
            game.playTurn(coord);

            // Then
            assertEquals(GameState.PLAYER2_TURN, game.getGameState());
            assertEquals(player2, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should increment turn counter after each turn")
        void shouldIncrementTurnCounter() throws InvalidCoordinateException {
            // Given
            Coordinate coord = new Coordinate(9, 9);

            // When
            game.playTurn(coord);

            // Then
            assertEquals(1, game.getNbTurns());
        }

        @Test
        @DisplayName("Should end game when opponent dies")
        void shouldEndGameWhenOpponentDies() throws Exception {
            // Given - Create a game with small ships that can be destroyed quickly
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Torpedo();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(0, 0), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // When - Destroy player2's ship
            testGame.playTurn(new Coordinate(0, 0)); // Hit
            testGame.playTurn(new Coordinate(9, 9)); // Miss - switch turn
            testGame.playTurn(new Coordinate(1, 0)); // Hit and sink

            // Then
            assertEquals(GameState.GAME_OVER, testGame.getGameState());
            assertTrue(testGame.isGameOver());
        }

        @Test
        @DisplayName("Should throw exception when playing turn on finished game")
        void shouldThrowExceptionWhenPlayingTurnOnFinishedGame() throws Exception {
            // Given
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Torpedo();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(0, 0), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // Destroy player2's ship
            testGame.playTurn(new Coordinate(0, 0));
            testGame.playTurn(new Coordinate(9, 9));
            testGame.playTurn(new Coordinate(1, 0));

            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                testGame.playTurn(new Coordinate(5, 5));
            });

            assertEquals("Game is already over", exception.getMessage());
        }

        @Test
        @DisplayName("Should get correct opponent")
        void shouldGetCorrectOpponent() {
            // When - Player 1 is current
            Player opponent = game.getOpponent();

            // Then
            assertEquals(player2, opponent);
        }
    }

    @Nested
    @DisplayName("Player Switch Tests")
    class PlayerSwitchTests {

        @BeforeEach
        void startGame() {
            player1.addShip(new Carrier());
            player2.addShip(new Cruiser());
            game.start();
        }

        @Test
        @DisplayName("Should switch from player1 to player2")
        void shouldSwitchFromPlayer1ToPlayer2() {
            // Given - Game started with Player 1
            assertEquals(GameState.PLAYER1_TURN, game.getGameState());

            // When
            game.switchPlayer();

            // Then
            assertEquals(GameState.PLAYER2_TURN, game.getGameState());
            assertEquals(player2, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should switch from player2 to player1")
        void shouldSwitchFromPlayer2ToPlayer1() {
            // Given - Switch to player 2
            game.switchPlayer();

            // When
            game.switchPlayer();

            // Then
            assertEquals(GameState.PLAYER1_TURN, game.getGameState());
            assertEquals(player1, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should alternate players correctly over multiple turns")
        void shouldAlternatePlayersCorrectly() {
            // When & Then
            assertEquals(player1, game.getCurrentPlayer());

            game.switchPlayer();
            assertEquals(player2, game.getCurrentPlayer());

            game.switchPlayer();
            assertEquals(player1, game.getCurrentPlayer());

            game.switchPlayer();
            assertEquals(player2, game.getCurrentPlayer());
        }
    }

    @Nested
    @DisplayName("Game Over and Winner Tests")
    class GameOverTests {

        @BeforeEach
        void startGame() throws Exception {
            Ship ship1 = new Torpedo();
            Ship ship2 = new Torpedo();
            player1.addShip(ship1);
            player2.addShip(ship2);
            player1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            player2.placeShipOnGrid(ship2, new Coordinate(0, 0), Direction.HORIZONTAL);
            game.start();
        }

        @Test
        @DisplayName("Should not be game over when game is ongoing")
        void shouldNotBeGameOverWhenGameOngoing() {
            // When
            boolean isGameOver = game.isGameOver();

            // Then
            assertFalse(isGameOver);
        }

        @Test
        @DisplayName("Should return null winner when game is not over")
        void shouldReturnNullWinnerWhenGameNotOver() {
            // When
            Player winner = game.getWinner();

            // Then
            assertNull(winner);
        }

        @Test
        @DisplayName("Should return player1 as winner when player2 is dead")
        void shouldReturnPlayer1AsWinnerWhenPlayer2IsDead() throws InvalidCoordinateException {
            // When - Destroy player2's ship
            game.playTurn(new Coordinate(0, 0)); // Hit
            game.playTurn(new Coordinate(9, 9)); // Miss - switch turn
            game.playTurn(new Coordinate(1, 0)); // Hit and sink

            // Then
            Player winner = game.getWinner();
            assertEquals(player1, winner);
            assertTrue(player2.isDead());
            assertFalse(player1.isDead());
        }

        @Test
        @DisplayName("Should return player2 as winner when player1 is dead")
        void shouldReturnPlayer2AsWinnerWhenPlayer1IsDead() throws InvalidCoordinateException {
            // Given - Switch to player 2
            game.switchPlayer();

            // When - Destroy player1's ship
            game.playTurn(new Coordinate(0, 0)); // Hit
            game.playTurn(new Coordinate(9, 9)); // Miss - switch turn
            game.playTurn(new Coordinate(1, 0)); // Hit and sink

            // Then
            Player winner = game.getWinner();
            assertEquals(player2, winner);
            assertTrue(player1.isDead());
            assertFalse(player2.isDead());
        }

        @Test
        @DisplayName("Should be game over when state is GAME_OVER")
        void shouldBeGameOverWhenStateIsGameOver() throws InvalidCoordinateException {
            // When - Destroy player2's ship
            game.playTurn(new Coordinate(0, 0));
            game.playTurn(new Coordinate(9, 9));
            game.playTurn(new Coordinate(1, 0));

            // Then
            boolean isGameOver = game.isGameOver();
            assertTrue(isGameOver);
            assertEquals(GameState.GAME_OVER, game.getGameState());
        }
    }

    @Nested
    @DisplayName("Power Attack Tests")
    class PowerAttackTests {

        @Test
        @DisplayName("Should use bomb attack and hit multiple cells")
        void shouldUseBombAttackAndHitMultipleCells() throws Exception {
            // Given
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // When
            AttackResponse response = testGame.playTurn(new Coordinate(5, 5), PowerType.BOMB);

            // Then
            assertTrue(response.isBombAttack());
            assertFalse(response.getAdditionalHits().isEmpty());
            assertEquals(1, p1.getBombCharges()); // One charge used
        }

        @Test
        @DisplayName("Should throw exception when no bomb charges available")
        void shouldThrowExceptionWhenNoBombCharges() throws Exception {
            // Given
            Player p1 = new Player("P1", 10, 0, 3); // No bomb charges
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                testGame.playTurn(new Coordinate(5, 5), PowerType.BOMB);
            });
            assertEquals("No bomb charges available", exception.getMessage());
        }

        @Test
        @DisplayName("Should use radar scan and detect ship")
        void shouldUseRadarScanAndDetectShip() throws Exception {
            // Given
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // When
            AttackResponse response = testGame.playTurn(new Coordinate(5, 5), PowerType.RADAR);

            // Then
            assertEquals(AttackResult.RADAR_USED, response.getResult());
            assertTrue(response.isRadarDetection());
            assertEquals(2, p1.getRadarCharges()); // One charge used
            // Radar doesn't switch turn
            assertEquals(p1, testGame.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should use radar scan and not detect ship")
        void shouldUseRadarScanAndNotDetectShip() throws Exception {
            // Given
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // When - Scan area with no ship
            AttackResponse response = testGame.playTurn(new Coordinate(0, 0), PowerType.RADAR);

            // Then
            assertEquals(AttackResult.RADAR_USED, response.getResult());
            assertFalse(response.isRadarDetection());
        }

        @Test
        @DisplayName("Should throw exception when no radar charges available")
        void shouldThrowExceptionWhenNoRadarCharges() throws Exception {
            // Given
            Player p1 = new Player("P1", 10, 2, 0); // No radar charges
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            // When & Then
            IllegalGameStateException exception = assertThrows(IllegalGameStateException.class, () -> {
                testGame.playTurn(new Coordinate(5, 5), PowerType.RADAR);
            });
            assertEquals("No radar charges available", exception.getMessage());
        }

        @Test
        @DisplayName("Radar scan should not increment turn counter")
        void radarScanShouldNotIncrementTurnCounter() throws Exception {
            // Given
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Carrier();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);
            testGame.start();

            int turnsBefore = testGame.getNbTurns();

            // When
            testGame.playTurn(new Coordinate(5, 5), PowerType.RADAR);

            // Then
            assertEquals(turnsBefore, testGame.getNbTurns());
        }
    }

    @Nested
    @DisplayName("Full Game Flow Tests")
    class FullGameFlowTests {

        @Test
        @DisplayName("Should complete a full game sequence")
        void shouldCompleteFullGameSequence() throws Exception {
            // Given
            Player p1 = new Player("P1", 10);
            Player p2 = new Player("P2", 10);
            Ship ship1 = new Torpedo();
            Ship ship2 = new Torpedo();
            p1.addShip(ship1);
            p2.addShip(ship2);
            p1.placeShipOnGrid(ship1, new Coordinate(0, 0), Direction.HORIZONTAL);
            p2.placeShipOnGrid(ship2, new Coordinate(5, 5), Direction.HORIZONTAL);

            Game testGame = new Game(p1, p2);

            // When - Start game
            testGame.start();
            assertEquals(GameState.PLAYER1_TURN, testGame.getGameState());

            // Player 1 attacks and misses
            testGame.playTurn(new Coordinate(9, 9));
            assertEquals(1, testGame.getNbTurns());
            assertEquals(GameState.PLAYER2_TURN, testGame.getGameState());

            // Player 2 attacks and misses
            testGame.playTurn(new Coordinate(9, 9));
            assertEquals(2, testGame.getNbTurns());
            assertEquals(GameState.PLAYER1_TURN, testGame.getGameState());

            // Player 1 attacks and hits
            AttackResponse hitResponse = testGame.playTurn(new Coordinate(5, 5));
            assertEquals(AttackResult.HIT, hitResponse.getResult());
            assertEquals(3, testGame.getNbTurns());
            assertEquals(GameState.PLAYER2_TURN, testGame.getGameState());

            // Player 2 attacks and misses
            testGame.playTurn(new Coordinate(8, 8));
            assertEquals(4, testGame.getNbTurns());

            // Player 1 attacks and sinks ship (wins)
            AttackResponse sunkResponse = testGame.playTurn(new Coordinate(6, 5));
            assertEquals(AttackResult.SUNK, sunkResponse.getResult());

            // Then
            assertEquals(5, testGame.getNbTurns());
            assertEquals(GameState.GAME_OVER, testGame.getGameState());
            assertTrue(testGame.isGameOver());
            assertEquals(p1, testGame.getWinner());
        }
    }
}