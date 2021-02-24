package com.codeandcoder.dsm;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.ClipboardManager;
import android.view.View;
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

public class InboxNew extends Activity {
	/** Called when the activity is first created. */
	ListView lv;
	EditText inbox;
	Button edit;
	TextView date, time, message, sender;
	ArrayList<String> ldate, ltime, lmessage, lsender, lname, lid, llongDate;
	SimpleAdapter nAdapter;
	ArrayList<HashMap<String, String>> dataHash, phoneHash;
	String addressForNumber;
	private String KEY_DATE = "iDate", KEY_TIME = "iTime",
			KEY_MESSAGE = "iMessage", KEY_SENDER = "iSender";
	boolean click = false;
	Handler mHandalar;
	Timer timeER;
	private static ClipboardManager m_ClipboardManager;
	CharSequence inboxoption[] = { "Forward", "Move to trash", "Call",
			"Copy Phone no", "Copy Message", "Add to Label", "Cancle" };
	RelativeLayout relative;
	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	SharedPreferences shared;
	Drawable dr;
	CharSequence items[];
	DatabaseInternal db;
	ArrayList<String> labelList, labelId;
	int i = -1;
	String id;
	boolean execute = false;
	int dataHashSize;
	boolean boot;
	boolean searchFunctional = true;

