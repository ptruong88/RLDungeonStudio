package com.example.longdungeon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShoppingTestActivity extends ActionBarActivity implements
		OnClickListener, OnItemClickListener {

	private TextView txtViewGold;

	private ArrayAdapter<String> adapterShop, adapterInventory;
	private ListView listItems, listItemsInventory;
	private AlertDialog.Builder alertDialog, alertDialogCancelBuy,
			alertDialogSell;
	private Player player;
	private Equipment[] sellWeapon, sellHelmet, sellShield, sellCloth,
			sellRing;
	private Potion[] sellHealPotion, sellManaPotion, sellStaminaPotion;
	private MediaPlayer medplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_shopping_test);

		getPlayerFromBundle();
		setUpPlayerTextView();
		setUpEquipmentToSell();
		setUpPotionToSell();
		setUpBuyingDialog();
		setUpSellingDialog();
		setUpCancelBuyingDialog();
		setUpListSellAndInventory();
		setUpButton();
		playMusic();
	}

	/**
	 * Get player information from bundle.
	 */
	private void getPlayerFromBundle() {
		// TODO Auto-generated method stub
		Bundle fromBattle = getIntent().getExtras();
		player = fromBattle.getParcelable(Player.PLAYER_DATA);

	}

	/**
	 * Set player gold to text view.
	 */
	private void setUpPlayerTextView() {
		// TODO Auto-generated method stub
		txtViewGold = (TextView) this.findViewById(R.id.textViewGold);
		txtViewGold.setText("Gold: " + player.getGold());
	}

	/**
	 * Set equipment to sell in array.
	 */
	private void setUpEquipmentToSell() {
		String[] materialsEquip = { "Iron", "Bronze", "Silver", "Gold",
				"Platinum" };

		double[] percent = randPercent(1.0, (byte) materialsEquip.length);
		sellWeapon = setUpItemToSell(Item.ITEM_SWORD, percent, materialsEquip);
		sellHelmet = setUpItemToSell(Item.ITEM_HELMET, percent, materialsEquip);
		sellShield = setUpItemToSell(Item.ITEM_SHIELD, percent, materialsEquip);
		sellCloth = setUpItemToSell(Item.ITEM_CLOTH, percent, materialsEquip);
		sellRing = setUpItemToSell(Item.ITEM_RING, percent, materialsEquip);
	}

	private Equipment[] setUpItemToSell(int itemType, double[] percent,
			String[] materialsEquip) {
		String des;
		int base;
		switch (itemType) {
		case Item.ITEM_SWORD:
			des = " Sword";
			base = player.getDamage();
			break;
		case Item.ITEM_HELMET:
			des = " Helmet";
			base = player.getDef();
			break;
		case Item.ITEM_SHIELD:
			des = " Shield";
			base = player.getDef();
			break;
		case Item.ITEM_CLOTH:
			des = " Cloth";
			base = player.getDef();
			break;
		default:
			des = " Ring";
			base = player.getMaxMana();
			break;
		}
		Equipment[] equip = new Equipment[materialsEquip.length];
		for (byte i = 0; i < equip.length; ++i) {
			equip[i] = new Equipment(materialsEquip[i] + des, itemType);
			equip[i].setStatNumber((int) (base * percent[i]));
			equip[i].setPosition(i);
		}
		return equip;
	}

	// End set up equipment to sell******************

	/**
	 * Set potion to sell in array.
	 */
	private void setUpPotionToSell() {
		String[] materialsPotion = { "Small", "Medium", "Large", "Super",
				"Super Super" };

		double[] percent = randPercent(0.0, (byte) materialsPotion.length);
		sellHealPotion = setUpPotionToSell(Item.ITEM_HEALTH_POTION, percent,
				materialsPotion);
		sellManaPotion = setUpPotionToSell(Item.ITEM_MANA_POTION, percent,
				materialsPotion);
		sellStaminaPotion = setUpPotionToSell(Item.ITEM_STAMINA_POTION,
				percent, materialsPotion);
	}

	private Potion[] setUpPotionToSell(int itemType, double[] percent,
			String[] materialsPotion) {
		String des = " Potion";
		int base;
		switch (itemType) {
		case Item.ITEM_HEALTH_POTION:
			base = (int) (player.getMaxHp() * 0.4);
			break;
		case Item.ITEM_MANA_POTION:
			base = (int) (player.getMaxMana() * 0.4);
			break;
		default:
			base = (int) (player.getMaxStm() * 0.4);
			break;
		}
		Potion[] potion = new Potion[materialsPotion.length];
		for (byte i = 0; i < potion.length; ++i) {
			potion[i] = new Potion(materialsPotion[i] + des, itemType);
			potion[i].setStatNumber((int) (base * percent[i]));
			potion[i].setPosition(i);
			potion[i].setSize(5);
		}
		return potion;
	}

	// End set up potion to sell*******************

	/**
	 * Set up random percent for equipment and potion
	 * 
	 * @param length
	 */
	private double[] randPercent(double range, byte size) {
		int[] num = new int[size];
		double a = 0.1;
		for (int i = 0; i < size; ++i, a += 0.1) {
			num[i] = (int) ((Math.random() + range + a) * 100);
			System.out.println("Number-- " + num[i]);
		}

		int min;
		for (int i = 0; i < size; ++i) {
			min = num[i];
			for (int j = i; j < size; ++j) {
				if (num[j] < min) {
					min = num[j];
					num[j] = num[i];
					num[i] = min;
				}
			}
		}

		double[] rand = new double[size];
		for (int i = 0; i < size; ++i)
			rand[i] = num[i] * 0.01;
		return rand;
	}// End set up random percent********************

	/**
	 * Set dialog for cancel buying because player doesn't have enough gold.
	 */
	private void setUpCancelBuyingDialog() {
		// Set up for buy confirm dialog.
		alertDialogCancelBuy = new AlertDialog.Builder(this);
		// Setting Dialog Title

		alertDialogCancelBuy.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						// Toast.makeText(getApplicationContext(),
						// "You clicked on YES",
						// Toast.LENGTH_SHORT).show();
						dialog.cancel();

					}
				});
	}// End cancel buy dialog*********************

	/**
	 * Set dialog for confirm buying.
	 */
	private void setUpBuyingDialog() {
		// Set up for buy confirm dialog.
		alertDialog = new AlertDialog.Builder(this);
		// Setting Dialog Title
		alertDialog.setTitle("Confirm buying...");
	}

	private void setUpSellingDialog() {
		// Set up for buy confirm dialog.
		alertDialogSell = new AlertDialog.Builder(this);
		// Setting Dialog Title
		alertDialogSell.setTitle("Confirm selling...");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		switch (parent.getId()) {
		case R.id.listViewItems:
			String message = parent.getItemAtPosition(position).toString();
			Item temp = getItemFromList(message);
			System.out.println("Buy from shop--- " + message);
			buying(message, temp);
			break;
		default:
			selling(parent, position);
			alertDialogSell.show();
			break;
		}
	}

	private Item getItemFromList(String e) {
		Item temp = null;
		if (e.contains("Sword"))
			temp = search(sellWeapon, e);
		else if (e.contains("Helmet"))
			temp = search(sellHelmet, e);
		else if (e.contains("Shield"))
			temp = search(sellShield, e);
		else if (e.contains("Cloth"))
			temp = search(sellCloth, e);
		else if (e.contains("Ring"))
			temp = search(sellRing, e);
		else if (e.contains("HP"))
			temp = search(sellHealPotion, e);
		else if (e.contains("STM"))
			temp = search(sellStaminaPotion, e);
		else
			temp = search(sellManaPotion, e);
		return temp;
	}

	private Item search(Item[] item, String e) {
		for (int i = 0; i < item.length; ++i) {
			if (item[i] != null && item[i].equals(e))
				return item[i];
		}
		return null;
	}

	private void selling(AdapterView<?> parent, int position) {
		final String item = parent.getItemAtPosition(position).toString();
		if (item.endsWith("E")) {
			alertDialogSell.setMessage("Can't sell an equipped item");
			alertDialogSell.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							dialog.cancel();
						}
					});
		} else {
			Item[] inventory = player.getPlayerInventory();
			Item temp = null;
			int cost;
			for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
				if (inventory[i].equals(item)) {
					temp = inventory[i];
					position = i;
					break;
				}
			}
			cost = (int) (temp.getCost() * 0.8);
			alertDialogSell.setMessage("Selling " + temp + " to get " + cost
					+ " Gold");
			setSellDialogButton(item, position, cost);
		}
	}

	private void setSellDialogButton(final String item, final int position,
			final int cost) {
		// TODO Auto-generated method stub
		alertDialogSell.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						adapterInventory.remove(item);
						player.removeItemFromInventory(position);
						player.setGold(player.getGold() + cost);
						txtViewGold.setText("Gold: " + player.getGold());
					}
				});
		// Setting Negative "NO" Btn
		alertDialogSell.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
	}

	private void buying(final String message, final Item item) {
		if (player.isInventoryFull()) {
			alertDialogCancelBuy.setTitle("Can't make a buy...");
			alertDialogCancelBuy
					.setMessage("You don't have enough inventory space to place a new item.");
			alertDialogCancelBuy.show();
		} else if (player.getGold() < item.getCost()) {
			alertDialogCancelBuy.setTitle("Can't make a buy...");
			alertDialogCancelBuy
					.setMessage("You don't have enough gold to buy the item");
			alertDialogCancelBuy.show();
		} else {
			// Setting Dialog Message
			String compareStat = "";
			switch (item.getItemType()) {
			case Item.ITEM_SWORD:
			case Item.ITEM_SHIELD:
			case Item.ITEM_HELMET:
			case Item.ITEM_CLOTH:
			case Item.ITEM_RING:
				int diff = item.getStatNumber()
						- player.getPlayerEquip(item.getItemType())
								.getStatNumber();
				compareStat = "\n(" + (diff > 0 ? "+" : "-") + diff + " "
						+ item.getStatName() + ")";
				break;
			}
			alertDialog.setMessage(message + compareStat);

			// Setting Positive "Yes" Btn
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on YES",
							// Toast.LENGTH_SHORT).show();

							// String v = "You bought " + message;
							// Toast.makeText(getApplicationContext(), v,
							// Toast.LENGTH_SHORT).show();
							// listItems.setEnabled(false);
							buyingStuff(message, item);
							// new Handler().postDelayed(new Runnable() {
							// @Override
							// public void run() {
							// listItems.setEnabled(true);
							// }
							//
							// }, Toast.LENGTH_LONG * 2200);
						}
					});
			// Setting Negative "NO" Btn
			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on NO", Toast.LENGTH_SHORT)
							// .show();
							dialog.cancel();
						}
					});
			alertDialog.show();
		}
	}

	private void buyingStuff(String message, Item item) {
		// if (adapterShop.getCount() > 0)
		// adapterShop.clear();
		switch (item.getItemType()) {
		case Item.ITEM_SWORD:
			updateListToSell(item.getPosition(), sellWeapon);
			updateAfterBuying(message, item);
			break;
		case Item.ITEM_HELMET:
			updateListToSell(item.getPosition(), sellHelmet);
			updateAfterBuying(message, item);
			break;
		case Item.ITEM_SHIELD:
			updateListToSell(item.getPosition(), sellShield);
			updateAfterBuying(message, item);
			break;
		case Item.ITEM_CLOTH:
			updateListToSell(item.getPosition(), sellCloth);
			updateAfterBuying(message, item);
			break;
		case Item.ITEM_RING:
			updateListToSell(item.getPosition(), sellRing);
			updateAfterBuying(message, item);
			break;
		case Item.ITEM_HEALTH_POTION:
			updateListToSell(item.getPosition(), sellHealPotion);
			updateAfterBuying(message, item);
			break;
		case Item.ITEM_STAMINA_POTION:
			updateListToSell(item.getPosition(), sellStaminaPotion);
			updateAfterBuying(message, item);
			break;
		default:
			updateListToSell(item.getPosition(), sellManaPotion);
			updateAfterBuying(message, item);
			break;
		}
		// listItems.setAdapter(adapterShop);
	}

	// ***********************************
	// Update sell equipment and display.
	private void updateListToSell(byte pos, Item[] sellItem) {
		sellItem[pos] = null;
		for (byte i = pos; i < sellItem.length - 1; ++i) {
			if (sellItem[i + 1] != null) {
				sellItem[i] = sellItem[i + 1];
				sellItem[i + 1] = null;
			}
		}
	}

	private void updateAfterBuying(String message, Item item) {
		player.insertItemToInventory(item);
		player.setGold(player.getGold() - item.getCost());
		txtViewGold.setText("Gold: " + player.getGold());
		adapterInventory.add(item.toString());
		adapterShop.remove(message);
	}

	// ***********************************
	// Update sell equipment and display.

	// ***********************************
	// Update sell potion and display.
	// private void updateListToSell(int pos, Potion[] sellPotion) {
	// sellPotion[pos] = null;
	// for (int i = pos; i < sellPotion.length - 1; ++i) {
	// if (sellPotion[i + 1] != null) {
	// sellPotion[i] = sellPotion[i + 1];
	// sellPotion[i + 1] = null;
	// }
	// }
	// }
	//
	// private void putUpdateSellItemToListView(int pos, Potion[] sellPotion) {
	// Potion equip = sellPotion[pos];
	// player.insertItemToInventory(equip);
	// player.setGold(player.getGold() - equip.getCost());
	// txtViewGold.setText("Gold: " + player.getGold());
	// adapterInventory.add(equip.toString());
	// }

	// ***********************************
	// Update sell potion and display.
	// End*****************

	/**
	 * Set list view for selling.
	 */
	private void setUpListSellAndInventory() {
		listItems = (ListView) this.findViewById(R.id.listViewItems);
		listItems.setOnItemClickListener(this);

		adapterShop = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1);

		// Display list view in the first place.
		displayToList(sellWeapon);
		listItems.setAdapter(adapterShop);

		// Display inventory items.
		listItemsInventory = (ListView) this
				.findViewById(R.id.listViewItemsInventory);
		listItemsInventory.setOnItemClickListener(this);

		adapterInventory = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.activity_list_item, android.R.id.text1);

		// Display weapon in the first place.
		if (!adapterInventory.isEmpty())
			adapterInventory.clear();
		adapterInventory.add(player.getPlayerEquip(Item.ITEM_SWORD).toString());
		displayFromInventory(Item.ITEM_SWORD);
		listItemsInventory.setAdapter(adapterInventory);
	}// End***************

	/**
	 * Set up button
	 */
	private void setUpButton() {
		// TODO Auto-generated method stub
		this.findViewById(R.id.buttonAll).setOnClickListener(this);
		this.findViewById(R.id.buttonWeapon).setOnClickListener(this);
		this.findViewById(R.id.buttonHelmet).setOnClickListener(this);
		this.findViewById(R.id.buttonShield).setOnClickListener(this);
		this.findViewById(R.id.buttonCloth).setOnClickListener(this);
		this.findViewById(R.id.buttonRing).setOnClickListener(this);
		this.findViewById(R.id.buttonPotion).setOnClickListener(this);
		this.findViewById(R.id.buttonInventory).setOnClickListener(this);
	}

	@Override
	public void onClick(View button) {
		// TODO Auto-generated method stub
		if (!adapterShop.isEmpty()) {
			adapterShop.clear();
			adapterInventory.clear();
		}
		switch (button.getId()) {
		case R.id.buttonAll:
			displayAll();
			break;
		case R.id.buttonWeapon:
			displayToList(sellWeapon);

			adapterInventory.add(player.getPlayerEquip(Item.ITEM_SWORD)
					.toString());
			System.out.println("Equipment "
					+ player.getPlayerEquip(Item.ITEM_SWORD).isEquipped());
			displayFromInventory(Item.ITEM_SWORD);
			break;
		case R.id.buttonHelmet:
			displayToList(sellHelmet);
			adapterInventory.add(player.getPlayerEquip(Item.ITEM_HELMET)
					.toString());
			displayFromInventory(Item.ITEM_HELMET);
			break;
		case R.id.buttonShield:
			displayToList(sellShield);
			adapterInventory.add(player.getPlayerEquip(Item.ITEM_SHIELD)
					.toString());
			displayFromInventory(Item.ITEM_SHIELD);
			break;
		case R.id.buttonCloth:
			displayToList(sellCloth);
			adapterInventory.add(player.getPlayerEquip(Item.ITEM_CLOTH)
					.toString());
			displayFromInventory(Item.ITEM_CLOTH);
			break;
		case R.id.buttonRing:
			displayToList(sellRing);
			adapterInventory.add(player.getPlayerEquip(Item.ITEM_RING)
					.toString());
			displayFromInventory(Item.ITEM_RING);
			break;
		case R.id.buttonPotion:
			displayToList(sellHealPotion);
			displayToList(sellManaPotion);
			displayToList(sellStaminaPotion);

			displayFromInventory(Item.ITEM_HEALTH_POTION);
			displayFromInventory(Item.ITEM_MANA_POTION);
			displayFromInventory(Item.ITEM_STAMINA_POTION);
			break;
		default:// Inventory button
			Intent intentInventory = new Intent(ShoppingTestActivity.this,
					InventoryTestActivity.class);
			// player.sortInventory();
			intentInventory.putExtra(Player.PLAYER_DATA, player);
			startActivity(intentInventory);
			break;
		}
		// listItems.setAdapter(adapterShop);
		// listItemsInventory.setAdapter(adapterInventory);
	}

	private void displayAll() {
		displayToList(sellWeapon);
		displayToList(sellHelmet);
		displayToList(sellShield);
		displayToList(sellCloth);
		displayToList(sellRing);
		displayToList(sellHealPotion);
		displayToList(sellManaPotion);
		displayToList(sellStaminaPotion);

		adapterInventory.add(player.getPlayerEquip(Item.ITEM_SWORD).toString());
		displayFromInventory(Item.ITEM_SWORD);
		adapterInventory
				.add(player.getPlayerEquip(Item.ITEM_HELMET).toString());
		displayFromInventory(Item.ITEM_HELMET);
		adapterInventory
				.add(player.getPlayerEquip(Item.ITEM_SHIELD).toString());
		displayFromInventory(Item.ITEM_SHIELD);
		adapterInventory.add(player.getPlayerEquip(Item.ITEM_CLOTH).toString());
		displayFromInventory(Item.ITEM_CLOTH);
		adapterInventory.add(player.getPlayerEquip(Item.ITEM_RING).toString());
		displayFromInventory(Item.ITEM_RING);
		displayFromInventory(Item.ITEM_HEALTH_POTION);
		displayFromInventory(Item.ITEM_MANA_POTION);
		displayFromInventory(Item.ITEM_STAMINA_POTION);

	}

	private void displayToList(Item[] sellPotion) {
		for (int i = 0; i < sellPotion.length; ++i)
			if (sellPotion[i] != null)
				adapterShop.add(sellPotion[i].toString() + " ("
						+ (sellPotion[i].getCost()) + " Gold)");
	}

	// End button action**************

	/**
	 * Display inventory to list.
	 */

	private void displayFromInventory(int itemType) {
		Item[] inventory = player.getPlayerInventory();
		for (int i = 0; i < player.getInventoryCurSpace(); ++i) {
			if (inventory[i].getItemType() == itemType) {
				adapterInventory.add(inventory[i].toString());
			}
		}
	}

	// End display inventory.

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
		System.out.println("onPause - shop");

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

	protected void onStop() {
		super.onStop();
		System.out.println("onStop - shop");
	}

	protected void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy - shop");
		medplay.stop();
	}

	private void playMusic() {
		medplay = MediaPlayer.create(this.getApplicationContext(),
				R.raw.clinthammer_shop);
		medplay.setLooping(true);
		medplay.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping, menu);
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

}
