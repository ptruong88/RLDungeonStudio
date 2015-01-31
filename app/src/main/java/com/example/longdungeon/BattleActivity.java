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

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class BattleActivity extends ActionBarActivity implements
		OnClickListener, OnItemClickListener, AnimationListener {

	private ListView listAbility;
	private View lyoutBattle;
	private TextView txtViewMobName, txtViewMobHp, txtViewPlayerName,
			txtViewPlayerScore, txtViewPlayerHp, txtViewPlayerMana,
			txtViewStamina, txtViewMobStamina;
	private Mob mob;
	private Player player;

	private Button btnAttack, btnDefend, btnMagic, btnItem, btnRun;
	private String[] listAttack, listMagic, listItem;
	private ArrayAdapter<String> adapterAttack, adapterMagic, adapterItem;

	private static int[] imgMobs = new int[] { R.drawable.goblin,
			R.drawable.skeleton, R.drawable.spider, R.drawable.bats,
			R.drawable.dragon };
	private ImageView imgMob, imgPlayer;

	// gordon's variables for the game loop
	// boolean playerTurn = true;
	boolean playerDefending = false;
	boolean enemyDefending = false;
	boolean ranAway = false;
	private int baseStm, baseMana;
	final private double mediumRatio = 1.3;
	final private int heavyRatio = 2;
	int d10Roll = 0;
	int atkVal;
	// Animation

	private Potion[] potions;
	private MediaPlayer medplay;
	private Random rand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle);
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
	}

	private void setUpListView() {
		listAbility = (ListView) this.findViewById(R.id.listViewAbilityOptions);
		listAbility.setOnItemClickListener(this);
	}

	private int baseDamage;// Player Attack

	private void setUpStringForListView() {
		listAttack = new String[3];
		// { "Heavy Attack 10DMG/10STM",
		// "Medium Attack 15DMG/15STM",
		// "Light Attack 20DMG/20STM" };
		baseDamage = player.getDamage();
		baseStm = player.getMaxStm() / 10;
		String attack = nameSkill("Light Attack ", baseDamage, baseStm, "STM");
		listAttack[0] = attack;
		attack = nameSkill("Medium Attack ", (int) (baseDamage * mediumRatio),
				(int) (baseStm * mediumRatio), "STM");
		listAttack[1] = attack;
		attack = nameSkill("Heavy Attack ", baseDamage * heavyRatio, baseStm
				* heavyRatio, "STM");
		listAttack[2] = attack;
		adapterAttack = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1,
				listAttack);

		baseMana = player.getMaxMana() / 10;
		listMagic = new String[3];
		attack = nameSkill("Fire Magic ", baseDamage, baseMana, "MANA");
		listMagic[0] = attack;
		attack = nameSkill("Ice Magic ", (int) (baseDamage * mediumRatio),
				(int) (baseMana * mediumRatio), "MANA");
		listMagic[1] = attack;
		attack = nameSkill("Lightning Magic ", baseDamage * heavyRatio,
				baseMana * heavyRatio, "MANA");
		listMagic[2] = attack;
		adapterMagic = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1,
				listMagic);

		adapterItem = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1);

		potions = new Potion[player.getInventoryCurSpace()];
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (player.getPlayerInventory()[i].getItemType() == Item.ITEM_HEALTH_POTION
					|| player.getPlayerInventory()[i].getItemType() == Item.ITEM_STAMINA_POTION
					|| player.getPlayerInventory()[i].getItemType() == Item.ITEM_MANA_POTION) {
				potions[i] = (Potion) player.getPlayerInventory()[i];
				adapterItem.add(potions[i].toString());
			}
		}

	}

	private String nameSkill(String string, int damage, int baseCost,
			String string2) {
		return string + damage + "DMG / " + baseCost + string2;
	}

	private void setUpButtonAction() {
		this.findViewById(R.id.buttonAttack).setOnClickListener(this);
		this.findViewById(R.id.buttonDefend).setOnClickListener(this);
		this.findViewById(R.id.buttonMagic).setOnClickListener(this);
		this.findViewById(R.id.buttonItem).setOnClickListener(this);
		this.findViewById(R.id.buttonRun).setOnClickListener(this);
	}

	private void enableButton(boolean enabled) {
		this.findViewById(R.id.buttonAttack).setClickable(enabled);
		this.findViewById(R.id.buttonDefend).setClickable(enabled);
		this.findViewById(R.id.buttonMagic).setClickable(enabled);
		this.findViewById(R.id.buttonItem).setClickable(enabled);
		this.findViewById(R.id.buttonRun).setClickable(enabled);
	}

	public void onClick(View button) {
		switch (button.getId()) {
		case R.id.buttonAttack:
			listAbility.setAdapter(adapterAttack);
			listAbility.setVisibility(View.VISIBLE);
			break;
		case R.id.buttonDefend: {
			playerDefending = true;
			if (player.getCurStm() < player.getMaxStm()) {
				int stmRegen = (player.getCurStm() / 5);
				player.setCurStm(stmRegen + player.getCurStm());// get back 1/5
																// of your
																// stamina
				txtViewStamina.setText("Stamina: " + player.getCurStm() + "/"
						+ player.getMaxStm());

			}

			enableButton(false);

			enemyTurn();// defending uses your turn
		}
			break;
		case R.id.buttonMagic:
			listAbility.setAdapter(adapterMagic);
			listAbility.setVisibility(View.VISIBLE);
			break;
		case R.id.buttonItem:
			listAbility.setAdapter(adapterItem);
			listAbility.setVisibility(View.VISIBLE);
			break;
		default:// button run way
			alertDialog.show();
			break;
		}

	}

	int mobMaxHp, mobCurHp;

	// Display what item is click on list view, such attack type, magic item, or
	// item type.
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// Toast.makeText(getApplicationContext(),
		// parent.getItemAtPosition(position).toString(),
		// Toast.LENGTH_LONG).show();
		listAbility.setVisibility(View.INVISIBLE);
		enableButton(false);
		if (parent.getItemAtPosition(position).toString().contains("Attack")) {
			// mobCurHp -= baseDamage;
			switch (position) {
			case 0:// basic attack case based on it being in the 0th position
				attackPlayer(baseDamage, baseStm, 0);
				break;
			case 1:// medium attack case based on it being in the 1st position
					// medium damage is more 4/3 than normal attack
				attackPlayer((int) (baseDamage * mediumRatio),
						(int) (baseStm * mediumRatio), 1);
				break;
			default:// heavy attack case based on it being in the 2nd
					// position
				// Heavy attack value is 2 times the base attack
				attackPlayer(baseDamage * heavyRatio, baseStm * heavyRatio, 2);
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
		}//

		else if (parent.getItemAtPosition(position).toString()
				.contains("Potion")) {
			potionClick(parent.getItemAtPosition(position).toString(), position);
			enemyTurn();
		}
	}

	int count = 1;

	private int defenseModDamage(int baseDmg,int defenseRating)
	{
		int modDamage=0;
		int d3Roll = rand.nextInt(3);
		if (d3Roll < 1){modDamage = baseDmg;}
		if (d3Roll < 2){modDamage = baseDmg-(defenseRating/2);}
		if (d3Roll < 3){modDamage = baseDmg-defenseRating;}
		if(modDamage<0){modDamage=0;}
		return modDamage;
		
	}
	
	
	private void magicPlayer(int damage, int mana, int attackType) {
		if (player.getCurMana() >= mana) {
			player.setCurMana((player.getCurMana()) - mana);
			txtViewPlayerMana.setText("Mana: " + player.getCurMana() + "/"
					+ player.getMaxMana());// attacks always cost mana, if they
											// miss or not

			int d3Roll = rand.nextInt(3);
			if (d3Roll < 1) {
				Toast.makeText(getApplicationContext(), "Your spell missed!",
						Toast.LENGTH_LONG).show();
				++count;
				System.out.println("___----" + count);
			}

			else if (d3Roll < 2) {
				// spells ignore defense
				String atkString = "Spell hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}

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

	private void attackPlayer(int damage, int stm, int attackType) {
		if (player.getCurStm() >= stm) {
			player.setCurStm((player.getCurStm()) - stm);
			txtViewStamina.setText("Stamina: " + player.getCurStm() + "/"
					+ player.getMaxStm());// attacks always cost
											// stamina, if they miss
											// or not

			d10Roll = rand.nextInt(10);
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
				damage = defenseModDamage(damage,mob.getDef());
				String atkString = "Glancing hit for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			// Full damage
			else if (d10Roll < 8) {
				damage = defenseModDamage(damage,mob.getDef());
				String atkString = "Attack hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			// Critical damage
			else if (d10Roll < 10) {
				damage *= 2;// critical attack doubles
				// damage
				damage = defenseModDamage(damage,mob.getDef());
				String atkString = "Critical attack hits for ";
				attackPlayerFinishMove(damage, atkString, attackType);
			}
			atkVal = 0;// clear attack val;
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
		animationPlayerAttack(attackType);
	}

	private boolean getEnemyDefending() {
		int a = rand.nextInt(10);
		return a > 7;
	}

	private void animationPlayerAttack(int attackType) {
		// load the animation
		switch (attackType) {
		case 0:
			ImageView imageEffect = (ImageView) this
					.findViewById(R.id.imageViewEffect);
			imageEffect.setImageResource(R.drawable.playerlightattack);
			attackPhysicAnimation(imageEffect);
			break;
		case 1:
			imageEffect = (ImageView) this.findViewById(R.id.imageViewEffect);
			imageEffect.setImageResource(R.drawable.playermediumattack);
			attackPhysicAnimation(imageEffect);
			break;
		case 2:
			imageEffect = (ImageView) this.findViewById(R.id.imageViewEffect);
			imageEffect.setImageResource(R.drawable.playerheavyattack);
			attackPhysicAnimation(imageEffect);
			break;
		case 3:
			ImageView imageMagic = (ImageView) this
					.findViewById(R.id.imageViewMagic);
			imageMagic.setImageResource(R.drawable.playerfireball);
			attackMagicAnimation(imageMagic);
			break;
		case 4:
			imageMagic = (ImageView) this.findViewById(R.id.imageViewMagic);
			imageMagic.setImageResource(R.drawable.playericeblast);
			attackMagicAnimation(imageMagic);
			break;
		default:
			imageMagic = (ImageView) this.findViewById(R.id.imageViewMagic);
			imageMagic.setImageResource(R.drawable.playerlightning);
			attackMagicAnimation(imageMagic);
			break;
		}

	}

	private void attackMagicAnimation(final ImageView imageMagic) {
		Animation animMove = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.move_magic_player);
		// set animation listener
		animMove.setAnimationListener(this);

		imageMagic.startAnimation(animMove);

		final Animation animShake = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.shake);

		// set animation listener
		animShake.setAnimationListener(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				imgMob.startAnimation(animShake);
				// imageMagic.setVisibility(View.INVISIBLE);
			}
		}, animMove.getDuration() + 100);
	}

	private void attackPhysicAnimation(final ImageView imageEffect) {
		Animation animMove = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.move);

		// set animation listener
		animMove.setAnimationListener(this);

		final Animation animShake = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.shake);
		// set animation listener
		animShake.setAnimationListener(this);

		final Animation animFadeout = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_out);
		// set animation listener
		animFadeout.setAnimationListener(this);

		imgPlayer.startAnimation(animMove);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				imgMob.startAnimation(animShake);
				imageEffect.startAnimation(animFadeout);
			}
		}, animMove.getDuration() + 200);
	}

	private void setUpPlayer() {
		// get player image
		imgPlayer = (ImageView) this.findViewById(R.id.imagePlayer);

		txtViewPlayerName = (TextView) this
				.findViewById(R.id.textViewPlayerName);
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

		imgMob = (ImageView) this.findViewById(R.id.imageMob);
		imgMob.setImageResource(imgMobs[(player.getLevel() % 5)]);
	}

	private void setUpHideListView() {

		lyoutBattle = (View) this.findViewById(R.id.layoutBattle);
		lyoutBattle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				listAbility.setVisibility(View.INVISIBLE);
				// listAbility.postDelayed(new Runnable() {
				// @Override
				// public void run() {
				// listAbility.setVisibility(View.GONE); // or
				// // View.INVISIBLE
				// // as Jason
				// // Leung wrote
				// }
				// }, 3000);
				return true;
			}
		});

	}

	private AlertDialog.Builder alertDialog;

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
						// Write your code here to execute after dialog
						// Toast.makeText(getApplicationContext(),
						// "You clicked on YES",
						// Toast.LENGTH_LONG).show();
						Intent intentShopping = new Intent(BattleActivity.this,
								ShoppingActivity.class);
						player.setLevel(player.getLevel()+1);
						intentShopping.putExtra(Player.PLAYER_DATA, player);
						startActivity(intentShopping);
						finish();
					}
				});
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						// Toast.makeText(getApplicationContext(),
						// "You clicked on NO", Toast.LENGTH_LONG)
						// .show();
						dialog.cancel();
					}
				});
	}

	private AlertDialog.Builder winDialog;

	private void setUpWinDialog() {

		winDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		winDialog.setTitle("YOU WIN!");

		ImageView imageV = new ImageView(this);
		imageV.setImageResource(R.drawable.victory);
		winDialog.setView(imageV);

		// Setting Positive "Yes" Btn
		winDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						// Toast.makeText(getApplicationContext(),
						// "You clicked on YES",
						// Toast.LENGTH_LONG).show();
						player.setLevel(player.getLevel() + 1);
						Intent intentShopping = new Intent(BattleActivity.this,
								ShoppingActivity.class);
						intentShopping.putExtra(Player.PLAYER_DATA, player);
						writeToFile();
						startActivity(intentShopping);
						finish();
					}
				});
	}

	private AlertDialog.Builder loseDialog;

	private void setLoseDialog() {

		loseDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		loseDialog.setTitle("YOU LOSE!");

		ImageView imageL = new ImageView(this);
		imageL.setImageResource(R.drawable.defeat);
		loseDialog.setView(imageL);

		// Setting Positive "Yes" Btn
		loseDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						// Toast.makeText(getApplicationContext(),
						// "You clicked on YES",
						// Toast.LENGTH_LONG).show();
						Intent intentLogin = new Intent(BattleActivity.this,
								LoginActivity.class);
						deleteFile(player.getNameFile());
						startActivity(intentLogin);
						finish();
					}
				});
	}

	// private void setUpPic() {
	//
	// BattleLayout relLyoutPic = (BattleLayout) this
	// .findViewById(R.id.layoutPic);
	// int lyoutX = relLyoutPic.getMeasuredWidth();
	// int lyoutY = relLyoutPic.getMeasuredHeight();
	//
	// System.out.println("Layout width " + lyoutX);
	// System.out.println("Layout height " + lyoutY);
	//
	// imgMob = (ImageView) this.findViewById(R.id.imageMob);
	// imgMob.getLayoutParams().width = (int) (lyoutX * 0.6);
	// imgMob.getLayoutParams().height = lyoutY;
	// // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	// // (int)(lyoutX*0.6), lyoutY);
	// // imgMob.setLayoutParams(params);
	//
	// imgPlayer = (ImageView) this.findViewById(R.id.imagePlayer);
	// // params = new RelativeLayout.LayoutParams(
	// // lyoutX/2, lyoutY/2);
	// // imgPlayer.setLayoutParams(params);
	// imgPlayer.getLayoutParams().width = (int) (lyoutX * 0.5);
	// imgPlayer.getLayoutParams().height = (int) (lyoutY * 0.8);
	// }

	public void enemyTurn() {
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

		} else {
			Toast.makeText(getApplicationContext(),
					mob.getName() + " attacks!", Toast.LENGTH_LONG).show();
			++count;
			System.out.println("___----" + count);
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

		if (player.getCurHp() == 0) {
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

	private void attackEnemy(int damage) {
		if (mob.getCurStm() >= damage) {
			mob.setCurStm(mob.getCurStm() - damage);// costs base stamina
		} else {
			mob.setCurStm(0);// can't have negative stamina
		}
		txtViewMobStamina.setText("Stamina: " + mob.getCurStm() + "/"
				+ mob.getMaxStm());
		d10Roll = rand.nextInt(10);
		if (d10Roll < 2) {
			Toast.makeText(getApplicationContext(),
					mob.getName() + "'s attack missed!", Toast.LENGTH_LONG)
					.show();
			++count;
			System.out.println("___----" + count);
		} else if (d10Roll < 5) {
			damage /= 2;
			damage = defenseModDamage(damage,player.getDef());
			attackEnemyFinishMove(damage, mob.getName()
					+ " lands a glancing blow for ", 0);

		} else if (d10Roll < 8) {
			// no modification to base damage
			damage = defenseModDamage(damage,player.getDef());
			attackEnemyFinishMove(damage,
					mob.getName() + "'s attack hits for ", 1);
		} else if (d10Roll < 10) {
			damage = (int) (damage * 1.5);
			damage = defenseModDamage(damage,player.getDef());
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

	private void attackEnemyFinishMove(int damage, String string,
			final int attackType) {
		if (playerDefending) {
			damage /= 2;
		}
		Toast.makeText(getApplicationContext(), string + damage + "damage!",
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
		}, Toast.LENGTH_LONG * 1000 * count);

	}

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

	private void attackEnemyPhysicAnimation(final ImageView imageEffect) {
		Animation animMove = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.move_right);
		// set animation listener
		animMove.setAnimationListener(this);

		imgMob.startAnimation(animMove);

		final Animation animShake = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.shake);

		// set animation listener
		animShake.setAnimationListener(this);

		final Animation animFadeout = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_out);

		// set animation listener
		animFadeout.setAnimationListener(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				imgPlayer.startAnimation(animShake);
				imageEffect.startAnimation(animFadeout);
			}
		}, animMove.getDuration() + 100);
	}

	public void potionClick(String e, int position) {
		for (int i = 0; i < potions.length; i++) {
			if (potions[i] != null && potions[i].equals(e)
					&& potions[i].getSize() > 0) {
				if (potions[i].getItemType() == Item.ITEM_HEALTH_POTION) {
					if (potions[i].getSize() > 0) {
						player.setCurHp(player.getCurHp()
								+ potions[i].getStatNumber());
						if (player.getCurHp() > player.getMaxHp()) {
							player.setCurHp(player.getMaxHp());
						}
						player.setCurHp(player.getCurHp()
								+ potions[i].getStatNumber());
						potions[i].setSize(potions[i].getSize() - 1);
						txtViewPlayerHp.setText("HP: " + player.getCurHp()
								+ "/" + player.getMaxHp());
						adapterItem.remove(e);
						adapterItem.insert(potions[i].toString(), position);
						break;
					}
				} else if (potions[i].getItemType() == Item.ITEM_MANA_POTION) {
					if (potions[i].getSize() > 0) {
						player.setCurMana(player.getCurMana()
								+ potions[i].getStatNumber());
						if (player.getCurMana() > player.getMaxMana()) {
							player.setCurMana(player.getMaxMana());
						}
						potions[i].setSize(potions[i].getSize() - 1);
						txtViewPlayerMana.setText("Mana: "
								+ player.getCurMana() + "/"
								+ player.getMaxMana());
						adapterItem.remove(e);
						adapterItem.insert(potions[i].toString(), position);
						break;
					}
				} else if (potions[i].getItemType() == Item.ITEM_STAMINA_POTION) {
					if (potions[i].getSize() > 0) {
						player.setCurStm(player.getCurStm()
								+ potions[i].getStatNumber());
						if (player.getCurStm() > player.getMaxStm()) {
							player.setCurStm(player.getMaxStm());
						}
						potions[i].setSize(potions[i].getSize() - 1);
						txtViewStamina.setText("Stamina: " + player.getCurStm()
								+ "/" + player.getMaxStm());
						adapterItem.remove(e);
						adapterItem.insert(potions[i].toString(), position);
						break;
					}
				}
			}
		}
	}

	// public int randomWithRange(int min, int max)// used for rolls to hit
	// {
	// int range = (max - min) + 1;
	// return (int) (Math.random() * range) + min;
	// }

	public void playerSetWin() {
		player.setCurHp(player.getMaxHp());
		player.setCurMana(player.getMaxMana());
		player.setCurStm(player.getMaxStm());
		player.setScore(player.getScore() + mob.getXP());
		player.setGold(player.getGold() + mob.getGold());
		player.setLevel(player.getLevel() + 1);
		player.setSkillPoint(player.getSkillPoint() + 5);// you get 5 skill
															// points for
															// defeating an
															// enemy
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
