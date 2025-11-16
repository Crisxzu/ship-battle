package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Sound;

public class SoundHandler implements Handler {

    private static Sound[] sounds;
    private static Music[] tracks;

    //Enum for sound identifiers.
    public enum SoundID {
        ERROR(0),
        CANNON_SHOT(1),
        HIT(2),
        MISS(3),
        SUNK(4);
        private int value;
        SoundID(int value) {
            this.value = value;
        }
    }

    //Enum for music identifiers.
    public enum TrackID {
        MENU_THEME(0),
        GAME_THEME(1),;
        private int value;
        TrackID(int value) {
            this.value = value;
        }
    }

    static Music currentTrack;

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
        };
        tracks = new Music[] {
            Gdx.audio.newMusic(Gdx.files.internal("musics/theme_menu.mp3")),
            Gdx.audio.newMusic(Gdx.files.internal("musics/theme_game.mp3")),
        };
    }

    /**
     * Method for playing sounds.
     * @param sound sound identifier (SoundID).
     * @param volume sound volume.
     */
    public static void playSound(SoundID sound, float volume){
        if (sounds != null && sound.value <= sounds.length)
            sounds[sound.value].play(volume);
    }

    /**
     * Method for playing music.
     * @param track sound identifier (SoundID).
     * @param volume sound volume.
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
