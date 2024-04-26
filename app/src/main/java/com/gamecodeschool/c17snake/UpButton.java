package com.gamecodeschool.c17snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class UpButton {
    private int left;
    private int top;
    private int right;
    private int bottom;
    private Rect buttonRect;


    public UpButton(int left, int top, int right, int bottom) {
        buttonRect = new Rect(left, top, right, bottom);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(buttonRect, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(buttonRect.right - 45, buttonRect.top + 20, buttonRect.right - 55, buttonRect.bottom - 15, paint);

        // Draw the triangle
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(buttonRect.left + (buttonRect.width() / 2), buttonRect.top + 1);
        path.lineTo(buttonRect.right - 35, buttonRect.bottom - 60);
        path.lineTo(buttonRect.left + 35, buttonRect.bottom - 60);
        path.close();

        canvas.drawPath(path, paint);
    }


    public boolean contains(float x, float y) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    public boolean buttonRange(MotionEvent motionEvent) {
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();

        return buttonRect.contains(touchX, touchY);
    }
}
