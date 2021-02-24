package com.codeandcoder.dsm;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Label extends Activity {
	ListView lv;
	EditText inbox;
	Button edit;
	TextView date, time, message, sender;
	DatabaseInternal db;
	ArrayList<String> ldate, ltime, lmessage, lsender, lname, lid, llongDate,
			labelList, labelId;
	SimpleAdapter nAdapter;
	ArrayList<HashMap<String, String>> dataHash, phoneHash;
	String addressForNumber;
	private String KEY_DATE = "iDate", KEY_TIME = "iTime",
			KEY_MESSAGE = "iMessage", KEY_SENDER = "iSender";
	private static ClipboardManager m_ClipboardManager;
	CharSequence inboxoption[] = { "Forward", "Delete", "Copy Phone no",
			"Copy Message", "Change Label", "Cancle" };
	RelativeLayout relative;
	String dataLabel;

	ProgressDialog pro;

	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	SharedPreferences shared;
	Drawable dr;

	public void FetchDatabase(final String id, final int position) {
		labelList.clear();
		db.open();
		Cursor c = db.getLabel();
		if (c != null && c.getCount() > 0) {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				if (!dataLabel.equals(c.getString(c
						.getColumnIndex("label_name"))))
					labelList.add(c.getString(c.getColumnIndex("label_name")));
			}
			CharSequence[] cs = labelList.toArray(new CharSequence[labelList
					.size()]);
			db.close();
			if (labelList.size() > 0) {
				AlertDialog.Builder alt = new AlertDialog.Builder(Label.this);
				alt.setTitle("Move to..").setSingleChoiceItems(cs, -1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String label = labelList.get(which);

								try {

									db.open();
									db.Update(label, id);
									db.close();
									dataHash.remove(position);
									nAdapter.notifyDataSetChanged();
									dialog.dismiss();
									// Toast.makeText(InboxNew.this,label+id+
									// "Label Update"+" "+Integer.toString(i),
									// 1).show();

								} catch (Exception e) {
									// TODO: handle exception
									Toast.makeText(Label.this, e.toString(), 1)
											.show();
								}

							}
						})

				.show();
			} else
				Toast.makeText(Label.this, "No more Label", 1).show();
		}

	}

	public void SharedTrigger() {
		shared = getSharedPreferences(filename, 0);
		boolean checkImg = shared.getBoolean(KEY_IMG_CHK, false);
		String data = shared.getString("IMG", "Noting");
		if (!data.equals("Nothing")) {
			File imageFile = new File(data);
			Bitmap bitmap = BitmapFactory.decodeFile(imageFile
					.getAbsolutePath());
			dr = new BitmapDrawable(bitmap);

		}

	}

	public void ClipBoard(String sTxt) {
		m_ClipboardManager.setText(sTxt);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		CreatObject();
		Intent i = getIntent();
		dataLabel = i.getStringExtra("label");
		setTitle(dataLabel);
		// Toast.makeText(Label.this, dataLabel, 1).show();
		try {
			new AsyncCall().execute();

			edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new AsyncCall().execute();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(Label.this, e.toString(), 1).show();
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"sms", lsender.get(position), null));
				startActivity(intent);
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arrayAdapter,
					final View arg1, final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alt = new AlertDialog.Builder(Label.this);
				alt.setTitle(
						lmessage.get(position).substring(
								0,
								lmessage.get(position).length() > 20 ? 20
										: lmessage.get(position).length())
								+ "...").setItems(inboxoption,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								switch (which) {
								case 0:
									Intent i = new Intent(Label.this,
											Create_msg.class);
									i.putExtra("forward", lmessage
											.get(position));
									startActivity(i);
									break;
								case 1:
									// deleteSMS(Label.this,lmessage.get(position),lsender.get(position),Long.parseLong(lid.get(position)));
									db.open();
									db.DeleteFromlabel(lid.get(position));
									db.close();
									dataHash.remove(position);
									nAdapter.notifyDataSetChanged();

									// new InboxRetrive().execute();
									Toast.makeText(getApplicationContext(),
											"Deleted", 1).show();
									break;

								case 2:
									ClipBoard(lsender.get(position));
									Toast.makeText(Label.this,
											"Phone number copied", 1).show();
									break;
								case 3:
									ClipBoard(lmessage.get(position));
									Toast.makeText(Label.this,
											"Message copied", 1).show();
									break;
								case 4:
									FetchDatabase(lid.get(position), position);
									dialog.dismiss();
									try {
										// fetchDatabaseLabel(lmessage.get(position),llongDate.get(position),lname.get(position),lsender.get(position));
									} catch (Exception e) {
										// TODO: handle exception
										// Toast.makeText(InboxNew.this,
										// e.toString(), 1).show();
									}

									break;
								case 5:
									dialog.dismiss();
									break;
								}

							}
						}).show();

				try {
					// deleteSMS(InboxNew.this,lmessage.get(position),lsender.get(position),Long.parseLong(lid.get(position)));
					// DeleteMessage(lid.get(position));
					// new InboxRetrive().execute();
					// Toast.makeText(getApplicationContext(),
					// lid.get(position), 1).show();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getApplicationContext(), e.toString(), 1)
							.show();
				}
				return false;
			}
		});
	}

	public void ChangeListen() {

		dataHash.clear();
		ldate.clear();
		lmessage.clear();
		ltime.clear();
		lsender.clear();
		getSMS(inbox.getText().toString());
		for (int i = 0; i < ldate.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_SENDER, lname.get(i));
			map.put(KEY_MESSAGE, lmessage.get(i));
			map.put(KEY_DATE, ldate.get(i));
			map.put(KEY_TIME, ltime.get(i));
			dataHash.add(map);
		}
		String[] from = { KEY_DATE, KEY_TIME, KEY_MESSAGE, KEY_SENDER };
		int[] to = { R.id.textView1date, R.id.textView1time,
				R.id.textView1message, R.id.textView1sender };
		nAdapter = new SimpleAdapter(getApplicationContext(), dataHash,
				R.layout.inboxheader, from, to);

		// ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(Home.this,android.R.layout.simple_list_item_1,
		// msgList);

	}

	public void getSMS(String add) {

		Cursor cur = null;
		try {

			// Toast.makeText(getApplicationContext(),"Test", 1).show();
			db.open();
			cur = db.getLabelMessage(dataLabel, add);

			// Toast.makeText(getApplicationContext(),Integer.toString(cur.getCount()),
			// 1).show();

		} catch (Exception e) {
			// TODO: handle exception
			// Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}

		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("sn"));
			lsender.add(address);
			lname.add(cur.getString(cur.getColumnIndex("ad")));
			lmessage.add(cur.getString(cur.getColumnIndexOrThrow("st")));
			lid.add(cur.getString(cur.getColumnIndex("_id")));
			String date = cur.getString(cur.getColumnIndexOrThrow("date"));
			if (date != null) {
				long l = Long.parseLong(date);
				Date d = new Date(l);
				ldate
						.add(DateFormat.getDateInstance(DateFormat.LONG)
								.format(d));
				ltime.add(DateFormat.getTimeInstance().format(d));
			}

			// Toast.makeText(getApplicationContext(), "database fetch",
			// 1).show();

		}

		db.close();

	}

	public void CreatObject() {
		lv = (ListView) findViewById(R.id.listView1);
		inbox = (EditText) findViewById(R.id.editText1inbox);
		edit = (Button) findViewById(R.id.button1Textchanged);
		date = (TextView) findViewById(R.id.textView1date);
		time = (TextView) findViewById(R.id.textView1time);
		message = (TextView) findViewById(R.id.textView1message);
		sender = (TextView) findViewById(R.id.textView1sender);

		ldate = new ArrayList<String>();
		ltime = new ArrayList<String>();
		lmessage = new ArrayList<String>();
		lsender = new ArrayList<String>();
		lname = new ArrayList<String>();
		lid = new ArrayList<String>();
		llongDate = new ArrayList<String>();
		labelList = new ArrayList<String>();
		labelId = new ArrayList<String>();
		dataHash = new ArrayList<HashMap<String, String>>();
		phoneHash = new ArrayList<HashMap<String, String>>();
		m_ClipboardManager = (ClipboardManager) Label.this
				.getSystemService(Context.CLIPBOARD_SERVICE);
		relative = (RelativeLayout) findViewById(R.id.relativeInbox);
		db = new DatabaseInternal(Label.this);

	}

	public class AsyncCall extends AsyncTask<String, Integer, Integer> {
		String e;
		boolean fault = false;

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Label.this.SharedTrigger();
				publishProgress();
				Label.this.ChangeListen();

			} catch (Exception e1) {
				// TODO: handle exception
				e = e1.toString();
				publishProgress();
				fault = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!fault)
				lv.setAdapter(nAdapter);
			pro.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pro = new ProgressDialog(Label.this);
			pro.setTitle("Label");
			pro.setMessage("Please wait a moment ..");
			pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pro.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if (fault) {
				pro.dismiss();
				Toast.makeText(Label.this, e, 1).show();

			}
			relative.setBackgroundDrawable(dr);

		}

	}

}
