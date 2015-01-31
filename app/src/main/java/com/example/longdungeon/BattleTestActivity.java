package com.example.longdungeon;

//
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import com.example.longdungeon.character.Mob;
import com.example.longdungeon.character.Person;
import com.example.longdungeon.character.Player;
import com.example.longdungeon.item.Item;
import com.example.longdungeon.item.Potion;
import com.example.longdungeon.layout.BattleLayout;
import com.example.longdungeon.layout.ImageBattle;
import com.example.longdungeon.layout.ImageObject;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class BattleTestActivity extends ActionBarActivity implements
		OnClickListener, OnItemClickListener, AnimationListener {

	//A group of strings used to generate options that the player can choose from
	private ListView listAbility;
	
	//tied to something in battle.xml
	private View lyoutBattle;
	
	//actual bit of text that is in the GUI
	private TextView txtViewMobName, txtViewMobHp, txtViewPlayerName,
			txtViewPlayerScore, txtViewPlayerHp, txtViewPlayerMana,
			txtViewStamina, txtViewMobStamina;
	
	//used to manage enemy
	private Mob mob;
	
	//used to manage player
	private Player player;

	//currently not used
	private Button btnAttack, btnDefend, btnMagic, btnItem, btnRun;
	
	private String[] listAttack, listMagic, listItem;
	
	//used for generating the list of choices you have dynamically
	private ArrayAdapter<String> adapterAttack, adapterMagic, adapterItem;

	//
	private static int[] imgMobs = new int[] { R.drawable.goblin,
			R.drawable.skeleton, R.drawable.spider, R.drawable.bats,
			R.drawable.dragon };
	
	//the image of the player
	private ImageView imgPlayer;
	
	//used to generate to view of battle, used to call frame by frame animation
	private ImageBattle imgBattle;

	//true when the player is defending
	boolean playerDefending = false;
	
	//true when to enemy is defending
	boolean enemyDefending = false;
	
	//true when the player runs away
	boolean ranAway = false;
	
	//stores the current values for stamina and mana you get from the player class
	private int baseStm, baseMana;
	
	//the multiplier used for medium attack
	final private double mediumRatio = 1.3;
	
	//the multiplier used heavy attack
	final private int heavyRatio = 2;
	
	//the recent roll from the 10 sided dice
	int d10Roll = 0;
	
	//the fully calculated value for an attack
	int atkVal;
	
	//an array that keeps track of the current potions in inventory
	private Potion[] potions;
	
	//used to play the music
	private MediaPlayer medplay;
	
	//used to generate dice rolls
	private Random rand;

	// Player Attack
	private int baseDamage;

	
	int mobMaxHp, mobCurHp;

	//pops up a window when the battle is exited
	private AlertDialog.Builder alertDialog;

	//pops up a window when the battle is won
	private AlertDialog.Builder winDialog;
	
	//pops up a window when the battle is lost
	private AlertDialog.Builder loseDialog;

	
	//onCreate runs when the Battle Activity Start
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//not sure
		super.onCreate(savedInstanceState);
		
		// finds the layout in Resources folder, called activity_batle_test
		setContentView(R.layout.activity_battle_test);
		
		//instantiates the random number generator
		rand = new Random();
		
		
		setUpPlayer();
		setUpMob();
		setUpListView();
		setUpStringForListView();
		setUpButtonAction();
		setUpHideListView();
		setUpDialogForRun();
		setUpWinDialog();
		setLoseDialog();
		playMusic();
		
		
		imgBattle = (ImageBattle) this
				.findViewById(R.id.imagePlayer);
	}

	
	private void setUpListView() {
		listAbility = (ListView) this.findViewById(R.id.listViewAbilityOptions);
		
		//when you click on a list item
		listAbility.setOnItemClickListener(this);
	}



	private void setUpStringForListView() {
	
		// { "Heavy Attack 10DMG/10STM",
		// "Medium Attack 15DMG/15STM",
		// "Light Attack 20DMG/20STM" };
		
		//setting up description of regular attacks
		baseDamage = player.getDamage();
		baseStm = player.getMaxStm() / 10;
		
		listAttack = new String[3];
		listAttack[0] = generateSkillDescription("Light Attack ", baseDamage, baseStm, "STM");		
		listAttack[1] = generateSkillDescription("Medium Attack ", (int) (baseDamage * mediumRatio),
				(int) (baseStm * mediumRatio), "STM");	
		listAttack[2] = generateSkillDescription("Heavy Attack ", baseDamage * heavyRatio, baseStm
				* heavyRatio, "STM");
	
		//takes the array of strings and generates a clickable list of buttons
		adapterAttack = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1,
				listAttack);

		//setting up descriptions of magic attacks
		baseMana = player.getMaxMana() / 10;
		listMagic = new String[3];	
		listMagic[0] = generateSkillDescription("Fire Magic ", baseDamage, baseMana, "MANA");
		listMagic[1] = generateSkillDescription("Ice Magic ", (int) (baseDamage * mediumRatio),
				(int) (baseMana * mediumRatio), "MANA");
		listMagic[2] = generateSkillDescription("Lightning Magic ", baseDamage * heavyRatio,
				baseMana * heavyRatio, "MANA");

		adapterMagic = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1,
				listMagic);

		//setting up descriptions of item options
		adapterItem = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1);

		potions = new Potion[player.getInventoryCurSpace()];
		
		//loop that goes through the inventory and adds any potions to the clickable description list
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (player.getPlayerInventory()[i].getItemType() == Item.ITEM_HEALTH_POTION
					|| player.getPlayerInventory()[i].getItemType() == Item.ITEM_STAMINA_POTION
					|| player.getPlayerInventory()[i].getItemType() == Item.ITEM_MANA_POTION) {
				potions[i] = (Potion) player.getPlayerInventory()[i];
				adapterItem.add(potions[i].toString());
			}
		}

	}

	//returns string that describes skills, used when attack or magic buttons are pressed
	//example: "Ice Magic 10DMG / 7MANA"
	private String generateSkillDescription(String skillName, int damage, int baseCost,
			String resourceBeingSpent) {
		return skillName + damage + "DMG / " + baseCost + resourceBeingSpent;
	}

	//makes buttons clickable
	private void setUpButtonAction() {
		this.findViewById(R.id.buttonAttack).setOnClickListener(this);
		this.findViewById(R.id.buttonDefend).setOnClickListener(this);
		this.findViewById(R.id.buttonMagic).setOnClickListener(this);
		this.findViewById(R.id.buttonItem).setOnClickListener(this);
		this.findViewById(R.id.buttonRun).setOnClickListener(this);
	}

	//enables or disables the buttons depending on the boolean passed to it
	private void enableButton(boolean enabled) {
		this.findViewById(R.id.buttonAttack).setClickable(enabled);
		this.findViewById(R.id.buttonDefend).setClickable(enabled);
		this.findViewById(R.id.buttonMagic).setClickable(enabled);
		this.findViewById(R.id.buttonItem).setClickable(enabled);
		this.findViewById(R.id.buttonRun).setClickable(enabled);
	}

	// If "attack", "defend", "magic", "item", or "run away" is pressed.	
	public void onClick(View button) {
		switch (button.getId()) {
		case R.id.buttonAttack: //if attack button is pressed
		
			//load in text descriptions and set it to visible
			listAbility.setAdapter(adapterAttack);
			listAbility.setVisibility(View.VISIBLE);
			// PlayerImage playerView =
			// (PlayerImage)this.findViewById(R.id.imagePlayer);
			// playerView.runKnightStand();
			break;
		case R.id.buttonDefend: { //if defend button is pressed
			playerDefending = true;
			if (player.getCurStm() < player.getMaxStm()) {
				int stmRegen = (player.getCurStm() / 5);
				player.setCurStm(stmRegen + player.getCurStm());// get back 1/5
																// of your
																// stamina
				txtViewStamina.setText("Stamina: " + player.getCurStm() + "/"
						+ player.getMaxStm());

			}
			
			//disables all the buttons
			enableButton(false);

			enemyTurn();// defending uses your turn
		}
			break;
		case R.id.buttonMagic: //if magic button is pressed
			listAbility.setAdapter(adapterMagic);
			listAbility.setVisibility(View.VISIBLE);
			break;
		case R.id.buttonItem: //if item button is pressed
			listAbility.setAdapter(adapterItem);
			listAbility.setVisibility(View.VISIBLE);
			break;
		default:// button run way
			alertDialog.show();
			break;
		}

	}

	//if an item in the "attack", "magic", or "item" sub-menu is clicked
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// Toast.makeText(getApplicationContext(),
		// parent.getItemAtPosition(position).toString(),
		// Toast.LENGTH_LONG).show();
		listAbility.setVisibility(View.INVISIBLE);
		enableButton(false);
		
		//when the player does a regular attack
		if (parent.getItemAtPosition(position).toString().contains("Attack")) {
			// mobCurHp -= baseDamage;

			
			switch (position) {
			case 0:// basic attack case based on it being in the 0th position
					
				// imgBattle.setStand();

				attackPlayer(baseDamage, baseStm, 3);
				
				// MobImage mobView =
				// (MobImage)this.findViewById(R.id.imageMob);
				// mobView.animationMob();

			case 1:// medium attack case based on it being in the 1st position
					// medium damage is more 4/3 than normal attack
				
				attackPlayer((int) (baseDamage * mediumRatio),
						 (int) (baseStm * mediumRatio), 3);
				break;
//				imgBattle.setMobAttack();

			default:// heavy attack case based on it being in the 2nd
					// position
				// Heavy attack value is 2 times the base attack
				
				attackPlayer(baseDamage * heavyRatio, baseStm * heavyRatio,3);
			
				// imgBattle.setPlayerAttack(2);
				// imgBattle.setPlayerMagic(0);

				break;
			}
		} else if (parent.getItemAtPosition(position).toString()
				.contains("Magic")) {
			switch (position) {
			case 0:// Fireball
				magicPlayer(baseDamage, baseMana, 3);
				break;
			case 1:// Ice spell
				magicPlayer((int) (baseDamage * mediumRatio),
						(int) (baseMana * mediumRatio), 4);
				break;
			default:// lightning storm
				magicPlayer(baseDamage * heavyRatio, baseMana * heavyRatio, 5);
				break;
			}
		}

		//uses a potion
		else if (parent.getItemAtPosition(position).toString()
				.contains("Potion")) {
			potionClick(parent.getItemAtPosition(position).toString(), position);
			enemyTurn();
		}
	}

	//used to count the amount of toast notifications
	int count = 1;

	//used when player does a magic attack
	private void magicPlayer(int damage, int mana, int attackType) {
		if (player.getCurMana() >= mana) {
			player.setCurMana((player.getCurMana()) - mana);
			txtViewPlayerMana.setText("Mana: " + player.getCurMana() + "/"
					+ player.getMaxMana());// attacks always cost mana, if they
											// miss or not

			int d3Roll = rand.nextInt(3);
			
			// die roll 1 is a miss
			if (d3Roll < 1) {
				Toast.makeText(getApplicationContext(), "Your spell missed!",
						Toast.LENGTH_LONG).show();
				++count;
				System.out.println("___----" + count);
			}
			
			//die roll 2 is a regular attack
			else if (d3Roll < 2) {
				// spells ignore defense
				String atkString = "Spell hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}

			//die roll 3 is a critical hit
			else if (d3Roll < 3) {
				damage *= 2;// critical attack doubles
							// damage
				// spells ignore defense
				String atkString = "Critical attack hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			atkVal = 0;// clear attack val;
			if (mob.getCurHp() <= 0) {
				playerSetWin();
			} else {
				enableButton(false);
				enemyTurn();// once you've attacked the enemy gets a
							// turn
			}
		} else {
			Toast.makeText(getApplicationContext(), "not enough Mana!",
					Toast.LENGTH_LONG).show();
			++count;
			System.out.println("___----" + count);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					enableButton(true);
					count = 1;
				}

			}, Toast.LENGTH_LONG * 2000 * count);
		}
	}

	//runs when the player attacks
	private void attackPlayer(int damage, int stm, int attackType) {
		if (player.getCurStm() >= stm) {
			player.setCurStm((player.getCurStm()) - stm);
			txtViewStamina.setText("Stamina: " + player.getCurStm() + "/"
					+ player.getMaxStm());// attacks always cost
											// stamina, if they miss
											// or not

			d10Roll = rand.nextInt(10);
			
			//starts the attack animation
			imgBattle.setPlayerAttack();
			
			// Attack missing
			if (d10Roll < 2) {
				Toast.makeText(getApplicationContext(), "Your attack missed!",
						Toast.LENGTH_LONG).show();
				++count;
				System.out.println("___----" + count);
			}
			// Half damage
			else if (d10Roll < 4) {
				damage /= 2;
				String atkString = "Glancing hit for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			// Full damage
			else if (d10Roll < 8) {
				String atkString = "Attack hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			// Critical damage
			else if (d10Roll < 10) {
				damage *= 2;// critical attack doubles
				// damage
				String atkString = "Critical attack hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			atkVal = 0;// clear attack val;
			
			//if the enemy has no health, you win. Otherwise, enemy turn.
			if (mob.getCurHp() <= 0) {
				playerSetWin();
			} else {
				enemyTurn();// once you've attacked the enemy gets a
							// turn
			}
		} else {
			String atkString = "you don't have enough stamina!";
			Toast.makeText(getApplicationContext(), atkString,
					Toast.LENGTH_LONG).show();
			++count;
			System.out.println("___----" + count);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					enableButton(true);
					count = 2;
				}

			}, Toast.LENGTH_LONG * 2000 * count);
		}
	}

	//if the attack is not a miss, this method will run
	private void attackPlayerFinishMove(int damage, String atkString,
			int attackType) {
		enemyDefending = getEnemyDefending();
		if (enemyDefending) {
			damage /= 2;
		}
		atkString += damage + " damage!";
		Toast.makeText(getApplicationContext(), atkString, Toast.LENGTH_LONG)
				.show();
		++count;
		System.out.println("___----" + count);
		mob.setCurHp(mob.getCurHp() - damage);
		txtViewMobHp.setText("HP: " + mob.getCurHp() + "/" + mob.getMaxHp());
		
		//starts the player attacking animation
		animationPlayerAttack(attackType);
	}

	//roll 10 sided die, if greater than 7, then enemy is defending
	private boolean getEnemyDefending() {
		int a = rand.nextInt(10);
		return a > 7;
	}

	
	//loads the static attack animations when the player attacks
	private void animationPlayerAttack(int attackType) {
		// load the animation
		switch (attackType) {
		case 0:
			ImageView imageEffect = (ImageView) this
					.findViewById(R.id.imageViewEffect);
			imageEffect.setImageResource(R.drawable.playerlightattack);
			playerPhysicalAttackAnimation(imageEffect);
			break;
		case 1:
			imageEffect = (ImageView) this.findViewById(R.id.imageViewEffect);
			imageEffect.setImageResource(R.drawable.playermediumattack);
			playerPhysicalAttackAnimation(imageEffect);
			break;
		case 2:
			imageEffect = (ImageView) this.findViewById(R.id.imageViewEffect);
			imageEffect.setImageResource(R.drawable.playerheavyattack);
			playerPhysicalAttackAnimation(imageEffect);
			break;
		case 3:
			ImageView imageMagic = (ImageView) this
					.findViewById(R.id.imageViewMagic);
			imageMagic.setImageResource(R.drawable.playerfireball);
			playerMagicAttackAnimation(imageMagic);
			break;
		case 4:
			imageMagic = (ImageView) this.findViewById(R.id.imageViewMagic);
			imageMagic.setImageResource(R.drawable.playericeblast);
			playerMagicAttackAnimation(imageMagic);
			break;
		default:
			imageMagic = (ImageView) this.findViewById(R.id.imageViewMagic);
			imageMagic.setImageResource(R.drawable.playerlightning);
			playerMagicAttackAnimation(imageMagic);
			break;
		}

	}

	//if the player does a magic attack, this is the method that
	//will run the animation
	private void playerMagicAttackAnimation(final ImageView imageMagic) {
/*
 * 		Animation animMove = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.move_magic_player);
		// set animation listener
		animMove.setAnimationListener(this);

		imageMagic.startAnimation(animMove);

		final Animation animShake = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.shake);
 */
		final Animation animFadeout = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_out);

		// set animation listener
		animFadeout.setAnimationListener(this);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// imgMob.startAnimation(animShake);
				imageMagic.startAnimation(animFadeout);
				//imageMagic.setVisibility(View.INVISIBLE);
			}
		}, 300);
	}

	//if the player does a physical attack, this is the 
	//method that will run the animation
	private void playerPhysicalAttackAnimation(final ImageView imageEffect) {
/*
 * 		Animation animMove = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.move);

		// set animation listener
		animMove.setAnimationListener(this);

		final Animation animShake = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.shake);
		// set animation listener
		animShake.setAnimationListener(this);
 */


		final Animation animFadeout = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_out);
		// set animation listener
		animFadeout.setAnimationListener(this);

		/*imgPlayer.startAnimation(animMove);
		// now start walk
		AnimationDrawable theKnightAnimation = (AnimationDrawable) imgPlayer
				.getBackground();
		theKnightAnimation.start();*/
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// imgMob.startAnimation(animShake);
				imageEffect.startAnimation(animFadeout);
			}
		},  1000);
	
	
	}

	//sets up player stats and info
	private void setUpPlayer() {
		// get player image
		 //imgPlayer = (ImageView) this.findViewById(R.id.imagePlayer);

		txtViewPlayerName = (TextView) this
				.findViewById(R.id.textViewPlayerName);
	
		//if game has been played previously, player data will be available at the login
		Intent intentLogin = getIntent();
		player = intentLogin.getExtras().getParcelable(Player.PLAYER_DATA);
		txtViewPlayerName.setText(player.getName());

		txtViewPlayerScore = (TextView) this
				.findViewById(R.id.textViewPlayerScore);
		txtViewPlayerScore.setText("Score: " + player.getScore());

		txtViewPlayerHp = (TextView) this.findViewById(R.id.textViewPlayerHp);
		txtViewPlayerHp.setText("HP: " + player.getCurHp() + "/"
				+ player.getMaxHp());

		txtViewPlayerMana = (TextView) this
				.findViewById(R.id.textViewPlayerMana);
		txtViewPlayerMana.setText("Mana: " + player.getCurMana() + "/"
				+ player.getMaxMana());

		txtViewStamina = (TextView) this.findViewById(R.id.textViewPlayerStm);
		txtViewStamina.setText("Stamina: " + player.getCurStm() + "/"
				+ player.getMaxStm());
	}

	

	//set up mob stats and information
	private void setUpMob() {

		String[] mobNames = { "Goblin", "Skeleton", "Spider", "Bats", "Dragon" };
		String nameMob = mobNames[player.getLevel() % 5];
		mob = new Mob(nameMob);
		// For the demo, mob hp will be 7 times than player damage.
		mob.setCurHp(player.getDamage() * 7);
		mob.setMaxHp(player.getDamage() * 7);
		// For the demo, mob stamina will be 5 times its damage.
		mob.setCurStm(mob.getDamage() * 5);
		mob.setMaxStm(mob.getDamage() * 5);

		txtViewMobName = (TextView) this.findViewById(R.id.textViewMobName);
		txtViewMobName.setText(nameMob);
		txtViewMobHp = (TextView) this.findViewById(R.id.textViewMobHp);
		txtViewMobHp.setText("HP: " + mob.getCurHp() + "/" + mob.getMaxHp());

		txtViewMobStamina = (TextView) this
				.findViewById(R.id.textViewMobStamina);
		txtViewMobStamina.setText("Stamina: " + mob.getCurStm() + "/"
				+ mob.getMaxStm());

		mobMaxHp = mob.getMaxHp();
		mobCurHp = mob.getCurHp();

		// imgMob = (MobImage) this.findViewById(R.id.imageMob);
		// imgMob.setImageResource(imgMobs[(player.getLevel() % 5)]);
	}

	//hides submenu if you click away
	private void setUpHideListView() {

		lyoutBattle = (View) this.findViewById(R.id.layoutBattle);
		lyoutBattle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				listAbility.setVisibility(View.INVISIBLE);

				return true;
			}
		});

	}

	
	//dialog that displays when you press run away
	private void setUpDialogForRun() {

		alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("Run away...");

		// Setting Dialog Message
		alertDialog.setMessage("Are you sure you want to run away?");

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						//brings up the shopping screen
						Intent intentShopping = new Intent(
								BattleTestActivity.this,
								ShoppingTestActivity.class);
						player.setLevel(player.getLevel() + 1);
						intentShopping.putExtra(Player.PLAYER_DATA, player);
						startActivity(intentShopping);

						//ends battle activity
						finish();
					}
				});
		// When the user clicks "NO", the pop-up window goes away
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
	}

	
	//runs when the player wins
	private void setUpWinDialog() {

		winDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		winDialog.setTitle("YOU WIN!");

		ImageView imageV = new ImageView(this);
		imageV.setImageResource(R.drawable.victory);
		winDialog.setView(imageV);

		// User can only hit an OK button
		winDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						player.setLevel(player.getLevel() + 1);
						Intent intentShopping = new Intent(
								BattleTestActivity.this,
								ShoppingTestActivity.class);
						intentShopping.putExtra(Player.PLAYER_DATA, player);
						writeToFile();
						startActivity(intentShopping);
						finish();
					}
				});
	}

	//runs when you lose
	private void setLoseDialog() {

		loseDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		loseDialog.setTitle("YOU LOSE!");

		ImageView imageL = new ImageView(this);
		imageL.setImageResource(R.drawable.defeat);
		loseDialog.setView(imageL);

		// User can only hit an OK button
		loseDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intentLogin = new Intent(
								BattleTestActivity.this,
								LoginTestActivity.class);
						deleteFile(player.getNameFile());
						startActivity(intentLogin);
						finish();
					}
				});
	}



	//this is what runs when the enemy starts their turn
	public void enemyTurn() {
		//if the enemy has no stamina left
		if (mob.getCurStm() < 1) {
			Toast.makeText(getApplicationContext(),
					mob.getName() + " wheezes and stops to catch it's breath",
					Toast.LENGTH_LONG).show();
			++count;
			System.out.println("___----" + count);
			mob.setCurStm(mob.getMaxStm());// enemy regains all stamina but
											// is open for a free hit
			txtViewMobStamina.setText("Stamina: " + mob.getCurStm() + "/"
					+ mob.getMaxStm());
					
			//this should be removed
			if (player.getCurHp() <= 0) {
				txtViewPlayerHp.setText("HP: 0" + "/" + player.getMaxHp());
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						loseDialog.show();
					}
				}, Toast.LENGTH_LONG * 2700 * count);
			}
			playerDefending = false;// player's defense lasts only 1 turn
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					enableButton(true);
					count = 1;
				}

			}, Toast.LENGTH_LONG * 2700 * count);

		} 
		
		//if enemy does have stamina left at the start of the turn
		else {
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					//start the enemy attack animation
					//when the previous animation is finished
					imgBattle.setMobAttack();
				}

			}, 4000);

			int enemyAtk = rand.nextInt(3);
			// Light attack
			if (enemyAtk < 1) {
				attackEnemy(mob.getDamage());
			}
			// Medium attack
			else if (enemyAtk < 2) {
				atkVal = (int) (mob.getDamage() * mediumRatio);
				attackEnemy(atkVal);
			}
			// Heavy attack
			else if (enemyAtk < 3) {
				atkVal = mob.getDamage() * heavyRatio;// heavy attack is twice
														// the
				// base value
				attackEnemy(atkVal);
			}
		}

		//if the player dies
		if (player.getCurHp() <= 0) {
			Toast.makeText(getApplicationContext(), "You died!",
					Toast.LENGTH_LONG).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					enableButton(true);
					count = 1;
				}

			}, Toast.LENGTH_LONG * 2700 * count);
		}
	}

	// when the enemy attacks
	private void attackEnemy(int damage) {
		
		
		if (mob.getCurStm() >= damage) {
			mob.setCurStm(mob.getCurStm() - damage);// costs base stamina
		} else {
			mob.setCurStm(0);// can't have negative stamina
		}
		txtViewMobStamina.setText("Stamina: " + mob.getCurStm() + "/"
				+ mob.getMaxStm());
		d10Roll = rand.nextInt(10);
	
		// die roll 1 is a miss
		if (d10Roll < 2) {
			Toast.makeText(getApplicationContext(),
					mob.getName() + "'s attack missed!", Toast.LENGTH_LONG)
					.show();
			++count;
			System.out.println("___----" + count);
		} 
		//die roll 2-4 is reduced damage attack
		else if (d10Roll < 5) {
			damage /= 2;
			attackEnemyFinishMove(damage, mob.getName()
					+ " lands a glancing blow for ", 0);

		}
		//die roll 5-7 is normal attack
		else if (d10Roll < 8) {
			// no modification to base damage
			attackEnemyFinishMove(damage,
					mob.getName() + "'s attack hits for ", 1);
		} 
		//die roll 9 is a critical hit
		else if (d10Roll < 10) {
			damage = (int) (damage * 1.5);
			attackEnemyFinishMove(damage, mob.getName()
					+ " lands a critical hit for ", 2);
		}
		atkVal = 0;
		d10Roll = 0;
		playerDefending = false;
		System.out.println("Count before end mob attack " + count);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				enableButton(true);
				count = 1;
			}

		}, Toast.LENGTH_LONG * 2700 * count);
		System.out.println("Count after end mob attack " + count);
		if (player.getCurHp() <= 0) {
			loseDialog.show();
		}
	}

	//if the enemy doesn't miss, this will run
	private void attackEnemyFinishMove(int damage, String attackDescription,
			final int attackType) {
		if (playerDefending) {
			damage /= 2;
		}
		Toast.makeText(getApplicationContext(), attackDescription + damage + " damage!",
				Toast.LENGTH_LONG).show();
		++count;
		System.out.println("___----" + count);
		player.setCurHp(player.getCurHp() - damage);
		txtViewPlayerHp.setText("HP: " + player.getCurHp() + "/"
				+ player.getMaxHp());
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				animationEnemyAttack(attackType);
			}
		}, Toast.LENGTH_LONG * 1000 * count+2000);

	}

	//animates enemy attack
	private void animationEnemyAttack(int attackType) {
		switch (attackType) {
		case 0:
			ImageView imageEffect = (ImageView) this
					.findViewById(R.id.imageViewEffectToPlayer);
			imageEffect.setImageResource(R.drawable.moblightattack);
			attackEnemyPhysicAnimation(imageEffect);
			break;
		case 1:
			imageEffect = (ImageView) this
					.findViewById(R.id.imageViewEffectToPlayer);
			imageEffect.setImageResource(R.drawable.mobmediumattack);
			attackEnemyPhysicAnimation(imageEffect);
			break;
		case 2:
			imageEffect = (ImageView) this
					.findViewById(R.id.imageViewEffectToPlayer);
			imageEffect.setImageResource(R.drawable.mobheavyattack);
			attackEnemyPhysicAnimation(imageEffect);
			break;
		}

	}

	//runs the animation when the enemy does a physical attack
	private void attackEnemyPhysicAnimation(final ImageView imageEffect) {

/*
 * 		Animation animMove = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.move_right);
		// set animation listener
		animMove.setAnimationListener(this);

		// imgMob.startAnimation(animMove);

		final Animation animShake = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.shake);

		// set animation listener
		animShake.setAnimationListener(this);
 */
		

		final Animation animFadeout = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_out);

		// set animation listener
		animFadeout.setAnimationListener(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				//imgPlayer.startAnimation(animShake);
				imageEffect.startAnimation(animFadeout);
			}
		},  300);
	}

	//runs when the user clicks on a potion
	public void potionClick(String e, int position) {
		for (int i = 0; i < potions.length; i++) {
			if (potions[i] != null && potions[i].equals(e)
					&& potions[i].getSize() > 0) {
				
				if (potions[i].getItemType() == Item.ITEM_HEALTH_POTION) {
					player.setCurHp(player.getCurHp()
							+ potions[i].getStatNumber());
					if (player.getCurHp() > player.getMaxHp()) {
						player.setCurHp(player.getMaxHp());
					}
				
					txtViewPlayerHp.setText("HP: " + player.getCurHp()
							+ "/" + player.getMaxHp());	
				} 
				
				else if (potions[i].getItemType() == Item.ITEM_MANA_POTION) {

					player.setCurMana(player.getCurMana()
							+ potions[i].getStatNumber());
					if (player.getCurMana() > player.getMaxMana()) {
						player.setCurMana(player.getMaxMana());
					}

					txtViewPlayerMana.setText("Mana: "
							+ player.getCurMana() + "/"
							+ player.getMaxMana());
				} 
				
				else if (potions[i].getItemType() == Item.ITEM_STAMINA_POTION) {

					player.setCurStm(player.getCurStm()
							+ potions[i].getStatNumber());
					if (player.getCurStm() > player.getMaxStm()) {
						player.setCurStm(player.getMaxStm());
					}
					potions[i].setSize(potions[i].getSize() - 1);
					txtViewStamina.setText("Stamina: " + player.getCurStm()
							+ "/" + player.getMaxStm());
				}
				
				//reduce amount of potions, because one has been used
				potions[i].setSize(potions[i].getSize() - 1);
				
				//update the potion amount
				adapterItem.remove(e);
				if(potions[i].getSize()>0)
				{
					adapterItem.insert(potions[i].toString(), position);
				}
				
				break;
			}
		}
	}

	//runs when the player wins, updates player stats
	public void playerSetWin() {
		player.setCurHp(player.getMaxHp()); //restore health
		player.setCurMana(player.getMaxMana()); //restore mana
		player.setCurStm(player.getMaxStm()); //restore stamina
		player.setScore(player.getScore() + mob.getXP());
		player.setGold(player.getGold() + mob.getGold());
		player.setLevel(player.getLevel() + 1);
		player.setSkillPoint(player.getSkillPoint() + 5);// you get 5 skill
															// points for
															// defeating an
															// enemy

			//removes null potions from inventory
			for (int i = 0; i < potions.length; i++) {
			if (potions[i] != null) {
				player.getPlayerInventory()[i] = potions[i];
			}
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				winDialog.show();
			}
		}, Toast.LENGTH_LONG * 2000 * count);
	}

	//runs when the player wins, saves player info to a text file
	private void writeToFile() {
		File file = new File(getFilesDir(), player.getNameFile());
		FileOutputStream outputStream;

		try {
			if (!file.exists())
				file.createNewFile();
			outputStream = openFileOutput(player.getNameFile(),
					Context.MODE_PRIVATE);
			player.writeToFile(player, outputStream);
			// System.out.println("Test file");
			// BufferedReader inputReader = new BufferedReader(
			// new InputStreamReader(
			// openFileInput(Player.PLAYER_FILE)));
			//
			//
			// System.out.println(inputReader.readLine());
			// System.out.println(inputReader.readLine());
			// System.out.println(inputReader.readLine());
			// System.out.println(inputReader.readLine());
			// System.out.println(inputReader.readLine());
			// System.out.println(inputReader.readLine());
			// System.out.println(inputReader.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Start music
	private void playMusic() {
		medplay = MediaPlayer.create(this.getApplicationContext(),
				R.raw.clinthammer_battle);
		medplay.setLooping(true);
		medplay.start();
	}

	protected void onStart() {
		super.onStart();
		System.out.println("onStart - battle");
	}

	protected void onRestart() {
		super.onRestart();
		System.out.println("onRestart - battle");
	}

	protected void onResume() {
		super.onResume();
		System.out.println("onResume - battle");
		medplay.start();
	}

	protected void onPause() {
		super.onPause();
		System.out.println("onPause - battle");
		medplay.pause();
	}

	protected void onStop() {
		super.onStop();
		System.out.println("onStop - battle");
	}

	protected void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy - battle");
		medplay.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.battle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.effect) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {

	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

}
