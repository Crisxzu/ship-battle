# ShipBattle UI Skin

Skin personnalisé pour le jeu ShipBattle avec un thème naval/maritime.

## 📁 Fichiers

- `shipbattle-skin.json` - Définitions des styles UI
- `shipbattle-skin.atlas` - Atlas de textures
- `shipbattle-skin.png` - Image de textures
- `font-export.fnt/png` - Font par défaut
- `font-title-export.fnt/png` - Font pour les titres

## 🎨 Palette de Couleurs

### Couleurs Navales
- **navy-dark** (RGB: 23, 38, 64) - Fond sombre
- **navy** (RGB: 38, 64, 102) - Boutons normaux
- **navy-light** (RGB: 64, 89, 128) - Hover
- **ocean** (RGB: 51, 102, 153) - Accent principal
- **ocean-light** (RGB: 77, 128, 179) - Accent clair

### Couleurs Fonctionnelles
- **gold** (RGB: 217, 166, 33) - Highlights, titres
- **red** (RGB: 230, 51, 51) - Erreurs, danger
- **green** (RGB: 51, 204, 51) - Succès
- **gray** (RGB: 128, 128, 128) - Désactivé
- **white** (RGB: 255, 255, 255) - Texte principal

## 🎯 Utilisation

### Charger le Skin

```java
Skin skin = new Skin(Gdx.files.internal("ui/shipbattle-skin.json"));
```

### Styles Disponibles

#### Boutons (TextButton)
```java
// Bouton par défaut (bleu navy)
TextButton button = new TextButton("Texte", skin);

// Bouton principal (même style que default)
TextButton primaryBtn = new TextButton("Texte", skin, "primary");

// Bouton danger (hover rouge)
TextButton dangerBtn = new TextButton("Quitter", skin, "danger");
```

#### Labels
```java
// Label normal (blanc)
Label label = new Label("Texte", skin);

// Titre (or, plus gros)
Label title = new Label("Titre", skin, "title");

// Sous-titre (bleu clair)
Label subtitle = new Label("Sous-titre", skin, "subtitle");

// Erreur (rouge)
Label error = new Label("Erreur!", skin, "error");

// Succès (vert)
Label success = new Label("Réussi!", skin, "success");
```

#### TextField (Champs de texte)
```java
TextField textField = new TextField("", skin);
textField.setMessageText("Entrez du texte...");
```

#### CheckBox
```java
CheckBox checkbox = new CheckBox(" Option activée", skin);
```

#### Slider (Curseurs)
```java
Slider slider = new Slider(0, 100, 1, false, skin, "default-horizontal");
```

#### ProgressBar
```java
ProgressBar progressBar = new ProgressBar(0, 100, 1, false, skin, "default-horizontal");
```

#### Window/Dialog
```java
// Fenêtre normale
Window window = new Window("Titre", skin);

// Dialog
Dialog dialog = new Dialog("Titre", skin, "dialog");
```

#### List/SelectBox
```java
// Liste
List<String> list = new List<>(skin);

// Menu déroulant
SelectBox<String> selectBox = new SelectBox<>(skin);
```

## 🛠️ Personnalisation

Pour modifier les couleurs, éditez `shipbattle-skin.json`:

```json
"com.badlogic.gdx.graphics.Color": {
  "navy": {
    "r": 0.15,  // Rouge (0-1)
    "g": 0.25,  // Vert (0-1)
    "b": 0.4,   // Bleu (0-1)
    "a": 1      // Alpha (0-1)
  }
}
```

## 📦 Ajouter de Nouvelles Textures

1. Éditez `shipbattle-skin.atlas` pour ajouter une région
2. Mettez à jour `shipbattle-skin.png` avec la nouvelle texture
3. Référencez-la dans `shipbattle-skin.json`

## 🎮 Exemple Complet

```java
public class MenuScreen implements Screen {
    private Stage stage;
    private Skin skin;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Charger le skin
        skin = new Skin(Gdx.files.internal("ui/shipbattle-skin.json"));

        // Créer UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Titre
        Label title = new Label("ShipBattle", skin, "title");
        table.add(title).padBottom(50).row();

        // Bouton
        TextButton button = new TextButton("Jouer", skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Action
            }
        });
        table.add(button).width(300).height(60);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
```

## 🔧 Notes Techniques

- Le skin utilise un pixel blanc qui est teint avec les couleurs définies
- Les fonts sont générées via la font par défaut de libGDX
- Pour de vraies fonts personnalisées, utilisez Hiero ou gdx-freetype
- L'atlas utilise un format RGBA8888 pour supporter la transparence

## 🚀 Prochaines Étapes

Pour améliorer le skin:
1. Ajouter de vraies textures (bordures, coins arrondis)
2. Créer des fonts personnalisées avec Hiero
3. Ajouter des icônes (navires, cibles, etc.)
4. Créer des animations (particules, vagues)
