package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;

public class Sugar extends GameObject implements ISpawnable{
    private boolean friendly = true;
    private Point mSpawnRange;
    private boolean spawned;
    private long nextSec;
    Random random;
    public Sugar(Context context, Point sr, int s){
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

        random = new Random();
    }

    public void reset(long currentTime){
        spawned = false;
        setNextSpawnTime(currentTime);
        location.x = -10;
    }

    //every certain amount of time, it randomly spawns
    public void checkSpawn(ArrayList<Point> segmentLocations, long currentTime, Canvas mCanvas, Paint mPaint){
        mCanvas.drawText("Sugar: " + nextSec%100000, 20, 330, mPaint);   //for testing   -- sugar's spawn time

        if(spawnTime(currentTime) && !spawned){
            spawn(segmentLocations);
            setNextSpawnTime(currentTime);
            spawned = true;
        }else if(spawnTime(currentTime)){   //when it hits the spawnTime and sugar is already existed
            setNextSpawnTime(currentTime);
        }
    }

    private boolean spawnTime(long currentTime){
        return nextSec == currentTime;
    }
    public void setNextSpawnTime(long currentTime){
        nextSec = currentTime + rand_long(5, 15);        //spawn in min 5 sec, max 15 sec
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
    public boolean isFriendly(){ return  friendly; }

    //when snake eat the sugar
    public int benefit(int mScore, long currentTime){
        location.x = -10;   //move it to outside of the screen
        spawned = false;
        setNextSpawnTime(currentTime);
        return mScore += 5;
    }
}
