package com.gamecodeschool.c17snake;

import android.graphics.Point;

import java.util.ArrayList;

public interface Object {
    void spawn();
    void spawn(ArrayList<Point> segmentLocations);
    void resetPosition();
    int effect(int mScore);
}
