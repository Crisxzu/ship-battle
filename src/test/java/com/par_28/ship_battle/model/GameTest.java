package com.par_28.ship_battle.model;

import com.par_28.ship_battle.model.enums.GameState;
import com.par_28.ship_battle.model.exceptions.InvalidCoordinateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Game class
 * Tests the core game logic including turn management, game state, and win conditions
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Game Logic Tests")
class GameTest {

    @Mock
    private Player mockPlayer1;

    @Mock
    private Player mockPlayer2;

    @Mock
    private Ship mockShip1;

    @Mock
    private Ship mockShip2;

    @Mock
    private AttackResponse mockAttackResponse;

    @Mock
    private Coordinate mockCoordinate;

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(mockPlayer1, mockPlayer2);
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
            List<Ship> ships1 = new ArrayList<>();
            ships1.add(mockShip1);
            List<Ship> ships2 = new ArrayList<>();
            ships2.add(mockShip2);

            when(mockPlayer1.getShips()).thenReturn(ships1);
            when(mockPlayer2.getShips()).thenReturn(ships2);

            // When
            game.start();

            // Then
            assertEquals(GameState.PLAYER1_TURN, game.getGameState());
            assertEquals(mockPlayer1, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should throw exception when starting game without player1 ships")
        void shouldThrowExceptionWhenPlayer1HasNoShips() {
            // Given
            when(mockPlayer1.getShips()).thenReturn(new ArrayList<>());
            when(mockPlayer2.getShips()).thenReturn(List.of(mockShip1));

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                game.start();
            });

            assertEquals("Both players must have ships to start the game", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when starting game without player2 ships")
        void shouldThrowExceptionWhenPlayer2HasNoShips() {
            // Given
            when(mockPlayer1.getShips()).thenReturn(List.of(mockShip1));
            when(mockPlayer2.getShips()).thenReturn(new ArrayList<>());

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                game.start();
            });

            assertEquals("Both players must have ships to start the game", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when starting already started game")
        void shouldThrowExceptionWhenGameAlreadyStarted() {
            // Given
            when(mockPlayer1.getShips()).thenReturn(List.of(mockShip1));
            when(mockPlayer2.getShips()).thenReturn(List.of(mockShip2));
            game.start();

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                game.start();
            });

