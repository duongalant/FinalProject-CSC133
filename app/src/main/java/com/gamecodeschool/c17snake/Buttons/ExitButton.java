package com.gamecodeschool.c17snake.Buttons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.gamecodeschool.c17snake.R;

public class ExitButton{
    private Bitmap mBitmapexit;
    public ExitButton(Context context) {
        // Load the image to the bitmap
        mBitmapexit = BitmapFactory.decodeResource(context.getResources(), R.drawable.exit);
        // Resize the bitmap
        mBitmapexit = Bitmap.createScaledBitmap(mBitmapexit,
                mBitmapexit.getWidth()/6,mBitmapexit.getHeight()/6, false);
    }
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapexit, 40, 885, paint);
    }

    //checks if player clicking the button
    public boolean buttonRange(MotionEvent motionEvent) {
        float left = 40;
        float right = left + mBitmapexit.getWidth();
        float top = 885;
        float bottom = top + mBitmapexit.getHeight();
        return motionEvent.getX() >= left && motionEvent.getX() <= right && motionEvent.getY() >= top && motionEvent.getY() <= bottom;
    }
}
