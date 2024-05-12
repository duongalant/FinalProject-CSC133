package com.gamecodeschool.c17snake.Buttons;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class RightButton {
    private int left;
    private int top;
    private int right;
    private int bottom;
    private Rect buttonRect;

    public RightButton() {
        int size = 100;

        left = 265;
        top = 1160; // Adjust position as needed
        right = left + size;
        bottom = top + size;

        buttonRect = new Rect(left, top, right, bottom);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(buttonRect, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(buttonRect.bottom - 909, buttonRect.top + 45, buttonRect.left + 15, buttonRect.bottom - 45, paint);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(buttonRect.right + 1, buttonRect.top + (buttonRect.height() / 2)); // Start at top left corner
        path.lineTo(buttonRect.right - 35, buttonRect.top + 35); // Top right corner
        path.lineTo(buttonRect.right - 35, buttonRect.bottom - 35); // Bottom right corner
        path.close();
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    //check if the player is clicking the button
    public boolean buttonRange(MotionEvent motionEvent){
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();

        return buttonRect.contains(touchX, touchY);
    }
}
