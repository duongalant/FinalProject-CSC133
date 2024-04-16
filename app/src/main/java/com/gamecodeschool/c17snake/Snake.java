package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake extends GameObject implements InSnake {

    // The location in the grid of all the segments
    public ArrayList<Point> segmentLocations;

    // How big is each segment of the snake?
    private int mSegmentSize;

    // How big is the entire grid
    private Point mMoveRange;

    // Where is the centre of the screen
    // horizontally in pixels?
    private int halfWayPoint;

    // For tracking movement Heading
    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    // Start by heading to the right
    private Heading heading = Heading.RIGHT;

    // A bitmap for each direction the head can face
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;

    // A bitmap for each direction the head can face
    private ArrayList<Bitmap> mBitmapHeads;
    // A bitmap for the body
    private Bitmap mBitmapBody;

    private UpButton upButton;
    private DownButton downButton;
    private LeftButton leftButton;
    private RightButton rightButton;



    Snake(Context context, Point mr, int ss) {

        // Initialize our ArrayList
        segmentLocations = new ArrayList<>();

        // Initialize the segment size and movement
        // range from the passed in parameters
        mSegmentSize = ss;
        mMoveRange = mr;

        createHead(context, ss);

        // Create and scale the body
        mBitmapBody = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.body);

        mBitmapBody = Bitmap
                .createScaledBitmap(mBitmapBody,
                        ss, ss, false);

        // The halfway point across the screen in pixels
        // Used to detect which side of screen was pressed
        halfWayPoint = mr.x * ss / 2;

        upButton = new UpButton(155, 1040,255,1140);
        downButton = new DownButton(155,1285,255,1385);
        rightButton = new RightButton(265,1160,365,1260);
        leftButton = new LeftButton(50,1160,150,1260);
    }
    private void createHead(Context context, int ss){
        mBitmapHeads = new ArrayList<>();

        // Create and scale the bitmaps
        for(int i = 0; i < 4; i++){
            mBitmapHeads.add(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.head));
        }

        // Modify the bitmaps to face the snake head
        // in the correct direction
        mBitmapHeads.set(0, Bitmap.createScaledBitmap(mBitmapHeads.get(0), ss, ss, false));

        // A matrix for scaling
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);

        int[] rotates = {-90, 180, -1};
        for(int i = 1; i < mBitmapHeads.size(); i++){
            mBitmapHeads.set(i, Bitmap.createBitmap(mBitmapHeads.get(0), 0, 0, ss, ss, matrix, true));

            // A matrix for rotating
            matrix.preRotate(rotates[i-1]);
        }
    }

    // Get the snake ready for a new game
    void reset(int w, int h) {

        // Reset the heading
        heading = Heading.RIGHT;

        // Delete the old contents of the ArrayList
        segmentLocations.clear();

        // Start with a single snake segment
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    void move() {
        // Move the body
        // Start at the back and move it
        // to the position of the segment in front of it
        for (int i = segmentLocations.size() - 1; i > 0; i--) {

            // Make it the same value as the next segment
            // going forwards towards the head
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }
        movingHead();

    }
    private void movingHead() {
        // Move the head in the appropriate heading
        // Get the existing head position
        Point p = segmentLocations.get(0);

        // Move it appropriately
        switch (heading) {
            case UP:
                p.y--;
                break;

            case RIGHT:
                p.x++;
                break;

            case DOWN:
                p.y++;
                break;

            case LEFT:
                p.x--;
                break;
        }
    }

    boolean detectDeath() {
        // Has the snake died?
        boolean dead = false;

        boolean overLeft = segmentLocations.get(0).x <= -1;
        boolean overRight = segmentLocations.get(0).x > mMoveRange.x;
        boolean overTop = segmentLocations.get(0).y <= -1;
        boolean overBottom = segmentLocations.get(0).y > mMoveRange.y;

        // Hit any of the screen edges
        if (overLeft || overRight || overTop || overBottom) {
            dead = true;
        }

        //Eaten itself?
        if(InSnake.checkSpot(segmentLocations, segmentLocations.get(0))) dead = true;

        return dead;
    }

    boolean checkDinner(Point l) {
        //if (snakeXs[0] == l.x && snakeYs[0] == l.y) {
        if (segmentLocations.get(0).x == l.x &&
                segmentLocations.get(0).y == l.y) {

            // Add a new Point to the list
            // located off-screen.
            // This is OK because on the next call to
            // move it will take the position of
            // the segment in front of it
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

        // Don't run this code if ArrayList has nothing in it
        if (!segmentLocations.isEmpty()) {
            // All the code from this method goes here
            // Draw the head
            switch (heading) {
                case RIGHT:
                    draw(canvas, paint, 0);     //right
                    break;

                case LEFT:
                    draw(canvas, paint, 1);     //left
                    break;

                case UP:
                    draw(canvas, paint, 2);     //up
                    break;

                case DOWN:
                    draw(canvas, paint, 3);     //down
                    break;
            }

            // Draw the snake body one block at a time
            for (int i = 1; i < segmentLocations.size(); i++) {
                canvas.drawBitmap(mBitmapBody,
                        segmentLocations.get(i).x
                                * mSegmentSize,
                        segmentLocations.get(i).y
                                * mSegmentSize, paint);
            }
        }
    }
    private void draw(Canvas canvas, Paint paint, int direction){   //0 = right, 1 = left, 2=up, 3=down
        canvas.drawBitmap(mBitmapHeads.get(direction),
                segmentLocations.get(0).x
                        * mSegmentSize,
                segmentLocations.get(0).y
                        * mSegmentSize, paint);
    }


    // Handle changing direction
    void switchHeading(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if (motionEvent.getX() >= halfWayPoint) {
            rotateRight();
        } else {
            // Rotate left
            rotateLeft();
        }

        if (upButton.contains(x, y)) {
            rotateUp();
        } else if (downButton.contains(x, y)) {
            rotateDown();
        } else if (leftButton.contains(x, y)) {
            rotateLeft();
        } else if (rightButton.contains(x, y)) {
            rotateRight();
        }
    }
    private void rotateUp() {
        if (heading != Heading.DOWN) {
            heading = Heading.UP;
        }

    }
    private void rotateDown(){
        if (heading != Heading.UP) {
            heading = Heading.DOWN;
        }

    }
    private void rotateRight() {

        if (heading !=Heading.LEFT){
            heading = Heading.RIGHT;
       }
        switch (heading) {
            // Rotate right
            case UP:
                heading = Heading.RIGHT;
                break;
            case RIGHT:
                heading = Heading.DOWN;
                break;
            case DOWN:
                heading = Heading.LEFT;
                break;
            case LEFT:
                heading = Heading.UP;
                break;

        }
    }
    private void rotateLeft() {
        if (heading !=Heading.RIGHT){
            //heading = Heading.LEFT;
        }
        switch (heading) {
            case UP:
                heading = Heading.LEFT;
                break;
            case LEFT:
                heading = Heading.DOWN;
                break;
            case DOWN:
                heading = Heading.RIGHT;
                break;
            case RIGHT:
                heading = Heading.UP;
                break;
        }
    }
}