package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

public class Sugar extends GameObject implements ISpawnable{
    private boolean friendly = true;
    private Point mSpawnRange;
    private boolean spawned = false;
    Random random;
    public Sugar(Context context, Point sr, int s){
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

    public boolean spawnTimer(int time, ArrayList<Point> segmentLocations){
        if(time==0 && !spawned){//1/3 possibility in every each 10 sec
            spawn(segmentLocations);
            spawned = true;
            return random.nextInt(3)==0;
        }else if(time==0 && spawned){      //if it's already existed but time to spawn
            spawn(segmentLocations);
            spawned = true;
            return random.nextInt(3)==0;
        }else if(spawned){  //if it's spawned, keep it draws
            return true;
        }

        return false;
    }
    public void spawn(){ resetPosition(); }
    public void spawn(ArrayList<Point> segmentLocations){
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
        spawned = false;
        return mScore += 5;
    }
}
