# ShipBattle - Diagramme de Classes (Contrôleurs GUI)

```mermaid
classDiagram
    direction TB

    %% ==================== GUI CONTROLLER (ABSTRACT) ====================
    class GuiController {
        <<abstract>>
        +GuiView view
        +ScreenController parent
        +GuiController(ScreenController parent)
        +update(float dt)* void
        +render(float dt) void
        +dispose() void
        +resize(int width, int height) void
        +reset() void
        #changeView(GuiView view) void
    }

    %% ==================== SCREEN CONTROLLER ====================
    class ScreenController {
        +ShipBattleApplication app
        -Map~GuiControllerEnum, GuiController~ controllers
        -GuiController currentController
        +ScreenController(ShipBattleApplication app)
        +getCurrentController() GuiController
        +update(float dt) void
        +render(float dt) void
        +changeController(GuiControllerEnum controller) void
        +resize(int width, int height) void
        +dispose() void
    }

    %% ==================== MAIN MENU CONTROLLER ====================
    class MainMenuController {
        +MainMenuController(ScreenController parent)
        +update(float dt) void
        +render(float dt) void
        +reset() void
    }

    %% ==================== SETUP MENU CONTROLLER ====================
    class SetupMenuController {
        -List~String~ names
        -AIDifficulty aiDifficulty
        -List~Ship~ shipsToPlace
        -Ship selectedShip
        -int currentSetupPlayerIndex
        -boolean[] placementDone
        +SetupMenuController(ScreenController parent)
        +update(float dt) void
        +render(float dt) void
        +reset() void
        +addName(String name) boolean
        +getNamesNumber() int
        +isAIMode() boolean
        +setAIDifficulty(AIDifficulty difficulty) void
        +goToDifficultyMenu() void
        +gotoSetupPlayerNameMenu() void
        +gotoGameModeMenu() void
        +getCurrentSetupPlayer() Player
        +canPlaceSelectedShip(Coordinate, Direction) boolean
        +placeSelectedShip(Coordinate, Direction) boolean
        +getShipsToPlace() List~Ship~
        +getShipsToPlaceCount() int
        +selectShip(Ship ship) boolean
        +getSelectedShip() Ship
        +cancelSelection() void
        +resetCurrentPlacement() void
        +placeShipsRandomly() boolean
        +hasPlacedShips() boolean
        +handlePlacementComplete() void
    }

    %% ==================== GAME CONTROLLER ====================
    class GameController {
        -PowerType currentPowerType
        +GameController(ScreenController parent)
        +update(float dt) void
        +render(float dt) void
        +reset() void
        +startTurn() void
        +changeTurn() void
        +playTurn(Coordinate attackCoord) AttackResponse
        +getAIShot() Coordinate
        +setCurrentPowerType(PowerType type) void
        +getCurrentPowerType() PowerType
        +getCurrentPlayerBombCharges() int
        +getCurrentPlayerRadarCharges() int
        +canUsePower(PowerType type) boolean
        +applyKonamiCode() void
        +resetGame() void
        +resetGameWithoutSamePlacement() void
    }

    %% ==================== SETTINGS CONTROLLER ====================
    class SettingsController {
        +SettingsController(ScreenController parent)
        +update(float dt) void
        +render(float dt) void
        +reset() void
        +changeSoundVolume(float volume) void
        +changeMusicVolume(float volume) void
        +saveSettings() void
    }

    %% ==================== ENUM ====================
    class GuiControllerEnum {
        <<enumeration>>
        MAIN_MENU
        SETUP_MENU
        GAME
        SETTINGS
    }

    %% ==================== EXTERNAL CLASSES (simplified) ====================
    class GuiView {
        <<abstract>>
        +Stage stage
        +show() void
        +hide() void
        +render(float dt) void
        +resize(int width, int height) void
        +dispose() void
    }

    class ShipBattleApplication {
        +Player player1
        +Player player2
        +Game game
        +SettingsHandler settingsHandler
        +int gridSize
        +int nbPlayers
    }

    %% ==================== RELATIONS ====================

    %% Inheritance
    ScreenController --|> GuiController
    MainMenuController --|> GuiController
    SetupMenuController --|> GuiController
    GameController --|> GuiController
    SettingsController --|> GuiController

    %% ScreenController manages all controllers
    ScreenController *-- "1" MainMenuController : controllers
    ScreenController *-- "1" SetupMenuController : controllers
    ScreenController *-- "1" GameController : controllers
    ScreenController *-- "1" SettingsController : controllers

    %% ScreenController uses enum for navigation
    ScreenController ..> GuiControllerEnum : uses

    %% GuiController has reference to parent and view
    GuiController o-- "1" ScreenController : parent
    GuiController o-- "1" GuiView : view

    %% ScreenController references application
    ScreenController o-- "1" ShipBattleApplication : app

    %% Controllers use model classes (dependencies)
    SetupMenuController ..> Player : creates/configures
    SetupMenuController ..> AIPlayer : creates
    SetupMenuController ..> Ship : manages
    SetupMenuController ..> Game : creates
    SetupMenuController ..> AIDifficulty : uses

    GameController ..> Game : controls
    GameController ..> Player : interacts
    GameController ..> AIPlayer : interacts
    GameController ..> AttackResponse : returns
    GameController ..> PowerType : uses
    GameController ..> Coordinate : uses
```

