package com.example.longdungeon.layout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class ImageObject {

	// The width and height of bitmap on screen
	private int objWidth, objHeight;
	// The coordinates of bitmap on screen
	private float objLeft, objTop;
	private Bitmap bitmap;
	// The paint to color bitmap on screen
	private Paint paint;
	// The width and height of bitmap bitmap
	private int bitmapW, bitmapH;
	private int frameCountX;
	private int frameCountY;
	// The width and height of layout
	private int width, height;
	// The source box for bitmap and destination box for draw bitmap on screen.
	private Rect srcBox;
	private RectF dstBox;
	private int oppWidth;// The opponent width on screen
	private float oppLeft,// The opponent left on screen
			oppTop,// The opponent top on screen
			velocity,// velocity between mob and player
			distance;// distance between mob and player
	private int[] frmStandX, frmStandY,// The frame stand of bitmap object
			frmMoveX, frmMoveY,// The frame move of bitmap object
			frmAttackX, frmAttackY, // The frame attack of bitmap object
			frmDefendX, frmDefendY;// The frame defend of bitmap object
	private double scale;// Scale ratio

	/**
	 * Constructor with bitmap of sprite sheet, the maximum frames in one row,
	 * and the maximum in one column.
	 * 
	 * @param bitmap
	 *            - Bitmap of a sprite sheet
	 * @param xFrame
	 *            - Maximum frames in one row of the sprite sheet
	 * @param yFrame
	 *            - Maximum frames in one column of the sprite sheet
	 */
	public ImageObject(Bitmap bitmap, int xFrame, int yFrame) {
		this.bitmap = bitmap;
		bitmapW = bitmap.getWidth() / xFrame;
		bitmapH = bitmap.getHeight() / yFrame;
		init();
	}

	/**
	 * Constructor with bitmap of sprite sheet, width and height of the layout
	 * screen size, the maximum frames in one row, and the maximum in one
	 * column.
	 * 
	 * @param bitmap
	 *            - Bitmap of a sprite sheet
	 * @param xFrame
	 *            - Maximum frames in one row of the sprite sheet
	 * @param yFrame
	 *            - Maximum frames in one column of the sprite sheet
	 * @param width
	 *            - Width of a layout screen size
	 * @param height
	 *            - Height of a layout screen size
	 */
	public ImageObject(Bitmap bitmap, int xFrame, int yFrame, int width,
			int height) {
		this.bitmap = bitmap;
		bitmapW = bitmap.getWidth() / xFrame;
		bitmapH = bitmap.getHeight() / yFrame;
		this.width = width;
		this.height = height;
		init();
	}

	// Default set up for box of image and onscreen
	private void init() {
		srcBox = new Rect(0, 0, bitmapW, bitmapH);
		dstBox = new RectF(0, 0, 100, 100);
		paint = new Paint();
		scale = 1.0;
	}

	/**
	 * Use the given canvas to draw object on screen
	 * 
	 * @param canvas
	 *            - A passing canvas
	 */
	public void render(Canvas canvas) {
		canvas.drawBitmap(bitmap, srcBox, dstBox, paint);
	}

	/**
	 * Update layout screen with the given width and height. The object size
	 * will be scale with new layout size with a define scale.
	 * 
	 * @param width
	 *            - A given width
	 * @param height
	 *            - A given height
	 */
	public void updateScreen(int width, int height) {
		this.width = width;
		this.height = height;
		double ratio = (double) bitmapW / bitmapH;
		if (width > height) {
			objHeight = (int) (height * scale);
			objWidth = (int) (objHeight * ratio);
		} else {
			objWidth = (int) (width * scale);
			objHeight = (int) (objWidth / ratio);
		}
		dstBox.right = dstBox.left + objWidth;
		dstBox.bottom = dstBox.top + objHeight;
	}

	/**
	 * Set scale size of bitmap base on width and height of layout screen
	 * 
	 * @param scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * Scale width of bitmap on screen with the given scale
	 * 
	 * @param scale
	 */
	public void scaleWidth(double scale) {
		objWidth = (int) (width * scale);
	}

	/**
	 * Scale height of bitmap on screen with the give height
	 * 
	 * @param scale
	 */
	public void scaleHeight(double scale) {
		objHeight = (int) (height * scale);
	}

	/**
	 * Set object position on screen
	 * 
	 * @param left
	 * @param top
	 */
	public void setPosition(int left, int top) {
		objLeft = left;
		objTop = top;
		dstBox.left = objLeft;
		dstBox.top = objTop;
		dstBox.right = objWidth + objLeft;
		dstBox.bottom = objHeight + objTop;
	}

	/**
	 * Set the begin number and the end number of frame stand will go through on
	 * a row and a column. (0 is first number)
	 * 
	 * -For example, frameX1 = 0, frameX2 = 3, frameY1 = 0, frameY2 = 1, frame
	 * animation will go through from column 0 to column 3 at row 0. After the
	 * animation is at column 3, it goes to row 1, and start from column 0 to
	 * column 3 again.
	 * 
	 * @param frameX1
	 *            - The begin column on one row.
	 * @param frameX2
	 *            - The end column on one row.
	 * @param frameY1
	 *            - The begin row on one column.
	 * @param frameY2
	 *            - The end row on one column.
	 */
	// Same for setFrameAttack, setFrameDefend
	public void setFrameStand(int frameX1, int frameX2, int frameY1, int frameY2) {
		int[] tempX = { frameX1, frameX2 };
		frmStandX = tempX;
		int[] tempY = { frameY1, frameY2 };
		frmStandY = tempY;
	}

	public void setFrameMove(int frameX1, int frameX2, int frameY1, int frameY2) {
		int[] tempX = { frameX1, frameX2 };
		frmMoveX = tempX;
		int[] tempY = { frameY1, frameY2 };
		frmMoveY = tempY;
	}

	public void setFrameAttack(int frameX1, int frameX2, int frameY1,
			int frameY2) {
		int[] tempX = { frameX1, frameX2 };
		frmAttackX = tempX;
		int[] tempY = { frameY1, frameY2 };
		frmAttackY = tempY;
	}

	public void setFrameDefend(int frameX1, int frameX2, int frameY1,
			int frameY2) {
		int[] tempX = { frameX1, frameX2 };
		frmDefendX = tempX;
		int[] tempY = { frameY1, frameY2 };
		frmDefendY = tempY;
	}

	private int count;
	private long beginTime, endTime, diffTime;

	/**
	 * Set stand of bitmap with frameStand X and Y
	 */
	public void setStand() {
		frameCountX = frmStandX[0];
		frameCountY = frmStandY[0];
		srcBox.left = frameCountX * bitmapW;
		srcBox.right = srcBox.left + bitmapW;
		srcBox.top = frameCountY * bitmapH;
		srcBox.bottom = srcBox.top + bitmapH;
		dstBox.left = objLeft;
		dstBox.right = objLeft + objWidth;
		beginTime = System.currentTimeMillis();
	}

	/**
	 * Update stand animation for bitmap object
	 */
	public void updateStand() {
		endTime = System.currentTimeMillis();
		diffTime = endTime - beginTime;
		if (diffTime > 100) {
			beginTime = endTime;
			srcBox.left = frameCountX * bitmapW;
			srcBox.right = srcBox.left + bitmapW;
			++frameCountX;

			if (frameCountX > frmStandX[1]) {
				frameCountX = frmStandX[0];
				srcBox.top = frameCountY * bitmapH;
				srcBox.bottom = srcBox.top + bitmapH;
				++frameCountY;
				if (frameCountY > frmStandY[1])
					frameCountY = frmStandY[0];
			}
		}
	}

	/**
	 * If direction move is 1, move right, otherwise move left.
	 * 
	 * @param directionMove
	 */
	public void setMove(int directionMove) {
		frameCountX = frmMoveX[0];
		frameCountY = frmMoveY[0];
		if (directionMove < 0 && velocity > 0)
			velocity *= -1;
		if (directionMove > 0 && velocity < 0)
			velocity *= -1;
		beginTime = System.currentTimeMillis();
	}

	/**
	 * Update move of bitmap object with a velocity
	 */
	public void updateMove() {
		endTime = System.currentTimeMillis();
		diffTime = endTime - beginTime;
		if (diffTime > 50) {
			beginTime = endTime;
			dstBox.left += velocity;
			dstBox.right += velocity;
			srcBox.left = frameCountX * bitmapW;
			srcBox.right = srcBox.left + bitmapW;
			++frameCountX;

			if (frameCountX > frmMoveX[1]) {
				frameCountX = frmMoveX[0];
				srcBox.top = frameCountY * bitmapH;
				srcBox.bottom = srcBox.top + bitmapH;
				++frameCountY;
				if (frameCountY > frmMoveY[1])
					frameCountY = frmMoveY[0];
			}
		}
	}

	/**
	 * Check if bitmap object is at the opponent position
	 * 
	 * @return true if bitmap object is at the opponent position, otherwise
	 *         false
	 */
	public boolean isAtOpponent() {
		if (velocity > 0)
			return dstBox.right > width - oppWidth;
		return dstBox.left < oppLeft + oppWidth;
	}

	/**
	 * Set attack for bitmap object
	 */
	public void setAttack() {
		if (velocity > 0) {
			dstBox.right = width - oppWidth;
			dstBox.left = dstBox.right - objWidth;
		} else {
			dstBox.left = oppLeft + oppWidth;
			dstBox.right = dstBox.left + objWidth;
		}
		frameCountX = frmAttackX[0];
		frameCountY = frmAttackY[0];
		srcBox.top = frameCountY * bitmapH;
		srcBox.bottom = srcBox.top + bitmapH;
		beginTime = System.currentTimeMillis();
	}

	/**
	 * Update attack animation for bitmap object
	 */
	public void updateAttack() {
		endTime = System.currentTimeMillis();
		diffTime = endTime - beginTime;
		if (diffTime > 200) {
			if (frameCountX > frmAttackX[1]) {
				frameCountX = frmAttackX[0];
				++frameCountY;
				srcBox.top = frameCountY * bitmapH;
				srcBox.bottom = srcBox.top + bitmapH;
			}
			beginTime = endTime;
			srcBox.left = frameCountX * bitmapW;
			srcBox.right = srcBox.left + bitmapW;
			++frameCountX;
		}
	}

	/**
	 * Check if bitmap object is done attacking
	 * 
	 * @return true if attack finished, otherwise false
	 */
	public boolean isDoneAttack() {
		return frameCountX > frmAttackX[1] && frameCountY == frmAttackY[1];
	}

	/**
	 * Check if bitmap object return from attacking
	 * 
	 * @return true if bitmap object is back, otherwise false.
	 */
	public boolean isBack() {
		if (velocity > 0)
			return dstBox.right > objLeft + objWidth;
		return dstBox.left < objLeft;
	}

	/**
	 * Set defend animation for bitmap object
	 */
	public void setDefend() {
		frameCountX = frmDefendX[0];
		frameCountY = frmDefendY[0];
		srcBox.left = frameCountX * bitmapW;
		srcBox.right = srcBox.left + bitmapW;
		srcBox.top = frameCountY * bitmapH;
		srcBox.bottom = srcBox.top + bitmapH;
		beginTime = System.currentTimeMillis();
	}

	/**
	 * Update defend animation for bitmap object
	 */
	public void updateDefend() {
		endTime = System.currentTimeMillis();
		diffTime = endTime - beginTime;
		if (diffTime > 200) {
			if (frameCountX > frmDefendX[1]) {
				frameCountX = frmDefendX[0];
				++frameCountY;
				srcBox.top = frameCountY * bitmapH;
				srcBox.bottom = srcBox.top + bitmapH;
			}
			beginTime = endTime;
			srcBox.left = frameCountX * bitmapW;
			srcBox.right = srcBox.left + bitmapW;
			++frameCountX;
		}
	}

	/**
	 * Check if bitmap object finished defend animation
	 * 
	 * @return true if bitmap object finished defend animation, otherwise false
	 */
	public boolean isDoneDefend() {
		return frameCountX == frmDefendX[1] && frameCountY == frmDefendY[1];
	}

	/**
	 * Get position on left side of bitmap
	 * 
	 * @return
	 */
	public float getLeft() {
		return objLeft;
	}

	/**
	 * Get position on top side of bitmap
	 * 
	 * @return
	 */
	public float getTop() {
		return objTop;
	}

	/**
	 * Set opponent left and top sides with given sides
	 * 
	 * @param left
	 * @param top
	 */
	public void setOpponentPosition(int left, int top) {
		oppLeft = left;
		oppTop = top;
	}

	public long getTimeDelay() {
		return 4000 - (count * 100);
	}

	public void setOpponentWidth(int width) {
		oppWidth = width;
		distance = this.width - oppWidth - objWidth;
		velocity = distance / 24;
		if (velocity < 1)
			velocity = 1;
	}

	public int getWidth() {
		return objWidth;
	}

	public int getHeight() {
		return objHeight;
	}

}
