package com.gamecodeschool.c17snake;

import android.graphics.Point;
import java.util.ArrayList;
//checks to make sure object is not colliding with snake
public class InSnake {
    public static boolean checkSpot(ArrayList<Point> segmentLocations, Point objLocation) {
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            // Have any of the sections collided
            if (objLocation.x == segmentLocations.get(i).x &&
                    objLocation.y == segmentLocations.get(i).y) {

                return true;
            }
        }
        return false;
    }

    public static boolean checkSpot(ArrayList<Point> segmentLocations, Point objLocation,int c) {
        for (int i = segmentLocations.size() - 1; i > c; i--) {
            // Have any of the sections collided
            if (objLocation.x == segmentLocations.get(i).x &&
                    objLocation.y == segmentLocations.get(i).y) {

                return true;
            }
        }
        return false;
    }
}