# 🚢 ShipBattle - Bataille Navale

Projet de jeu de bataille navale développé en Java dans le cadre d'un projet universitaire.

## 📋 Description

Implémentation complète du jeu classique de bataille navale avec :
- Architecture orientée objet (POO)
- **151 tests unitaires (100% de réussite)** ✅
- Couverture de code avec JaCoCo (objectif : >80%)
- Interface en ligne de commande
- Mode joueur vs joueur
- Système de coordonnées flexible (format lettre ou virgule)

## 🎯 Objectifs pédagogiques

- Respect des principes SOLID
- Tests unitaires exhaustifs avec JUnit 5
- Documentation technique complète
- Gestion d'exceptions métier
- Travail en équipe

## 🛠️ Technologies

- **Java** : 17
- **Build** : Maven 3.9+
- **Tests** : JUnit 5
- **Couverture** : JaCoCo
- **Documentation** : Javadoc

## 📦 Installation

### Prérequis
- JDK 17 ou supérieur
- Maven 3.9+

### Compilation
```bash
mvn clean compile
```

### Exécution des tests
```bash
mvn test
```

### Génération du rapport de couverture
```bash
mvn test jacoco:report
```
Le rapport sera disponible dans `target/site/jacoco/index.html`

### Génération de la Javadoc
```bash
mvn javadoc:javadoc
```
La documentation sera disponible dans `target/site/apidocs/index.html`

### Création du JAR exécutable
```bash
mvn package
```

### Exécution du jeu
```bash
java -jar target/battleship-game-1.0.0-jar-with-dependencies.jar
```
Ou simplement :
```bash
mvn exec:java -Dexec.mainClass="com.battleship.Main"
```

## 🎮 Comment jouer

1. Lancer le programme
2. Entrer les noms des joueurs
3. Définir la taille de la grille (par défaut : 10x10)
4. Placer vos navires sur la grille :
    - **Carrier** (Porte-avions) : 5 cases
    - **Cruiser** (Croiseur) : 4 cases
    - **Destroyer** (Contre-torpilleur) : 3 cases
    - **Torpedo** (Torpilleur) : 2 cases
5. Tour par tour, choisir une coordonnée pour attaquer
6. Le premier joueur à couler tous les navires adverses gagne !

### Format des coordonnées

Le jeu supporte deux formats de saisie :

**Format lettre + nombre :**
- Exemple : `A1`, `B5`, `C10`
- Colonne en lettre (A, B, C...), ligne en nombre (1-10)

**Format virgule :**
- Exemple : `0,0`, `1,4`, `2,9`
- Format X,Y (coordonnées zéro-indexées)

## 📊 Structure du projet

```
src/
├── main/java/com/par_28/ship_battle/
│   ├── model/
│   │   ├── enums/             # Énumérations (GameState, Direction, AttackResult)
│   │   ├── exceptions/        # Exceptions métier personnalisées
│   │   ├── Cell.java          # Cellule de grille
│   │   ├── Coordinate.java    # Système de coordonnées
│   │   ├── Grid.java          # Grille de jeu
│   │   ├── Ship.java          # Classe abstraite des navires
│   │   ├── Carrier.java       # Porte-avions (5 cases)
│   │   ├── Cruiser.java       # Croiseur (4 cases)
│   │   ├── Destroyer.java     # Contre-torpilleur (3 cases)
│   │   ├── Torpedo.java       # Torpilleur (2 cases)
│   │   ├── Player.java        # Joueur
│   │   ├── Game.java          # Logique de partie
│   │   └── AttackResponse.java # Résultat d'attaque
│   ├── controller/            # Contrôleurs
│   └── view/                  # Interface CLI
└── test/java/com/par_28/ship_battle/model/
    ├── CoordinateTest.java    # 43 tests
    ├── ShipTest.java          # 24 tests
    ├── GridTest.java          # 35 tests
    ├── PlayerTest.java        # 26 tests
    └── GameTest.java          # 23 tests
```

## 📈 Métriques

- **Tests unitaires** : 151 tests (100% de réussite) ✅
- **Couverture de code** : Objectif >80% (JaCoCo)
- **Classes du modèle** : 19 classes
- **Exceptions métier** : 3 types (InvalidCoordinateException, ShipPlacementException, InvalidDirectionException)

## ✅ Tests unitaires

Le projet dispose d'une couverture de tests complète avec **151 tests unitaires** :

| Classe de test | Nombre de tests | Couverture                       |
|----------------|-----------------|----------------------------------|
| CoordinateTest | 43 tests        | Parsing, égalité, hashCode       |
| ShipTest       | 24 tests        | Navires, dégâts, destruction     |
| GridTest       | 35 tests        | Placement, validation, attaques  |
| PlayerTest     | 26 tests        | Gestion joueur, flotte, attaques |
| GameTest       | 23 tests        | Logique partie, tours, victoire  |

**Résultat :** `Tests run: 151, Failures: 0, Errors: 0, Skipped: 0` ✅

Pour plus d'informations, consultez le [README des tests](src/test/README.md).

## 📚 Documentation

La documentation technique et les tests sont disponibles :
- **Tests unitaires** : [src/test/README.md](src/test/README.md)
- **Javadoc** : Générer avec `mvn javadoc:javadoc`
- **Rapport JaCoCo** : Générer avec `mvn test jacoco:report`

## 🔄 Commandes Maven utiles
```bash
# Nettoyer le projet
mvn clean

# Compiler
mvn compile

# Exécuter les tests
mvn test

# Vérifier la couverture
mvn verify

# Générer le site complet (Javadoc + rapports)
mvn site

# Package (créer le JAR)
mvn package

# Tout en une fois
mvn clean test package
```

## 📝 Licence

Projet universitaire - 2025