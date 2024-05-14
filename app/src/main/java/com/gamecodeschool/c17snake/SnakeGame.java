package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.core.content.res.ResourcesCompat;
import android.graphics.drawable.Drawable;
import com.gamecodeschool.c17snake.Buttons.ControlButton;
import com.gamecodeschool.c17snake.Buttons.ExitButton;
import com.gamecodeschool.c17snake.Buttons.PauseButton;

class SnakeGame extends SurfaceView implements Runnable {
    // Game Loop/Thread Management
    private Thread mThread = null;
    private long mNextFrameTime;
    private long frameInSecond;
    private GameState gameState;

    // Playable Area Settings
    private static final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    private int mBlockSize;

    // Screen Range
    private Point mScreenRange;

    // Player Score
    private int mScore;
    private static int MAX_SCORE = 100;

    // Drawing Objects
    private Draw draw;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Typeface mAtariFont;
    private Drawable mBackground;

    // Sound
    private SoundManager soundManager;

    // Game Objects
    private Snake mSnake;
    private boolean gifOn = false;
    private NormalApple mNormalApple;
    private ColdApple mColdApple;
    private FastApple mFastApple;
    private BlackApple mBlackApple;
    private Rock mRock;
    private Rock[] rocks = new Rock[103];
    private Sugar mSugar;
    private PauseButton pauseButton;
    private ControlButton controlButton;
    private ExitButton exitButton;

    // Snake Speed Constants
    private final int NORMAL_SPEED = 1; // Normal speed constant
    private final int FAST_SPEED = 2; // Fast speed constant

    // Snake Speed and State
    private int mSnakeSpeed = NORMAL_SPEED; // Current snake speed
    private boolean mIsSlowed = false; // Flag indicating if snake is slowed
    private boolean mIsFast = false; // Flag indicating if snake is fast
    private long mSlowCoolDown = 0; // Start time for cooldown
    private long mFastCoolDown = 0;
    private long mBlackAppleCooldownStartTime = 0;
    boolean mIsBlackAppled;

