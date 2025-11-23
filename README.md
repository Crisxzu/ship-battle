# ShipBattle - Bataille Navale

Projet de jeu de bataille navale développé en Java avec LibGDX dans le cadre d'un projet universitaire.

![Logo](assets/logo.png)

## Description

Implémentation complète du jeu classique de bataille navale avec :
- Interface graphique 2D (LibGDX)
- Mode Joueur vs Joueur
- Mode Joueur vs IA (3 niveaux de difficulté)
- Pouvoirs spéciaux (Bombe, Radar)
- Architecture MVC
- Tests unitaires avec JUnit 5

## Fonctionnalités

### Modes de jeu
- **Joueur vs Joueur** : Deux joueurs humains s'affrontent en local
- **Joueur vs IA** : Affrontez une intelligence artificielle avec 3 niveaux :
  - **Facile** : Tirs aléatoires
  - **Moyen** : Système HUNT/TARGET (cible les cases adjacentes après un tir réussi)
  - **Difficile** : Algorithme de probabilité avec analyse d'orientation des navires

### Système d'attaque
- **Attaque normale** : Cible une seule case
- **Bombe** (2 charges) : Attaque en zone 3x3
- **Radar** (3 charges) : Détecte les navires dans une zone 3x3 sans infliger de dégâts

### Navires
| Navire                        | Taille  | Quantité |
|-------------------------------|---------|----------|
| Porte-avion (Carrier)         | 5 cases | 1        |
| Croiseur (Cruiser)            | 4 cases | 1        |
| Contre-derailleur (Destroyer) | 3 cases | 2        |
| Torpilleur (Torpedo)          | 2 cases | 1        |

### Bonus
- **Code Konami** : Séquence secrète pour recharger tous les pouvoirs
- Animations de personnages inspirées des animés
- Références à la pop culture

## Technologies

- **Java** : 17+
- **Framework** : LibGDX
- **UI** : Scene2D, VisUI
- **Build** : Gradle
- **Tests** : JUnit 5

## Installation

### Prérequis
- JDK 17 ou supérieur
- Gradle (ou utiliser le wrapper inclus)

### Compilation et exécution
```bash
# Compiler le projet
./gradlew build

# Lancer le jeu (version desktop)
./gradlew lwjgl3:run

# Exécuter les tests
./gradlew test
```

### Création du JAR
```bash
./gradlew lwjgl3:jar
```

## Comment jouer

1. Lancer le jeu
2. Choisir le mode de jeu (vs IA ou vs Joueur)
3. Sélectionner la difficulté (si mode IA)
4. Entrer le(s) nom(s) du/des joueur(s)
5. Placer vos navires sur la grille :
   - Cliquer sur un navire dans la liste
   - Cliquer sur la grille pour le placer
   - Utiliser le bouton de rotation pour changer l'orientation
   - Option de placement aléatoire disponible
6. Pendant la partie :
   - Cliquer sur la grille adverse pour attaquer
   - Utiliser les boutons Bombe/Radar pour les pouvoirs spéciaux
7. Le premier à couler tous les navires adverses gagne !

### Contrôles
- **Souris** : Sélection et placement
- **Clavier** : Rotation des navires, Code Konami

## Structure du projet

```
ShipBattle/
├── core/src/main/java/com/par_28/ship_battle/
│   ├── model/
│   │   ├── ai/                # Intelligence artificielle (Easy, Medium, Hard)
│   │   ├── enums/             # Énumérations (GameState, Direction, AttackResult)
│   │   ├── Cell.java          # Cellule de grille
│   │   ├── Coordinate.java    # Système de coordonnées
│   │   ├── Grid.java          # Grille de jeu
│   │   ├── Ship.java          # Classe abstraite des navires
│   │   ├── Player.java        # Joueur
│   │   ├── Game.java          # Logique de partie
│   │   └── AttackResponse.java
│   ├── controller/
│   │   └── gui/               # Contrôleurs GUI
│   └── view/
│       └── gui/               # Vues LibGDX (menus, jeu, etc.)
├── lwjgl3/                    # Module desktop (LWJGL3)
├── assets/                    # Sprites, sons, musiques
└── gdd-assets/                # Assets du Game Design Document
```

## Assets et crédits

Les assets proviennent de sources open source :
- **Navires** : [Naval Battle Assets Pack](https://opengameart.org/content/naval-battle-assets-pack)
- **Radar** : [Animated Radar Assets](https://opengameart.org/content/animated-radar-assets)
- **Explosions** : [Bomb Explosion](https://opengameart.org/content/bomb-explosion)
- **Musiques** : Reprise du jeu **Crimson Skies**

## Documentation

- **Game Design Document** : [game design document.md](game%20design%20document.md)
- **Javadoc** : Générer avec `./gradlew javadoc`

### Diagrammes UML

- **Modèles** : [uml-model-classes.md](docs/uml-model-classes.md) - Classes du domaine (Game, Player, Ship, Grid, AI)
- **Contrôleurs GUI** : [uml-gui-controllers.md](docs/uml-gui-controllers.md) - Architecture MVC côté contrôleurs
- **Vues GUI** : [uml-gui-views.md](docs/uml-gui-views.md) - Interfaces graphiques et handlers

### Couverture de code (JaCoCo)

- **Rapport HTML** : [docs/coverage/index.html](docs/coverage/index.html)

| Package | Couverture Instructions | Couverture Branches |
|---------|------------------------|---------------------|
| `model` | 99% | 97% |
| `model.ai` | 93% | 87% |
| `model.enums` | 100% | n/a |
| `model.exceptions` | 100% | n/a |

> Note : Les packages `view.gui` et `controller.gui` ne sont pas encore testés pour le moment, leur dépendance à libGDX rend la question plus délicate.

Généré via `./gradlew :core:test :core:jacocoTestReport`

## Auteurs

Projet universitaire - 2025

## Licence

Projet universitaire - Usage éducatif
