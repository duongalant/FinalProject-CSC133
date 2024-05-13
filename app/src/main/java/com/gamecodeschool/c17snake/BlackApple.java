package com.gamecodeschool.c17snake;

import android.graphics.Point;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Random;

public class BlackApple extends GameObject implements ISpawnable {
    private boolean friendly = true;
    private int currentIndex = 0;
    private int nextScore = 2;
    private ISpawnable apple;
    private Point mSpawnRange;

    Random random;

    public BlackApple(ISpawnable apple, Context context, Point sr, int s) {
        super();
        random = new Random();
        this.apple = apple;
        mSpawnRange = sr;
        mSize = s;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bapple);
        // Make a note of the size of an apple

        // Hide the apple off-screen until the game starts
        location.x = -10;
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
    public boolean isFriendly() {
        return false;
    }

    @Override
    public int benefit(int mScore) {
        return mScore += 1;

    }

    public void reset(){
        currentIndex = 0;
    }


    public int getIndex(){
        return currentIndex;
    }
}