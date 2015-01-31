package com.example.longdungeon;

import java.io.File;
import java.io.FileOutputStream;

import com.example.longdungeon.character.Player;
import com.example.longdungeon.item.Equipment;
import com.example.longdungeon.item.Item;
import com.example.longdungeon.item.Potion;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryTestActivity extends ActionBarActivity implements
		OnClickListener, OnItemClickListener {

	private TextView txtViewPlayerName, txtViewPlayerHp, txtViewPlayerMana,
			txtViewPlayerStm, txtViewPlayerDmg, txtViewPlayerDef, txtViewGold,
			txtViewScore;
	private ListView listItems;
	private ArrayAdapter<String> adapter;
	private AlertDialog.Builder alertDialog;
	private Player player;
	private MediaPlayer medplay;
	private int exp;
	private TextView txtEXP;
	private Equipment itemToEquip;
	private int prevHP, prevSTM, prevMANA, prevDMG, prevDEF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);

		getPlayerFromBundle();
		setUpButtonAction();
		setUpListItems();
		setUpTextView();
		playMusic();
		setStats();

		alertDialog = new AlertDialog.Builder(this);
	}

	private void setUpTextView() {
		txtViewPlayerName = (TextView) this
				.findViewById(R.id.textViewPlayerName);
		txtViewPlayerName.setText(player.getName());

		txtViewPlayerHp = (TextView) this.findViewById(R.id.textViewPlayerHp);
		txtViewPlayerHp.setText(player.getCurHp() + "/" + player.getMaxHp());

		txtViewPlayerMana = (TextView) this
				.findViewById(R.id.textViewPlayerMana);
		txtViewPlayerMana.setText(player.getCurMana() + "/"
				+ player.getMaxMana());

		txtViewPlayerStm = (TextView) this.findViewById(R.id.textViewPlayerStm);
		txtViewPlayerStm.setText(player.getCurStm() + "/" + player.getMaxStm());

		txtViewPlayerDmg = (TextView) this
				.findViewById(R.id.textViewPlayerDamage);
		txtViewPlayerDmg.setText(player.getDamage() + "");

		txtViewPlayerDef = (TextView) this
				.findViewById(R.id.textViewPlayerDefend);
		txtViewPlayerDef.setText(player.getDef() + "");

		txtViewGold = (TextView) this.findViewById(R.id.textViewGold);
		txtViewGold.setText("Gold: " + player.getGold());

		txtViewScore = (TextView) this.findViewById(R.id.textViewScore);
		txtViewScore.setText("Score: " + player.getScore());
	}

	private void getPlayerFromBundle() {

		Bundle fromBattle = getIntent().getExtras();
		player = fromBattle.getParcelable(Player.PLAYER_DATA);
	}

	private void setUpButtonAction() {
		this.findViewById(R.id.buttonAll).setOnClickListener(this);
		this.findViewById(R.id.buttonWeapon).setOnClickListener(this);
		this.findViewById(R.id.buttonHelmet).setOnClickListener(this);
		this.findViewById(R.id.buttonShield).setOnClickListener(this);
		this.findViewById(R.id.buttonCloth).setOnClickListener(this);
		this.findViewById(R.id.buttonRing).setOnClickListener(this);
		this.findViewById(R.id.buttonPotion).setOnClickListener(this);
		this.findViewById(R.id.buttonShop).setOnClickListener(this);
		this.findViewById(R.id.buttonBattle).setOnClickListener(this);
		this.findViewById(R.id.buttonSkill).setOnClickListener(this);
	}

	@Override
	public void onClick(View button) {
		if (adapter.getCount() > 0)
			adapter.clear();
		switch (button.getId()) {
		case R.id.buttonAll:
			displayAll();
			listItems.setAdapter(adapter);
			break;
		case R.id.buttonWeapon:
			displayWeapon();
			listItems.setAdapter(adapter);
			break;
		case R.id.buttonHelmet:
			displayHelmet();
			listItems.setAdapter(adapter);
			break;
		case R.id.buttonShield:
			displayShield();
			listItems.setAdapter(adapter);
			break;
		case R.id.buttonCloth:
			displayCloth();
			listItems.setAdapter(adapter);
			break;

		case R.id.buttonRing:
			displayRing();
			listItems.setAdapter(adapter);
			break;

		case R.id.buttonPotion:
			displayPotion();
			listItems.setAdapter(adapter);
			break;
		case R.id.buttonShop:
			Intent intentShop = new Intent(InventoryTestActivity.this,
					ShoppingTestActivity.class);
			intentShop.putExtra(Player.PLAYER_DATA, player);
			startActivity(intentShop);
			break;
		case R.id.buttonBattle:
			Intent intentBattle = new Intent(InventoryTestActivity.this,
					BattleTestActivity.class);
			intentBattle.putExtra(Player.PLAYER_DATA, player);
			startActivity(intentBattle);
			break;
		case R.id.buttonSkill:
			Button btn = (Button) this.findViewById(R.id.buttonSkill);
			if (btn.getText().equals("Skill")) {
				this.findViewById(R.id.scrollViewCategory).setVisibility(
						View.INVISIBLE);
				this.findViewById(R.id.listViewItems).setVisibility(
						View.INVISIBLE);
				this.findViewById(R.id.layoutSkill).setVisibility(View.VISIBLE);
				btn.setText("Inventory");
			} else {
				this.findViewById(R.id.scrollViewCategory).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.listViewItems).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.layoutSkill).setVisibility(
						View.INVISIBLE);
				btn.setText("Skill");
			}
			break;
		}

	}

	/*
	 * Display player equipment and inventory to list view if player presses All
	 * button.
	 */
	private void displayAll() {
		displayWeapon();
		displayHelmet();
		displayShield();
		displayCloth();
		displayRing();
		displayPotion();
	}

	private void displayWeapon() {
		Equipment equipments = player.getPlayerEquip()[Item.ITEM_SWORD];

		adapter.add(equipments.toString());

		Item[] inventory = player.getPlayerInventory();
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == Item.ITEM_SWORD) {
				equipments = (Equipment) inventory[i];

				adapter.add(equipments.toString());
			}
		}
	}

	private void displayHelmet() {
		Equipment equipments = player.getPlayerEquip()[Item.ITEM_HELMET];
		adapter.add(equipments.toString());

		Item[] inventory = player.getPlayerInventory();
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == Item.ITEM_HELMET) {
				equipments = (Equipment) inventory[i];
				adapter.add(equipments.toString());
			}
		}
	}

	private void displayShield() {
		Equipment equipments = player.getPlayerEquip()[Item.ITEM_SHIELD];
		adapter.add(equipments.toString());

		Item[] inventory = player.getPlayerInventory();
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == Item.ITEM_SHIELD) {
				equipments = (Equipment) inventory[i];
				adapter.add(equipments.toString());
			}
		}
	}

	private void displayCloth() {
		Equipment equipments = player.getPlayerEquip()[Item.ITEM_CLOTH];
		adapter.add(equipments.toString());

		Item[] inventory = player.getPlayerInventory();
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == Item.ITEM_CLOTH) {
				equipments = (Equipment) inventory[i];
				adapter.add(equipments.toString());
			}
		}
	}

	private void displayRing() {
		Equipment equipments = player.getPlayerEquip()[Item.ITEM_RING];
		adapter.add(equipments.toString());

		Item[] inventory = player.getPlayerInventory();
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == Item.ITEM_RING) {
				equipments = (Equipment) inventory[i];
				adapter.add(equipments.toString());
			}
		}
	}

	private void displayPotion() {
		Item[] inventory = player.getPlayerInventory();
		Potion potion;
		String name;
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == Item.ITEM_HEALTH_POTION
					|| inventory[i].getItemType() == Item.ITEM_MANA_POTION
					|| inventory[i].getItemType() == Item.ITEM_STAMINA_POTION) {
				potion = (Potion) inventory[i];
				name = "+" + potion.getStatNumber() + " "
						+ potion.getStatName() + " " + potion.getName();
				adapter.add(name);
			}
		}
	}

	private void setUpListItems() {
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1);

		listItems = (ListView) this.findViewById(R.id.listViewItems);
		listItems.setVisibility(View.VISIBLE);
		listItems.setOnItemClickListener(this);

		displayWeapon();
		listItems.setAdapter(adapter);

		this.findViewById(R.id.scrollViewCategory).setVisibility(View.VISIBLE);
		this.findViewById(R.id.listViewItems).setVisibility(View.VISIBLE);
		this.findViewById(R.id.layoutSkill).setVisibility(View.INVISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String value = (String) parent.getItemAtPosition(position);
		setUpConfirmBuy(value, position);
	}

	private void setUpConfirmBuy(final String message, final int position) {

		// Setting Dialog Title
		// alertDialog.setTitle("");

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String v = "null";
						if (!message.contains("Potion")) {
							equipItem(message, position);
							v = "Equiped " + message;
						} else {
							v = "Cannot equip potion!";
						}

						Toast.makeText(getApplicationContext(), v,
								Toast.LENGTH_SHORT).show();
					}
				});
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change, menu);
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

	// Equip item and update affected player stats and inventory screen
	private void equipItem(String message, int position) {
		for (int i = 0; i < player.getInventoryCurSpace(); i++) {
			if (player.getPlayerInventory()[i].equals(message)) {
				itemToEquip = (Equipment) player.getPlayerInventory()[i];
				Equipment itemToUnequip = player.getPlayerEquip(itemToEquip
						.getItemType());
				player.insertItemToInventory(itemToUnequip, i);
				player.insertNewEquipment(itemToEquip);
				itemToUnequip.setEquipped((byte) 0);
				itemToEquip.setEquipped((byte) 1);
				switch (itemToEquip.getItemType()) {
				case 0:
					player.setDamage(player.getDamage()
							- itemToUnequip.getStatNumber()
							+ itemToEquip.getStatNumber());
					txtViewPlayerDmg = (TextView) this
							.findViewById(R.id.textViewPlayerDamage);
					txtViewPlayerDmg.setText(player.getDamage() + "");
					prevDMG = player.getDamage();
					if (!adapter.isEmpty()) {
						adapter.clear();
					}
					displayWeapon();
					break;
				case 1:
					player.setDef(player.getDef()
							- itemToUnequip.getStatNumber()
							+ itemToEquip.getStatNumber());
					txtViewPlayerDef = (TextView) this
							.findViewById(R.id.textViewPlayerDefend);
					txtViewPlayerDef.setText(player.getDef() + "");
					prevDEF = player.getDef();
					if (!adapter.isEmpty()) {
						adapter.clear();
					}
					displayHelmet();
					break;
				case 2:
					player.setDef(player.getDef()
							- itemToUnequip.getStatNumber()
							+ itemToEquip.getStatNumber());
					txtViewPlayerDef = (TextView) this
							.findViewById(R.id.textViewPlayerDefend);
					txtViewPlayerDef.setText(player.getDef() + "");
					prevDEF = player.getDef();
					if (!adapter.isEmpty()) {
						adapter.clear();
					}
					displayShield();
					break;
				case 3:
					player.setDef(player.getDef()
							- itemToUnequip.getStatNumber()
							+ itemToEquip.getStatNumber());
					txtViewPlayerDef = (TextView) this
							.findViewById(R.id.textViewPlayerDefend);
					txtViewPlayerDef.setText(player.getDef() + "");
					prevDEF = player.getDef();
					if (!adapter.isEmpty()) {
						adapter.clear();
					}
					displayCloth();
					break;
				case 4:
					int newMANA = (player.getMaxMana()
							- itemToUnequip.getStatNumber() + itemToEquip
							.getStatNumber());
					player.setMaxMana(newMANA);
					player.setCurMana(newMANA);
					txtViewPlayerMana = (TextView) this
							.findViewById(R.id.textViewPlayerMana);
					txtViewPlayerMana.setText(player.getCurMana() + "/"
							+ player.getMaxMana());
					prevMANA = player.getMaxMana();
					if (!adapter.isEmpty()) {
						adapter.clear();
					}
					displayRing();
					break;
				}
				break;
			}
		}
	}

	// Sets player stats at start
	private void setStats() {
		exp = player.getSkillPoint();
		prevHP = player.getMaxHp();
		prevSTM = player.getMaxStm();
		prevMANA = player.getMaxMana();
		prevDMG = player.getDamage();
		prevDEF = player.getDef();
	}

	// Listeners for Skill menu
	public void onClickplusHP(View v) {
		TextView txtHP = (TextView) this
				.findViewById(R.id.textViewSkillHpInput);
		if (exp > 0) {
			String hpValue = (String) txtHP.getText();
			int hpint = Integer.parseInt(hpValue);
			txtHP.setText(Integer.toString(hpint + 1));
			exp--;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newHP = (prevHP + Integer.parseInt((String) txtHP.getText()));
			player.setMaxHp(newHP);
			player.setCurHp(newHP);
			txtViewPlayerHp = (TextView) this
					.findViewById(R.id.textViewPlayerHp);
			txtViewPlayerHp
					.setText(player.getCurHp() + "/" + player.getMaxHp());
		}
	}

	public void onClickminusHP(View v) {
		TextView txtHP = (TextView) this
				.findViewById(R.id.textViewSkillHpInput);
		int hp = Integer.parseInt((String) txtHP.getText());
		if (hp > 0) {
			txtHP.setText(Integer.toString(hp - 1));
			exp++;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newHP = (prevHP + Integer.parseInt((String) txtHP.getText()));
			player.setMaxHp(newHP);
			player.setCurHp(newHP);
			txtViewPlayerHp = (TextView) this
					.findViewById(R.id.textViewPlayerHp);
			txtViewPlayerHp
					.setText(player.getCurHp() + "/" + player.getMaxHp());
		}
	}

	public void onClickplusplusHP(View v) {
		TextView txtHP = (TextView) this
				.findViewById(R.id.textViewSkillHpInput);
		int hp = Integer.parseInt((String) txtHP.getText());
		if (exp > 0) {
			txtHP.setText(Integer.toString(hp + exp));
			exp = 0;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newHP = (prevHP + Integer.parseInt((String) txtHP.getText()));
			player.setMaxHp(newHP);
			player.setCurHp(newHP);
			txtViewPlayerHp = (TextView) this
					.findViewById(R.id.textViewPlayerHp);
			txtViewPlayerHp
					.setText(player.getCurHp() + "/" + player.getMaxHp());
		}
	}

	public void onClickminusminusHP(View v) {
		TextView txtHP = (TextView) this
				.findViewById(R.id.textViewSkillHpInput);
		int hp = Integer.parseInt((String) txtHP.getText());
		if (hp > 0) {
			txtHP.setText("0");
			exp += hp;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			player.setMaxHp(prevHP);
			player.setCurHp(prevHP);
			txtViewPlayerHp = (TextView) this
					.findViewById(R.id.textViewPlayerHp);
			txtViewPlayerHp
					.setText(player.getCurHp() + "/" + player.getMaxHp());
		}
	}

	public void onClickplusSTM(View v) {
		TextView txtSTM = (TextView) this
				.findViewById(R.id.textViewSkillStaminaInput);
		if (exp > 0) {
			String STMValue = (String) txtSTM.getText();
			int STMint = Integer.parseInt(STMValue);
			txtSTM.setText(Integer.toString(STMint + 1));
			exp--;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newSTM = (prevSTM + Integer.parseInt((String) txtSTM.getText()));
			player.setMaxStm(newSTM);
			player.setCurStm(newSTM);
			txtViewPlayerStm = (TextView) this
					.findViewById(R.id.textViewPlayerStm);
			txtViewPlayerStm.setText(player.getCurStm() + "/"
					+ player.getMaxStm());
		}
	}

	public void onClickminusSTM(View v) {
		TextView txtSTM = (TextView) this
				.findViewById(R.id.textViewSkillStaminaInput);
		int STM = Integer.parseInt((String) txtSTM.getText());
		if (STM > 0) {
			txtSTM.setText(Integer.toString(STM - 1));
			exp++;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newSTM = (prevSTM + Integer.parseInt((String) txtSTM.getText()));
			player.setMaxStm(newSTM);
			player.setCurStm(newSTM);
			txtViewPlayerStm = (TextView) this
					.findViewById(R.id.textViewPlayerStm);
			txtViewPlayerStm.setText(player.getCurStm() + "/"
					+ player.getMaxStm());
		}
	}

	public void onClickplusplusSTM(View v) {
		TextView txtSTM = (TextView) this
				.findViewById(R.id.textViewSkillStaminaInput);
		int STM = Integer.parseInt((String) txtSTM.getText());
		if (exp > 0) {
			txtSTM.setText(Integer.toString(STM + exp));
			exp = 0;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newSTM = (prevSTM + Integer.parseInt((String) txtSTM.getText()));
			player.setMaxStm(newSTM);
			player.setCurStm(newSTM);
			txtViewPlayerStm = (TextView) this
					.findViewById(R.id.textViewPlayerStm);
			txtViewPlayerStm.setText(player.getCurStm() + "/"
					+ player.getMaxStm());
		}
	}

	public void onClickminusminusSTM(View v) {
		TextView txtSTM = (TextView) this
				.findViewById(R.id.textViewSkillStaminaInput);
		int STM = Integer.parseInt((String) txtSTM.getText());
		if (STM > 0) {
			txtSTM.setText("0");
			exp += STM;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			player.setMaxStm(prevSTM);
			player.setCurStm(prevSTM);
			txtViewPlayerStm = (TextView) this
					.findViewById(R.id.textViewPlayerStm);
			txtViewPlayerStm.setText(player.getCurStm() + "/"
					+ player.getMaxStm());
		}
	}

	public void onClickplusMANA(View v) {
		TextView txtMANA = (TextView) this
				.findViewById(R.id.textViewSkillManaInput);
		if (exp > 0) {
			String MANAValue = (String) txtMANA.getText();
			int MANAint = Integer.parseInt(MANAValue);
			txtMANA.setText(Integer.toString(MANAint + 1));
			exp--;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newMANA = (prevMANA + Integer.parseInt((String) txtMANA
					.getText()));
			player.setMaxMana(newMANA);
			player.setCurMana(newMANA);
			txtViewPlayerMana = (TextView) this
					.findViewById(R.id.textViewPlayerMana);
			txtViewPlayerMana.setText(player.getCurMana() + "/"
					+ player.getMaxMana());
		}
	}

	public void onClickminusMANA(View v) {
		TextView txtMANA = (TextView) this
				.findViewById(R.id.textViewSkillManaInput);
		int MANA = Integer.parseInt((String) txtMANA.getText());
		if (MANA > 0) {
			txtMANA.setText(Integer.toString(MANA - 1));
			exp++;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newMANA = (prevMANA + Integer.parseInt((String) txtMANA
					.getText()));
			player.setMaxMana(newMANA);
			player.setCurMana(newMANA);
			txtViewPlayerMana = (TextView) this
					.findViewById(R.id.textViewPlayerMana);
			txtViewPlayerMana.setText(player.getCurMana() + "/"
					+ player.getMaxMana());
		}
	}

	public void onClickplusplusMANA(View v) {
		TextView txtMANA = (TextView) this
				.findViewById(R.id.textViewSkillManaInput);
		int MANA = Integer.parseInt((String) txtMANA.getText());
		if (exp > 0) {
			txtMANA.setText(Integer.toString(MANA + exp));
			exp = 0;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			int newMANA = (prevMANA + Integer.parseInt((String) txtMANA
					.getText()));
			player.setMaxMana(newMANA);
			player.setCurMana(newMANA);
			txtViewPlayerMana = (TextView) this
					.findViewById(R.id.textViewPlayerMana);
			txtViewPlayerMana.setText(player.getCurMana() + "/"
					+ player.getMaxMana());
		}
	}

	public void onClickminusminusMANA(View v) {
		TextView txtMANA = (TextView) this
				.findViewById(R.id.textViewSkillManaInput);
		int MANA = Integer.parseInt((String) txtMANA.getText());
		if (MANA > 0) {
			txtMANA.setText("0");
			exp += MANA;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			player.setMaxMana(prevMANA);
			player.setCurMana(prevMANA);
			txtViewPlayerMana = (TextView) this
					.findViewById(R.id.textViewPlayerMana);
			txtViewPlayerMana.setText(player.getCurMana() + "/"
					+ player.getMaxMana());
		}
	}

	public void onClickplusDMG(View v) {
		TextView txtDMG = (TextView) this
				.findViewById(R.id.textViewSkillDMGInput);
		if (exp > 0) {
			String DMGValue = (String) txtDMG.getText();
			int DMGint = Integer.parseInt(DMGValue);
			txtDMG.setText(Integer.toString(DMGint + 1));
			exp--;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			DMGValue = (String) txtDMG.getText();
			int newDMG = (prevDMG + Integer.parseInt(DMGValue));
			player.setDamage(newDMG);
			txtViewPlayerDmg = (TextView) this
					.findViewById(R.id.textViewPlayerDamage);
			txtViewPlayerDmg.setText(player.getDamage() + "");
		}
	}

	public void onClickminusDMG(View v) {
		TextView txtDMG = (TextView) this
				.findViewById(R.id.textViewSkillDMGInput);
		int DMG = Integer.parseInt((String) txtDMG.getText());
		if (DMG > 0) {
			txtDMG.setText(Integer.toString(DMG - 1));
			exp++;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			String DMGValue = (String) txtDMG.getText();
			DMGValue = (String) txtDMG.getText();
			int newDMG = (prevDMG + Integer.parseInt(DMGValue));
			player.setDamage(newDMG);
			txtViewPlayerDmg = (TextView) this
					.findViewById(R.id.textViewPlayerDamage);
			txtViewPlayerDmg.setText(player.getDamage() + "");
		}
	}

	public void onClickplusplusDMG(View v) {
		TextView txtDMG = (TextView) this
				.findViewById(R.id.textViewSkillDMGInput);
		int DMG = Integer.parseInt((String) txtDMG.getText());
		if (exp > 0) {
			txtDMG.setText(Integer.toString(DMG + exp));
			exp = 0;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			String DMGValue = (String) txtDMG.getText();
			DMGValue = (String) txtDMG.getText();
			int newDMG = (prevDMG + Integer.parseInt(DMGValue));
			player.setDamage(newDMG);
			txtViewPlayerDmg = (TextView) this
					.findViewById(R.id.textViewPlayerDamage);
			txtViewPlayerDmg.setText(player.getDamage() + "");
		}
	}

	public void onClickminusminusDMG(View v) {
		TextView txtDMG = (TextView) this
				.findViewById(R.id.textViewSkillDMGInput);
		int DMG = Integer.parseInt((String) txtDMG.getText());
		if (DMG > 0) {
			txtDMG.setText("0");
			exp += DMG;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			player.setDamage(prevDMG);
			txtViewPlayerDmg = (TextView) this
					.findViewById(R.id.textViewPlayerDamage);
			txtViewPlayerDmg.setText(player.getDamage() + "");
		}
	}

	public void onClickplusDFND(View v) {
		TextView txtDFND = (TextView) this
				.findViewById(R.id.textViewSkillDEFENDInput);
		if (exp > 0) {
			String DFNDValue = (String) txtDFND.getText();
			int DFNDint = Integer.parseInt(DFNDValue);
			txtDFND.setText(Integer.toString(DFNDint + 1));
			exp--;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			DFNDValue = (String) txtDFND.getText();
			int newDEF = (prevDEF + Integer.parseInt(DFNDValue));
			player.setDef(newDEF);
			txtViewPlayerDef = (TextView) this
					.findViewById(R.id.textViewPlayerDefend);
			txtViewPlayerDef.setText(player.getDef() + "");
		}
	}

	public void onClickminusDFND(View v) {
		TextView txtDFND = (TextView) this
				.findViewById(R.id.textViewSkillDEFENDInput);
		int DFND = Integer.parseInt((String) txtDFND.getText());
		if (DFND > 0) {
			txtDFND.setText(Integer.toString(DFND - 1));
			exp++;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			String DFNDValue = (String) txtDFND.getText();
			DFNDValue = (String) txtDFND.getText();
			int newDEF = (prevDEF + Integer.parseInt(DFNDValue));
			player.setDef(newDEF);
			txtViewPlayerDef = (TextView) this
					.findViewById(R.id.textViewPlayerDefend);
			txtViewPlayerDef.setText(player.getDef() + "");
		}
	}

	public void onClickplusplusDFND(View v) {
		TextView txtDFND = (TextView) this
				.findViewById(R.id.textViewSkillDEFENDInput);
		int DFND = Integer.parseInt((String) txtDFND.getText());
		if (exp > 0) {
			txtDFND.setText(Integer.toString(DFND + exp));
			exp = 0;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			String DFNDValue = (String) txtDFND.getText();
			DFNDValue = (String) txtDFND.getText();
			int newDEF = (prevDEF + Integer.parseInt(DFNDValue));
			player.setDef(newDEF);
			txtViewPlayerDef = (TextView) this
					.findViewById(R.id.textViewPlayerDefend);
			txtViewPlayerDef.setText(player.getDef() + "");
		}
	}

	public void onClickminusminusDFND(View v) {
		TextView txtDFND = (TextView) this
				.findViewById(R.id.textViewSkillDEFENDInput);
		int DFND = Integer.parseInt((String) txtDFND.getText());
		if (DFND > 0) {
			txtDFND.setText("0");
			exp += DFND;
			txtEXP = (TextView) this.findViewById(R.id.textViewSkillNumber);
			txtEXP.setText(Integer.toString(exp));
			player.setDef(prevDEF);
			txtViewPlayerDef = (TextView) this
					.findViewById(R.id.textViewPlayerDefend);
			txtViewPlayerDef.setText(player.getDef() + "");
		}
	}

	// End listeners for skills

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
				R.raw.clinthammer_equip);
		medplay.setLooping(true);
		medplay.start();
	}

	protected void onStart() {
		super.onStart();
		System.out.println("onStart - shop");
	}

	protected void onRestart() {
		super.onRestart();
		System.out.println("onRestart - shop");
	}

	protected void onResume() {
		super.onResume();
		System.out.println("onResume - shop");
		medplay.start();
	}

	/**
	 * Data save when player doesn't play anymore.
	 */
	protected void onPause() {
		super.onPause();
		medplay.pause();
		writeToFile();
	}

	protected void onStop() {
		super.onStop();
		System.out.println("onStop - shop");
	}

	protected void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy - shop");
		medplay.stop();
	}

}