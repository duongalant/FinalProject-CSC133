package com.gamecodeschool.c17snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.gamecodeschool.c17snake.Buttons.ExitButton;

public class Draw {
    private Canvas canvas;
    private Paint paint;

    public Draw(Canvas canvas, Paint paint) {
        this.canvas = canvas;
        this.paint = paint;
    }

    public void drawBackground(Drawable background, int width, int height) {
        background.setBounds(0, 0, width, height);
        background.draw(canvas);
    }

    public void drawText(Canvas canvas, Paint paint, GameState gameState, int score, ExitButton exitButton) {
        paint.setColor(Color.argb(255, 0, 0, 0));
            // Draw some text while paused
            if(gameState.getnotInGame()){  //title
                paint.setTextSize(150);
                drawingText("Sugaraddict", canvas.getWidth()/6, canvas.getHeight()/3 + 50);
                drawingText("Snake", canvas.getWidth()/3, canvas.getHeight()/3 + 250);
                paint.setTextSize(50);
                drawingText("Click Anywhere to Start the Game", canvas.getWidth()/4 - 100, canvas.getHeight()/2 + 200);
            }else if(gameState.getPaused()) {
                // Set the size and color of the mPaint for the text
                //mPaint.setColor(Color.argb(255, 255, 255, 255));  //redundancy
                // Draw our names
                paint.setTextSize(50);
                drawingText("Alan Duong", 1700, 50);
                drawingText("Kenny Ahn", 1700, 100);
                drawingText("Taekjin Jung", 1700, 150);
                drawingText("David Pham", 1700, 200);
                drawingText("Nancy Zhu", 1700, 250);

                if(!gameState.getReset()) {
                    // Draw pause instruction
                    drawingText("Click to resume", 1325, 525);
                } else if(gameState.getWinner()) {
                    paint.setTextSize(120);
                    drawingText("Congratulations!", canvas.getWidth() / 6, canvas.getHeight() / 3 + 50);
                    drawingText("You Win!", canvas.getWidth() / 3, (canvas.getWidth() / 3) + 120);
                    exitButton.draw(canvas, paint);
                } else if(gameState.getDead()) {
                    paint.setTextSize(120);
                    drawingText("Game Over!", canvas.getWidth() / 6, 400);
                    paint.setTextSize(60);
                    drawingText("Score:" + score, canvas.getWidth() / 6, 500);
                    drawingText("Tap anywhere for new game", canvas.getWidth() / 6, 600);
                    //draw the menu button
                    exitButton.draw(canvas, paint);
                } else {
                    // Draw the message
                    // We will give this an international upgrade soon
                    //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                    paint.setTextSize(120);
                    drawingText("Tap to Play!", 100, 800);
                }
            } else {
                // Draw the score
                paint.setTextSize(120);
                drawingText("" + score, 20, 120);
            }
    }

    public void drawingText(String text, int x, int y) {
        canvas.drawText(text, x, y, paint);
    }

    public void drawSnake(Snake snake) {
        // Draw the snake on the canvas
        snake.draw(canvas, paint);
    }
}
