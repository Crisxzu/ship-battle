# Architecture du Projet ShipBattle

## Table des Matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture MVC](#architecture-mvc)
3. [Structure du Projet](#structure-du-projet)
4. [Couche Model](#couche-model)
5. [Couche View](#couche-view)
6. [Couche Controller](#couche-controller)
7. [Guide de Développement GUI](#guide-de-développement-gui)
8. [Tâches Restantes](#tâches-restantes)

---

## Vue d'ensemble

ShipBattle est un jeu de bataille navale implémenté en Java avec une architecture MVC claire. Le projet utilise **libGDX** (avec LWJGL3) pour l'interface graphique et Gradle comme système de build.

### Technologies
- **Java** - Langage principal
- **libGDX** - Framework de jeu multi-plateforme
- **LWJGL3** - Backend desktop pour libGDX
- **Scene2D** - Système UI de libGDX
- **Gradle** - Build automation

### État Actuel
- ✅ Logique de jeu complète (Model)
- ✅ Interface console fonctionnelle
- ✅ Menu principal GUI
- ✅ 151 tests unitaires (100% passants)
- ⚠️ Écrans de jeu GUI à implémenter

---

## Architecture MVC

Le projet suit strictement le pattern **Model-View-Controller** avec une séparation claire entre les couches.

### Flux de Données

```
Utilisateur → View → Controller → Model → Controller → View → Rendu
```

### Implémentation Actuelle

```
┌─────────────────────────────────────────────────────────────────────┐
│                   ShipBattleApplication                              │
│                  (ApplicationAdapter)                                │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │            ScreenController                                 │    │
│  │         (Orchestrateur principal)                           │    │
│  │                                                              │    │
│  │  HashMap<GuiControllerEnum, GuiController>                  │    │
│  │                                                              │    │
│  │  ┌──────────────────┐  ┌──────────────────┐               │    │
│  │  │ MainMenuController│  │ SetupMenuController              │    │
│  │  │                   │  │                  │                │    │
│  │  │  MainMenuView     │  │  SetupPlayerNameView             │    │
│  │  │                   │  │  SetupPlayerShipView             │    │
│  │  └──────────────────┘  └──────────────────┘                │    │
│  │                                                              │    │
│  │  ┌──────────────────────────────────┐                      │    │
│  │  │     GameController                │                      │    │
│  │  │                                    │                      │    │
│  │  │  GameTurnDisplayView               │                      │    │
│  │  │  GameView                          │                      │    │
│  │  └──────────────────────────────────┘                      │    │
│  │                                                              │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
                          ↕
                     Model Layer
        ┌────────────────────────────────────┐
        │ Game → Player → Grid → Ship → Cell │
        └────────────────────────────────────┘
```

---

## Structure du Projet

```
ShipBattle/
├── core/                           # Module principal (logique + UI)
│   └── src/main/java/com/par_28/ship_battle/
│       ├── model/                  # 🔵 MODEL LAYER
│       │   ├── Game.java           # Orchestrateur du jeu
│       │   ├── Player.java         # Entité joueur
│       │   ├── Grid.java           # Grille de jeu
│       │   ├── Cell.java           # Cellule de grille
│       │   ├── Ship.java           # Classe abstraite bateau
│       │   ├── Carrier.java        # Porte-avion (5 cases)
│       │   ├── Cruiser.java        # Croiseur (4 cases)
│       │   ├── Destroyer.java      # Destroyer (3 cases)
│       │   ├── Torpedo.java        # Torpilleur (2 cases)
│       │   ├── Coordinate.java     # Système de coordonnées
│       │   ├── AttackResponse.java # Résultat d'attaque
│       │   ├── GameState.java      # États du jeu (enum)
│       │   ├── AttackResult.java   # Types de résultat (enum)
│       │   ├── Direction.java      # Orientation bateau (enum)
│       │   └── exceptions/         # Exceptions métier
│       │
│       ├── view/                   # 🟢 VIEW LAYER
│       │   ├── console/
│       │   │   ├── ConsoleView.java       # Interface console
│       │   │   └── InputHandler.java      # Gestion entrées
│       │   └── gui/
│       │       ├── MainMenuView.java      # Menu principal
│       │       └── SoundHandler.java      # Gestion audio
│       │
│       ├── controller/             # 🟡 CONTROLLER LAYER
│       │   ├── console/
│       │   │   └── ConsoleGameController.java  # Contrôleur console
│       │   └── gui/
│       │       ├── ScreenController.java       # Orchestrateur GUI
│       │       ├── GuiController.java          # Contrôleur abstrait
│       │       ├── MainMenuController.java     # Contrôleur menu
│       │       └── GuiControllerEnum.java      # Énumération écrans
│       │
│       └── ShipBattleApplication.java   # Point d'entrée GUI
│
├── lwjgl3/                         # Module launcher desktop
│   └── src/main/java/com/par_28/ship_battle/lwjgl3/
│       └── Lwjgl3Launcher.java     # Main desktop
│
└── assets/                         # Ressources (images, sons, UI)
    ├── background.png
    ├── uiskin.json/atlas/png
    ├── ui/shipbattle-skin.json/atlas/png
    └── fonts/
```

---

## Couche Model

La couche Model contient **toute la logique métier** du jeu. Elle est complètement indépendante de l'UI.

### Composants Principaux

#### 1. **Game.java** (151 lignes)
Le chef d'orchestre du jeu.

**Responsabilités :**
- Gérer l'état du jeu (`GameState`)
- Orchestrer les tours de jeu
- Vérifier les conditions de victoire
- Compteur de tours

**États du jeu :**
```java
enum GameState {
    SETUP,           // Phase de placement des bateaux
    PLAYER1_TURN,    // Tour du joueur 1
    PLAYER2_TURN,    // Tour du joueur 2
    GAME_OVER        // Partie terminée
}
```

**Méthodes clés :**
- `Game(Player player1, Player player2)` - Constructeur
- `AttackResponse attack(Coordinate coordinate)` - Attaque un joueur
- `void switchTurn()` - Change de joueur actif
- `boolean isGameOver()` - Vérifie si la partie est terminée
- `Player getWinner()` - Retourne le gagnant

#### 2. **Player.java** (157 lignes)
Représente un joueur.

**Propriétés :**
- `String name` - Nom du joueur
- `Grid grid` - Grille avec ses bateaux
- `Grid trackingGrid` - Grille de suivi des attaques
- `List<Ship> ships` - Flotte de bateaux

**Méthodes clés :**
- `void placeShipOnGrid(Ship ship, Coordinate position, Direction direction)` - Place un bateau
- `AttackResponse receiveAttack(Coordinate coordinate)` - Reçoit une attaque
- `void recordAttack(Coordinate coordinate, AttackResult result)` - Enregistre une attaque
- `boolean isDead()` - Vérifie si tous les bateaux sont coulés

#### 3. **Grid.java** (299 lignes)
Grille de jeu 2D.

**Responsabilités :**
- Gérer le plateau de jeu (tableau 2D de `Cell`)
- Placer les bateaux avec validation
- Gérer les attaques
- Vérifier les collisions et adjacences

**Méthodes clés :**
- `Grid(int width, int height)` - Constructeur
- `void placeShip(Ship ship, Coordinate position, Direction direction)` - Place un bateau
- `boolean canPlaceShip(Ship ship, Coordinate position, Direction direction)` - Validation
- `AttackResponse attack(Coordinate coordinate)` - Traite une attaque
- `String display(boolean hideShips)` - Affichage ASCII

**Validation de placement :**
- Vérifie que le bateau ne dépasse pas de la grille
- Vérifie qu'il n'y a pas de collision avec un autre bateau
- Vérifie qu'aucun bateau n'est adjacent (règle des 8 directions)

#### 4. **Ship.java** (152 lignes)
Classe abstraite pour tous les bateaux.

**Propriétés :**
- `String name` - Nom du bateau
- `int length` - Longueur (nombre de cases)
- `int life` - Points de vie restants
- `Direction direction` - Orientation (H/V)
- `List<Coordinate> positions` - Coordonnées occupées

**Types de bateaux :**
- `Carrier` - Porte-avion (5 cases)
- `Cruiser` - Croiseur (4 cases)
- `Destroyer` - Destroyer (3 cases)
- `Torpedo` - Torpilleur (2 cases)

**Méthodes :**
- `void receiveDamage()` - Inflige 1 dégât
- `boolean isDestroyed()` - Vérifie si coulé

#### 5. **Cell.java** (97 lignes)
Cellule individuelle de la grille.

**Propriétés :**
- `Coordinate coordinate` - Position
- `Ship ship` - Référence au bateau (null si vide)
- `boolean isShot` - A été attaquée

**Méthodes :**
- `AttackResult shoot()` - Tire sur la cellule

#### 6. **Coordinate.java** (161 lignes)
Système de coordonnées flexible.

**Formats supportés :**
- Format lettre-nombre : `"A1"`, `"B5"`, `"AA10"` (colonnes multi-lettres)
- Format x,y : `"0,0"`, `"5,3"`

**Méthodes :**
- `Coordinate(String coordinate)` - Parse une chaîne
- `Coordinate(int x, int y)` - Constructeur x,y
- `String toString()` - Format "x,y"
- Conversions automatiques entre formats

#### 7. **AttackResponse.java** (63 lignes)
Enveloppe le résultat d'une attaque.

**Contenu :**
- `AttackResult result` - Type de résultat
- `Ship ship` - Bateau touché (si applicable)

**Types de résultats :**
```java
enum AttackResult {
    MISS,          // Raté (eau)
    HIT,           // Touché
    SUNK,          // Coulé
    ALREADY_HIT    // Déjà tiré ici
}
```

### Règles du Jeu Implémentées

✅ **Phase de setup :**
- Chaque joueur place 5 bateaux (1 Carrier, 1 Cruiser, 2 Destroyers, 1 Torpedo)
- Les bateaux ne peuvent pas se chevaucher
- Les bateaux ne peuvent pas être adjacents (diagonales comprises)
- Les bateaux doivent être dans les limites de la grille

✅ **Phase de jeu :**
- Tour par tour
- Un coup par tour
- Feedback immédiat (touché/raté/coulé)
- Grille de tracking pour suivre ses attaques
- Impossible de tirer deux fois au même endroit

✅ **Conditions de victoire :**
- Tous les bateaux de l'adversaire doivent être coulés
- Le gagnant est annoncé
- Nombre de tours comptabilisé

---

## Couche View

La couche View est responsable de **l'affichage** et de la **capture des entrées utilisateur**.

### Implémentations Actuelles

#### 1. **ConsoleView.java** (364 lignes)
Interface console complète et fonctionnelle.

**Méthodes d'affichage :**
- `displayWelcomeMessage()` - Message de bienvenue
- `displayMainMenu()` - Menu principal
- `displayTurnStartMessage(Player player)` - Début de tour (affiche les deux grilles)
- `displayPlayerGrid(Grid grid, boolean hideShips)` - Rendu ASCII de la grille
- `displayAttackResponse(AttackResponse response)` - Résultat de l'attaque
- `displayGameOverMessage(Player winner, int turns)` - Écran de fin

**Méthodes d'entrée :**
- `String askPlayerName()` - Demande le nom
- `Coordinate askCoordinate()` - Demande une coordonnée
- `Direction askShipDirection()` - Demande l'orientation (H/V)

**Rendu de grille ASCII :**
```
    A B C D E F G H I J
  1 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
  2 ~ ◼ ◼ ◼ ~ ~ ~ ~ ~ ~
  3 ~ ~ ~ ~ ~ X ~ ~ ~ ~
  4 ~ ~ ~ O ~ ~ ~ ~ ~ ~
  5 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

Légende :
~ = Eau (vide)
◼ = Bateau
X = Touché
O = Raté
```

#### 2. **MainMenuView.java** (140 lignes)
Menu principal GUI avec Scene2D.

**Architecture Scene2D :**
```java
public class MainMenuView implements Screen {
    private Stage stage;           // Conteneur UI
    private Table table;           // Layout principal
    private Skin skin;             // Style UI
    private Texture background;    // Image de fond
}
```

**Composants UI :**
- Titre du jeu
- 4 boutons :
  - "Nouvelle Partie" → Lance une nouvelle partie
  - "Charger Partie" → Charge une sauvegarde
  - "Options" → Paramètres
  - "Quitter" → Ferme l'application
- Label de version
- Image de fond

**Cycle de vie libGDX :**
- `show()` - Appelé quand l'écran devient actif
- `render(float delta)` - Appelé chaque frame
- `resize(int width, int height)` - Appelé lors du redimensionnement
- `hide()` - Appelé quand l'écran devient inactif
- `dispose()` - Libère les ressources

**Utilisation du Skin :**
```java
skin = new Skin(Gdx.files.internal("uiskin.json"));
TextButton button = new TextButton("Nouvelle Partie", skin);
```

#### 3. **SoundHandler.java** (81 lignes)
Gestionnaire de sons et musiques.

**Structure :**
```java
enum SoundId {
    MENU_MUSIC,
    GAME_MUSIC,
    HIT_SOUND,
    MISS_SOUND,
    SUNK_SOUND
}

Map<SoundId, Music> musics;
Map<SoundId, Sound> sounds;
```

**Méthodes :**
- `playMusic(SoundId id)` - Joue une musique en boucle
- `playSound(SoundId id)` - Joue un effet sonore
- `stopMusic(SoundId id)` - Arrête une musique
- `dispose()` - Libère les ressources audio

---

## Couche Controller

La couche Controller fait le **lien entre View et Model**. Elle contient la logique de flux du jeu.

### Architecture GUI

#### 1. **ScreenController.java** (107 lignes)
L'orchestrateur principal qui gère les transitions d'écrans et stocke l'état du jeu.

**Responsabilités :**
- Maintenir une map de tous les contrôleurs GUI
- Changer d'écran actif via `changeController()`
- Déléguer les appels render/resize/dispose au contrôleur actif
- Stocker les références au jeu et aux joueurs (via `app` - `ShipBattleApplication`)
- Instancier les contrôleurs avec une référence vers lui-même (pattern parent)

**Code clé :**
```java
public class ScreenController extends GuiController {
    public ShipBattleApplication app;
    Map<GuiControllerEnum, GuiController> controllers;
    GuiController currentController;

    public ScreenController(ShipBattleApplication app) {
        super(null);  // Pas de parent pour ScreenController
        this.app = app;

        controllers = new HashMap<>();
        controllers.put(GuiControllerEnum.MAIN_MENU, new MainMenuController(this));
        controllers.put(GuiControllerEnum.SETUP_MENU, new SetupMenuController(this));
        controllers.put(GuiControllerEnum.GAME, new GameController(this));

        changeController(GuiControllerEnum.MAIN_MENU);
    }

    public void changeController(GuiControllerEnum controller) {
        currentController = controllers.get(controller);
        currentController.reset();  // Réinitialise le controller
        currentController.view.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        currentController.render(delta);
    }
}
```

**Utilisation :**
```java
// Dans une View, pour changer d'écran
parent.changeController(GuiControllerEnum.SETUP_MENU);
```

#### 2. **GuiController.java** (40 lignes)
Classe abstraite pour tous les contrôleurs GUI.

**Structure :**
```java
public abstract class GuiController {
    public GuiView view;
    public ScreenController parent;  // Référence au parent

    public GuiController(ScreenController parent) {
        this.parent = parent;
    }

    public abstract void update(float dt);  // Mise à jour logique

    public void render(float dt) {
        update(dt);  // Appelle update puis laisse la vue se dessiner
    }

    public void dispose() {
        // Libération des ressources
    }

    public void resize(int width, int height) {
        // Gestion du redimensionnement
    }

    public void reset() {
        // Réinitialisation du contrôleur (appelé à chaque activation)
    }

    protected void changeView(GuiView view) {
        // Permet de changer dynamiquement de vue
        if (this.view != null) {
            this.view.hide();
            this.view.dispose();
        }
        this.view = view;
        this.view.show();
    }
}
```

**Pattern implémenté :**
- Chaque contrôleur a une référence au `ScreenController` parent via `this.parent`
- Le contrôleur peut accéder aux données du jeu via `parent.app.game`
- Méthode `reset()` : recréer la vue à chaque activation de l'écran
- Méthode `changeView()` : permet de changer dynamiquement de vue au sein d'un même contrôleur

#### 3. **GuiView.java** (111 lignes)
Classe abstraite pour toutes les vues GUI.

**Structure :**
```java
public abstract class GuiView implements Screen {
    protected Texture backgroundTexture;
    protected ScreenController parent;
    public Stage stage;
    protected Skin skin;
    protected Array<TextButton> menuButtons;
    float base;  // Taille de base pour le scaling responsive

    public GuiView(ScreenController parent) {
        this.parent = parent;
        this.stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        menuButtons = new Array<>();
        backgroundTexture = SpriteHandler.getTexture(SpriteHandler.SpriteID.BACKGROUND);
    }

    protected void buildUI() {
        // À implémenter par les vues concrètes
    }

    protected Cell<TextButton> addMenuButton(Table table, String text, Runnable action) {
        // Méthode utilitaire pour créer des boutons de menu
        TextButton button = new TextButton(text, skin);
        menuButtons.add(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        Cell<TextButton> cell = table.add(button);
        cell.row();
        return cell;
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        // Dessiner le background
        stage.getBatch().begin();
        if(backgroundTexture != null) {
            stage.getBatch().draw(backgroundTexture, 0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        stage.getBatch().end();

        // Dessiner les widgets
        stage.act(delta);
        stage.draw();
    }

    public void update(float delta) {
        // Logique de mise à jour de la vue
    }
}
```

**Fonctionnalités fournies :**
- Gestion automatique du background
- Méthode `addMenuButton()` pour créer facilement des boutons
- Gestion automatique du stage et du skin
- Scaling responsive avec la variable `base`

#### 4. **MainMenuController.java** (26 lignes)
Contrôleur du menu principal.

**Implémentation actuelle :**
```java
public class MainMenuController extends GuiController {
    public MainMenuController(ScreenController parent) {
        super(parent);
    }

    public void update(float dt) {
        // Pas de logique de mise à jour pour le menu
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        view.render(dt);
    }

    @Override
    public void reset() {
        super.reset();
        view = new MainMenuView(parent);  // Recrée la vue
    }
}
```

**Pattern utilisé :**
- La vue est recréée à chaque appel de `reset()`
- Le contrôleur passe la référence au `parent` à la vue
- La vue peut ainsi appeler `parent.changeController()` pour naviguer

#### 5. **SetupMenuController.java** (69 lignes)
Contrôleur pour la configuration de la partie (saisie des noms des joueurs).

**Responsabilités :**
- Collecter les noms des 2 joueurs
- Valider les noms (non vides, pas de doublons)
- Créer les objets `Player` une fois les noms collectés
- Gérer dynamiquement les vues (SetupPlayerNameView → SetupPlayerShipView)

**Implémentation :**
```java
public class SetupMenuController extends GuiController {
    List<String> names = new ArrayList<>();

    public boolean addName(String name) {
        if(name.isEmpty()) {
            SoundHandler.playSound(SoundHandler.SoundID.ERROR, 0.2f);
            Dialogs.showErrorDialog(view.stage, "Please enter a name");
            return false;
        }

        if(names.contains(name)) {
            SoundHandler.playSound(SoundHandler.SoundID.ERROR, 0.2f);
            Dialogs.showErrorDialog(view.stage,
                String.format("%s already registered", name));
            return false;
        }

        this.names.add(name);

        if(this.names.size() >= this.parent.app.nbPlayers) {
            // Créer les joueurs
            Player player1 = new Player(this.names.get(0), this.parent.app.gridSize);
            Player player2 = new Player(this.names.get(1), this.parent.app.gridSize);

            this.parent.app.player1 = player1;
            this.parent.app.player2 = player2;

            // Changer dynamiquement de vue vers SetupPlayerShipView
            changeView(new SetupPlayerShipView(parent));
        }

        return true;
    }

    @Override
    public void reset() {
        if(names != null) {
            names.clear();
        }
        view = new SetupPlayerNameView(this.parent, this);
    }
}
```

**Pattern avancé :**
- Le contrôleur garde une liste des noms collectés
- Il change dynamiquement de vue avec `changeView()` quand tous les noms sont collectés
- Les Players sont stockés dans `parent.app` pour être accessibles partout

#### 6. **GameController.java** (48 lignes)
Contrôleur de la bataille principale.

**Responsabilités :**
- Gérer les tours de jeu
- Alterner entre GameView (jeu) et GameTurnDisplayView (transition entre tours)
- Traiter les attaques des joueurs
- Jouer les sons appropriés

**Implémentation :**
```java
public class GameController extends GuiController {
    public void startTurn() {
        changeView(new GameView(parent, this));
    }

    public void changeTurn() {
        changeView(new GameTurnDisplayView(parent, this));
    }

    public AttackResponse playTurn(Coordinate attackCord) {
        AttackResponse response = this.parent.app.game.playTurn(attackCord);
        SoundHandler.playSound(SoundHandler.SoundID.CANNON_SHOT, 0.3f);
        return response;
    }

    @Override
    public void reset() {
        super.reset();
        changeView(new GameTurnDisplayView(parent, this));
    }
}
```

**Pattern avancé :**
- Le contrôleur alterne dynamiquement entre 2 vues :
  - `GameTurnDisplayView` : écran de transition "Tour de [Joueur]"
  - `GameView` : l'écran de jeu avec les 2 grilles
- Les vues appellent `controller.startTurn()` et `controller.changeTurn()`
- Le contrôleur garde la référence au `Game` via `parent.app.game`

#### 7. **GuiControllerEnum.java**
Énumération de tous les écrans disponibles.

**Actuel :**
```java
public enum GuiControllerEnum {
    MAIN_MENU,
    SETUP_MENU,  // Configuration + saisie des noms
    GAME         // Jeu principal (alterne entre GameView et GameTurnDisplayView)
}
```

**À ajouter (futur) :**
```java
public enum GuiControllerEnum {
    MAIN_MENU,
    SETUP_MENU,
    GAME,
    GAME_OVER,       // Fin de partie
    OPTIONS,         // Paramètres
    LOAD_GAME        // Chargement
}
```

### Contrôleur Console (Référence)

#### **ConsoleGameController.java** (162 lignes)
Implémentation complète du jeu en console - **à utiliser comme référence pour les contrôleurs GUI**.

**Flow de jeu :**
```java
public void launch() {
    while (true) {
        displayMainMenu();
        int choice = askMenuChoice();
        switch (choice) {
            case 1: play(); break;
            case 2: loadGame(); break;
            case 3: options(); break;
            case 4: exit(); break;
        }
    }
}

public void play() {
    // 1. Demander noms des joueurs
    String name1 = view.askPlayerName();
    String name2 = view.askPlayerName();

    // 2. Créer les joueurs et la partie
    Player p1 = new Player(name1, new Grid(10, 10));
    Player p2 = new Player(name2, new Grid(10, 10));
    Game game = new Game(p1, p2);

    // 3. Phase de setup (placement bateaux)
    setupPlayerShip(p1);
    setupPlayerShip(p2);

    // 4. Boucle de jeu
    while (!game.isGameOver()) {
        Player currentPlayer = game.getCurrentPlayer();
        view.displayTurnStartMessage(currentPlayer);

        Coordinate target = view.askCoordinate();
        AttackResponse response = game.attack(target);

        view.displayAttackResponse(response);
        game.switchTurn();
    }

    // 5. Fin de partie
    view.displayGameOverMessage(game.getWinner(), game.getTurnCount());
}

private void setupPlayerShip(Player player) {
    List<Ship> ships = getDefaultShips();
    for (Ship ship : ships) {
        boolean placed = false;
        while (!placed) {
            view.displayPlayerGrid(player.getGrid(), false);
            view.displayMessage("Placez votre " + ship.getName());

            Coordinate coord = view.askCoordinate();
            Direction dir = view.askShipDirection();

            try {
                player.placeShipOnGrid(ship, coord, dir);
                placed = true;
            } catch (ShipPlacementException e) {
                view.displayError(e.getMessage());
            }
        }
    }
}

private List<Ship> getDefaultShips() {
    return Arrays.asList(
        new Carrier(),
        new Cruiser(),
        new Destroyer(),
        new Destroyer(),
        new Torpedo()
    );
}
```

**Ce pattern doit être adapté pour les contrôleurs GUI !**

---

## Guide de Développement GUI

### Comment Ajouter un Nouvel Écran

Suivez ces étapes pour créer un nouvel écran en respectant l'architecture MVC :

#### Étape 1 : Ajouter l'Écran à l'Énumération

```java
// GuiControllerEnum.java
public enum GuiControllerEnum {
    MAIN_MENU,
    GAME_SETUP,    // ← Ajouter ici
    // ...
}
```

#### Étape 2 : Créer la View

```java
// view/gui/GameSetupView.java
package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class GameSetupView implements Screen {
    private Stage stage;
    private Skin skin;
    // ... autres composants UI

    public GameSetupView() {
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Construire l'UI avec Scene2D
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Ajouter des composants
        Label title = new Label("Placement des Bateaux", skin);
        table.add(title).padTop(50);
        table.row();

        // ... suite de l'UI
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    // Méthodes publiques pour le contrôleur
    public void updateGridDisplay(Grid grid) {
        // Mettre à jour l'affichage de la grille
    }

    public void showError(String message) {
        // Afficher un message d'erreur
    }
}
```

#### Étape 3 : Créer le Controller

```java
// controller/gui/GameSetupController.java
package com.par_28.ship_battle.controller.gui;

import com.par_28.ship_battle.model.*;
import com.par_28.ship_battle.view.gui.GameSetupView;

public class GameSetupController extends GuiController {
    private GameSetupView gameSetupView;
    private Player player1;
    private Player player2;
    private Game game;
    private int currentPlayerIndex = 0;  // 0 = player1, 1 = player2

    public GameSetupController() {
        this.gameSetupView = new GameSetupView();
        this.view = gameSetupView;
    }

    @Override
    public void update() {
        // Initialiser une nouvelle partie
        player1 = new Player("Joueur 1", new Grid(10, 10));
        player2 = new Player("Joueur 2", new Grid(10, 10));
        game = new Game(player1, player2);

        // Mettre à jour la vue
        gameSetupView.updateGridDisplay(player1.getGrid());
    }

    // Méthode appelée quand l'utilisateur clique pour placer un bateau
    public void placeShip(Ship ship, Coordinate coord, Direction dir) {
        Player currentPlayer = (currentPlayerIndex == 0) ? player1 : player2;

        try {
            currentPlayer.placeShipOnGrid(ship, coord, dir);
            gameSetupView.updateGridDisplay(currentPlayer.getGrid());

            // Si tous les bateaux sont placés, passer au joueur suivant
            if (allShipsPlaced(currentPlayer)) {
                if (currentPlayerIndex == 0) {
                    currentPlayerIndex = 1;
                    gameSetupView.updateGridDisplay(player2.getGrid());
                } else {
                    // Les deux joueurs sont prêts → passer à la bataille
                    transitionToBattle();
                }
            }
        } catch (ShipPlacementException e) {
            gameSetupView.showError(e.getMessage());
        }
    }

    private boolean allShipsPlaced(Player player) {
        // Vérifier que tous les bateaux sont placés
        return player.getShips().stream().allMatch(ship -> ship.getPositions().size() > 0);
    }

    private void transitionToBattle() {
        // Passer à l'écran de bataille
        // Note: il faut passer l'objet Game au BattleController
        ScreenController sc = (ScreenController) /* obtenir référence */;
        sc.changeController(GuiControllerEnum.BATTLE);
    }
}
```

#### Étape 4 : Enregistrer le Controller dans ScreenController

```java
// ScreenController.java
public ScreenController() {
    controllers = new HashMap<>();
    controllers.put(GuiControllerEnum.MAIN_MENU, new MainMenuController());
    controllers.put(GuiControllerEnum.GAME_SETUP, new GameSetupController());  // ← Ajouter ici

    currentController = controllers.get(GuiControllerEnum.MAIN_MENU);
    this.view = currentController.getView();
}
```

#### Étape 5 : Déclencher la Transition

```java
// Dans MainMenuView.java, bouton "Nouvelle Partie"
TextButton newGameButton = new TextButton("Nouvelle Partie", skin);
newGameButton.addListener(new ClickListener() {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        // Obtenir le ScreenController et changer d'écran
        // Note: il faut passer une référence au ScreenController
        screenController.changeController(GuiControllerEnum.GAME_SETUP);
    }
});
```

### Bonnes Pratiques

#### 1. **Séparation des Responsabilités**
- ❌ **NE PAS** mettre de logique métier dans la View
- ✅ **FAIRE** toute la logique dans le Controller
- ✅ **FAIRE** des méthodes publiques dans la View pour mettre à jour l'affichage

```java
// ❌ MAUVAIS : Logique dans la View
public class BattleView implements Screen {
    public void onCellClicked(int x, int y) {
        AttackResponse response = game.attack(new Coordinate(x, y));  // NON !
        displayResult(response);
    }
}

// ✅ BON : Logique dans le Controller
public class BattleView implements Screen {
    private BattleController controller;

    public void onCellClicked(int x, int y) {
        controller.handleCellClick(x, y);  // Déléguer au controller
    }

    public void displayAttackResult(AttackResponse response) {
        // Mise à jour de l'affichage seulement
    }
}
```

#### 2. **Communication Controller ↔ View**
- Le Controller appelle des méthodes publiques de la View pour mettre à jour l'affichage
- La View appelle des méthodes du Controller pour réagir aux événements utilisateur

```java
// View → Controller (événement utilisateur)
public class BattleView {
    private BattleController controller;

    public void setController(BattleController controller) {
        this.controller = controller;
    }

    private void setupGrid() {
        gridButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.handleAttack(coordinate);  // ← Événement vers controller
            }
        });
    }
}

// Controller → View (mise à jour affichage)
public class BattleController {
    public void handleAttack(Coordinate coord) {
        AttackResponse response = game.attack(coord);
        battleView.displayAttackResult(response);  // ← Mise à jour de la vue
        battleView.updateGrids(game.getCurrentPlayer().getTrackingGrid());
    }
}
```

#### 3. **Gestion du Game State**
- Créer le `Game` dans le `GameSetupController`
- Le passer au `BattleController` lors de la transition
- Utiliser un système de données partagées (singleton ou injection)

```java
// Pattern Singleton pour partager le Game
public class GameManager {
    private static GameManager instance;
    private Game currentGame;

    private GameManager() {}

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void setGame(Game game) {
        this.currentGame = game;
    }

    public Game getGame() {
        return currentGame;
    }
}

// Utilisation dans GameSetupController
public void transitionToBattle() {
    GameManager.getInstance().setGame(this.game);
    screenController.changeController(GuiControllerEnum.BATTLE);
}

// Utilisation dans BattleController
@Override
public void update() {
    this.game = GameManager.getInstance().getGame();
    battleView.initialize(game.getPlayer1(), game.getPlayer2());
}
```

#### 4. **Référence au ScreenController**
Pour permettre les transitions, passez une référence au `ScreenController` lors de la création des contrôleurs :

```java
public abstract class GuiController {
    protected Screen view;
    protected ScreenController screenController;  // ← Ajouter

    public void setScreenController(ScreenController sc) {
        this.screenController = sc;
    }
}

// Dans ScreenController
public ScreenController() {
    controllers = new HashMap<>();

    MainMenuController mainMenu = new MainMenuController();
    mainMenu.setScreenController(this);
    controllers.put(GuiControllerEnum.MAIN_MENU, mainMenu);

    GameSetupController setup = new GameSetupController();
    setup.setScreenController(this);
    controllers.put(GuiControllerEnum.GAME_SETUP, setup);

    // ...
}
```

### Composants Scene2D Utiles

#### Layout avec Table
```java
Table table = new Table();
table.setFillParent(true);
table.top();  // Aligner en haut

// Ajouter des widgets
Label label = new Label("Texte", skin);
table.add(label).padTop(20).padBottom(10);
table.row();  // Nouvelle ligne

TextButton button = new TextButton("Bouton", skin);
table.add(button).width(200).height(50);
```

#### Grille de Boutons (pour le plateau de jeu)
```java
Table gridTable = new Table();

for (int y = 0; y < 10; y++) {
    for (int x = 0; x < 10; x++) {
        TextButton cellButton = new TextButton("", skin);
        cellButton.setName(x + "," + y);  // Stocker les coordonnées

        final int finalX = x;
        final int finalY = y;
        cellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onCellClicked(finalX, finalY);
            }
        });

        gridTable.add(cellButton).size(40, 40);
    }
    gridTable.row();
}
```

#### Images et Textures
```java
Texture texture = new Texture(Gdx.files.internal("ship.png"));
Image image = new Image(texture);
table.add(image).size(100, 50);

// N'oubliez pas de dispose() dans dispose()
@Override
public void dispose() {
    texture.dispose();
}
```

#### Listeners d'Événements
```java
button.addListener(new ClickListener() {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        // Clic sur le bouton
    }
});

button.addListener(new InputListener() {
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        // Survol de la souris
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        // Sortie du survol
    }
});
```

---

## Tâches Restantes

Voici la liste complète des tâches pour implémenter l'interface graphique du jeu.

### Phase 1 : Écrans de Base (Priorité Haute)

#### 1.1 Écran de Configuration de Partie
**Fichiers à créer :**
- `view/gui/GameConfigView.java`
- `controller/gui/GameConfigController.java`

**Fonctionnalités :**
- [ ] Formulaire de saisie des noms des joueurs
  - [ ] Champ texte pour Joueur 1
  - [ ] Champ texte pour Joueur 2
  - [ ] Validation (noms non vides)
- [ ] Sélection de la taille de grille
  - [ ] Options : 10x10 (par défaut), 12x12, 15x15
  - [ ] Radio buttons ou dropdown
- [ ] Bouton "Commencer"
  - [ ] Créer les objets Player
  - [ ] Créer l'objet Game
  - [ ] Transition vers GameSetupView

**Complexité :** 🟡 Moyenne (1-2 jours)

---

#### 1.2 Écran de Placement des Bateaux
**Fichiers à créer :**
- `view/gui/GameSetupView.java`
- `controller/gui/GameSetupController.java`

**Fonctionnalités :**
- [ ] Affichage de la grille interactive (10x10)
  - [ ] Table de boutons cliquables
  - [ ] Coordonnées (A-J en colonnes, 1-10 en lignes)
  - [ ] États visuels : vide, bateau, invalide
- [ ] Liste des bateaux à placer
  - [ ] 5 bateaux avec leur longueur
  - [ ] Indicateur visuel du bateau sélectionné
  - [ ] Compteur de bateaux restants
- [ ] Contrôles de placement
  - [ ] Sélection d'un bateau dans la liste
  - [ ] Bouton toggle Horizontal/Vertical
  - [ ] Clic sur la grille pour placer
  - [ ] Validation en temps réel (vert/rouge)
  - [ ] Bouton "Annuler" pour retirer un bateau
- [ ] Feedback visuel
  - [ ] Aperçu du placement au survol
  - [ ] Messages d'erreur (placement invalide)
  - [ ] Animation de confirmation
- [ ] Gestion des deux joueurs
  - [ ] Écran intermédiaire "Tour de [Joueur]"
  - [ ] Cacher la grille entre les joueurs
  - [ ] Bouton "Prêt" pour passer au joueur 2
- [ ] Transition vers bataille quand les deux joueurs sont prêts

**Complexité :** 🔴 Élevée (3-5 jours)

**Référence :** `ConsoleGameController.setupPlayerShip()` (lines 85-110)

---

#### 1.3 Écran de Bataille Principale
**Fichiers à créer :**
- `view/gui/BattleView.java`
- `controller/gui/BattleController.java`

**Fonctionnalités :**
- [ ] Interface double grille
  - [ ] **Grille gauche** : Grille du joueur actuel (lecture seule)
    - [ ] Affichage des bateaux
    - [ ] Affichage des impacts reçus
  - [ ] **Grille droite** : Grille de tracking (interactive)
    - [ ] Cliquable pour attaquer
    - [ ] Affichage des tirs précédents (touché/raté)
    - [ ] Désactivation des cases déjà tirées
- [ ] Indicateurs de jeu
  - [ ] Label "Tour de [Joueur]"
  - [ ] Compteur de tours
  - [ ] Liste des bateaux ennemis (avec état : intact/touché/coulé)
- [ ] Gestion des attaques
  - [ ] Clic sur case → `BattleController.attack()`
  - [ ] Animation d'attaque (explosion, splash)
  - [ ] Popup de résultat (Raté/Touché/Coulé!)
  - [ ] Sons (hit.wav, miss.wav, sunk.wav)
- [ ] Changement de tour
  - [ ] Écran intermédiaire "Passez l'écran à [Joueur]"
  - [ ] Bouton "Prêt" pour continuer
  - [ ] Masquage des informations sensibles
- [ ] Détection de fin de partie
  - [ ] Vérification `game.isGameOver()`
  - [ ] Transition vers GameOverView

**Complexité :** 🔴 Très Élevée (5-7 jours)

**Référence :** `ConsoleGameController.play()` (lines 45-70)

---

#### 1.4 Écran de Fin de Partie
**Fichiers à créer :**
- `view/gui/GameOverView.java`
- `controller/gui/GameOverController.java`

**Fonctionnalités :**
- [ ] Annonce du gagnant
  - [ ] Message "Victoire de [Joueur]!"
  - [ ] Animation de célébration
- [ ] Statistiques de partie
  - [ ] Nombre de tours
  - [ ] Précision de tir pour chaque joueur
  - [ ] Nombre de coups portés/ratés
  - [ ] Temps de partie (si implémenté)
- [ ] Grilles finales
  - [ ] Affichage des deux grilles complètes
  - [ ] Visualisation des stratégies de placement
- [ ] Actions possibles
  - [ ] Bouton "Rejouer" → Retour à GameConfigView
  - [ ] Bouton "Menu Principal" → Retour à MainMenuView
  - [ ] Bouton "Quitter"

**Complexité :** 🟢 Faible (1 jour)

---

### Phase 2 : Amélioration Visuelle (Priorité Moyenne)

#### 2.1 Assets Graphiques
**Tâches :**
- [ ] Créer/trouver des sprites de bateaux
  - [ ] Carrier (5 cases)
  - [ ] Cruiser (4 cases)
  - [ ] Destroyer (3 cases)
  - [ ] Torpedo (2 cases)
  - [ ] Versions horizontale et verticale
  - [ ] États : intact, endommagé, coulé
- [ ] Textures de grille
  - [ ] Case d'eau (vague animée optionnellement)
  - [ ] Case avec bateau
  - [ ] Case touchée (feu, fumée)
  - [ ] Case ratée (splash)
- [ ] Effets visuels
  - [ ] Explosion (sprite sheet pour animation)
  - [ ] Splash d'eau
  - [ ] Particules de fumée
- [ ] UI elements
  - [ ] Boutons personnalisés
  - [ ] Panneaux décoratifs
  - [ ] Curseurs personnalisés

**Outils suggérés :**
- Kenney.nl (assets gratuits)
- Aseprite (création de sprites)
- GIMP/Photoshop (édition)

**Complexité :** 🟡 Moyenne (2-3 jours)

---

#### 2.2 Animations et Effets
**Fichiers à créer/modifier :**
- `view/gui/animations/ExplosionAnimation.java`
- `view/gui/animations/SplashAnimation.java`
- `view/gui/effects/ParticleEffectManager.java`

**Fonctionnalités :**
- [ ] Système de particules libGDX
  - [ ] Effet d'explosion au touché
  - [ ] Splash d'eau au raté
  - [ ] Fumée pour bateau coulé
- [ ] Animations de sprites
  - [ ] Sprite sheet pour explosions
  - [ ] Animation de vagues sur grille
  - [ ] Pulsation sur bateau sélectionné
- [ ] Transitions d'écrans
  - [ ] Fade in/out
  - [ ] Slide animations
- [ ] Feedback utilisateur
  - [ ] Bouton hover (changement de couleur)
  - [ ] Bouton click (scale down)
  - [ ] Shake de l'écran lors de coup critique

**Complexité :** 🟡 Moyenne (2-3 jours)

---

#### 2.3 Sons et Musique
**Fichiers à créer :**
- `assets/sounds/hit.wav`
- `assets/sounds/miss.wav`
- `assets/sounds/sunk.wav`
- `assets/sounds/place_ship.wav`
- `assets/music/menu.mp3`
- `assets/music/battle.mp3`
- `assets/music/victory.mp3`

**Fonctionnalités :**
- [ ] Effets sonores
  - [ ] Son de placement de bateau
  - [ ] Son de tir
  - [ ] Son de touché
  - [ ] Son de raté
  - [ ] Son de bateau coulé (dramatique)
  - [ ] Son de clic UI
- [ ] Musiques de fond
  - [ ] Menu principal (calme)
  - [ ] Bataille (tendue)
  - [ ] Victoire (triomphante)
  - [ ] Défaite (sombre)
- [ ] Intégration avec SoundHandler
  - [ ] Ajouter les nouveaux SoundId
  - [ ] Charger les assets
  - [ ] Appeler aux bons moments

**Sources suggérées :**
- Freesound.org
- OpenGameArt.org
- ZapSplat (effets gratuits)

**Complexité :** 🟢 Faible (1 jour)

---

### Phase 3 : Fonctionnalités Avancées (Priorité Basse)

#### 3.1 Système de Sauvegarde/Chargement
**Fichiers à créer :**
- `model/SaveGame.java`
- `model/GameSerializer.java`
- `view/gui/LoadGameView.java`
- `controller/gui/LoadGameController.java`

**Fonctionnalités :**
- [ ] Sérialisation du Game
  - [ ] Sauvegarder état complet (grilles, tours, joueurs)
  - [ ] Format JSON avec Gson/Jackson
  - [ ] Sauvegarde automatique chaque tour
- [ ] Interface de chargement
  - [ ] Liste des sauvegardes avec date/heure
  - [ ] Aperçu de la partie (tours, joueurs)
  - [ ] Bouton "Charger"
  - [ ] Bouton "Supprimer"
- [ ] Désérialisation
  - [ ] Reconstruire l'objet Game
  - [ ] Valider l'intégrité
  - [ ] Reprendre la partie au bon tour

**Complexité :** 🟡 Moyenne (2-3 jours)

---

#### 3.2 Écran d'Options
**Fichiers à créer :**
- `view/gui/OptionsView.java`
- `controller/gui/OptionsController.java`
- `model/Settings.java`

**Fonctionnalités :**
- [ ] Paramètres audio
  - [ ] Slider volume musique (0-100%)
  - [ ] Slider volume effets sonores
  - [ ] Checkbox "Muet"
- [ ] Paramètres graphiques
  - [ ] Checkbox "Plein écran"
  - [ ] Dropdown résolution
  - [ ] Checkbox "VSync"
  - [ ] Checkbox "Animations"
- [ ] Paramètres de jeu
  - [ ] Taille de grille par défaut
  - [ ] Checkbox "Confirmation avant tir"
  - [ ] Checkbox "Afficher les probabilités" (IA)
- [ ] Sauvegarde des préférences
  - [ ] Fichier settings.json
  - [ ] Chargement au démarrage

**Complexité :** 🟡 Moyenne (2 jours)

---

#### 3.3 Intelligence Artificielle (Mode Solo)
**Fichiers à créer :**
- `model/ai/AIPlayer.java`
- `model/ai/AIStrategy.java`
- `model/ai/SimpleAI.java` (tirs aléatoires)
- `model/ai/SmartAI.java` (stratégie)

**Fonctionnalités :**
- [ ] IA Simple (aléatoire)
  - [ ] Tir sur case aléatoire non-tirée
  - [ ] Placement aléatoire des bateaux
- [ ] IA Intelligente
  - [ ] Après un touché, tirer autour pour trouver le bateau
  - [ ] Mode "chasse" vs mode "ciblage"
  - [ ] Éviter de tirer là où un bateau ne peut pas être
- [ ] Niveaux de difficulté
  - [ ] Facile (100% aléatoire)
  - [ ] Moyen (stratégie basique)
  - [ ] Difficile (stratégie avancée)
- [ ] Interface de sélection
  - [ ] Bouton "1 Joueur" / "2 Joueurs" dans GameConfigView
  - [ ] Choix du niveau de l'IA

**Complexité :** 🔴 Élevée (4-5 jours)

---

#### 3.4 Mode Multijoueur en Réseau
**Fichiers à créer :**
- `network/GameServer.java`
- `network/GameClient.java`
- `network/NetworkProtocol.java`
- `view/gui/MultiplayerLobbyView.java`

**Fonctionnalités :**
- [ ] Serveur de jeu
  - [ ] Socket server (TCP)
  - [ ] Accepter deux connexions
  - [ ] Synchroniser l'état du jeu
  - [ ] Transmettre les coups
- [ ] Client de jeu
  - [ ] Connexion au serveur (IP + port)
  - [ ] Envoi des actions
  - [ ] Réception des mises à jour
- [ ] Protocole réseau
  - [ ] Messages JSON
  - [ ] Types : ATTACK, SHIP_PLACEMENT, TURN_END, etc.
  - [ ] Gestion des déconnexions
- [ ] Interface lobby
  - [ ] Créer une partie (devenir serveur)
  - [ ] Rejoindre une partie (IP + port)
  - [ ] Attente du deuxième joueur
  - [ ] Chat optionnel

**Complexité :** 🔴 Très Élevée (7-10 jours)

---

#### 3.5 Tutoriel Interactif
**Fichiers à créer :**
- `view/gui/TutorialView.java`
- `controller/gui/TutorialController.java`
- `model/TutorialStep.java`

**Fonctionnalités :**
- [ ] Étapes du tutoriel
  - [ ] Étape 1 : Explication des règles
  - [ ] Étape 2 : Placement guidé d'un bateau
  - [ ] Étape 3 : Effectuer un tir
  - [ ] Étape 4 : Interpréter les résultats
- [ ] UI de guidage
  - [ ] Flèches pointant vers éléments
  - [ ] Bulles de dialogue
  - [ ] Boutons "Suivant" / "Précédent"
  - [ ] Bouton "Passer le tutoriel"
- [ ] Progression
  - [ ] Sauvegarde "Tutoriel terminé"
  - [ ] Proposition au premier lancement

**Complexité :** 🟡 Moyenne (2-3 jours)

---

### Phase 4 : Polish et Optimisation (Priorité Finale)

#### 4.1 Tests d'Interface
**Fichiers à créer :**
- `test/view/gui/MainMenuViewTest.java`
- `test/controller/gui/BattleControllerTest.java`

**Tâches :**
- [ ] Tests unitaires des contrôleurs
  - [ ] Tester la logique de placement
  - [ ] Tester la gestion des attaques
  - [ ] Tester les transitions d'état
- [ ] Tests d'intégration
  - [ ] Scénario complet de partie
  - [ ] Sauvegarde/chargement
- [ ] Tests manuels
  - [ ] Checklist de fonctionnalités
  - [ ] Tests sur différentes résolutions
  - [ ] Tests de performance

**Complexité :** 🟡 Moyenne (2-3 jours)

---

#### 4.2 Optimisation des Performances
**Tâches :**
- [ ] Profiling
  - [ ] Identifier les ralentissements
  - [ ] Mesurer le FPS
- [ ] Optimisations
  - [ ] Pooling d'objets (animations, effets)
  - [ ] Sprite batching
  - [ ] Lazy loading des textures
  - [ ] Caching des renders
- [ ] Memory management
  - [ ] Vérifier les fuites mémoires
  - [ ] Dispose() correct de toutes les ressources

**Complexité :** 🟡 Moyenne (1-2 jours)

---

#### 4.3 Polissage Final
**Tâches :**
- [ ] Cohérence visuelle
  - [ ] Palette de couleurs uniforme
  - [ ] Fonts harmonisés
  - [ ] Tailles et espacements constants
- [ ] Accessibilité
  - [ ] Support clavier (Tab, Enter, Esc)
  - [ ] Tooltips explicatifs
  - [ ] Colorblind mode (optionnel)
- [ ] Feedback utilisateur
  - [ ] Tous les boutons ont un hover
  - [ ] Tous les boutons ont un son de clic
  - [ ] Messages d'erreur clairs
  - [ ] Loading screens si nécessaire
- [ ] Documentation
  - [ ] README avec captures d'écran
  - [ ] Guide de build
  - [ ] Guide utilisateur

**Complexité :** 🟢 Faible (2-3 jours)

---

## Résumé des Tâches par Priorité

### ⚡ Priorité Haute (MVP - Minimum Viable Product)
1. ✅ Écran de configuration (GameConfigView)
2. ✅ Écran de placement (GameSetupView)
3. ✅ Écran de bataille (BattleView)
4. ✅ Écran de fin (GameOverView)

**Estimation totale : 8-13 jours**

### 🎨 Priorité Moyenne (Amélioration de l'expérience)
5. Assets graphiques
6. Animations et effets
7. Sons et musique
8. Système de sauvegarde
9. Écran d'options

**Estimation totale : 8-12 jours**

### 🚀 Priorité Basse (Fonctionnalités avancées)
10. Intelligence artificielle
11. Multijoueur réseau
12. Tutoriel interactif

**Estimation totale : 13-18 jours**

### ✨ Priorité Finale (Polish)
13. Tests d'interface
14. Optimisation
15. Polissage final

**Estimation totale : 5-8 jours**

---

## Ordre de Développement Recommandé

### Semaine 1-2 : Fondations
1. GameConfigView + Controller
2. GameSetupView + Controller (partie affichage grille)
3. GameSetupView (partie placement interactif)

### Semaine 3-4 : Gameplay
4. BattleView + Controller (double grille)
5. BattleView (interactions d'attaque)
6. GameOverView + Controller

### Semaine 5 : Assets et Feedback
7. Création/intégration des sprites
8. Ajout des sons de base
9. Animations simples (explosions, splash)

### Semaine 6 : Fonctionnalités Secondaires
10. Écran d'options
11. Système de sauvegarde basique
12. Polish de l'UI

### Semaine 7+ : Fonctionnalités Avancées (Optionnelles)
13. IA (si mode solo souhaité)
14. Multijoueur (si souhaité)
15. Tests et optimisation finale

---

## Checklist de Validation

Pour chaque écran implémenté, vérifier :

- [ ] Le contrôleur est enregistré dans `ScreenController`
- [ ] L'enum `GuiControllerEnum` est à jour
- [ ] Le contrôleur étend `GuiController`
- [ ] La view implémente `Screen`
- [ ] Tous les assets sont chargés et dispose() est appelé
- [ ] Les transitions vers/depuis cet écran fonctionnent
- [ ] Aucune logique métier dans la View
- [ ] Les entrées utilisateur sont déléguées au Controller
- [ ] Le Model est correctement utilisé
- [ ] Les erreurs sont gérées proprement

---

## Ressources Utiles

### Documentation libGDX
- [Scene2D UI Guide](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d-ui)
- [Skins](https://libgdx.com/wiki/graphics/2d/scene2d/skin)
- [Asset Management](https://libgdx.com/wiki/managing-your-assets)
- [Particle Effects](https://libgdx.com/wiki/tools/2d-particle-editor)

### Assets Gratuits
- [Kenney.nl](https://kenney.nl/) - Sprites et UI
- [OpenGameArt.org](https://opengameart.org/)
- [Freesound.org](https://freesound.org/) - Sons
- [Incompetech.com](https://incompetech.com/) - Musiques

### Outils
- [Aseprite](https://www.aseprite.org/) - Création sprites
- [Tiled](https://www.mapeditor.org/) - Map editor
- [Particle Editor](https://libgdx.com/wiki/tools/2d-particle-editor) (inclus dans libGDX)
- [Skin Composer](https://github.com/raeleus/skin-composer) - Éditeur de skins UI

---

## Conclusion

Le projet ShipBattle dispose d'une **base solide** avec :
- ✅ Architecture MVC propre et claire
- ✅ Logique de jeu complète et testée
- ✅ Système de contrôleurs extensible
- ✅ Framework libGDX bien intégré

L'implémentation de l'interface graphique peut suivre un **développement itératif** :
1. **MVP** : Les 4 écrans de base pour une partie jouable
2. **Amélioration** : Assets, sons, animations
3. **Extension** : IA, multijoueur, tutoriel
4. **Polish** : Tests, optimisation, finitions

En respectant le pattern MVC établi et en suivant ce guide, l'ajout de nouveaux écrans sera **cohérent** et **maintenable**.

Bonne chance pour le développement ! 🚢⚓