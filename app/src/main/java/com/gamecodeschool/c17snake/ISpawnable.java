package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

public interface ISpawnable {
    /// Set up the apple in the constructor
    void spawn();
    void spawn(ArrayList<Point> segmentLocations);

    void resetPosition();

    int effect(int mScore);
}
