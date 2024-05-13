package com.gamecodeschool.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.KeyEvent;
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
    private Thread mThread = null; // Thread for the game loop
    private long mNextFrameTime; // Timing of the next frame update
    private long frameInSecond; // Frame rate of the game

    // Playable Area Settings
    private final int NUM_BLOCKS_WIDE = 40; // Width of playable area in segments
    private int mNumBlocksHigh; // Height of playable area88
    private int mBlockSize; // Size of each block in the game area

    // Screen Range
    private Point mScreenRange; // Screen range for the game

    // Player Score
    private int mScore; // Current player score
    private int maxScore = 100; // Maximum possible score

    // Drawing Objects
    private Canvas mCanvas; // Canvas for drawing
    private SurfaceHolder mSurfaceHolder; // Surface holder for drawing on a SurfaceView
    private Paint mPaint; // Paint object for drawing
    private Typeface mAtariFont; // Typeface for text
    private Drawable mBackground; // Background drawable

    // Sound
    private SoundManager soundManager; // Sound manager for game audio
    private GameState gameState;
    // Game Objects
    private Snake mSnake; // Snake object
    private boolean gifOn = false; // Flag for GIF animation
    private NormalApple mNormalApple; // Normal apple object
    private ColdApple mColdApple; // Cold apple object
    private FastApple mFastApple; // Fast apple object
    private BlackApple mBlackApple; // Black apple object
    private Mole mMole; // Mole object
    private Rock mRock; // Rock object
    private Rock[] rocks = new Rock[3]; // Array of rocks
    private Sugar mSugar; // Sugar object
    private PauseButton pauseButton; // Pause button object
    private ControlButton controlButton; // Control button object
    private ExitButton exitButton; // Exit button object
    private KeyEvent keyEvent; // Key event for player input

    // Snake Speed Constants
    private final int NORMAL_SPEED = 1; // Normal speed constant
    private final int FAST_SPEED = 3; // Fast speed constant
    private final double SLOWED_SPEED = 0.1; // Slowed speed constant

    // Snake Speed and State
    private int mSnakeSpeed = NORMAL_SPEED; // Current snake speed
    private boolean mIsSlowed = false; // Flag indicating if snake is slowed
    private boolean mIsFast = true; // Flag indicating if snake is fast
    private long mCooldownStartTime = 0; // Start time for cooldown
    boolean mIsBlackAppled;
    private final long BLACK_APPLE_DURATION = 100000;

    // This is the constructor method that gets called
    // from SnakeActivity
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
        setObjects(context, size);
    }

    private void setObjects(Context context, Point size){
        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mAtariFont = ResourcesCompat.getFont(getContext(), R.font.atariclassic);
        mBackground = context.getResources().getDrawable(R.drawable.grass);
        mScreenRange = new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Call the constructors of our game objects
        mSnake = new Snake(context, mScreenRange, mBlockSize);
        mNormalApple = (NormalApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.NORMAL_APPLE, mScreenRange, mBlockSize);
        mColdApple = (ColdApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.COLD_APPLE, mScreenRange, mBlockSize);
        mFastApple = (FastApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.FAST_APPLE, mScreenRange, mBlockSize);
        mBlackApple = (BlackApple) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.BLACK_APPLE, mScreenRange, mBlockSize);
        mRock = (Rock) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.ROCK, mScreenRange, mBlockSize);
        mMole = (Mole) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.MOLE, mScreenRange, mBlockSize);
        for(int i = 0; i < rocks.length; i++) {
            rocks[i] = new Rock(context, mScreenRange, mBlockSize);
        }
        mSugar = (Sugar) GameObjectFactory.createObject(context, GameObjectFactory.ObjectType.SUGAR, mScreenRange, mBlockSize);

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
        // Get the apple ready for dinner
        mNormalApple.spawn();
        mColdApple.spawn();
        mFastApple.spawn();
        mBlackApple.spawn();
        mRock.spawn();
        mMole.spawn();
        for(int i = 1; i < rocks.length; i++){
            rocks[i].location.x = -10;
        }

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
        while (gameState.getNotPlaying()) {
            if(!gameState.getPaused()){
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
        for (int i = 0; i < mSnakeSpeed; i++) {
            mSnake.move();
        }
        if (mIsSlowed) {
            // Check if the cool down period has elapsed
            long COOLDOWN_DURATION = 8000;
            if (System.currentTimeMillis() - mCooldownStartTime >= COOLDOWN_DURATION) {
                // Cool down period has elapsed, revert to normal speed
                mIsSlowed = true;
                mSnakeSpeed = NORMAL_SPEED;
            }
        }if (mIsFast) {
            // Check if the cool down period has elapsed
            long COOLDOWN_DURATION = 8000;
            if (System.currentTimeMillis() + mCooldownStartTime >= COOLDOWN_DURATION) {
                // Cool down period has elapsed, revert to normal speed
                mIsSlowed = false;
                mSnakeSpeed = NORMAL_SPEED;
            }
        }
        // Update the mole
        //deltaTime / targetFPS
        mMole.update(10 / 10, getHeight()); // Pass the screen height to the mole
        if(mSnake.checkEnemy(mMole.getLocation(), frameInSecond)){
            mMole.spawn(mSnake.segmentLocations);

            if(!mSnake.isImmune(frameInSecond))
                mScore = mMole.effect(mScore);

            soundManager.playCrashSound();
        }

        if (mIsBlackAppled) {
            long mBlackAppleCooldownStartTime = 5;
            if (System.currentTimeMillis() - mBlackAppleCooldownStartTime >= BLACK_APPLE_DURATION) {
                // Reset the state of the snake
                mIsBlackAppled = false;
                // Restore the normal snake speed
                mSnakeSpeed = 0;
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
            mCooldownStartTime = System.currentTimeMillis();
            mSnakeSpeed = (int) (SLOWED_SPEED);

            if(mRock.moreSpawn(mScore)){
                rocks[mRock.getIndex()].resetPosition();
            }

            soundManager.playEatSound();
        }

        if(mSnake.checkDinner(mFastApple.getLocation())) {
            mFastApple.spawn(mSnake.segmentLocations);
            mScore = mFastApple.effect(mScore);
            mSnakeSpeed = (FAST_SPEED);
            if(mRock.moreSpawn(mScore)){
                rocks[mRock.getIndex()].resetPosition();
            }
            soundManager.playEatSound();
        }

        long mBlackAppleCooldownStartTime = 0;
        if(mSnake.checkDinner(mBlackApple.getLocation())){
            mBlackApple.spawn(mSnake.segmentLocations);
            mScore = mBlackApple.effect(mScore);
            mIsBlackAppled = true;
            mSnakeSpeed = 0;
            mBlackAppleCooldownStartTime = System.currentTimeMillis();
            soundManager.playEatSound();
        }

        // Check if the effect of the black apple has worn off
        if (mIsBlackAppled && System.currentTimeMillis() - mBlackAppleCooldownStartTime >= BLACK_APPLE_DURATION) {
            // Reset the state of the snake
            mIsBlackAppled = false;
            // Restore the normal snake speed
            mSnakeSpeed = NORMAL_SPEED;
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
        if(mScore >= maxScore) {
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
        if(mSnake.checkEnemy(mRock.getLocation(), frameInSecond)){
            mRock.spawn(mSnake.segmentLocations);

            if(!mSnake.isImmune(frameInSecond)){
                mSnake.segmentLocations.get(0).x = -10;
                mScore = mRock.effect(mScore);
            }

            soundManager.playCrashSound();
        }

        if(mSnake.checkEnemy(rocks[index].getLocation(), frameInSecond)){
            rocks[index].spawn(mSnake.segmentLocations);

            if(!mSnake.isImmune(frameInSecond)) {
                mSnake.segmentLocations.get(0).x = -10;
                mScore = mRock.effect(mScore);
            }

            soundManager.playCrashSound();
        }
    }

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

            drawText();
            if (!gameState.getnotInGame()) {
                inGameDrawing();
                //set the snake's look different when it eats sugar item
                if (mSnake.isImmune(frameInSecond) && gifOn) {
                    mSnake.setGif(getContext());
                } else if (gifOn) {   //when snake is back normal from immunity
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
        //mCanvas.drawText("Time: " + frameInSecond%100000, 20, 220, mPaint);    //for testing

        // Draw the objects
        mNormalApple.draw(mCanvas, mPaint);
        mColdApple.draw(mCanvas,mPaint);
        mFastApple.draw(mCanvas,mPaint);
        mBlackApple.draw(mCanvas,mPaint);
        mSnake.draw(mCanvas, mPaint);
        mRock.draw(mCanvas, mPaint);
        mMole.draw(mCanvas, mPaint);
        for (int i = 0; i < rocks.length; i++)
            rocks[i].draw(mCanvas, mPaint);
        mSugar.draw(mCanvas, mPaint);

        // Draw the control button
        controlButton.draw(mCanvas, mPaint);
        // Draw the pause button
        pauseButton.draw(mCanvas, mPaint);
    }

    private void drawText() {
        mPaint.setColor(Color.argb(255, 0, 0, 0));
        // Draw some text while paused
        if(gameState.getnotInGame()){  //title
            mPaint.setTextSize(150);
            drawingText("Sugaraddict", mCanvas.getWidth()/6, mCanvas.getHeight()/3 + 50);
            drawingText("Snake", mCanvas.getWidth()/3, mCanvas.getHeight()/3 + 250);
            mPaint.setTextSize(50);
            drawingText("Click Anywhere to Start the Game", mCanvas.getWidth()/4 - 100, mCanvas.getHeight()/2 + 200);
        }else if(gameState.getPaused()) {
            // Set the size and color of the mPaint for the text
            //mPaint.setColor(Color.argb(255, 255, 255, 255));  //redundancy
            // Draw our names
            mPaint.setTextSize(50);
            drawingText("Alan Duong", 1700, 50);
            drawingText("Kenny Ahn", 1700, 100);
            drawingText("Taekjin Jung", 1700, 150);
            drawingText("David Pham", 1700, 200);
            drawingText("Nancy Zhu", 1700, 250);

            if(!gameState.getReset()) {
                // Draw pause instruction
                drawingText("Click to resume", 1325, 525);
            }else if(gameState.getWinner()) {
                mPaint.setTextSize(120);
                drawingText(getResources().getString(R.string.for_winner1), mCanvas.getWidth()/6, mCanvas.getHeight()/3+50);
                drawingText(getResources().getString(R.string.for_winner2), mCanvas.getWidth()/3, (mCanvas.getWidth()/3)+120);
                exitButton.draw(mCanvas, mPaint);
            }else if(gameState.getDead()) {
                mPaint.setTextSize(120);
                drawingText(getResources().getString(R.string.for_loser),
                        mCanvas.getWidth() / 6, 400);
                mPaint.setTextSize(60);
                drawingText("Score:" + mScore, mCanvas.getWidth() / 6, 500);
                drawingText("Tap anywhere for new game", mCanvas.getWidth() / 6, 600);
                //draw the menu button
                exitButton.draw(mCanvas, mPaint);
            }else {
                // Draw the message
                // We will give this an international upgrade soon
                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                mPaint.setTextSize(120);
                drawingText(getResources().getString(R.string.tap_to_play), 100, 800);
            }
        }else {
            // Draw the score
            mPaint.setTextSize(120);
            drawingText("" + mScore, 20, 120);
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

    private boolean validTouch(MotionEvent motionEvent) {
        if (gameState.getWinner() || gameState.getDead()) {
            if (exitButton.buttonRange(motionEvent)) {
                //go to another screen
                gameState.setNotInGame();
            }
            gameState.notWinnerDead();
        } else if (gameState.getPaused() && gameState.getnotInGame()) {
            gameState.inGame();
        }else if (gameState.getPaused() && gameState.getReset()) {  //for new start
            gameState.setPauseResetFalse();
            newGame();

            return true;
        }else if(!gameState.getPaused() && pauseButton.buttonRange(motionEvent)){ //to pause button
            gameState.setPaused();
            soundManager.stopBackgroundMusic();

        }else if(gameState.getPaused() && pauseButton.buttonRange(motionEvent)){  //to play button
            gameState.setNotPaused();
            mSugar.setNextSpawnTime(frameInSecond);
            soundManager.startBackgroundMusic();

        }else if(!gameState.getPaused()){                                     //when the game is playing                                  //when the game is playing
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
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }
    // Start the thread
    public void resume() {
        gameState.setPlaying();
        mThread = new Thread(this);
        mThread.start();
    }
}