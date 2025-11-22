package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
     * Animations
     */
    private static Animation<TextureRegion>[] animations;

    /**
     * Sprite identifiers.
     */
    public enum TextureID {
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
        PAUSE_BACKGROUND(10, "PauseBackground"),

        /**
         * Game logo sprite
         */
        LOGO(11, "Logo"),
        
        /**
         * Radar icon sprite
         */
        RADAR(12, "Radar"),

        /**
         * Bomb icon sprite
         */
        BOMB(13, "Bomb");

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
        TextureID(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * Animation identifiers.
     */
    public enum AnimationID {
        /**
         * Loli talking animation
         */
        LOLI(0, "loli_talking.gif"),
        /**
         * Radar found nothing animation
         */
        RADAR_NOTHING(1, "radar_nothing.gif"),
        
        /**
         * Radar found ship animation
         */
        RADAR_FOUND(2, "radar_found.gif"),;

        /**
         * Index of animation
         */
        private int value;

        /**
         * Name of animation file
         */
        private String name;

        /**
         * Initialize animation
         * 
         * @param value index of animation
         * @param name name of animation file
         */
        AnimationID(int value, String name) {
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
            new Texture("logo.png"),
            new Texture("radar.png"),
            new Texture("bomb.png"),
        };

        animations = new Animation[AnimationID.values().length];

        for (int i = 0; i < AnimationID.values().length; i++) {
            animations[i] = GifDecoder.loadGIFAnimation(
                Animation.PlayMode.NORMAL,
                Gdx.files.internal(AnimationID.values()[i].name).read()
            );
        }
    }

    /**
     * Get texture by sprite ID
     *
     * @param textureID sprite identifier
     * @return texture
     */
    public static Texture getTexture(TextureID textureID) {
        if(textures != null && textureID.value <= textures.length) {
            return textures[textureID.value];
        }
        return null;
    }

    /**
     * Get animation by animation ID
     * 
     * @param animationID animation identifier
     * @return animation
     */
    public static Animation<TextureRegion> getAnimation(AnimationID animationID) {
        if(animations != null && animationID.value <= animations.length) {
            return animations[animationID.value];
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
        for (TextureID textureID : TextureID.values()) {
            if (Objects.equals(textureID.name, shipName)) {
                return textures[textureID.value];
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