## Architecture des Contrôleurs

### Pattern utilisé : **State Pattern** via ScreenController

Le `ScreenController` agit comme un gestionnaire d'états qui :
1. Maintient une map de tous les contrôleurs disponibles
2. Permet la navigation entre les écrans via `changeController()`
3. Délègue le rendu et les événements au contrôleur actif

### Flux de navigation

```
┌─────────────────┐
│  MainMenuController  │
│  (MainMenuView)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐     ┌─────────────────┐
│ SetupMenuController │────▶│ SettingsController │
│ - GameModeView      │     │ (SettingsView)     │
│ - DifficultyView    │     └─────────────────┘
│ - SetupPlayerNameView│
│ - SetupPlayerShipView│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  GameController      │
│ - GameTurnDisplayView│
│ - GameView           │
│ - AIGameView         │
│ - GameOverView       │
└─────────────────┘
```

### Responsabilités

| Contrôleur | Responsabilité |
|------------|----------------|
| `ScreenController` | Gestion des écrans, navigation, cycle de vie |
| `MainMenuController` | Affichage menu principal, musique |
| `SetupMenuController` | Configuration partie, noms, difficulté IA, placement navires |
| `GameController` | Logique de jeu, tours, attaques, pouvoirs, fin de partie |
| `SettingsController` | Volume son/musique, sauvegarde préférences |

### Légende

| Symbole | Visibilité |
|---------|------------|
| `+` | public |
| `-` | private |
| `#` | protected |

| Relation | Signification |
|----------|---------------|
| `--\|>` | Héritage (extends) |
| `*--` | Composition |
| `o--` | Agrégation |
| `..>` | Dépendance |

## Couleurs 

| Couleur       | Classe(s)                                         | Justification            |
  |---------------|---------------------------------------------------|--------------------------|
| 🔵 Bleu foncé | GuiController                                     | Classe abstraite de base |
| 🔷 Bleu clair | ScreenController                                  | Gestionnaire central     |
| 🟢 Vert       | MainMenuController                                | Point d'entrée           |
| 🟡 Jaune      | SetupMenuController                               | Configuration/Setup      |
| 🔴 Rouge      | GameController                                    | Logique de jeu (action)  |
| 🟣 Violet     | SettingsController                                | Paramètres               |
| ⚪ Gris        | GuiControllerEnum, GuiView, ShipBattleApplication | Classes externes/enum    |
