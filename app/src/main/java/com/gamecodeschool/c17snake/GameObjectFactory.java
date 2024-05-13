package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Point;

public class GameObjectFactory {
    public static Object createObject(Context context, ObjectType type, Point sr, int s) {
        switch (type) {
            case NORMAL_APPLE:
                return new NormalApple(context, sr, s);
            case COLD_APPLE:
                return new ColdApple(context, sr, s);
            case FAST_APPLE:
                return new FastApple(context, sr, s);
            case ROCK:
                return new Rock(context, sr, s);
            case SUGAR:
                return new Sugar(context, sr, s);
            default:
                throw new IllegalArgumentException("Invalid object type");
        }
    }
    public enum ObjectType {
        NORMAL_APPLE,
        COLD_APPLE,
        FAST_APPLE,
        ROCK,
        SUGAR
    }
}
