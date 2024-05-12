package com.gamecodeschool.c17snake;

import android.graphics.Point;

import java.util.ArrayList;

public class SugarDecorator extends SnakeDecorator{
    SugarDecorator(SnakeComponent decoratedSnake) {
        super(decoratedSnake);
    }

    public boolean check(Point l, long currentTime) {
        boolean result = decoratedSnake.check(l, currentTime);

        if(result){
            ((Snake) decoratedSnake).segmentLocations.add(new Point(-10, -10));
            ((Snake) decoratedSnake).getImmune(currentTime);
        }
        return result;
    }
}
