package com.gamecodeschool.c17snake.Buttons;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class ControlButton {
    private Rect buttonRect;

    private int left;
    private int right;
    private int top;
    private int bottom;

    private RightButton rB;
    private LeftButton lB;
    private DownButton dB;
    private UpButton uB;

    public ControlButton(int size) {
        left = 20; // Adjust position as needed
        top = 1025; // Adjust position as needed
        right = left + size;
        bottom = top + size;

        buttonRect = new Rect(left, top, right, bottom);

        rB = new RightButton();
        lB = new LeftButton();
        dB = new DownButton();
        uB = new UpButton();
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(buttonRect, paint);

        uB.draw(canvas, paint);
        dB.draw(canvas, paint);
        lB.draw(canvas, paint);
        rB.draw(canvas, paint);
    }


    public char buttonRange(MotionEvent motionEvent) {
        if(uB.buttonRange(motionEvent)){
            return 'u';
        }else if(dB.buttonRange(motionEvent)){
            return 'd';
        }else if(lB.buttonRange(motionEvent)){
            return 'l';
        }else if(rB.buttonRange(motionEvent)){
            return 'r';
        }

        return 'f';     //invalid value
    }
}

