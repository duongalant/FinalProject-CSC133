package com.gamecodeschool.c17snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;

public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private MediaPlayer bg;
    private int eatSoundId;
    private int crashSoundId;

    private SoundManager(Context context) {
        // Initialize background music
        bg = MediaPlayer.create(context, R.raw.background);
        bg.setLooping(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("get_apple.ogg");
            eatSoundId = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            crashSoundId = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    public void startBackgroundMusic() {
        if (!bg.isPlaying()) {
            bg.start();
        }
    }

    public void stopBackgroundMusic() {
        if (bg.isPlaying()) {
            bg.pause();
        }
    }

    public void release() {
        if (bg != null) {
            bg.release();
            bg = null;
        }
    }
    public void playEatSound() {
        soundPool.play(eatSoundId, 1, 1, 0, 0, 1);
    }

    public void playCrashSound() {
        soundPool.play(crashSoundId, 1, 1, 0, 0, 1);
    }
}