package com.gamecodeschool.c17snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class ControlButton {

    private Rect buttonRect;

    public ControlButton(int left, int top, int right, int bottom) {
        buttonRect = new Rect(left, top, right, bottom);

    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(buttonRect, paint);

    }


    public boolean buttonRange(MotionEvent motionEvent) {
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();

        return buttonRect.contains(touchX, touchY);
    }
}

