package com.gamecodeschool.c17snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private List<GameObserver> observers = new ArrayList<>();
    // Method to subscribe an observer
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    // Method to notify observers when an event occurs
    protected void notifyAppleEaten() {
        for (GameObserver observer : observers) {
            observer.onAppleEaten();
        }
    }
    protected void notifyObstacleCollision() {
        for (GameObserver observer : observers) {
            observer.onObstacleCollision();
        }
    }
    protected Point location = new Point();

    public int mSize;

    // An image to represent the object
    public Bitmap mBitmap;

    public Point getLocation(){
        return location;
    }

    // Draw the apple
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmap,
                location.x * mSize, location.y * mSize, paint);

    }


}
