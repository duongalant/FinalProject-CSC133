package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.Random;

public class BlackApple extends GameObject implements Object {
    private Point mSpawnRange;
    Random random;

    public BlackApple(Context context, Point sr, int s) {
        random = new Random();
        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an apple
        mSize = s;
        // Hide the apple off-screen until the game starts
        location.x = -10;
        // Load the image to the bitmap
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bapple);
        // Resize the bitmap
        mBitmap = Bitmap.createScaledBitmap(mBitmap, s, s, false);
    }

    @Override
    public void spawn() {
        resetPosition();
    }
    @Override
    public void spawn(ArrayList<Point> segmentLocations) {
        while(InSnake.checkSpot(segmentLocations, location, -1)){
            resetPosition();
        }
    }
    @Override
    public void resetPosition() {
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    @Override
    public int effect(int mScore) {
        return mScore += 1;
    }
}
