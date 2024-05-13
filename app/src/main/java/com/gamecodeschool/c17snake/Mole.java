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
    private ArrayList<Bitmap> mBitmapHeads;
    public ArrayList<Point> segmentLocations;
    private int mSegmentSize;
    private int moleHead;
    private Point mSpawnRange;
    Random random;

    Mole(Context context, Point sr, int s) {
        random = new Random();
        mSpawnRange = sr;
        mSize = s;
        location.x = -10;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mole);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, s, s, false);
        createHead(context, s);
    }
    private void createHead(Context context, int ss) {
        mBitmapHeads = new ArrayList<>();
        moleHead = R.drawable.mole;

        // Create and scale the bitmaps
        for (int i = 0; i < 4; i++) {
            mBitmapHeads.add(BitmapFactory.decodeResource(context.getResources(),
                    moleHead));
        }

        // Modify the bitmaps to face the mole head
        // in the correct direction
        mBitmapHeads.set(0, Bitmap.createScaledBitmap(mBitmapHeads.get(0), ss, ss, false));


        // A matrix for rotating
        for (int i = 1; i < mBitmapHeads.size(); i++) {
            mBitmapHeads.set(i, rotateBitmap(mBitmapHeads.get(0), 90 * i));
        }
    }
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
    private void draw(Canvas canvas, Paint paint, int direction) {   //0 = right, 1 = left, 2=up, 3=down
        canvas.drawBitmap(mBitmapHeads.get(direction),
                segmentLocations.get(0).x

                *mSegmentSize,
                segmentLocations.get(0).y
                *mSegmentSize, paint);}


    public boolean isOutOfBounds(int screenHeight) {
        return location.y > screenHeight;
    }

    public boolean isFriendly() {
        return false;
    }

    @Override
    public int benefit(int mScore) {
        return 0;
    }

    public int penalty(int mScore){
        return mScore -= 1;
    }
}