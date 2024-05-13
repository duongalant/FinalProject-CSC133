package com.gamecodeschool.c17snake;

import android.content.Context;

public class GameState {
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying;
    private volatile boolean mPaused;
    private volatile boolean gotReset;
    private volatile boolean winner;
    private volatile boolean dead;
    private volatile boolean notInGame;

    GameState() {
        mPlaying = false;
        mPaused = true;
        gotReset = true;
        winner = false;
        dead = false;
        notInGame = true;

    }

    boolean paused() {
        return mPaused;
    }
    boolean notPlaying() {
        return mPlaying;
    }
    void setPlaying() {
        mPlaying = true;
    }
    boolean getnotInGame() {
        return notInGame;
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
    void reachMaxScore() {
        setPauseResetTrue();
        winner = true;
    }
    void snakeDied() {
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
    void notWinnerDead(){
        winner = false;
        dead = false;
    }
}
