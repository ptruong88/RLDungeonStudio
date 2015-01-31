package com.example.longdungeon.layout;

import android.graphics.Canvas;
import android.util.Log;

public class GameLoopThread extends Thread {

	static final long FPS = 12;
	private BattleLayout view;
	private boolean running = false;
	
	public GameLoopThread(BattleLayout view) {
		this.view = view;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		while (running) {
			Canvas c = null;
			startTime = System.currentTimeMillis();
			try {
				c = view.getHolder().lockCanvas();
				c.drawRGB(0, 0, 0);
				Log.i("Running", ""+running);
				view.updateKnightStand();
				synchronized (view.getHolder()) {
					
					view.draw(c);
				}
			} finally {
				if (c != null) {
					view.getHolder().unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {
			}
		}
	}
}