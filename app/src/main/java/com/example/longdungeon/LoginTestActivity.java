package com.example.longdungeon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import com.example.longdungeon.character.Player;
import com.example.longdungeon.item.Equipment;
import com.example.longdungeon.item.Item;
import com.example.longdungeon.item.Potion;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginTestActivity extends ActionBarActivity implements
		OnClickListener {
	private Player player1, player2;
	private boolean buttonPress1;
	private AlertDialog.Builder alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_test);

		this.findViewById(R.id.layoutLoginText).setVisibility(View.INVISIBLE);
		this.findViewById(R.id.layoutLoginChoose).setVisibility(View.VISIBLE);

		player1 = getPlayerFromFile(Player.PLAYER_FILE_1);
		player2 = getPlayerFromFile(Player.PLAYER_FILE_2);
		displayOnLoginChoose((byte) 1, player1,
				(Button) this.findViewById(R.id.buttonData1));
		displayOnLoginChoose((byte) 2, player2,
				(Button) this.findViewById(R.id.buttonData2));

		((Button) this.findViewById(R.id.buttonData1)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonData2)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonPlay)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonNew)).setOnClickListener(this);
		
		((Button) this.findViewById(R.id.buttonPlay)).setEnabled(false);
		((Button) this.findViewById(R.id.buttonNew)).setEnabled(false);
		setUpNewDialog();
	}

	

	@Override
	public void onClick(View v) {
		String a;
		switch (v.getId()) {
		case R.id.buttonData1:
			a = ((Button) v).getText().toString();
			if (a.contains("No data"))
				newPlayer(Player.PLAYER_FILE_1);
			else {
				((Button) v).setEnabled(false);
				((Button) this.findViewById(R.id.buttonData2)).setEnabled(true);
				buttonPress1 = true;
				((Button) this.findViewById(R.id.buttonPlay)).setEnabled(true);
				((Button) this.findViewById(R.id.buttonNew)).setEnabled(true);
			}
			break;
		case R.id.buttonData2:
			a = ((Button) v).getText().toString();
			if (a.contains("No data"))
				newPlayer(Player.PLAYER_FILE_2);
			else {
				((Button) v).setEnabled(false);
				((Button) this.findViewById(R.id.buttonData1)).setEnabled(true);
				buttonPress1 = false;
				((Button) this.findViewById(R.id.buttonPlay)).setEnabled(true);
				((Button) this.findViewById(R.id.buttonNew)).setEnabled(true);
			}
			break;
		case R.id.buttonPlay:
			Intent intentBattle = new Intent(LoginTestActivity.this,
					BattleTestActivity.class);
//			Intent intentBattle = new Intent(LoginTestActivity.this,
//					BattleTestAnimation.class);
			intentBattle.putExtra(Player.PLAYER_DATA, buttonPress1 ? player1
					: player2);
			startActivity(intentBattle);
			finish();
			break;
		default:
			alertDialog.show();
			break;
		}
	}

	

	private void setUpNewDialog() {
		alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("New file...");

		// Setting Dialog Message
		alertDialog.setMessage("Are you sure you want to overwrite?");

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						newPlayer(buttonPress1 ? player1.getNameFile()
								: player2.getNameFile());
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
	}

	private void displayOnLoginChoose(byte num, Player player, Button button) {
		button.setText("Data "
				+ num
				+ (player == null ? "\nName: No data\nGold: 0" : "\nName: "
						+ player.getName() + "\nGold: " + player.getGold()));
	}

	private Player getPlayerFromFile(String playerFile) {
		Player player = null;
		File file = new File(getFilesDir(), playerFile);
		if (file.exists()) {
			try {
				BufferedReader inputReader = new BufferedReader(
						new InputStreamReader(openFileInput(playerFile)));

				player = new Player();
				player.readFromFile(player, inputReader);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return player;
	}

	private void newPlayer(final String playerFile) {
		this.findViewById(R.id.layoutLoginText).setVisibility(View.VISIBLE);
		this.findViewById(R.id.layoutLoginChoose).setVisibility(View.INVISIBLE);
		
		final EditText edTxtLogin = (EditText) this
				.findViewById(R.id.editTextLogin);
		final Button btnLogin = (Button) this.findViewById(R.id.buttonLogin);
		btnLogin.setVisibility(Button.INVISIBLE);

		edTxtLogin.addTextChangedListener(new TextWatcher() {
			String namePlayer;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				namePlayer = edTxtLogin.getText().toString();
				btnLogin.setVisibility(namePlayer.length() < 1 ? Button.INVISIBLE
						: Button.VISIBLE);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Player player = new Player(edTxtLogin.getText().toString());
				player.setNameFile(playerFile);
				Intent intentBattle = new Intent(LoginTestActivity.this,
						BattleTestActivity.class);
//				Intent intentBattle = new Intent(LoginTestActivity.this,
//						BattleTestAnimation.class);
				intentBattle.putExtra(Player.PLAYER_DATA, player);
				startActivity(intentBattle);
				finish();
			}
		});
	}

	protected void onResume() {
		super.onResume();

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

}
