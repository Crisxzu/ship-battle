# 🚢 Bataille Navale (Battleship)

Projet de jeu de bataille navale développé en Java dans le cadre d'un projet universitaire.

## 📋 Description

Implémentation complète du jeu classique de bataille navale avec :
- Architecture orientée objet (POO)
- Tests unitaires avec JUnit 5
- Couverture de code avec JaCoCo
- Documentation Javadoc
- Mode joueur vs joueur
- Mode joueur vs IA (bonus)

## 🎯 Objectifs pédagogiques

- Respect des principes SOLID
- Architecture MVC
- Tests unitaires (>80% de couverture)
- Documentation technique complète
- Travail en équipe (3 personnes)

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
3. Placer vos navires sur la grille (5 navires par joueur) :
    - Porte-avions (5 cases)
    - Cuirassé (4 cases)
    - Croiseur (3 cases)
    - Sous-marin (3 cases)
    - Destroyer (2 cases)
4. Tour par tour, choisir une coordonnée pour attaquer
5. Le premier joueur à couler tous les navires adverses gagne !

### Format des coordonnées
- Colonne : A à J
- Ligne : 1 à 10
- Exemple : `A5`, `C3`, `J10`

## 📊 Structure du projet
```
src/
├── main/java/com/battleship/
│   ├── model/          # Logique métier
│   ├── controller/     # Contrôleurs
│   ├── view/           # Interface utilisateur
│   └── ai/             # Intelligence artificielle
└── test/java/com/battleship/
    └── model/          # Tests unitaires
```

## 👥 Équipe

- **Personne 1** : Modèle & Logique métier
- **Personne 2** : Joueurs & IA
- **Personne 3** : Contrôleur & Interface

## 📈 Métriques

- **Couverture de tests** : Objectif >80%
- **Classes** : ~20
- **Lignes de code** : ~2000

## 📚 Documentation

La documentation complète (Javadoc) est générée via Maven.
Le GDD (Game Design Document) est disponible dans le dossier `docs/`.

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