    // This is the constructor method that gets called from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);
        // Work out how many pixels each block is
        mBlockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / mBlockSize;
        // Sound Manager
        soundManager = SoundManager.getInstance(context);
        // Game State
        gameState = GameState.getInstance();
        // Setting Game Environment
        setObjects(context);
        setGameObjects(context);
        setButtons(context, size);
    }

    private void setObjects(Context context){
        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mAtariFont = ResourcesCompat.getFont(getContext(), R.font.atariclassic);
        mBackground = context.getResources().getDrawable(R.drawable.grass);
        mScreenRange = new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh);
    }

    private void setGameObjects(Context context) {
        // Call the constructors of our game objects
        mSnake = new Snake(context, mScreenRange, mBlockSize);
        mNormalApple = (NormalApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.NORMAL_APPLE, mScreenRange, mBlockSize);
        mColdApple = (ColdApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.COLD_APPLE, mScreenRange, mBlockSize);
        mFastApple = (FastApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.FAST_APPLE, mScreenRange, mBlockSize);
        mBlackApple = (BlackApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.BLACK_APPLE, mScreenRange, mBlockSize);
        mRock = (Rock) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.ROCK, mScreenRange, mBlockSize);
        for(int i = 0; i < rocks.length; i++) {
            rocks[i] = new Rock(context, mScreenRange, mBlockSize);
        }
        mSugar = (Sugar) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.SUGAR, mScreenRange, mBlockSize);
    }

    private void setButtons(Context context, Point size) {
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
        mRock.reset();
        mNormalApple.spawn();
        mColdApple.spawn();
        mFastApple.spawn();
        mBlackApple.spawn();
        mRock.spawn();
        for (int i = 1; i < rocks.length; i++) {
            rocks[i].location.x = -10;
        }

        // Reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Reset the mScore
        mScore = 0;

        // Reset other Triggers
        mIsSlowed = false;
        mIsFast = false;
        mIsBlackAppled = false;
        mSnakeSpeed = NORMAL_SPEED;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
        frameInSecond = mNextFrameTime/1000;

        // Reset the sugar
        mSugar.reset(frameInSecond);

        // Reset the background music
        soundManager.restartBackgroundMusic(getContext());
        soundManager.startBackgroundMusic();
    }

    // Handles the game loop
    @Override
    public void run() {
        while (gameState.getNotPlaying()) {
            if (!gameState.getPaused()) {
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
        if (mNextFrameTime <= System.currentTimeMillis()) {
            // Tenth of a second has passed
            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;
            frameInSecond = mNextFrameTime/1000;
            // Return true so that the update and draw methods are executed
            return true;
        }
        return false;
    }

    // Update all the game objects
    public void update() {

        // Move the snake
        for (int i = 0; i < mSnakeSpeed; i++) {
            if(mIsSlowed && mNextFrameTime/100 % 2 == 0) {

            }else{
                mSnake.move();
            }
        }
        if (mIsSlowed) {
            // Check if the cool down period has elapsed
            if (frameInSecond - mSlowCoolDown >= 3) {
                // Cool down period has elapsed, revert to normal speed
                mIsSlowed = false;
                mSnakeSpeed = NORMAL_SPEED;
            }
        }
        if (mIsFast) {
            // Check if the cool down period has elapsed
            if (frameInSecond - mFastCoolDown >= 1) {
                // Cool down period has elapsed, revert to normal speed
                mIsFast = false;
                mSnakeSpeed = NORMAL_SPEED;
            }
        }


        if (mIsBlackAppled) {
            if (frameInSecond - mBlackAppleCooldownStartTime >= 2) {
                // Reset the state of the snake
                mIsBlackAppled = false;
                // Restore the normal snake speed
                mSnakeSpeed = NORMAL_SPEED;
            }

        }
        //deltaTime / targetFPS
        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mNormalApple.getLocation())){
            mNormalApple.spawn(mSnake.segmentLocations);
            mScore = mNormalApple.effect(mScore);

            if(mRock.moreSpawn(mScore)){
                rocks[mRock.getIndex()].resetPosition();
            }

            soundManager.playEatSound();
        }

        if(mSnake.checkDinner(mColdApple.getLocation())){
            mColdApple.spawn(mSnake.segmentLocations);
            mScore = mColdApple.effect(mScore);

            mIsSlowed = true;
            mSlowCoolDown = frameInSecond;

            if(mRock.moreSpawn(mScore)){
                rocks[mRock.getIndex()].resetPosition();
            }

            soundManager.playColdSound();
        }

        if(mSnake.checkDinner(mFastApple.getLocation())) {
            mFastApple.spawn(mSnake.segmentLocations);
            mScore = mFastApple.effect(mScore);
            mFastCoolDown = frameInSecond;
            mIsFast = true;
            mSnakeSpeed = FAST_SPEED;
            if(mRock.moreSpawn(mScore)){
                rocks[mRock.getIndex()].resetPosition();
            }
            soundManager.playFastSound();
        }

        if(mSnake.checkDinner(mBlackApple.getLocation())){
            mBlackApple.spawn(mSnake.segmentLocations);
            mScore = mBlackApple.effect(mScore);
            mIsBlackAppled = true;
            mSnakeSpeed = 0;
            mBlackAppleCooldownStartTime = frameInSecond;
            soundManager.playEatSound();
        }

        if(mSnake.checkSugar(mSugar.getLocation(), frameInSecond)){
            mScore = mSugar.effect(mScore, frameInSecond);
            gifOn = true;
            if(mRock.moreSpawn(mScore)){
                rocks[mRock.getIndex()].resetPosition();
            }
            soundManager.playSugarSound();
        }

        for(int i = 0; i < rocks.length; i++)
            checkRock(i);
        if(mScore >= MAX_SCORE) {
            gameState.setReachMax();
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            soundManager.playDeathSound();
            gameState.setSnakeDied();
        }
    }

    private void checkRock(int index){
        if(mSnake.checkEnemy(mRock.getLocation(), frameInSecond)) {
            mRock.spawn(mSnake.segmentLocations);
            if (!mSnake.isImmune(frameInSecond)) {
                mScore = mRock.effect(mScore);
            }
            soundManager.playCrashSound();
        }
        if(mSnake.checkEnemy(rocks[index].getLocation(), frameInSecond)) {
            rocks[index].spawn(mSnake.segmentLocations);
            if(!mSnake.isImmune(frameInSecond)) {
                mScore = mRock.effect(mScore);
            }
            soundManager.playCrashSound();
        }
    }

    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Draw Manager
            draw = new Draw(mCanvas, mPaint);
            // Draw the background image
            draw.drawBackground(mBackground, getWidth(), getHeight());

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 0, 0, 0));
            mPaint.setTextSize(120);
            mPaint.setTypeface(mAtariFont);

            draw.drawText(mCanvas, mPaint, gameState, mScore, exitButton);
            if (!gameState.getnotInGame()) {
                inGameDrawing();
                // Change the snake appearance when eating sugar
                if (mSnake.isImmune(frameInSecond) && gifOn) {
                    mSnake.setGif(getContext());
                } else if (gifOn) {   // When snake is back to normal from immunity
                    mSnake.setNormal(getContext());
                    soundManager.startBackgroundMusic();
                    gifOn = false;
                }
                mSugar.checkSpawn(mSnake.segmentLocations, frameInSecond);
            }
            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void inGameDrawing() {
        // Draw the objects
        mNormalApple.draw(mCanvas, mPaint);
        mColdApple.draw(mCanvas,mPaint);
        mFastApple.draw(mCanvas,mPaint);
        mBlackApple.draw(mCanvas,mPaint);
        mSnake.draw(mCanvas, mPaint);
        mRock.draw(mCanvas, mPaint);
        for (int i = 0; i < rocks.length; i++)
            rocks[i].draw(mCanvas, mPaint);
        mSugar.draw(mCanvas, mPaint);
        // Draw the control button
        controlButton.draw(mCanvas, mPaint);
        // Draw the pause button
        pauseButton.draw(mCanvas, mPaint);
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

    private boolean validTouch(MotionEvent motionEvent) {
        if (gameState.getWinner() || gameState.getDead()) {
            if (exitButton.buttonRange(motionEvent)) {
                // Go to another screen
                gameState.setNotInGame();
            }
            gameState.notWinnerDead();
        } else if (gameState.getPaused() && gameState.getnotInGame()) {
            gameState.inGame();
        } else if (gameState.getPaused() && gameState.getReset()) {  // For new start
            gameState.setPauseResetFalse();
            newGame();
            return true;
        } else if (!gameState.getPaused() && pauseButton.buttonRange(motionEvent)) { // To pause button
            gameState.setPaused();
            soundManager.stopBackgroundMusic();
        } else if (gameState.getPaused() && pauseButton.buttonRange(motionEvent)) {  // To play button
            gameState.setNotPaused();
            mSugar.setNextSpawnTime(frameInSecond);
            soundManager.startBackgroundMusic();
        } else if (!gameState.getPaused()) {  // When the game is playing
            // Let the Snake class handle the input
            mSnake.switchHeading(motionEvent, controlButton);
        }
        // Don't want to process snake direction for this tap
        return true;
    }

    // Stop the thread
    public void pause() {
        gameState.setNotPlaying();
        try {
            if (mThread != null) {
                mThread.join();
            }
        } catch (InterruptedException e) {
            // Error
            e.printStackTrace(); // Log the error
        }
    }
    // Start the thread
    public void resume() {
        gameState.setPlaying();
        mThread = new Thread(this);
        mThread.start();
    }
}