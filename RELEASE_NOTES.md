# ShipBattle v1.0.0 - Release Notes

**Date de sortie** : 23 novembre 2025

## Description

Première version stable de ShipBattle, une implémentation moderne du jeu classique de bataille navale développée en Java avec LibGDX.

## Téléchargements

| Plateforme | Fichier | Prérequis |
|------------|---------|-----------|
| Windows 64-bit | `ShipBattle-winX64.zip` | Aucun (JDK 17 inclus) |
| macOS Apple Silicon (M1/M2/M3) | `ShipBattle-macM1.zip` | Aucun (JDK 17 inclus) |
| macOS Intel | `ShipBattle-macX64.zip` | Aucun (JDK 17 inclus) |

### Installation

1. Télécharger l'archive correspondant à votre système
2. Extraire l'archive
3. Lancer l'exécutable :
   - **Windows** : Double-cliquer sur `ShipBattle.exe`
   - **macOS** : Double-cliquer sur `ShipBattle.app`

## Fonctionnalités

### Modes de jeu
- **Joueur vs Joueur** : Deux joueurs humains s'affrontent en local
- **Joueur vs IA** : Affrontez une intelligence artificielle avec 3 niveaux de difficulté
  - **Facile** : Tirs aléatoires
  - **Moyen** : Système HUNT/TARGET intelligent
  - **Difficile** : Algorithme de probabilité avec analyse d'orientation

### Système de combat
- **Attaque normale** : Cible une seule case
- **Bombe** (2 charges) : Attaque en zone 3x3
- **Radar** (3 charges) : Détecte les navires dans une zone 3x3 sans infliger de dégâts

### Flotte
| Navire | Taille | Quantité |
|--------|--------|----------|
| Porte-avion (Carrier) | 5 cases | 1 |
| Croiseur (Cruiser) | 4 cases | 1 |
| Contre-torpilleur (Destroyer) | 3 cases | 2 |
| Torpilleur (Torpedo) | 2 cases | 1 |

### Interface
- Interface graphique 2D moderne
- Placement des navires par glisser-déposer
- Option de placement aléatoire
- Prévisualisation du placement (vert = valide, rouge = invalide)
- Animations de personnages inspirées des animés
- Effets sonores et musiques

### Bonus
- **Code Konami** : Séquence secrète pour recharger tous les pouvoirs
- Références à la pop culture

## Spécifications techniques

- **Langage** : Java 17
- **Framework** : LibGDX
- **UI** : Scene2D, VisUI
- **Architecture** : MVC (Model-View-Controller)
- **Tests** : JUnit 5 avec couverture JaCoCo (99% sur le modèle)

## Documentation

- [Game Design Document](game%20design%20document.md)
- [Diagramme UML - Modèles](docs/uml-model-classes.md)
- [Diagramme UML - Contrôleurs](docs/uml-gui-controllers.md)
- [Diagramme UML - Vues](docs/uml-gui-views.md)
- [Rapport de couverture JaCoCo](docs/coverage/index.html)

## Auteurs

| Nom | GitHub |
|-----|--------|
| Kouassi Chris Emerson | [@Crisxzu](https://github.com/Crisxzu) |
| James Pamara | [@OhBadBoy](https://github.com/OhBadBoy) |
| Daouda Bamba | [@Daoudbamba](https://github.com/Daoudbamba) |

## Crédits assets

- **Navires** : [Naval Battle Assets Pack](https://opengameart.org/content/naval-battle-assets-pack)
- **Radar** : [Animated Radar Assets](https://opengameart.org/content/animated-radar-assets)
- **Explosions** : [Bomb Explosion](https://opengameart.org/content/bomb-explosion)
- **Musiques** : Crimson Skies

---

Projet universitaire Epitech - 2025