package com.codeandcoder.dsm;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends ListActivity {

	ArrayList<String> menuItems;
	String[] listItem;
	EditText addItem;
	DatabaseInternal db;
	int count = 0;
	SharedPreferences shared;
	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	Drawable dr;
	ListView lv;

	public void SharedTrigger() {
		shared = getSharedPreferences(filename, 0);
		boolean checkImg = shared.getBoolean(KEY_IMG_CHK, false);
		
		
		if (checkImg) {
			String data = shared.getString("IMG", "Noting");
			if (!data.equals("Nothing")) {
				File imageFile = new File(data);
				Bitmap bitmap = BitmapFactory.decodeFile(imageFile
						.getAbsolutePath());
				dr = new BitmapDrawable(bitmap);
				lv.setBackgroundDrawable(dr);
				

			}
		} else

		{
			lv.setBackgroundResource(R.drawable.white);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		db = new DatabaseInternal(MainActivity.this);
		lv = getListView();
		lv.setVerticalScrollBarEnabled(false);
		lv.setHorizontalScrollBarEnabled(false);
		lv.setVerticalFadingEdgeEnabled(false);
		lv.setHorizontalFadingEdgeEnabled(false);
		// ColorDrawable sage= new
		// ColorDrawable(this.getResources().getColor(Color.TRANSPARENT));
		
		
		
		
		lv.setDivider(this.getResources().getDrawable(
				R.drawable.transperent_color));
		lv.setDividerHeight(3);
		lv.setPadding(5, 5, 5, 5);
		SharedTrigger();
		menuItems = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			switch (i) {
			case 0:
				menuItems.add("Create Message");
				break;
			case 1:
				menuItems.add("Inbox");
				break;
			case 2:
				menuItems.add("Draft");
				break;
			case 3:
				menuItems.add("Send Item");
				break;
			case 4:
				menuItems.add("Settings");
				break;
			case 5:
				menuItems.add("Trash");
				break;
			}
		}
		try {
			db.open();
			Cursor c = db.getLabel();
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				menuItems.add(c.getString(c.getColumnIndex("label_name")));
			}
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		menuItems.add("Add Label");
		listItem = menuItems.toArray(new String[menuItems.size()]);

		// String[] listItem =
		// {"Create Message","Inbox","Send Item","Settings"};
		/*
		 * ArrayAdapter adapter = new
		 * ArrayAdapter(this,R.layout.list_sms_layout,R.id.textView1,listItem);
		 * setListAdapter(adapter);
		 */

		setListAdapter(new SmsArrayAdapter(this, listItem));

		lv.setScrollingCacheEnabled(false);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				try {
					// getContact();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(MainActivity.this, e.toString(), 1).show();
				}
				if (position > 5 && position < menuItems.size() - 1) {
					AlertDialog.Builder alt = new AlertDialog.Builder(
							MainActivity.this);
					alt.setTitle("Delete label").setMessage(
							"Are you sure to delete this lebel")
							.setPositiveButton("Yes", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									try {
										db.open();
										db.DeleteLabel(menuItems.get(position));
										db.close();

										menuItems.remove(position);
										listItem = menuItems
												.toArray(new String[menuItems
														.size()]);
										setListAdapter(new SmsArrayAdapter(
												MainActivity.this, listItem));
										Toast.makeText(MainActivity.this,
												"Label deleted", 1).show();
									} catch (Exception e) {
										// TODO: handle exception
										Toast.makeText(MainActivity.this,
												e.toString(), 1).show();
									}

								}
							}).setNegativeButton("Cancel",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub

										}
									}).show();

					// Toast.makeText(MainActivity.this, "Long pressed",
					// 1).show();

				}
				return true;
			}
		});
		/*
		 * lv. setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
		 * position, long arg3) { // TODO Auto-generated method stub
		 * if(position>4 && position<menuItems.size()-1) {
		 * 
		 * 
		 * Toast.makeText(MainActivity.this, menuItems.get(position), 1).show();
		 * 
		 * }
		 * 
		 * } });
		 */

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		SharedTrigger();
	}

	@Override
	protected void onListItemClick(final ListView lv, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(lv, v, position, id);

		count++;
		switch (position) {
		case 0:
			Intent i1 = new Intent(this, Create_msg.class);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_in_right);
			startActivity(i1);
			break;

		case 1:
			Intent i2 = new Intent(this, InboxNew.class);
			startActivity(i2);
			break;

		case 2:
			Intent i3 = new Intent(this, Draft.class);
			startActivity(i3);
			break;
		case 3:
			Intent i4 = new Intent(this, SI.class);
			startActivity(i4);
			break;
		case 4:
			Intent i5 = new Intent(this, Settings.class);
			startActivity(i5);
			break;
		case 5:
			Intent i6 = new Intent(this, Trash.class);
			i6.putExtra("Trash", "Trash");
			startActivity(i6);
			// Toast.makeText(getApplicationContext(), "Trash", 1).show();

			break;

		default:
			Object obj = lv.getAdapter().getItem(position);
			// Toast.makeText(getApplicationContext(),obj.toString(), 1).show();

			if (obj.toString().equals("Add Label")) {
				addItem = new EditText(MainActivity.this);
				addItem.setHint("Label Name");
				AlertDialog.Builder alt = new AlertDialog.Builder(this);
				alt.setView(addItem).setTitle("Add label").setNeutralButton(
						"Ok", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								try {
									if (addItem.getText().toString().length() != 0) {
										String labelName = addItem.getText()
												.toString();
										db.open();
										db.CreateLabel(labelName);
										db.close();
										// TODO Auto-generated method stub
										menuItems.remove(menuItems.size() - 1);
										menuItems.add(addItem.getText()
												.toString());
										menuItems.add("Add Label");
										listItem = menuItems
												.toArray(new String[menuItems
														.size()]);
										setListAdapter(new SmsArrayAdapter(
												MainActivity.this, listItem));

									} else {
										Toast.makeText(MainActivity.this,
												"Please insert a name", 1)
												.show();
									}
								} catch (Exception e) {
									// TODO: handle exception
									Toast.makeText(MainActivity.this,
											e.toString(), 1).show();
								}
							}
						}).show();
			} else {
				Intent i = new Intent(MainActivity.this, Label.class);
				i.putExtra("label", menuItems.get(position));
				startActivity(i);
				// Toast.makeText(MainActivity.this,
				// menuItems.get(position)+"  "+Integer.toString(count),
				// 1).show();
			}

			break;
		}
	}

}
