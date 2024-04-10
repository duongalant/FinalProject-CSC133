package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

class Apple extends GameObject implements InSnake {
    // The range of values we can choose from
    // to spawn an apple
    private Point mSpawnRange;
    /// Set up the apple in the constructor
    Apple(Context context, Point sr, int s){

        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an apple
        mSize = s;
        // Hide the apple off-screen until the game starts
        location.x = -10;

        // Load the image to the bitmap
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        // Resize the bitmap
        mBitmap = Bitmap.createScaledBitmap(mBitmap, s, s, false);
    }

    // This is called every time an apple is eaten
    void spawn(){
        // Choose two random values and place the apple
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }
    void spawn(ArrayList<Point> segmentLocations) {
        Random random = new Random();
        int maxAttempts = 100; // Limit the number of attempts
        int attempt = 0;
        boolean appleSpawned = false;

        while (!appleSpawned && attempt < maxAttempts) {
            // Choose two random values and place the apple
            location.x = random.nextInt(mSpawnRange.x) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;

            // Check if the apple spawns in snake's body
            if (!InSnake.checkSpot(segmentLocations, location, -1)) {
                appleSpawned = true;
            }

            attempt++;
        }
        // If the maximum number of attempts is reached and the apple still couldn't spawn,
        // display a message to the user
        if (!appleSpawned) {
            System.out.println("Apple couldn't spawn");
            System.out.println("You Win!");
            // this should be a Canvas.drawText("YOU WIN!!", 1700, 100, mPaint); kind of thing instead of the sysOUT print stuff but i don't wanna bother with it rn lol
        }
    }
    /*
    void spawn(ArrayList<Point> segmentLocations){
        // Choose two random values and place the apple
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;

        if(InSnake.checkSpot(segmentLocations, location, -1)) this.spawn(segmentLocations);
    }
    */
}