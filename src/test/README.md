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

### ✅ Tests complètement fonctionnels

- **CoordinateTest.java** (138 tests)
  - Classe `Coordinate` déjà implémentée
  - Tous les tests peuvent s'exécuter immédiatement
  - Couverture : création, parsing, égalité, hashCode, toString

### ⏳ Tests en attente d'implémentation

Les tests suivants sont **écrits et prêts** mais nécessitent que les classes correspondantes soient implémentées :

- **GameTest.java** (20 tests avec mocks)
  - Attend : classes `Player`, `Ship`, `AttackResponse`, enum `GameState`

- **PlayerTest.java** (16 tests avec mocks)
  - Attend : classes `Grid`, `Ship`, `Cell`, `AttackResponse`, enum `Direction`

- **GridTest.java** (24 tests avec mocks)
  - Attend : classes `Cell`, `Ship`, `AttackResponse`, enums `Direction`, `AttackResult`

- **ShipTest.java** (23 tests avec mocks)
  - Attend : classe abstraite `Ship` et ses sous-classes (`Carrier`, `Cruiser`, `Destroyer`, `Torpedo`)
  - Attend : enum `Direction`

## 🔧 Technologies utilisées

- **JUnit 5** (v5.10.0) - Framework de test
- **Mockito** (v5.5.0) - Framework de mocking
- **Maven Surefire** (v3.1.2) - Exécution des tests
- **JaCoCo** (v0.8.11) - Couverture de code (objectif : 80%)

## 📝 Comment utiliser ces tests

### 1. Tests immédiatement exécutables

Pour exécuter les tests de `Coordinate` qui fonctionnent déjà :

```bash
mvn test -Dtest=CoordinateTest
```

### 2. Une fois les classes manquantes implémentées

Après que vos collègues aient implémenté les classes manquantes, vous pourrez activer progressivement les tests :

#### Pour tester une classe spécifique :

```bash
# Tests de Game (après implémentation de GameState, AttackResponse, etc.)
mvn test -Dtest=GameTest

# Tests de Player (après implémentation de Grid, Ship, Cell, etc.)
mvn test -Dtest=PlayerTest

# Tests de Grid (après implémentation de Cell, Ship, Direction, etc.)
mvn test -Dtest=GridTest

# Tests de Ship (après implémentation de Ship et ses sous-classes)
mvn test -Dtest=ShipTest
```

#### Pour exécuter tous les tests :

```bash
mvn test
```

### 3. Décommenter les tests

Chaque fichier de test contient des sections **commentées** avec `// TODO: Uncomment when [ClassName] is implemented`.

Une fois qu'une classe est implémentée, vous devez :

1. Ouvrir le fichier de test correspondant
2. Rechercher `TODO: Uncomment when`
3. Décommenter le code de test
4. Exécuter les tests

**Exemple dans `GridTest.java` :**

```java
// AVANT (code commenté)
@Test
void shouldInitializeGridWithCorrectDimensions() {
    // When
    // grid = new Grid(10, 10);

    // Then
    // assertEquals(10, grid.getWidth());

    // TODO: Uncomment when Grid class is implemented
    assertTrue(true, "Test requires Grid implementation");
}

// APRÈS (une fois Grid implémentée)
@Test
void shouldInitializeGridWithCorrectDimensions() {
    // When
    grid = new Grid(10, 10);

    // Then
    assertEquals(10, grid.getWidth());
    assertEquals(10, grid.getHeight());
}
```

## 🎯 Classes à implémenter (par priorité)

### Priorité 1 : Enums de base
- `GameState` (SETUP, PLAYER1_TURN, PLAYER2_TURN, GAME_OVER)
- `Direction` (HORIZONTAL, VERTICAL)
- `AttackResult` (MISS, HIT, SUNK, ALREADY_HIT, INVALID)

### Priorité 2 : Modèles de base
- `Cell` (représente une cellule de la grille)
- `Grid` (représente la grille de jeu)
- `AttackResponse` (résultat d'une attaque)

### Priorité 3 : Hiérarchie des navires
- `Ship` (classe abstraite)
- `Carrier` (5 cases) - Porte-avions
- `Cruiser` (4 cases) - Croiseur
- `Destroyer` (3 cases) - Contre-torpilleur (x2 dans une partie)
- `Torpedo` (2 cases) - Torpilleur

## 📊 Couverture de code

Le projet est configuré avec **JaCoCo** pour mesurer la couverture de code avec un objectif de **80%**.

Pour générer le rapport de couverture :

```bash
mvn clean test jacoco:report
```

Le rapport sera disponible dans : `target/site/jacoco/index.html`

## 🐛 Bugs connus à corriger

Dans `Player.java`, ligne 37, il manque l'import :

```java
import java.util.List;
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

### GameTest (20 tests)
- ✅ Initialisation du jeu
- ✅ Démarrage de la partie
- ✅ Gestion des tours
- ✅ Alternance des joueurs
- ✅ Conditions de victoire
- ✅ État de fin de partie

### PlayerTest (16 tests)
- ✅ Initialisation du joueur
- ✅ Ajout de navires
- ✅ Placement de navires
- ✅ État vivant/mort
- ✅ Réception d'attaques
- ✅ Enregistrement des attaques

### GridTest (24 tests)
- ✅ Initialisation de la grille
- ✅ Validation des coordonnées
- ✅ Accès aux cellules
- ✅ Cellules adjacentes
- ✅ Placement de navires (horizontal/vertical)
- ✅ Détection de collisions
- ✅ Gestion des attaques (MISS, HIT, SUNK, ALREADY_HIT)

### ShipTest (23 tests)
- ✅ Initialisation des navires
- ✅ Réception de dégâts
- ✅ Détection de destruction
- ✅ Gestion de la direction
- ✅ Gestion des positions
- ✅ Tests pour chaque type de navire

### CoordinateTest (138 tests) ✅ FONCTIONNELS
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

## 🚦 Commencer à tester

### Étape 1 : Tester Coordinate (fonctionne maintenant !)

```bash
mvn test -Dtest=CoordinateTest
```

Vous devriez voir tous les tests passer en vert ✅

### Étape 2 : Après merge du code de vos collègues

1. Identifiez quelles classes ont été implémentées
2. Ouvrez les fichiers de test correspondants
3. Décommentez les tests (recherchez `TODO: Uncomment`)
4. Exécutez les tests
5. Corrigez les bugs révélés par les tests
6. Répétez jusqu'à ce que tous les tests passent

### Étape 3 : Vérifier la couverture

```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

## 📞 Questions ?

Si vous avez des questions sur les tests ou leur utilisation :
1. Lisez les commentaires dans les fichiers de test
2. Consultez la spécification UML
3. Examinez les tests de `CoordinateTest.java` comme exemple de référence

---

**Note importante** : Ces tests sont des **squelettes** qui documentent le comportement attendu de chaque classe selon la spécification UML. Ils serviront de guide pour vos collègues lors de l'implémentation et permettront de valider que le code répond aux spécifications.