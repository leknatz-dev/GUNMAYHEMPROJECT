// core/src/main/java/io/github/leknatz/AudioManager.java
package io.github.leknatz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class AudioManager implements Disposable {
    private static AudioManager instance;

    private final Map<String, Sound> soundMap;
    private final Map<String, Music> musicMap;

    private Music currentMusic;
    private float soundVolume = 1.0f;
    private float musicVolume = 0.1f;
    private boolean soundMuted = false;
    private boolean musicMuted = false;

    private AudioManager() {
        soundMap = new HashMap<>();
        musicMap = new HashMap<>();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // --- Sound FX Management ---

    public void loadSound(String name, String filePath) {
        if (!soundMap.containsKey(name)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundMap.put(name, sound);
        }
    }

    public void playSound(String name) {
        playSound(name, 1.0f, 1.0f, 0f);
    }

    /**
     * Plays a sound effect with pitch variation for dynamic combat audio.
     */
    public void playSoundWithPitchVar(String name, float pitchVariance) {
        float randomPitch = 1.0f + MathUtils.random(-pitchVariance, pitchVariance);
        playSound(name, 1.0f, randomPitch, 0f);
    }

    public void playSound(String name, float volumeMultiplier, float pitch, float pan) {
        if (soundMuted) return;

        Sound sound = soundMap.get(name);
        if (sound != null) {
            float finalVolume = MathUtils.clamp(soundVolume * volumeMultiplier, 0f, 1f);
            sound.play(finalVolume, pitch, pan);
        } else {
            Gdx.app.error("AudioManager", "Sound not loaded: " + name);
        }
    }

    // --- Music Management ---

    public void loadMusic(String name, String filePath) {
        if (!musicMap.containsKey(name)) {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
            musicMap.put(name, music);
        }
    }

    public void playMusic(String name, boolean loop) {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }

        Music music = musicMap.get(name);
        if (music != null) {
            currentMusic = music;
            currentMusic.setLooping(loop);
            currentMusic.setVolume(musicMuted ? 0f : musicVolume);
            currentMusic.play();
        } else {
            Gdx.app.error("AudioManager", "Music not loaded: " + name);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    // --- Volume & Mute Controls ---

    public void setSoundVolume(float volume) {
        this.soundVolume = MathUtils.clamp(volume, 0f, 1f);
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = MathUtils.clamp(volume, 0f, 1f);
        if (currentMusic != null && !musicMuted) {
            currentMusic.setVolume(this.musicVolume);
        }
    }

    public void toggleSoundMute() {
        soundMuted = !soundMuted;
    }

    public void toggleMusicMute() {
        musicMuted = !musicMuted;
        if (currentMusic != null) {
            currentMusic.setVolume(musicMuted ? 0f : musicVolume);
        }
    }

    // --- Resource Cleanup ---

    @Override
    public void dispose() {
        for (Sound sound : soundMap.values()) {
            sound.dispose();
        }
        for (Music music : musicMap.values()) {
            music.dispose();
        }
        soundMap.clear();
        musicMap.clear();
        currentMusic = null;
    }
}