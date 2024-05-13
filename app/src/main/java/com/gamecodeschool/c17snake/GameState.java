package com.gamecodeschool.c17snake;

import android.content.Context;

public class GameState {
    private static GameState instance;
    // Game State
    private volatile boolean mPlaying; // Is the game currently playing?
    private volatile boolean mPaused ; // Is the game paused?
    private volatile boolean gotReset; // Flag for game reset
    private volatile boolean winner; // Flag indicating player win
    private volatile boolean dead; // Flag indicating player death
    private volatile boolean notInGame; // Flag indicating not in game

    private GameState() {
        mPlaying = false;
        mPaused = true;
        gotReset = true;
        winner = false;
        dead = false;
        notInGame = true;
    }

    public static GameState getInstance() {
        if(instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    boolean getPaused() {
        return mPaused;
    }
    boolean getNotPlaying() {
        return mPlaying;
    }
    void setPlaying() {
        mPlaying = true;
    }
    void setNotPlaying() {
        mPlaying = false;
    }
    boolean getnotInGame() {
        return notInGame;
    }
    void setNotInGame() {
        notInGame = true;
    }
    void inGame() {
        notInGame = false;
    }
    void setNotPaused() {
        mPaused = false;
    }
    void setPaused() {
        mPaused = true;
    }
    boolean getReset() {
        return gotReset;
    }
    boolean getWinner() {
        return winner;
    }
    boolean getDead() {
        return dead;
    }
    void setReachMax() {
        setPauseResetTrue();
        winner = true;
    }
    void setSnakeDied() {
        setPauseResetTrue();
        dead = true;
    }
    void setPauseResetTrue() {
        mPaused =true;
        gotReset = true;
    }
    void setPauseResetFalse() {
        mPaused =false;
        gotReset = false;
    }
    void notWinnerDead() {
        winner = false;
        dead = false;
    }
}
