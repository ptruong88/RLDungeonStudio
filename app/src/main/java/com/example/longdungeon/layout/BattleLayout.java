package com.example.longdungeon.layout;

import com.example.longdungeon.R;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Rect;

import android.graphics.BitmapFactory;

import android.graphics.Canvas;

import android.graphics.Color;

import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import android.view.SurfaceView;

public class BattleLayout extends SurfaceView {

	private Bitmap knightMap;
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private int x = -1;
	private int xSpeed = 1;

	public BattleLayout(Context context) {
		super(context);
		defaultSetup();
	}

	public BattleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		defaultSetup();
	}

	public BattleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultSetup();
	}

	private void defaultSetup() {
		gameLoopThread = new GameLoopThread(this);
		holder = getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				while (retry) {
					try {
						gameLoopThread.join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
		if (knightMap == null) {
			knightMap = BitmapFactory.decodeResource(getResources(),
					R.drawable.knight_animation);
			frameCount = 0;

			knightW = knightMap.getWidth() / 7;
			knightH = knightMap.getHeight() / 3;
			initialize();
		}
	}

	private void initialize() {
		double ratio = (double) knightW / knightH;
		int yKnightTemp = (int) (getHeight() * 0.8);
		int xKnightTemp = (int) (yKnightTemp * ratio);
		leftKnight = getWidth() - xKnightTemp;
		topKnight = getHeight() - yKnightTemp;
		rightKnight = getWidth();
		bottomKnight = getHeight();
		sourceKnight = new Rect(0, 0, knightW, knightH);
		destKnight = new Rect(leftKnight, topKnight, getWidth(), getHeight());
	}

	private int knightW, knightH, leftKnight, rightKnight, topKnight, bottomKnight;
	private int frameCount;
	private Rect sourceKnight, destKnight;

	public void setRun(boolean run) {
		gameLoopThread.setRunning(run);
		if (run)
			gameLoopThread.run();
	}

	private boolean knight;

	public void draw(Canvas canvas) {
		if (x < 0) {
			x = 0;
			initialize();
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(knightMap, sourceKnight, destKnight, null);
			knight = true;
			Log.i("Draw", "x < 0");
		}
		else{
			canvas.drawColor(Color.BLACK);
			Rect destKnight = new Rect(leftKnight, topKnight, rightKnight, bottomKnight);
			canvas.drawBitmap(knightMap, sourceKnight, destKnight, null);
			Log.i("Draw", "x > 0");
		}
	}

	public void updateKnightStand() {
		++frameCount;
		sourceKnight.left = frameCount * knightW;
		sourceKnight.right = (frameCount + 1) * knightW;
		// if (frameCount == 1 || frameCount == 3) {
		// destKnight.left -= 10;
		// destKnight.right -= 10;
		// } else {
		// destKnight.left += 10;
		// destKnight.right += 10;
		// }
		if (destKnight.left > 300) {
			leftKnight -= 10;
			rightKnight -= 10;
		}
		if (frameCount > 3)
			frameCount = 0;
	}

	public void drawKnightMove(Canvas canvas) {

	}

	public void drawKnightAttack(Canvas canvas) {

	}

	public void drawMobShake(Canvas canvas) {

	}

	public void drawEffect(Canvas canvas) {

	}

}