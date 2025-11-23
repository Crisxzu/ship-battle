# Tests Unitaires - ShipBattle

## 📋 Vue d'ensemble

Ce répertoire contient les tests unitaires pour le jeu de bataille navale. Les tests ont été créés en suivant la spécification UML et sont structurés pour tester la **logique du jeu** en priorité.

## 🎯 Objectif des tests

Les tests se concentrent sur les composants critiques de la logique métier :

1. **Game.java** - Logique de partie (tours, état, conditions de victoire)
2. **Player.java** - Gestion des joueurs (navires, attaques, état)
3. **Grid.java** - Grille de jeu (placement, validation, attaques)
4. **Ship.java** - Hiérarchie des navires (dégâts, destruction)
5. **Coordinate.java** - Système de coordonnées (parsing, égalité)

## 📁 Structure des tests

```
src/test/java/com/par_28/ship_battle/model/
├── GameTest.java           # Tests de la logique de partie
├── PlayerTest.java         # Tests de gestion des joueurs
├── GridTest.java          # Tests de la grille de jeu
├── ShipTest.java          # Tests des navires
└── CoordinateTest.java    # Tests des coordonnées
```

## 🚀 État actuel

### ✅ Tous les tests sont fonctionnels !

**Total : 151 tests - 100% de réussite**

- **CoordinateTest.java** (43 tests)
  - Tests de création de coordonnées
  - Parsing format virgule (3,5) et lettre (A1, B5, AA10)
  - Tests d'égalité, hashCode, toString
  - Gestion complète des erreurs

- **ShipTest.java** (24 tests)
  - Tests d'initialisation des 4 types de navires (Carrier, Cruiser, Destroyer, Torpedo)
  - Tests de dégâts et destruction
  - Gestion des directions (HORIZONTAL, VERTICAL)
  - Gestion des positions

- **GridTest.java** (35 tests)
  - Initialisation de grille (validation des dimensions)
  - Validation des coordonnées
  - Placement de navires (horizontal/vertical, détection de collision/adjacence)
  - Gestion des attaques (MISS, HIT, SUNK, ALREADY_HIT)

- **PlayerTest.java** (26 tests)
  - Initialisation des joueurs avec grilles
  - Gestion de flotte (ajout, placement de navires)
  - Statut du joueur (vivant/mort selon l'état des navires)
  - Réception et enregistrement des attaques

- **GameTest.java** (23 tests)
  - Initialisation et démarrage de partie
  - Gestion des tours et alternance des joueurs
  - Conditions de victoire et fin de partie
  - Scénarios de jeu complets

### 📊 Résultats des tests

```
Tests run: 151, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 🔧 Technologies utilisées

- **JUnit 5** (v5.10.0) - Framework de test
- **Mockito** (v5.5.0) - Framework de mocking
- **Maven Surefire** (v3.1.2) - Exécution des tests
- **JaCoCo** (v0.8.11) - Couverture de code (objectif : 80%)

## 📝 Comment exécuter les tests

### Exécuter tous les tests

```bash
mvn test
```

### Exécuter une classe de test spécifique

```bash
# Tests de coordonnées
mvn test -Dtest=CoordinateTest

# Tests de navires
mvn test -Dtest=ShipTest

# Tests de grille
mvn test -Dtest=GridTest

# Tests de joueur
mvn test -Dtest=PlayerTest

# Tests de partie
mvn test -Dtest=GameTest
```

### Exécuter les tests avec rapport de couverture

```bash
mvn clean test jacoco:report
```

Le rapport sera disponible dans `target/site/jacoco/index.html`

## 📊 Couverture de code

Le projet est configuré avec **JaCoCo** pour mesurer la couverture de code avec un objectif de **80%**.

Pour générer et visualiser le rapport de couverture :

```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

## 📖 Structure des tests

Chaque classe de test utilise une structure **Nested** pour une meilleure organisation :

```java
@DisplayName("Game Logic Tests")
class GameTest {

    @Nested
    @DisplayName("Game Initialization Tests")
    class InitializationTests {
        // Tests d'initialisation
    }

    @Nested
    @DisplayName("Turn Management Tests")
    class TurnManagementTests {
        // Tests de gestion des tours
    }

    // etc.
}
```

Cette structure offre :
- Une meilleure lisibilité dans les rapports de test
- Un regroupement logique par fonctionnalité
- Une exécution sélective des tests par groupe

## 🔍 Détail des scénarios testés

### GameTest (23 tests)
- ✅ Initialisation du jeu
- ✅ Démarrage de la partie
- ✅ Gestion des tours
- ✅ Alternance des joueurs
- ✅ Conditions de victoire
- ✅ État de fin de partie

### PlayerTest (26 tests)
- ✅ Initialisation du joueur
- ✅ Ajout de navires
- ✅ Placement de navires
- ✅ État vivant/mort
- ✅ Réception d'attaques
- ✅ Enregistrement des attaques

### GridTest (35 tests)
- ✅ Initialisation de la grille
- ✅ Validation des coordonnées
- ✅ Accès aux cellules
- ✅ Cellules adjacentes
- ✅ Placement de navires (horizontal/vertical)
- ✅ Détection de collisions
- ✅ Gestion des attaques (MISS, HIT, SUNK, ALREADY_HIT)

### ShipTest (24 tests)
- ✅ Initialisation des navires
- ✅ Réception de dégâts
- ✅ Détection de destruction
- ✅ Gestion de la direction
- ✅ Gestion des positions
- ✅ Tests pour chaque type de navire

### CoordinateTest (43 tests)
- ✅ Création de coordonnées
- ✅ Parsing format virgule (3,5)
- ✅ Parsing format lettre (A1, B5, AA10)
- ✅ Gestion des erreurs
- ✅ Égalité (equals)
- ✅ HashCode
- ✅ ToString
- ✅ Round-trip (parsing/toString)

## 🎓 Bonnes pratiques suivies

1. **Given-When-Then** : Structure claire des tests
2. **Naming** : Noms de tests descriptifs avec `@DisplayName`
3. **Mocking** : Utilisation de Mockito pour les dépendances
4. **Isolation** : Chaque test est indépendant
5. **Coverage** : Tests de cas nominaux et cas d'erreur
6. **Documentation** : Commentaires explicites dans le code

## 🚦 Démarrage rapide

### Exécuter tous les tests

```bash
mvn test
```

Résultat attendu : **151 tests passent avec succès** ✅

### Vérifier la couverture de code

```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```