	public void fetchDatabaseLabel(final String messageDatabase,
			final String dateDatabase, final String sender,
			final String sender_number) {

		labelList.clear();
		db.open();
		Cursor c = db.getLabel();
		if (c != null && c.getCount() > 0) {
			// Toast.makeText(InboxNew.this, "label Created", 1).show();

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				labelList.add(c.getString(c.getColumnIndex("label_name")));
			}
			CharSequence[] cs = labelList.toArray(new CharSequence[labelList
					.size()]);
			db.close();
			String Category = "Nothing";

			try {
				db.open();
				Cursor cMes = db.getPosition(dateDatabase);
				cMes.moveToFirst();
				if (cMes != null && cMes.getCount() > 0) {
					Category = cMes.getString(cMes.getColumnIndex("label"));
					id = cMes.getString(cMes.getColumnIndex("id"));

				}
				db.close();

				for (i = 0; i < labelList.size(); i++) {
					if (Category.equals(labelList.get(i))) {
						break;
					}
				}
				if (i == labelList.size()) {
					i = -1;
				}
				// Toast.makeText(InboxNew.this, Category, 1).show();
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(InboxNew.this, e.toString(), 1).show();
			}
			AlertDialog.Builder alt = new AlertDialog.Builder(InboxNew.this);
			alt.setTitle("Move to..").setSingleChoiceItems(cs, i,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String label = labelList.get(which);
							if (i == -1) {
								try {

									// Toast.makeText(InboxNew.this, id,
									// 1).show();
									db.open();
									db.CreatLableMessage(label,
											messageDatabase, dateDatabase,
											sender, sender_number);
									db.close();
									dialog.dismiss();
								} catch (Exception e) {
									// TODO: handle exception
									Toast.makeText(InboxNew.this, e.toString(),
											1).show();
								}
							} else if (i == which) {

								dialog.dismiss();
								// Toast.makeText(InboxNew.this, "Same",
								// 1).show();

							} else {
								try {

									db.open();
									db.Update(label, id);
									db.close();
									dialog.dismiss();
									// Toast.makeText(InboxNew.this,label+id+
									// "Label Update"+" "+Integer.toString(i),
									// 1).show();

								} catch (Exception e) {
									// TODO: handle exception
									Toast.makeText(InboxNew.this, e.toString(),
											1).show();
								}
							}
						}
					})

			.show();
			db.close();
		} else {
			Toast.makeText(InboxNew.this, "No label Created", 1).show();
		}

	}

	public void SharedTrigger() {

		boolean checkImg = shared.getBoolean(KEY_IMG_CHK, false);

		if (checkImg) {

			String data = shared.getString("IMG", "Noting");
			if (!data.equals("Nothing")) {
				File imageFile = new File(data);
				Bitmap bitmap = BitmapFactory.decodeFile(imageFile
						.getAbsolutePath());
				dr = new BitmapDrawable(bitmap);

			}
		}

	}

	public void ClipBoard(String sTxt) {
		m_ClipboardManager.setText(sTxt);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		// Toast.makeText(InboxNew.this, "OnRetain", 1).show();
		return nAdapter;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		// Toast.makeText(InboxNew.this, "OnRetain", 1).show();
	}

	@SuppressWarnings("unchecked")
	public void SharedDataBase() {
		try {

			// new InboxRetrive().execute();
			// SharedTrigger();

			long first = System.currentTimeMillis();

			lmessage = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("lmessage", ObjectSerializer
							.serialize(new ArrayList<String>())));
			lsender = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("lsender", ObjectSerializer
							.serialize(new ArrayList<String>())));
			lname = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("lname", ObjectSerializer
							.serialize(new ArrayList<String>())));
			lid = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("lid", ObjectSerializer
							.serialize(new ArrayList<String>())));
			llongDate = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("llongDate", ObjectSerializer
							.serialize(new ArrayList<String>())));
			ldate = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("ldate", ObjectSerializer
							.serialize(new ArrayList<String>())));
			dataHash = (ArrayList<HashMap<String, String>>) ObjectSerializer
					.deserialize(shared
							.getString(
									"Task",
									ObjectSerializer
											.serialize(new ArrayList<HashMap<String, String>>())));
			phoneHash = dataHash;
			dataHashSize = dataHash.size();

			if (dataHashSize > 0) {

				String[] from = { KEY_DATE, KEY_TIME, KEY_MESSAGE, KEY_SENDER };
				int[] to = { R.id.textView1date, R.id.textView1time,
						R.id.textView1message, R.id.textView1sender };
				nAdapter = new SimpleAdapter(getApplicationContext(), dataHash,
						R.layout.inboxheader, from, to);
				long second = System.currentTimeMillis();
			} else {

			}

		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		CreatObjects();

		try {
			db.open();
			Cursor c = db.SendData("");
			int countdata = c.getCount();
			if (countdata > 0)
				execute = true;
			else
				execute = false;
			new InboxRetrive().execute();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(InboxNew.this, e.toString(), 1).show();
		}

		mHandalar = new Handler() {
			public void handleMessage(Message msg) {
				// this is the textview

				if (SmsBrodcast.isReceived) {

					// new InboxRetrive().execute();
					try {
						getLastSMS();
						lv.setAdapter(nAdapter);
						// Toast.makeText(InboxNew.this, "Timer", 1).show();
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(InboxNew.this, e.toString(), 1).show();
					}

					SmsBrodcast.isReceived = false;

				}

			}
		};

		timeER.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				mHandalar.obtainMessage(1).sendToTarget();
			}

		}, 5000, 1000);

		edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchFunctional = false;
				click = true;
				new InboxRetrive().execute();

				// Toast.makeText(InboxNew.this, "Edit Click", 1).show();
			}
		});

		try {

		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"sms", lsender.get(position), null));
				startActivity(intent);
				// Toast.makeText(getApplicationContext(),
				// Integer.toString(position), 1).show();
				/*
				 * Intent i=new Intent(InboxNew.this,Create_msg.class);
				 * i.putExtra("reply", lsender.get(position)); startActivity(i);
				 */
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arrayAdapter,
					final View arg1, final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alt = new AlertDialog.Builder(InboxNew.this);
				alt.setTitle(
						dataHash.get(position).get(KEY_MESSAGE).toString()
								.substring(
										0,
										dataHash.get(position).get(KEY_MESSAGE)
												.toString().length() > 20 ? 20
												: dataHash.get(position).get(
														KEY_MESSAGE).toString()
														.length())
								+ "...").setItems(inboxoption,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								switch (which) {
								case 0:
									Intent i = new Intent(InboxNew.this,
											Create_msg.class);
									i.putExtra("forward", dataHash
											.get(position).get(KEY_MESSAGE)
											.toString());
									startActivity(i);
									break;
								case 1:
									try {
										// deleteSMS(InboxNew.this,lmessage.get(position),lsender.get(position),Long.parseLong(lid.get(position)));
										/*
										 * map.put("key_long_date",
										 * llongDate.get(i));
										 * map.put("key_sender_number",
										 * lsender.get(i));
										 */
										db.open();
										db.CreatTrash(dataHash.get(position)
												.get(KEY_MESSAGE).toString(),
												dataHash.get(position).get(
														"key_sender_number")
														.toString(), dataHash
														.get(position)
														.get("key_long_date")
														.toString(), dataHash
														.get(position).get(
																KEY_SENDER)
														.toString());
										db.close();
										// Toast.makeText(getApplicationContext(),
										// llongDate.get(position), 1).show();
										dataHash.remove(position);
										nAdapter.notifyDataSetChanged();
										sharedTrigger();
										// new InboxRetrive().execute();
										Toast.makeText(getApplicationContext(),
												"Moved to trash", 1).show();
									} catch (Exception e) {
										// TODO: handle exception
										Toast.makeText(getApplicationContext(),
												e.toString(), 1).show();
									}
									break;
								case 2:
									Intent intent = new Intent(
											Intent.ACTION_CALL,
											Uri
													.parse("tel:"
															+ dataHash
																	.get(
																			position)
																	.get(
																			"key_sender_number")
																	.toString()));
									startActivity(intent);
									break;
								case 3:
									ClipBoard(dataHash.get(position).get(
											"key_sender_number").toString());
									break;
								case 4:
									ClipBoard(dataHash.get(position).get(
											KEY_MESSAGE).toString());
									break;
								case 5:
									dialog.dismiss();
									try {
										// llongDate

										fetchDatabaseLabel(dataHash.get(
												position).get(KEY_MESSAGE)
												.toString(), dataHash.get(
												position).get("key_long_date")
												.toString(), dataHash.get(
												position).get(KEY_SENDER)
												.toString(), dataHash.get(
												position).get(
												"key_sender_number").toString());
									} catch (Exception e) {
										// TODO: handle exception
										Toast.makeText(InboxNew.this,
												e.toString(), 1).show();
									}

									break;
								case 6:
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

	public void InternalChangeListen() {

		dataHash.clear();
		ldate.clear();
		lmessage.clear();
		ltime.clear();
		lsender.clear();
		InboxNew.this.getSMSInternalDatabase(inbox.getText().toString());
		for (int i = 0; i < ldate.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_SENDER, lname.get(i));
			map.put(KEY_MESSAGE, lmessage.get(i));
			map.put(KEY_DATE, ldate.get(i));
			map.put(KEY_TIME, ltime.get(i));
			map.put("key_long_date", llongDate.get(i));
			map.put("key_sender_number", lsender.get(i));

			dataHash.add(map);
		}
		String[] from = { KEY_DATE, KEY_TIME, KEY_MESSAGE, KEY_SENDER };
		int[] to = { R.id.textView1date, R.id.textView1time,
				R.id.textView1message, R.id.textView1sender };
		nAdapter = new SimpleAdapter(getApplicationContext(), dataHash,
				R.layout.inboxheader, from, to);
		// lv. setAdapter(nAdapter); //ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(Home.this,android.R.layout.simple_list_item_1,
		// msgList);

	}

	public void ChangeListen() {

		dataHash.clear();
		ldate.clear();
		lmessage.clear();
		ltime.clear();
		lsender.clear();
		InboxNew.this.getSMS(inbox.getText().toString());
		for (int i = 0; i < ldate.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_SENDER, lname.get(i));
			map.put(KEY_MESSAGE, lmessage.get(i));
			map.put(KEY_DATE, ldate.get(i));
			map.put(KEY_TIME, ltime.get(i));
			map.put("key_long_date", llongDate.get(i));
			map.put("key_sender_number", lsender.get(i));
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

	public void getLastSMS() {
		// Toast.makeText(InboxNew.this,
		// Integer.toString(SmsBrodcast.lsender.size()), 1).show();
		db.open();
		for (int i = 0; i < SmsBrodcast.lsender.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			String sender = SmsBrodcast.lsender.get(i);
			lsender.add(0, sender);
			String senderD = InboxNew.this.getDisplayName(sender);
			lname.add(0, senderD);
			map.put(KEY_SENDER, senderD);
			String message = SmsBrodcast.lmessage.get(i);

			lmessage.add(0, message);
			map.put(KEY_MESSAGE, message);

			llongDate.add(0, SmsBrodcast.llongDate.get(i));
			map.put("key_long_date", SmsBrodcast.llongDate.get(i));
			map.put("key_sender_number", SmsBrodcast.lsender.get(i));

			map.put(KEY_DATE, SmsBrodcast.ldate.get(i));
			map.put(KEY_TIME, SmsBrodcast.ltime.get(i));

			lid.add(SmsBrodcast.lid.get(i));
			db.CreateStore(senderD, message, SmsBrodcast.llongDate.get(i));

			dataHash.add(0, map);
			if (!click)
				sharedTrigger();
			// Toast.makeText(InboxNew.this, dataHash.get(i).toString(),
			// 1).show();
		}
		db.close();
		nAdapter.notifyDataSetChanged();

	}

	public void getSMSInternalDatabase(String add) {
		// List<String> sms = new ArrayList<String>();

		// Uri uriSMSURI = Uri.parse("content://sms/inbox");
		// Cursor cur = getContentResolver().query(uriSMSURI,new
		// String[]{"_id","address","body","date"},null, null, null);
		db.open();
		Cursor cur = db.SendData(add);
		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			lsender.add(address);
			String nameAndAddress = InboxNew.this.getDisplayName(address);
			lname.add(nameAndAddress);
			String body = cur.getString(cur.getColumnIndexOrThrow("body"));

			lmessage.add(body);

			String _id = cur.getString(cur.getColumnIndex("_id"));

			lid.add(_id);

			String date = cur.getString(cur.getColumnIndexOrThrow("date"));

			llongDate.add(date);
			if (date != null) {
				long l = Long.parseLong(date);
				Date d = new Date(l);
				ldate
						.add(DateFormat.getDateInstance(DateFormat.LONG)
								.format(d));
				ltime.add(DateFormat.getTimeInstance().format(d));
			}

			// sms.add("Number: " + addressS + " .Message: " +
			// body+" date "+date);
		}
		db.close();

	}

	public void getSMS(String add) {
		// List<String> sms = new ArrayList<String>();

		Uri uriSMSURI = Uri.parse("content://sms/inbox");
		Cursor cur = getContentResolver().query(uriSMSURI,
				new String[] { "_id", "address", "body", "date" }, null, null,
				"date desc");
		db.open();
		// Cursor cur=db.SendData();
		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			lsender.add(address);
			String nameAndAddress = InboxNew.this.getDisplayName(address);
			lname.add(nameAndAddress);
			String body = cur.getString(cur.getColumnIndexOrThrow("body"));

			lmessage.add(body);

			String _id = cur.getString(cur.getColumnIndex("_id"));

			lid.add(_id);

			String date = cur.getString(cur.getColumnIndexOrThrow("date"));

			llongDate.add(date);
			if (date != null) {
				long l = Long.parseLong(date);
				Date d = new Date(l);
				ldate
						.add(DateFormat.getDateInstance(DateFormat.LONG)
								.format(d));
				ltime.add(DateFormat.getTimeInstance().format(d));
			}

			db.CreateStore(nameAndAddress, body, date);
			// sms.add("Number: " + addressS + " .Message: " +
			// body+" date "+date);
		}
		db.close();

	}

	public String getDisplayName(String phoneNumber) {
		String name = "Nothing", Result;
		// Toast.makeText(getApplicationContext(), phoneNumber, 1).show();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		Cursor people = getContentResolver().query(uri, projection, null, null,
				null);

		int indexName = people
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = people
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		if (people != null && people.getCount() > 0) {

			people.moveToFirst();
			do {
				String number = people.getString(indexNumber);
				// Toast.makeText(getApplicationContext(),
				// number+people.getString(indexName) , 1).show();
				if (number.equals(phoneNumber)) {
					name = people.getString(indexName);
					// Toast.makeText(getApplicationContext(), name, 1).show();
				}
				// Do work...
			} while (people.moveToNext());
		}
		if (!name.equals("Nothing")) {
			Result = name + "<" + phoneNumber + ">";
		} else
			Result = phoneNumber;

		return Result;

	}

	public void sharedTrigger() {
		Editor ed = shared.edit();
		try {
			ed.putString("Task", ObjectSerializer.serialize(dataHash));
			ed.putString("ldata", ObjectSerializer.serialize(ldate));
			ed.putString("ltime", ObjectSerializer.serialize(ltime));
			ed.putString("lmessage", ObjectSerializer.serialize(lmessage));
			ed.putString("lsender", ObjectSerializer.serialize(lsender));
			ed.putString("lname", ObjectSerializer.serialize(lname));
			ed.putString("lid", ObjectSerializer.serialize(lid));
			ed.putString("llongDate", ObjectSerializer.serialize(llongDate));
			ed.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(InboxNew.this, e.toString(), 1).show();
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (searchFunctional)
			sharedTrigger();
		timeER.cancel();
	}

	public void CreatObjects() {
		shared = getSharedPreferences(filename, 0);
		timeER = new Timer();
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
		m_ClipboardManager = (ClipboardManager) InboxNew.this
				.getSystemService(Context.CLIPBOARD_SERVICE);
		relative = (RelativeLayout) findViewById(R.id.relativeInbox);
		db = new DatabaseInternal(InboxNew.this);
		
	}

	public class InboxRetrive extends AsyncTask<String, Integer, Integer> {
		ProgressDialog pro;
		String e;
		long first, second;
		String type;

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pro.cancel();
			InboxNew.this.lv.setAdapter(nAdapter);
			second = System.currentTimeMillis();

			// Toast.makeText(InboxNew.this,Long.toString(second-first)+"---"+type,
			// 1).show();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			first = System.currentTimeMillis();
			pro = new ProgressDialog(InboxNew.this);
			boot=shared.getBoolean("boot", false);
			if(boot)
			{
			SharedPreferences.Editor edit=shared.edit();
			edit.putBoolean("boot", false);
			edit.commit();
			}
			// pro.setTitle("Inbox");
			if (!execute && !click || boot) {
				pro.setMessage("Loading SMS only first time.");
				pro.setIndeterminate(false);
				pro.setMax(100);
				pro.setProgress(20);
				pro.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			} else {
				pro.setMessage("Please wait a moment ..");
				pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			}
			// if(InboxNew.this.typeOrNot)
			pro.setCancelable(false);
			pro.show();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			pro.setProgress(values[0]);
			relative.setBackgroundDrawable(dr);
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				
				
				if (click) {
					click = false;
					InboxNew.this.InternalChangeListen();
				} else {

					InboxNew.this.SharedTrigger();
					publishProgress(50);

					if (!execute || boot) {
						InboxNew.this.ChangeListen();
						InboxNew.this.sharedTrigger();
						publishProgress(100);
						type = "Database";
					} else {
						InboxNew.this.SharedDataBase();
						type = "Sharedpreferance";
						publishProgress(100);
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
				this.e = e.toString();
				pro.cancel();

				// Toast.makeText(c, this.e.toString(), 1).show();
			}

			return null;

		}

	}
}