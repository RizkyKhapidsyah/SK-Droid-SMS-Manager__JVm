package com.codeandcoder.dsm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.channels.AlreadyConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Settings extends Activity {

	Button save, discart;
	CheckBox checkWaitMessage, checkBackground, checkSettings;
	SeekBar seek;
	ImageView imv;
	RelativeLayout relativelayout;
	String previousData;
	CharSequence option[] = { "Set New" };
	String encodedImageString;
	DatabaseInternal db;
	ArrayList<HashMap<String, String>> dataHash;
	ArrayList<String> name, date, time;
	Dialog info;
	List<Drawable> array;
	String selectedImagePath = "Nothing";
	byte[] images;
	int selactedImge;
	ListView lv;
	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	SharedPreferences shared;
	int finalProgress = 30;
	String bigImage = "";
	Drawable dr;
	boolean chkUndo, checkImg, longpressed = false, longpressdialog = false;
	int progressHas;
	String abc = "known";

	int stepSize = 5;
	AlertDialog.Builder alt;

	public void CreatObject() {
		shared = getSharedPreferences(filename, 0);
		save = (Button) findViewById(R.id.button1);
		discart = (Button) findViewById(R.id.button2);
		checkWaitMessage = (CheckBox) findViewById(R.id.checkBox1);
		checkBackground = (CheckBox) findViewById(R.id.checkBox2);

		seek = (SeekBar) findViewById(R.id.seekBar1);
		relativelayout = (RelativeLayout) findViewById(R.id.settingsRelative);
		imv = (ImageView) findViewById(R.id.imageView1Button);
		db = new DatabaseInternal(Settings.this);
		array = new ArrayList<Drawable>();
		name = new ArrayList<String>();
		date = new ArrayList<String>();
		time = new ArrayList<String>();
		images = new byte[100];

	}

	public void shareDsave(boolean checkUndo, int progressHas, boolean checkImg) {
		SharedPreferences.Editor editor = shared.edit();
		editor.putBoolean(KEY_CHK_WAT, checkUndo);
		editor.putInt(KEY_CHK_WAT_TIME, progressHas);
		editor.putBoolean(KEY_IMG_CHK, checkImg);
		abc = selectedImagePath;
		editor.putString("IMG", selectedImagePath);
		editor.commit();
	}

	public void SharedHit() {

		chkUndo = shared.getBoolean(KEY_CHK_WAT, false);
		checkImg = shared.getBoolean(KEY_IMG_CHK, false);
		progressHas = shared.getInt(KEY_CHK_WAT_TIME, 30);
		finalProgress = progressHas;

		checkWaitMessage.setChecked(chkUndo);
		seek.setEnabled(chkUndo);
		checkBackground.setChecked(checkImg);
		checkWaitMessage.setText(previousData.concat("\n( "
				+ Integer.toString(progressHas / 10) + " sec)"));
		if (checkImg) {

			String data = shared.getString("IMG", "Nothing");
			selectedImagePath = data;
			if (!data.equals("Nothing")) {
				File imageFile = new File(data);
				Bitmap bitmap = BitmapFactory.decodeFile(imageFile
						.getAbsolutePath());
				dr = new BitmapDrawable(bitmap);
				abc = "3";

			}

		}

	}

	public void MediaAlert() {
		alt = new AlertDialog.Builder(Settings.this);
		alt.setTitle("Set Background").setItems(option,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {

						case 0:
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(Intent.createChooser(intent,
									"Select Picture"), 10);
							break;
						}
					}

				});
		alt.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub

				if (keyCode == KeyEvent.KEYCODE_BACK && longpressdialog) {
					if (checkBackground.isChecked()) {
						checkBackground.setChecked(true);
					} else
						checkBackground.setChecked(false);
					// Toast.makeText(getApplicationContext(), "AlertBack",
					// 1).show();
					dialog.dismiss();

				} else if (keyCode == KeyEvent.KEYCODE_BACK) {

					if (longpressed) {
						checkBackground.setChecked(true);
					} else
						checkBackground.setChecked(false);
					// Toast.makeText(getApplicationContext(), "AlertBack",
					// 1).show();
					dialog.dismiss();

				}
				return true;
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		CreatObject();
		previousData = " Wait for send message";
		new SettingShit().execute();

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				shareDsave(checkWaitMessage.isChecked(), finalProgress,
						checkBackground.isChecked());
				// Toast.makeText(Settings.this, abc, 1).show();
				finish();

			}
		});

		discart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});

		checkBackground.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				longpressdialog = false;
				if (checkBackground.isChecked()) {

					alt.show();
					checkBackground.setChecked(false);

				} else {
					relativelayout.setBackgroundColor(Color.WHITE);
				}
			}
		});
		checkBackground.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (checkBackground.isChecked()) {
					longpressdialog = true;
					alt.show();

				} else {

					// alt.show();
				}
				return false;
			}
		});

		checkWaitMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkWaitMessage.isChecked()) {

					seek.setEnabled(true);
				} else {
					seek.setEnabled(false);
				}
			}
		});
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// Integer.toString(progress), 1).show();
				progress = ((int) Math.round(progress / stepSize)) * stepSize;
				finalProgress = progress;
				if (progress / 10 < 3) {
					seekBar.setProgress(30);

				} else {
					seekBar.setProgress(progress);
					checkWaitMessage.setText(previousData.concat("\n( "
							+ Integer.toString(progress / 10) + " sec)"));

				}

				// save.
				// setText("Save ".concat("( "+Integer.toString(progress/10)+" %)"));

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 10) {

			if (data != null) {

				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);
				// Toast.makeText(getApplicationContext(), selectedImagePath,
				// 1).show();

				try {
					File imageFile = new File(selectedImagePath);
					Bitmap bitmap = BitmapFactory.decodeFile(imageFile
							.getAbsolutePath());
					BitmapDrawable dr = new BitmapDrawable(bitmap);
					relativelayout.setBackgroundDrawable(dr);
					checkBackground.setChecked(true);
					longpressdialog = false;
				} catch (Exception e) {
				}

			}
		}
	}

	private String getPath(Uri selectedImageUri) {

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImageUri,
				projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public class SettingShit extends AsyncTask<String, Integer, String>

	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			Settings.this.SharedHit();
			publishProgress();
			Settings.this.MediaAlert();

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			/*
			 * if(Settings.this.checkImg) { Toast.makeText(Settings.this,
			 * "Checked"+abc, 1).show(); } else Toast.makeText(Settings.this,
			 * "UnChecked", 1).show();
			 */
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			seek.setProgress(progressHas);
			relativelayout.setBackgroundDrawable(dr);
		}

	}

}
