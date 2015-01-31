package com.example.longdungeon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.example.longdungeon.character.Mob;
import com.example.longdungeon.character.Player;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity implements OnClickListener {
	private MediaPlayer medplay;
	private Player player1, player2;
	private boolean buttonPress1;
	private AlertDialog.Builder alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 //Remove title bar
//	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		playMusic();
		loadingNewLoad();
		setUpButton();
		setUpDeleteDialog();
	}

	private void setUpButton() {
		((Button) this.findViewById(R.id.buttonNew)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonLoad)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonData1)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonData2)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonPlay)).setOnClickListener(this);
		((Button) this.findViewById(R.id.buttonDelete))
				.setOnClickListener(this);

		((Button) this.findViewById(R.id.buttonPlay)).setEnabled(false);
		((Button) this.findViewById(R.id.buttonDelete)).setEnabled(false);
	}

	private void loadingNewLoad() {
		this.findViewById(R.id.layoutLoginText).setVisibility(View.INVISIBLE);
		this.findViewById(R.id.layoutLoginChoose).setVisibility(View.INVISIBLE);
		this.findViewById(R.id.layoutLoginNewLoad).setVisibility(View.VISIBLE);

		player1 = getPlayerFromFile(Player.PLAYER_FILE_1);
		player2 = getPlayerFromFile(Player.PLAYER_FILE_2);
	}

	@Override
	public void onClick(View v) {
		String a;
		switch (v.getId()) {
		case R.id.buttonNew:
			buttonClickNew();
			break;
		case R.id.buttonLoad:
			buttonClickLoad();
			break;
		case R.id.buttonData1:
			a = ((Button) v).getText().toString();
			if (a.contains("No data"))
				newPlayer(Player.PLAYER_FILE_1);
			else {
				((Button) v).setEnabled(false);
				((Button) this.findViewById(R.id.buttonData2)).setEnabled(true);
				buttonPress1 = true;
				((Button) this.findViewById(R.id.buttonPlay)).setEnabled(true);
				((Button) this.findViewById(R.id.buttonDelete))
						.setEnabled(true);
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
				((Button) this.findViewById(R.id.buttonDelete))
						.setEnabled(true);
			}
			break;
		case R.id.buttonPlay:
			Intent intentBattle = new Intent(LoginActivity.this,
					BattleActivity.class);
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

	private void buttonClickLoad() {
		this.findViewById(R.id.layoutLoginText).setVisibility(View.INVISIBLE);
		this.findViewById(R.id.layoutLoginChoose).setVisibility(View.VISIBLE);
		this.findViewById(R.id.layoutLoginNewLoad)
				.setVisibility(View.INVISIBLE);
		displayOnLoginChoose((byte) 1, player1,
				(Button) this.findViewById(R.id.buttonData1));
		displayOnLoginChoose((byte) 2, player2,
				(Button) this.findViewById(R.id.buttonData2));
	}

	private void buttonClickNew() {
		if (player1 == null || (player1 != null && player2 != null))
			newPlayer(Player.PLAYER_FILE_1);
		else if (player2 == null)
			newPlayer(Player.PLAYER_FILE_2);
	}

	private void setUpDeleteDialog() {
		alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("Delete file...");

		// Setting Dialog Message
		alertDialog
				.setMessage("Are you sure you want to delete the save data?");

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (buttonPress1)
							deleteData((byte) 1);
						else
							deleteData((byte) 2);
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

	protected void deleteData(byte b) {
		switch (b) {
		case 1:
			player1 = null;
			displayOnLoginChoose((byte) 1, player1,
					((Button) this.findViewById(R.id.buttonData1)));
			((Button) this.findViewById(R.id.buttonData1)).setEnabled(true);
			deleteFile(Player.PLAYER_FILE_1);
			break;
		default:
			player2 = null;
			displayOnLoginChoose((byte) 2, player2,
					((Button) this.findViewById(R.id.buttonData2)));
			((Button) this.findViewById(R.id.buttonData2)).setEnabled(true);
			deleteFile(Player.PLAYER_FILE_2);
			break;
		}
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
		this.findViewById(R.id.layoutLoginNewLoad)
				.setVisibility(View.INVISIBLE);

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
				// For demo, player hp will be 7 times than default mob damage.
				Mob m = new Mob();
				player.setCurHp(m.getDamage() * 7);
				player.setMaxHp(m.getDamage() * 7);
				Intent intentBattle = new Intent(LoginActivity.this,
						BattleActivity.class);
				intentBattle.putExtra(Player.PLAYER_DATA, player);
				startActivity(intentBattle);
				finish();
			}
		});
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

	// Start music
	private void playMusic() {
		medplay = MediaPlayer.create(this.getApplicationContext(),
				R.raw.clinthammer_opening);
		medplay.setLooping(true);
		medplay.start();
	}

	protected void onResume() {
		super.onResume();
		medplay.start();
	}

	protected void onPause() {
		super.onPause();
		medplay.pause();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
		medplay.stop();
	}
}
