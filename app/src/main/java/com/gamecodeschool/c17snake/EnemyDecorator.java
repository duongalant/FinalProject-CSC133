package com.gamecodeschool.c17snake;

import android.graphics.Point;

public class EnemyDecorator extends SnakeDecorator{
    EnemyDecorator(SnakeComponent decoratedSnake) {
        super(decoratedSnake);
    }

    @Override
    public boolean check(Point l, long currentTime) {
        boolean result = decoratedSnake.check(l, currentTime);
        if (result) {
            if (decoratedSnake instanceof Snake && ((Snake) decoratedSnake).segmentLocations.size() > 1
                    && !((Snake) decoratedSnake).isImmune(currentTime)) {
                ((Snake) decoratedSnake).segmentLocations.remove(((Snake) decoratedSnake).segmentLocations.size() - 1);
            } else if (!(((Snake) decoratedSnake).isImmune(currentTime))) {
                ((Snake) decoratedSnake).setDead(true);
            }
        }
        return result;
    }
}
