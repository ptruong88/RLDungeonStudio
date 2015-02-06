package com.example.longdungeon.layout;

import com.example.longdungeon.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ImageBattle extends View {

	private ImageObject player;
	private ImageObject mob;
	private int atkType;// The atkTypeition of effect
	private int width, height;// Screen size
	

	public ImageBattle(Context context) {
		super(context);
		setUp();
	}

	public ImageBattle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setUp();
	}

	public ImageBattle(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUp();
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (width != w) {
			width = w;
			height = h;
			updateScreenSize();
		}
	}

	// Update screen size to object variables and position
	private void updateScreenSize() {
		player.updateScreen(width, height);
		player.setPosition(width - player.getWidth(),
				height - player.getHeight());

		mob.updateScreen(width, height);
		mob.setPosition(0, 0);

		player.setOpponentWidth(mob.getWidth() - 150);
		player.setOpponentPosition((int) mob.getLeft(), (int) mob.getTop());

		mob.setOpponentWidth(player.getWidth() - 150);
		mob.setOpponentPosition((int) player.getLeft(), (int) player.getTop());
	}

	// Set up method to create player and mob object.
	// Passing frame range for stand, move, attack, defend.
	private void setUp() {
		player = new ImageObject(BitmapFactory.decodeResource(getResources(),
				R.drawable.knight_animation), 7, 3);
		player.setScale(0.8);
		player.setFrameStand(0, 3, 0, 0);
		player.setFrameMove(0, 3, 0, 0);
		player.setFrameAttack(0, 6, 1, 1);
		player.setFrameDefend(0, 6, 2, 2);
		player.setStand();
		playerChoice = STAND;

		mob = new ImageObject(BitmapFactory.decodeResource(getResources(),
				R.drawable.spider_sprite), 5, 3);
		mob.setScale(1.0);
		mob.setFrameStand(0, 3, 0, 0);
		mob.setFrameMove(0, 3, 0, 0);
		mob.setFrameAttack(0, 4, 1, 1);
		mob.setFrameDefend(0, 2, 2, 2);
		mob.setStand();
		mobChoice = STAND;
	}

	public void onDraw(Canvas canvas) {
		canvas.save();
		mob.render(canvas);
		player.render(canvas);
		canvas.restore();

		updateGame();

	}

	private void updateGame() {
		playerChoice();
		mobChoice();
		invalidate();
	}

	private static final int MOVE_LEFT = 0;
	private static final int MOVE_RIGHT = 1;
	private static final int ATTACK = 2;
	private static final int DEFEND = 3;
	private static final int STAND = 4;
	private int playerChoice, mobChoice;

	public void setPlayerAttack() {
		
		player.setMove(-1);
		playerChoice = MOVE_LEFT;
		playerChoice();
		
	}

	public void setMobAttack() {
		mob.setMove(1);
		mobChoice = MOVE_RIGHT;
		mobChoice();
	}

	// For each choice, the player object will update its frame and move.
	//this method is always running during the battle
	//it's what continuously updates the animation on the player
	private void playerChoice() {
		switch (playerChoice) {
		case MOVE_LEFT:
			player.updateMove();
			if (player.isAtOpponent()) {
				player.setAttack();
				playerChoice = ATTACK;
				mob.setDefend();
				mobChoice = DEFEND;
			}
			break;
		case MOVE_RIGHT:
			player.updateMove();
			if (player.isBack()) {
				player.setStand();
				playerChoice = STAND;
			}
			break;
		case ATTACK:
			player.updateAttack();
			if (player.isDoneAttack()) {
				player.setMove(1);
				playerChoice = MOVE_RIGHT;
			}
			break;
		case DEFEND:
			player.updateDefend();
			if (player.isDoneDefend()) {
				player.setStand();
				playerChoice = STAND;
			}
			break;
		case STAND:
			player.updateStand();
			break;
		}
	}

	// For each choice, the mob object will update its frame and move.
	//this method is always running during the battle
	//it's what continuously updates the animation on the mob
	private void mobChoice() {
		switch (mobChoice) {
		case MOVE_LEFT:
			mob.updateMove();
			if (mob.isBack()) {
				mob.setStand();
				mobChoice = STAND;
			}
			break;
		case MOVE_RIGHT:
			mob.updateMove();
			if (mob.isAtOpponent()) {
				mob.setAttack();
				mobChoice = ATTACK;
				player.setDefend();
				playerChoice = DEFEND;
			}
			break;
		case ATTACK:
			mob.updateAttack();
			if (mob.isDoneAttack()) {
				mob.setMove(-1);
				mobChoice = MOVE_LEFT;
			}
			break;
		case DEFEND:
			mob.updateDefend();
			if (mob.isDoneDefend()) {
				mob.setStand();
				mobChoice = STAND;
			}
			break;
		case STAND:
			mob.updateStand();
			break;
		}
	}

}
