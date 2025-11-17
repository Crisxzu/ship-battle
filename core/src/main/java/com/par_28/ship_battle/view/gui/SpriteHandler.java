package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.graphics.Texture;

import java.util.Objects;

/**
 * Handler for sprites in the GUI.
 */
public class SpriteHandler implements Handler {
    /**
     * Textures
     */
    private static Texture[] textures;

    /**
     * Sprite identifiers.
     */
    public enum SpriteID {
        /**
         * Background sprite
         */
        BACKGROUND(0, "Background"),

        /**
         * Grid case sprite
         */
        GRID_CASE(1,"GridCase"),

        /**
         * Carrier ship sprite
         */
        CARRIER(2, "Carrier"),

        /**
         * Cruiser ship sprite
         */
        CRUISER(3, "Cruiser"),

        /**
         * Destroyer ship sprite
         */
        DESTROYER(4, "Destroyer"),

        /**
         * Torpedo sprite
         */
        TORPEDO(5, "Torpedo"),

        /**
         * Snipe sprite
         */
        SNIPE(6, "Snipe"),

        /**
         * Miss sprite
         */
        MISS(7, "Miss"),
        
        /**
         * Hit sprite
         */
        HIT(8, "Hit"),

        /**
         * Sunk sprite
         */
        SUNK(9, "Sunk"),

        /**
         * Pause background sprite
         */
        PAUSE_BACKGROUND(10, "PauseBackground"),;

        /**
         * Name of sprite
         */
        private final String name;

        /**
         * Index of sprite
         */
        private final int value;
        
        /**
         * Initialize sprite
         * 
         * @param value index of sprite
         * @param name name of sprite
         */
        SpriteID(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * Initialize handler and load sprites.
     */
    public SpriteHandler() {
        loadContent();
    }

    /**
     * Load textures.
     */
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

    /**
     * Get texture by sprite ID
     * 
     * @param spriteID sprite identifier
     * @return texture
     */
    public static Texture getTexture(SpriteID spriteID) {
        if(textures != null && spriteID.value <= textures.length) {
            return textures[spriteID.value];
        }
        return null;
    }

    /**
     * Get ship texture by name
     * 
     * @param shipName name of ship
     * @return texture
     */
    public static Texture getShipByName(String shipName) {
        for (SpriteID spriteID : SpriteID.values()) {
            if (Objects.equals(spriteID.name, shipName)) {
                return textures[spriteID.value];
            }
        }

        return null;
    }

    /**
     * Dispose resources
     */
    public void dispose() {
        for(Texture texture : textures) {
            texture.dispose();
        }

        System.out.println("Textures content disposed");
    }
}
