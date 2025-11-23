# ShipBattle - Diagramme de Classes (Modèles)

```mermaid
classDiagram
    direction TB

    %% ==================== GAME ====================
    class Game {
        -Player player1
        -Player player2
        -Player currentPlayer
        -GameState gameState
        -int nbTurns
        +start() void
        +playTurn(Coordinate coord) AttackResponse
        +playTurn(Coordinate coord, PowerType powerType) AttackResponse
        +switchPlayer() void
        +isGameOver() boolean
        +getWinner() Player
        +getCurrentPlayer() Player
        +getOpponent() Player
    }

    %% ==================== PLAYER ====================
    class Player {
        -String name
        -Grid grid
        -Grid trackingGrid
        -List~Ship~ ships
        -int bombCharges
        -int radarCharges
        +isDead() boolean
        +addShip(Ship ship) void
        +placeShipOnGrid(Ship, Coordinate, Direction) void
        +receiveAttack(Coordinate coord) AttackResponse
        +recordAttack(Coordinate, AttackResponse) void
        +resetFleet() void
        +isAI() boolean
        +useBombCharge() boolean
        +useRadarCharge() boolean
        +placeShipsRandomly(List~Ship~) boolean
    }

    %% ==================== AIPLAYER ====================
    class AIPlayer {
        -AIStrategy strategy
        -AIDifficulty difficulty
        +chooseShot(List~Ship~) Coordinate
        +notifyAttackResult(Coordinate, AttackResponse) void
        +resetStrategy() void
        +isAI() boolean
        +getRandomName()$ String
    }

    %% ==================== GRID ====================
    class Grid {
        -int width
        -int height
        -Cell[][] cells
        +isValidCoordinate(Coordinate) boolean
        +getCell(Coordinate) Cell
        +placeShip(Ship, Coordinate, Direction) void
        +previewPlacement(Ship, Coordinate, Direction) List~Coordinate~
        +receiveAttack(Coordinate) AttackResponse
        +receiveBombAttack(Coordinate) AttackResponse
        +radarScan(Coordinate) AttackResponse
        +clearShips() void
    }

    %% ==================== CELL ====================
    class Cell {
        -Coordinate coordinate
        -Ship ship
        -boolean shot
        +shoot() void
        +isShot() boolean
        +hasShip() boolean
        +reset() void
    }

    %% ==================== COORDINATE ====================
    class Coordinate {
        -int x
        -int y
        +fromString(String)$ Coordinate
        +toLetterFormat() String
        +equals(Object) boolean
        +hashCode() int
    }

    %% ==================== SHIP (ABSTRACT) ====================
    class Ship {
        <<abstract>>
        #Integer length
        #String name
        #Integer life
        #Direction direction
        #List~Coordinate~ positions
        +receiveDamage() void
        +isDestroyed() boolean
        +refillLife() void
        +occupiesPositions(Coordinate) boolean
    }

    %% ==================== SHIP SUBCLASSES ====================
    class Carrier {
        +Carrier()
        name = "Carrier"
        length = 5
    }

    class Cruiser {
        +Cruiser()
        name = "Cruiser"
        length = 4
    }

    class Destroyer {
        +Destroyer()
        name = "Destroyer"
        length = 3
    }

    class Torpedo {
        +Torpedo()
        name = "Torpedo"
        length = 2
    }

    %% ==================== ATTACKRESPONSE ====================
    class AttackResponse {
        -AttackResult result
        -Ship ship
        -Coordinate coordinate
        -List~AttackResponse~ additionalHits
        -boolean radarDetection
        +isHit() boolean
        +isBombAttack() boolean
        +isRadarDetection() boolean
    }

    %% ==================== AI STRATEGY ====================
    class AIStrategy {
        <<interface>>
        +chooseShot(Grid, List~Ship~) Coordinate
        +updateAfterShot(Coordinate, AttackResponse) void
        +reset() void
    }

    class AI {
        <<abstract>>
        #Random random
        #List~Coordinate~ hitHistory
        #getAvailableShots(Grid) List~Coordinate~
    }

    class EasyAI {
        Tir aléatoire simple
    }

    class MediumAI {
        HUNT/TARGET mode
    }

    class HardAI {
        Probabilité + orientation
    }

    %% ==================== ENUMS ====================
    class GameState {
        <<enumeration>>
        SETUP
        PLAYER1_TURN
        PLAYER2_TURN
        GAME_OVER
    }

    class AttackResult {
        <<enumeration>>
        MISS
        HIT
        SUNK
        ALREADY_HIT
        RADAR_USED
    }

    class Direction {
        <<enumeration>>
        HORIZONTAL
        VERTICAL
    }

    class PowerType {
        <<enumeration>>
        NORMAL
        BOMB
        RADAR
    }

    class AIDifficulty {
        <<enumeration>>
        EASY
        MEDIUM
        HARD
    }

    %% ==================== RELATIONS ====================

    %% === GAME ===
    %% Composition (Game owns Players)
    Game *-- "2" Player : players
    %% Game returns AttackResponse
    Game ..> AttackResponse : returns
    %% Game uses enums
    Game ..> GameState
    Game ..> PowerType

    %% === PLAYER ===
    %% Composition (Player owns Grids)
    Player *-- "2" Grid : grid, trackingGrid
    %% Aggregation (Player has Ships)
    Player o-- "*" Ship : ships
    %% Player returns/uses AttackResponse
    Player ..> AttackResponse : returns/uses

    %% === GRID ===
    %% Composition (Grid owns Cells)
    Grid *-- "*" Cell : cells
    %% Grid places Ships
    Grid ..> Ship : places
    %% Grid returns AttackResponse
    Grid ..> AttackResponse : returns

    %% === CELL ===
    %% Composition (Cell has Coordinate)
    Cell *-- "1" Coordinate : coordinate
    %% Association (Cell may reference Ship)
    Cell ..> "0..1" Ship : ship

    %% === SHIP ===
    %% Ship has positions
    Ship o-- "*" Coordinate : positions
    %% Ship uses Direction
    Ship ..> Direction

    %% === ATTACKRESPONSE ===
    %% AttackResponse references Ship
    AttackResponse ..> "0..1" Ship : ship
    %% AttackResponse has Coordinate
    AttackResponse o-- "1" Coordinate : coordinate
    %% AttackResponse can have additional hits (recursive)
    AttackResponse o-- "*" AttackResponse : additionalHits
    %% AttackResponse uses AttackResult
    AttackResponse ..> AttackResult

    %% === AI HIERARCHY ===
    %% Inheritance
    AIPlayer --|> Player
    %% AIPlayer composition with Strategy
    AIPlayer *-- "1" AIStrategy : strategy
    %% AIPlayer uses AIDifficulty
    AIPlayer ..> AIDifficulty

    %% AI implements AIStrategy
    AI ..|> AIStrategy

    %% AI subclasses
    EasyAI --|> AI
    MediumAI --|> AI
    HardAI --|> AI

    %% === SHIP SUBCLASSES ===
    Carrier --|> Ship
    Cruiser --|> Ship
    Destroyer --|> Ship
    Torpedo --|> Ship
```

## Légende

### Visibilité des membres

| Symbole | Visibilité | Description |
|---------|------------|-------------|
| `+` | public | Accessible partout |
| `-` | private | Accessible uniquement dans la classe |
| `#` | protected | Accessible dans la classe et ses sous-classes |
| `~` | package | Accessible dans le même package |

### Relations entre classes

| Symbole | Signification |
|---------|---------------|
| `*--` | Composition (le conteneur possède et gère le cycle de vie) |
| `o--` | Agrégation (le conteneur référence mais ne possède pas) |
| `-->` | Association |
| `..>` | Dépendance |
| `--\|>` | Héritage (extends) |
| `..\|>` | Implémentation (implements) |

## Classes par couleur (dans draw.io)

| Couleur | Type |
|---------|------|
| 🔵 Bleu | Classes de jeu (Game, AttackResponse) |
| 🟢 Vert | Player |
| 🟡 Jaune | IA (AIPlayer, AIStrategy, AI, EasyAI, MediumAI, HardAI) |
| 🟣 Violet | Grid |
| 🔴 Rouge | Cell |
| 🟠 Orange | Ship et sous-classes |
| ⚪ Gris | Enums et Coordinate |