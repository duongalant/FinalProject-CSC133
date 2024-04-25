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
    private long mNextFrameTime;
    private int seconds;
    Random random;
    public Sugar(Context context, Point sr, int s){
        random = new Random();

        mNextFrameTime = System.currentTimeMillis();    //timer

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

    //every certain amount of time, it randomly spawns
    public boolean checkSpawn(ArrayList<Point> segmentLocations, Canvas mCanvas, Paint mPaint){
        //1/3 possibility in every each 10 sec
        int num = random.nextInt(2);
        mCanvas.drawText("Testing: " + num, 20, 330, mPaint);

        if(num == 0)
            spawn(segmentLocations);

        return false;   //once execute, make it stop
    }
    public void spawn(){ resetPosition(); }
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

    public int benefit(int mScore){
        location.x = -10;
        return mScore += 5;
    }
}
