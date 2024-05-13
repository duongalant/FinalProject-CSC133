package com.gamecodeschool.c17snake.Buttons;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class LeftButton{
    private int left;
    private int top;
    private int right;
    private int bottom;
    private Rect buttonRect;

    public LeftButton() {
        int size = 100;

        left = 50;
        top = 1160; // Adjust position as needed
        right = left + size;
        bottom = top + size;

        buttonRect = new Rect(left, top, right, bottom);
    }

    public void draw(Canvas canvas, Paint paint) {
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
        paint.setStyle(Paint.Style.STROKE);
    }

    public boolean buttonRange(MotionEvent motionEvent) {
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();

        return buttonRect.contains(touchX, touchY);
    }
}