            assertEquals("Game is already started", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Turn Management Tests")
    class TurnManagementTests {

        @BeforeEach
        void startGame() {
            when(mockPlayer1.getShips()).thenReturn(List.of(mockShip1));
            when(mockPlayer2.getShips()).thenReturn(List.of(mockShip2));
            game.start();
        }

        @Test
        @DisplayName("Should play turn and attack opponent")
        void shouldPlayTurnAndAttackOpponent() throws InvalidCoordinateException {
            // Given
            when(mockPlayer2.receiveAttack(mockCoordinate)).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(false);

            // When
            AttackResponse response = game.playTurn(mockCoordinate);

            // Then
            assertEquals(mockAttackResponse, response);
            verify(mockPlayer2).receiveAttack(mockCoordinate);
            verify(mockPlayer1).recordAttack(mockCoordinate, mockAttackResponse);
        }

        @Test
        @DisplayName("Should switch player after turn")
        void shouldSwitchPlayerAfterTurn() throws InvalidCoordinateException {
            // Given
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(false);

            // When
            game.playTurn(mockCoordinate);

            // Then
            assertEquals(GameState.PLAYER2_TURN, game.getGameState());
            assertEquals(mockPlayer2, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should increment turn counter after each turn")
        void shouldIncrementTurnCounter() throws InvalidCoordinateException {
            // Given
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(false);

            // When
            game.playTurn(mockCoordinate);

            // Then
            assertEquals(1, game.getNbTurns());
        }

        @Test
        @DisplayName("Should end game when opponent dies")
        void shouldEndGameWhenOpponentDies() throws InvalidCoordinateException {
            // Given
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(true);

            // When
            game.playTurn(mockCoordinate);

            // Then
            assertEquals(GameState.GAME_OVER, game.getGameState());
            assertTrue(game.isGameOver());
        }

        @Test
        @DisplayName("Should throw exception when playing turn on finished game")
        void shouldThrowExceptionWhenPlayingTurnOnFinishedGame() throws InvalidCoordinateException {
            // Given
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(true);
            game.playTurn(mockCoordinate);

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                game.playTurn(mockCoordinate);
            });

            assertEquals("Game is already over", exception.getMessage());
        }

        @Test
        @DisplayName("Should get correct opponent")
        void shouldGetCorrectOpponent() {
            // When - Player 1 is current
            Player opponent = game.getOpponent();

            // Then
            assertEquals(mockPlayer2, opponent);
        }
    }

    @Nested
    @DisplayName("Player Switch Tests")
    class PlayerSwitchTests {

        @BeforeEach
        void startGame() {
            when(mockPlayer1.getShips()).thenReturn(List.of(mockShip1));
            when(mockPlayer2.getShips()).thenReturn(List.of(mockShip2));
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
            assertEquals(mockPlayer2, game.getCurrentPlayer());
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
            assertEquals(mockPlayer1, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("Should alternate players correctly over multiple turns")
        void shouldAlternatePlayersCorrectly() {
            // When & Then
            assertEquals(mockPlayer1, game.getCurrentPlayer());

            game.switchPlayer();
            assertEquals(mockPlayer2, game.getCurrentPlayer());

            game.switchPlayer();
            assertEquals(mockPlayer1, game.getCurrentPlayer());

            game.switchPlayer();
            assertEquals(mockPlayer2, game.getCurrentPlayer());
        }
    }

    @Nested
    @DisplayName("Game Over and Winner Tests")
    class GameOverTests {

        @BeforeEach
        void startGame() {
            when(mockPlayer1.getShips()).thenReturn(List.of(mockShip1));
            when(mockPlayer2.getShips()).thenReturn(List.of(mockShip2));
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
            // Given
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(true);
            when(mockPlayer1.isDead()).thenReturn(false);
            game.playTurn(mockCoordinate);

            // When
            Player winner = game.getWinner();

            // Then
            assertEquals(mockPlayer2, winner);
        }

        @Test
        @DisplayName("Should return player2 as winner when player1 is dead")
        void shouldReturnPlayer2AsWinnerWhenPlayer1IsDead() throws InvalidCoordinateException {
            // Given
            game.switchPlayer(); // Switch to player 2
            when(mockPlayer1.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer1.isDead()).thenReturn(true);
            when(mockPlayer2.isDead()).thenReturn(false);
            game.playTurn(mockCoordinate);

            // When
            Player winner = game.getWinner();

            // Then
            assertEquals(mockPlayer1, winner);
        }

        @Test
        @DisplayName("Should be game over when state is GAME_OVER")
        void shouldBeGameOverWhenStateIsGameOver() throws InvalidCoordinateException {
            // Given
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer2.isDead()).thenReturn(true);
            game.playTurn(mockCoordinate);

            // When
            boolean isGameOver = game.isGameOver();

            // Then
            assertTrue(isGameOver);
            assertEquals(GameState.GAME_OVER, game.getGameState());
        }
    }

    @Nested
    @DisplayName("Full Game Flow Tests")
    class FullGameFlowTests {

        @Test
        @DisplayName("Should complete a full game sequence")
        void shouldCompleteFullGameSequence() throws InvalidCoordinateException {
            // Given
            when(mockPlayer1.getShips()).thenReturn(List.of(mockShip1));
            when(mockPlayer2.getShips()).thenReturn(List.of(mockShip2));
            when(mockPlayer2.receiveAttack(any())).thenReturn(mockAttackResponse);
            when(mockPlayer1.receiveAttack(any())).thenReturn(mockAttackResponse);

            // When - Start game
            game.start();
            assertEquals(GameState.PLAYER1_TURN, game.getGameState());

            // Player 1 attacks
            when(mockPlayer2.isDead()).thenReturn(false);
            game.playTurn(mockCoordinate);
            assertEquals(1, game.getNbTurns());
            assertEquals(GameState.PLAYER2_TURN, game.getGameState());

            // Player 2 attacks
            when(mockPlayer1.isDead()).thenReturn(false);
            game.playTurn(mockCoordinate);
            assertEquals(2, game.getNbTurns());
            assertEquals(GameState.PLAYER1_TURN, game.getGameState());

            // Player 1 attacks and wins
            when(mockPlayer2.isDead()).thenReturn(true);
            game.playTurn(mockCoordinate);

            // Then
            assertEquals(3, game.getNbTurns());
            assertEquals(GameState.GAME_OVER, game.getGameState());
            assertTrue(game.isGameOver());
            assertNotNull(game.getWinner());
        }
    }
}