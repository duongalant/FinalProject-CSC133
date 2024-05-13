package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.Random;

public class Sugar extends GameObject implements Object {
    private Point mSpawnRange;
    Random random;
    private boolean spawned;
    private long nextSec;

    public Sugar(Context context, Point sr, int s) {
        random = new Random();
        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an apple
        mSize = s;
        // Hide the apple off-screen until the game starts
        location.x = -10;
        // Load the image to the bitmap
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sugar);
        // Resize the bitmap
        mBitmap = Bitmap.createScaledBitmap(mBitmap, s, s, false);
    }

    public void reset(long currentTime) {
        spawned = false;
        setNextSpawnTime(currentTime);
        location.x = -10;
    }

    // After a certain amount of time, it spawns
    public void checkSpawn(ArrayList<Point> segmentLocations, long currentTime) {

        if(spawnTime(currentTime) && !spawned) {
            spawn(segmentLocations);
            setNextSpawnTime(currentTime);
            spawned = true;
        } else if(spawnTime(currentTime)) { // If sugar exists, it skips to next time cycle
            setNextSpawnTime(currentTime);
        }
    }

    private boolean spawnTime(long currentTime){
        return nextSec == currentTime;
    }
    public void setNextSpawnTime(long currentTime){
        nextSec = currentTime + rand_long(5, 15); // Spawn in min 5 sec, max 15 secs
    }
    public long rand_long(long min, long max) {
        return (long) ((Math.random() * (max - min)) + min);
    }

    public void spawn(){
        resetPosition();
    }
    public void spawn(ArrayList<Point> segmentLocations){
        spawn();
        while(InSnake.checkSpot(segmentLocations, location, -1)){
            resetPosition();
        }
    }

    public void resetPosition(){
        // Choose two random values and place the apple
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    @Override
    public int effect(int mScore) {
        return 0;
    }

    // When snake eat the sugar
    public int effect(int mScore, long currentTime){
        location.x = -10;   // Move it to outside of the screen
        spawned = false;
        setNextSpawnTime(currentTime);
        return mScore += 5;
    }
}
