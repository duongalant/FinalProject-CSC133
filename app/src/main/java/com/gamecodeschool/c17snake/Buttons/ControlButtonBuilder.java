package com.gamecodeschool.c17snake.Buttons;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public interface ControlButtonBuilder {
    public void draw(Canvas canvas, Paint paint);

    public char buttonRange(MotionEvent motionEvent);
}
