package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

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
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    @Override
    public boolean isFriendly() {
        return false;
    }

    public int penalty(int mScore){

        return mScore -= 1;
    }
}
