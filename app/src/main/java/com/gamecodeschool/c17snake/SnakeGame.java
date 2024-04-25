package com.gamecodeschool.c17snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.core.content.res.ResourcesCompat;
import java.io.IOException;
import java.util.ArrayList;

class SnakeGame extends SurfaceView implements Runnable {
    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private volatile boolean gotReset = true;
    private volatile boolean winner = false;
    private volatile boolean dead = false;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    private int mBlockSize;

    //get the screen range
    private Point mScreenRange;

    // How many points does the player have
    private int mScore;
    private int maxScore = 5;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Typeface mAtariFont;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;
    private Rock mRock;
    private Mole mMole;
    private PauseButton pauseButton;
    private ControlButton controlButton;
    private UpButton upButton;
    private DownButton downButton;
    private RightButton rightButton;
    private LeftButton leftButton;


    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Work out how many pixels each block is
        mBlockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / mBlockSize;

        setSounds(context);
        setObjects(context, size);
    }

    private void setObjects(Context context, Point size){
        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mAtariFont = ResourcesCompat.getFont(getContext(), R.font.atariclassic);

        mScreenRange = new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Call the constructors of our two game objects
        mApple = new Apple(context, mScreenRange, mBlockSize);
        mSnake = new Snake(context, mScreenRange, mBlockSize);
        mRock = new Rock(context, mScreenRange, mBlockSize);
        mMole = new Mole(context, mScreenRange, mBlockSize);

        // Calculate button size and position
        int buttonSize = 100;
        int controlButtonSize = 375;

        int buttonLeft = size.x - buttonSize - 20; // Adjust position as needed
        int buttonTop = 450; // Adjust position as needed
        int buttonRight = buttonLeft + buttonSize;
        int buttonBottom = buttonTop + buttonSize;

        int controlButtonLeft = 20; // Adjust position as needed
        int controlButtonTop = 1025; // Adjust position as needed
        int controlButtonRight = controlButtonLeft + controlButtonSize;
        int controlButtonBottom = controlButtonTop + controlButtonSize;

        int upButtonLeft = 155;
        int upButtonTop = 1040; // Adjust position as needed
        int upButtonRight = upButtonLeft + buttonSize;
        int upButtonBottom = upButtonTop + buttonSize;

        int downButtonLeft = 155;
        int downButtonTop = 1285; // Adjust position as needed
        int downButtonRight = downButtonLeft + buttonSize;
        int downButtonBottom = downButtonTop + buttonSize;

        int rightButtonLeft = 265;
        int rightButtonTop = 1160; // Adjust position as needed
        int rightButtonRight = rightButtonLeft + buttonSize;
        int rightButtonBottom = rightButtonTop + buttonSize;

        int leftButtonLeft = 50;
        int leftButtonTop = 1160; // Adjust position as needed
        int leftButtonRight = leftButtonLeft + buttonSize;
        int leftButtonBottom = leftButtonTop + buttonSize;

        // Create the pause button
        pauseButton = new PauseButton(buttonLeft, buttonTop, buttonRight, buttonBottom);

        // Create the control button
        controlButton = new ControlButton(controlButtonLeft, controlButtonTop, controlButtonRight, controlButtonBottom);
        upButton = new UpButton(upButtonLeft, upButtonTop, upButtonRight, upButtonBottom);
        downButton = new DownButton(downButtonLeft, downButtonTop, downButtonRight, downButtonBottom);
        rightButton = new RightButton(rightButtonLeft, rightButtonTop, rightButtonRight, rightButtonBottom);
        leftButton = new LeftButton(leftButtonLeft, leftButtonTop, leftButtonRight, leftButtonBottom);
    }

    private void setSounds(Context context){
        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }
    }


    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();
        mRock.spawn();
        mMole.spawn();

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }


    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {
        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }
        return false;
    }


    // Update all the game objects
    public void update() {

        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            mApple.spawn(mSnake.segmentLocations);
            mScore = mApple.benefit(mScore);

            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        if(mSnake.checkEnemy(mRock.getLocation())){
            mRock.spawn(mSnake.segmentLocations);
            mScore = mRock.penalty(mScore);
        }
        if(mSnake.checkEnemy(mMole.getLocation())){
            mMole.spawn(mSnake.segmentLocations);
            mScore = mMole.penalty(mScore);

        }

        if(mScore == maxScore){
            mPaused =true;
            gotReset = true;
            winner = true;
        }

        // Did the snake die?
        if (mSnake.detectDeath() || mSnake.detectDeath(mScore)) {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            mPaused =true;
            gotReset = true;
            dead = true;
        }

    }

    /*
    private void checkFriendly(ISpawnable spawnable){
        spawnable.spawn(mSnake.segmentLocations);

        if(spawnable.isFriendly()){
            spawnable.benefit(mScore);

            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }else{
            spawnable.penalty(mScore);
        }
    }
    */


    // Do all the drawing
    public void draw() {    //we can make start page, in-game page
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            mCanvas.drawColor(Color.argb(255, 80, 200, 120));

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);

            mPaint.setTypeface(mAtariFont);

            // Draw the score
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            // Draw the objects
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            mRock.draw(mCanvas, mPaint);
            mMole.draw(mCanvas, mPaint);

            // Draw the control button
            controlButton.draw(mCanvas, mPaint);

            upButton.draw(mCanvas, mPaint);
            downButton.draw(mCanvas, mPaint);
            leftButton.draw(mCanvas, mPaint);
            rightButton.draw(mCanvas, mPaint);
            // Draw the pause button
            pauseButton.draw(mCanvas, mPaint);

            drawText();

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawText(){
        // Draw some text while paused
        if(mPaused){
            // Set the size and color of the mPaint for the text
            //mPaint.setColor(Color.argb(255, 255, 255, 255));  //redundancy

            // Draw our names
            mPaint.setTextSize(50);
            mCanvas.drawText("Alan Duong", 1700, 50, mPaint);
            mCanvas.drawText("Kenny Ahn", 1700, 100, mPaint);
            mCanvas.drawText("Taekjin Jung", 1700, 150, mPaint);
            mCanvas.drawText("David Pham", 1700, 200, mPaint);
            mCanvas.drawText("Nancy Zhu", 1700, 250, mPaint);

            if(!gotReset){
                // Draw pause instruction
                mCanvas.drawText("Click to resume", 1325, 525, mPaint);
            }else if(winner){
                mPaint.setTextSize(120);
                mCanvas.drawText(getResources().getString(R.string.for_winner1),
                        mCanvas.getWidth()/6, mCanvas.getHeight()/3+50, mPaint);
                mCanvas.drawText(getResources().getString(R.string.for_winner2),
                        mCanvas.getWidth()/3, (mCanvas.getWidth()/3)+120, mPaint);
            }else if(dead){
                mPaint.setTextSize(120);
                mCanvas.drawText(getResources().getString(R.string.for_loser),
                        mCanvas.getWidth()/6, mCanvas.getHeight()/3+110, mPaint);
            }else{
                // Draw the message
                // We will give this an international upgrade soon
                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                mPaint.setTextSize(120);
                mCanvas.drawText(getResources().getString(R.string.tap_to_play),
                        100, 800, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                return validTouch(motionEvent);

            default:
                break;

        }
        return true;
    }

    private boolean validTouch(MotionEvent motionEvent){
        if(winner || dead){
            winner = false;
            dead = false;
        }else if (mPaused && gotReset) {  //for new start
            mPaused = false;
            gotReset = false;
            newGame();

            return true;
        }else if(!mPaused && pauseButton.buttonRange(motionEvent)){ //to pause button
            mPaused = true;

        }else if(mPaused && pauseButton.buttonRange(motionEvent)){  //to play button
            mPaused = false;

        }else if(!mPaused){                                     //when the game is playing
            // Let the Snake class handle the input
            mSnake.switchHeading(motionEvent);
        }

        // Don't want to process snake direction for this tap
        return true;
    }


    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }
    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}