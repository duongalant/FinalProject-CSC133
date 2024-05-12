package com.gamecodeschool.c17snake;

import android.graphics.Point;

public class SnakeDecorator implements SnakeComponent{
    protected SnakeComponent decoratedSnake;

    SnakeDecorator(SnakeComponent decoratedSnake) {
        this.decoratedSnake = decoratedSnake;
    }

    @Override
    public boolean check(Point l, long currentTime) {
        return decoratedSnake.check(l, currentTime);
    }
}
