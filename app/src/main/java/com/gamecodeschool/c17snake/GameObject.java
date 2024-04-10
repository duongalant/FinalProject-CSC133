package com.gamecodeschool.c17snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class GameObject {
    public Point location = new Point();

    public int mSize;

    // An image to represent the object
    public Bitmap mBitmap;

    Point getLocation(){
        return location;
    }

    // Draw the apple
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmap,
                location.x * mSize, location.y * mSize, paint);

    }
}
