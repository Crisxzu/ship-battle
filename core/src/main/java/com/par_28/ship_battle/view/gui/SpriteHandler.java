package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.graphics.Texture;

import java.util.Objects;

public class SpriteHandler implements Handler {
    private static Texture[] textures;

    public enum SpriteID {
        BACKGROUND(0, "Background"),
        GRID_CASE(1,"GridCase"),
        CARRIER(2, "Carrier"),
        CRUISER(3, "Cruiser"),
        DESTROYER(4, "Destroyer"),
        TORPEDO(5, "Torpedo"),
        SNIPE(6, "Snipe"),
        MISS(7, "Miss"),
        HIT(8, "Hit"),
        SUNK(9, "Sunk"),
        PAUSE_BACKGROUND(10, "PauseBackground"),;

        private final String name;
        private final int value;

        SpriteID(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    public SpriteHandler() {
        loadContent();
    }

    public void loadContent() {
        textures = new Texture[] {
            new Texture("background.png"),
            new Texture("gridCase.png"),
            new Texture("ships/carrier.png"),
            new Texture("ships/cruiser.png"),
            new Texture("ships/destroyer.png"),
            new Texture("ships/torpedo.png"),
            new Texture("snipe.png"),
            new Texture("miss.png"),
            new Texture("hit.png"),
            new Texture("sunk.png"),
            new Texture("pause_background.png"),
        };
    }

    public static Texture getTexture(SpriteID spriteID) {
        if(textures != null && spriteID.value <= textures.length) {
            return textures[spriteID.value];
        }
        return null;
    }

    public static Texture getShipByName(String shipName) {
        for (SpriteID spriteID : SpriteID.values()) {
            if (Objects.equals(spriteID.name, shipName)) {
                return textures[spriteID.value];
            }
        }

        return null;
    }

    public void dispose() {
        for(Texture texture : textures) {
            texture.dispose();
        }

        System.out.println("Textures content disposed");
    }
}
