package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Sound;

/**
 * Handler for sound and music in the GUI.
 */
public class SoundHandler implements Handler {
    /**
     * Sounds.
     */
    private static Sound[] sounds;
    /**
     * Music tracks.
     */
    private static Music[] tracks;

    /**
     * Sound identifiers.
     */
    public enum SoundID {
        /**
         * Error sound
         */
        ERROR(0),

        /**
         * Cannon shot sound
         */
        CANNON_SHOT(1),
        /**
         * Hit sound
         */
        HIT(2),

        /**
         * Miss sound
         */
        MISS(3),

        /**
         * Sunk sound
         */
        SUNK(4),

        /**
         * Already hit sound
         */
        ALREADY_HIT(5),

        /**
         * Radar found nothing sound
         */
        RADAR_NOTHING(6),

        /**
         * Radar found ship sound
         */
        RADAR_FOUND(7),

        /**
         * Bomb shot sound
         */
        BOMB_SHOT(8),

        /**
         * Cheat code sound
         */
        CHEAT_CODE(9);

        /**
         * Index of sound
         */
        private int value;

        /**
         * Initialize sound
         *
         * @param value sound index
         */
        SoundID(int value) {
            this.value = value;
        }
    }

    /**
     * Music track identifiers.
     */
    public enum TrackID {
        /**
         * Menu theme track
         */
        MENU_THEME(0),
        /**
         * Game theme track
         */
        GAME_THEME(1),;

        /**
         * Index of track
         */
        private int value;

        /**
         * Initialize track
         *
         * @param value track index
         */
        TrackID(int value) {
            this.value = value;
        }
    }

    /**
     * Current playing track
     */
    static Music currentTrack;

    /**
     * Initialize handler and load sounds/music.
     */
    public SoundHandler() {
        loadContent();
    }

    /**
     * Loads all sounds and music.
     */
    public void loadContent() {
        sounds = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/wrong.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/cannon_shot.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/hit.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/miss.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/sunk.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/already_hit.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/radar_nothing.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/radar_found.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/bomb_shot.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/cheat_code.mp3")),
        };
        tracks = new Music[] {
            Gdx.audio.newMusic(Gdx.files.internal("musics/theme_menu.mp3")),
            Gdx.audio.newMusic(Gdx.files.internal("musics/theme_game.mp3")),
        };
    }

    /**
     * Play a sound.
     *
     * @param sound sound identifier (SoundID).
     * @param volume sound volume.
     */
    public static void playSound(SoundID sound, float volume){
        if (sounds != null && sound.value <= sounds.length)
            sounds[sound.value].play(volume);
    }

    /**
     * Play a music track.
     *
     * @param track track identifier (TrackID).
     * @param volume track volume.
     * @param loop states if the track should be looping.
     */
    public static void playTrack(TrackID track, float volume, boolean loop){
        if (tracks != null && track.value <= tracks.length) {
            if(currentTrack != null){
                currentTrack.stop();
            }

            tracks[track.value].setVolume(volume);
            tracks[track.value].setLooping(loop);
            tracks[track.value].play();
            currentTrack = tracks[track.value];
        }
    }

    /**
     * Disposes all sounds and music.
     */
    public void dispose() {
        for (Sound sound : sounds)
            sound.dispose();
        if(tracks != null) {
            for (Music track : tracks)
                track.dispose();
        }


        System.out.println("SoundHandler content disposed");
    }
}
