package com.gamecodeschool.c17snake;

import android.graphics.Point;

import java.util.ArrayList;

public class AppleDecorator extends SnakeDecorator{
    AppleDecorator(SnakeComponent decoratedSnake) {
        super(decoratedSnake);
    }

    @Override
    public boolean check(Point l, long currentTime) {
        boolean result = decoratedSnake.check(l, currentTime);
        if (result) {
            ((Snake) decoratedSnake).segmentLocations.add(new Point(-10, -10));
        }
        return result;
    }
}
