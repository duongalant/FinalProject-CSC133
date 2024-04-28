package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

public class Mole extends GameObject implements ISpawnable {

    private Point mSpawnRange;


    Random random;

    Mole(Context context, Point sr, int s) {
        random = new Random();
        mSpawnRange = sr;
        mSize = s;
        location.x = -10;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mole);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, s, s, false);
    }

    public void spawn() {
        resetPosition();
    }

    public void spawn(ArrayList<Point> segmentLocations) {
        while (InSnake.checkSpot(segmentLocations, location, -1)) {
            resetPosition();
        }
    }
    public void resetPosition(){
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = 0;
    }

    public void update(int deltaTime, int screenHeight) {
        int speed = 1;
        location.y += speed * deltaTime;

        // Check if the mole has reached the bottom of the screen
        // apparently the bottom of the screen is like 20 pixels from the top
        // this should be int screenHeight but for now its hardcoded as 20
        if (location.y > 20) {
            // Reset mole position to the top
            resetPosition();
        }
    }

    public boolean isOutOfBounds(int screenHeight) {
        return location.y > screenHeight;
    }

    public boolean isFriendly() {
        return false;
    }

    public int penalty(int mScore){
        return mScore -= 1;
    }
}
