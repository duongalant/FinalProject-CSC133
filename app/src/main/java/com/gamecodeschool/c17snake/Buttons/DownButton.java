package com.gamecodeschool.c17snake.Buttons;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class DownButton {
    private int left;
    private int top;
    private int right;
    private int bottom;


    private Rect buttonRect;

    public DownButton() {
        int size = 100;

        left = 155;
        top = 1285; // Adjust position as needed
        right = left + size;
        bottom = top + size;

        buttonRect = new Rect(left, top, right, bottom);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(buttonRect, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(buttonRect.right - 45, buttonRect.top + 15, buttonRect.right - 55, buttonRect.bottom - 30, paint);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(buttonRect.left + (buttonRect.width() / 2), buttonRect.bottom - 1);
        path.lineTo(buttonRect.right - 35, buttonRect.top + 60);
        path.lineTo(buttonRect.left + 35, buttonRect.top + 60);
        path.close();

        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    public boolean buttonRange(MotionEvent motionEvent) {
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();

        return buttonRect.contains(touchX, touchY);
    }


    }

