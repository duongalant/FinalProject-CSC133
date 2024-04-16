package com.gamecodeschool.c17snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class LeftButton {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private Rect buttonRect;

    public LeftButton(int left, int top, int right, int bottom) {
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
        canvas.drawRect(buttonRect.left + 30, buttonRect.top + 45, buttonRect.right - 15, buttonRect.bottom - 45, paint);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(buttonRect.left - 1, buttonRect.top + (buttonRect.height() / 2)); // Start at top left corner
        path.lineTo(buttonRect.left + 35, buttonRect.top + 35); // Top right corner
        path.lineTo(buttonRect.left + 35, buttonRect.bottom - 35); // Bottom right corner
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
