package com.gamecodeschool.c17snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class PauseButton {

    private Rect buttonRect;

    public PauseButton(int left, int top, int right, int bottom) {
        buttonRect = new Rect(left, top, right, bottom);
    }

    public void draw(Canvas canvas, Paint paint) {
        // Draw the pause button rectangle
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(buttonRect, paint);

        // Draw the pause symbol
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(buttonRect.left + 20, buttonRect.top + 20, buttonRect.left + 40, buttonRect.bottom - 20, paint);
        canvas.drawRect(buttonRect.right - 40, buttonRect.top + 20, buttonRect.right - 20, buttonRect.bottom - 20, paint);
    }

    /*
    public Rect getButtonRect() {
        return buttonRect;
    }
    */

    public boolean buttonRange(MotionEvent motionEvent){
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();

        return buttonRect.contains(touchX, touchY);
    }
}