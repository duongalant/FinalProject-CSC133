package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;

import com.gamecodeschool.c17snake.Buttons.ControlButton;
import com.gamecodeschool.c17snake.Buttons.ExitButton;
import com.gamecodeschool.c17snake.Buttons.PauseButton;

class SnakeGame extends SurfaceView implements Runnable {
    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    private long frameInSecond;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private volatile boolean gotReset = true;
    private volatile boolean winner = false;
    private volatile boolean dead = false;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    private int mBlockSize;

    //get the screen range
    private Point mScreenRange;

    // How many points does the player have
    private int mScore;
    private int maxScore = 100;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Typeface mAtariFont;
    private Drawable mBackground;

    // Sound
    private SoundManager soundManager;

    // A snake ssss
    private Snake mSnake;
    private boolean gifOn = false;
    // And an apple
    private Apple mApple;
    private Rock mRock;
    private Sugar mSugar;
    private PauseButton pauseButton;
    private ControlButton controlButton;
    private ExitButton exitButton;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);
        // Sound Manager

        // Work out how many pixels each block is
        mBlockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / mBlockSize;

        soundManager = SoundManager.getInstance(context);
        setObjects(context, size);
    }

    private void setObjects(Context context, Point size){
        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mAtariFont = ResourcesCompat.getFont(getContext(), R.font.atariclassic);
        mBackground = context.getResources().getDrawable(R.drawable.grass);

        mScreenRange = new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Call the constructors of our two game objects
        mApple = new Apple(context, mScreenRange, mBlockSize);
        mSnake = new Snake(context, mScreenRange, mBlockSize);
        mRock = new Rock(context, mScreenRange, mBlockSize);
        mSugar = new Sugar(context, mScreenRange, mBlockSize);

        // Calculate button size and position
        int buttonSize = 100;
        int controlButtonSize = 375;

        // Create the control button
        controlButton = new ControlButton(controlButtonSize);

        // Create the pause button
        pauseButton = new PauseButton(size.x, buttonSize);

        //Create the exit button
        exitButton = new ExitButton(context);
    }

    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();
        mRock.spawn();

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
        frameInSecond = mNextFrameTime/1000;

        // reset the sugar
        mSugar.reset(frameInSecond);

        // reset the bg music
        soundManager.restartBackgroundMusic(getContext());
        soundManager.startBackgroundMusic();
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
            frameInSecond = mNextFrameTime/1000;

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

            soundManager.playEatSound();
        }

        if(mSnake.checkSugar(mSugar.getLocation(), frameInSecond)){
            mScore = mSugar.benefit(mScore, frameInSecond);
            gifOn = true;

            soundManager.playSugarSound();
        }

        if(mSnake.checkEnemy(mRock.getLocation(), frameInSecond)){
            mRock.spawn(mSnake.segmentLocations);

            if(!mSnake.isImmune(frameInSecond))
                mScore = mRock.penalty(mScore);

            soundManager.playCrashSound();
        }

        if(mScore == maxScore){
            mPaused =true;
            gotReset = true;
            winner = true;
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            soundManager.playDeathSound();

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

            // Draw the background image
            mBackground.setBounds(0, 0, getWidth(), getHeight());
            mBackground.draw(mCanvas);

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 0, 0, 0));
            mPaint.setTextSize(120);

            mPaint.setTypeface(mAtariFont);

            // Draw the score
            drawingText("" + mScore, 20, 120);

            //mCanvas.drawText("Time: " + frameInSecond%100000, 20, 220, mPaint);    //for testing

            // Draw the objects
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);
            mRock.draw(mCanvas, mPaint);
            mSugar.draw(mCanvas, mPaint);

            // Draw the control button
            controlButton.draw(mCanvas, mPaint);

            //set the snake's look different when it eats sugar item
            if(mSnake.isImmune(frameInSecond) && gifOn){
                mSnake.setGif(getContext());
            }else if (gifOn){   //when snake is back normal from immunity
                mSnake.setNormal(getContext());
                soundManager.startBackgroundMusic();

                gifOn = false;
            }

            mSugar.checkSpawn(mSnake.segmentLocations, frameInSecond, mCanvas, mPaint);

            // Draw the pause button
            pauseButton.draw(mCanvas, mPaint);

            drawText();

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawText(){
        mPaint.setColor(Color.argb(255, 0, 0, 0));
        // Draw some text while paused
        if(mPaused){
            // Set the size and color of the mPaint for the text
            //mPaint.setColor(Color.argb(255, 255, 255, 255));  //redundancy
            // Draw our names
            mPaint.setTextSize(50);
            drawingText("Alan Duong", 1700, 50);
            drawingText("Kenny Ahn", 1700, 100);
            drawingText("Taekjin Jung", 1700, 150);
            drawingText("David Pham", 1700, 200);
            drawingText("Nancy Zhu", 1700, 250);

            if(!gotReset){
                // Draw pause instruction
                drawingText("Click to resume", 1325, 525);
            }else if(winner){
                mPaint.setTextSize(120);
                drawingText(getResources().getString(R.string.for_winner1), mCanvas.getWidth()/6, mCanvas.getHeight()/3+50);
                drawingText(getResources().getString(R.string.for_winner2), mCanvas.getWidth()/3, (mCanvas.getWidth()/3)+120);
                exitButton.draw(mCanvas, mPaint);
            }else if(dead) {
                mPaint.setTextSize(120);
                drawingText(getResources().getString(R.string.for_loser),
                        mCanvas.getWidth() / 6, 400);
                mPaint.setTextSize(60);
                drawingText("Score:" + mScore, mCanvas.getWidth() / 6, 500);
                drawingText("Tap anywhere for new game", mCanvas.getWidth() / 6, 600);
                //draw the menu button
                exitButton.draw(mCanvas, mPaint);
            }else{
                // Draw the message
                // We will give this an international upgrade soon
                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                mPaint.setTextSize(120);
                drawingText(getResources().getString(R.string.tap_to_play), 100, 800);
            }
        }
    }
    //method to reduce duplicated code
    private void drawingText(String text, int x, int y) {
        mCanvas.drawText(text, x, y, mPaint);
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
        if (winner || dead) {
            if (exitButton.buttonRange(motionEvent)) {
                //mPaused = true;
                //go to another screen
            }
            winner = false;
            dead = false;
        }else if (mPaused && gotReset) {  //for new start
            mPaused = false;
            gotReset = false;
            newGame();

            return true;
        }else if(!mPaused && pauseButton.buttonRange(motionEvent)){ //to pause button
            mPaused = true;
            soundManager.stopBackgroundMusic();

        }else if(mPaused && pauseButton.buttonRange(motionEvent)){  //to play button
            mPaused = false;
            mSugar.setNextSpawnTime(frameInSecond);
            soundManager.startBackgroundMusic();

        }else if(!mPaused){                                     //when the game is playing
            // Let the Snake class handle the input
            mSnake.switchHeading(motionEvent, controlButton);
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