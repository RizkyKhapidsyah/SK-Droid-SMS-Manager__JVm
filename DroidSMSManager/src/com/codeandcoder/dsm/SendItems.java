package com.codeandcoder.dsm;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import android.provider.ContactsContract;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
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

public class SendItems extends Activity {
	/** Called when the activity is first created. */

	ListView lv;
	EditText inbox;
	TextView date, time, message, sender;
	ArrayList<String> ldate, ltime, lmessage, lsender, lname, lid, llongDate;
	ArrayList<String> sltime, sldate, slemessage, slsendrt, slname, slid;

	SimpleAdapter nAdapter;
	ArrayList<HashMap<String, String>> dataHash, phoneHash;
	String addressForNumber;
	private String KEY_DATE = "iDate", KEY_TIME = "iTime",
			KEY_MESSAGE = "iMessage", KEY_SENDER = "iSender";
	private boolean finish = false;
	private boolean typeOrNot = true;
	private boolean firstTime = true;
	ClipboardManager m_ClipboardManager;
	// Handler mHandalar;
	// Timer timeER;
	CharSequence inboxoption[] = { "Forward", "Delete", "Copy", "Cancle" };
	Button edit;
	RelativeLayout relative;
	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	SharedPreferences shared;
	Drawable dr;
	boolean sharedStart = true;
	int dataHashSizePrevious = 0, dataHashSizeCurrent = 0;

	public void ClipBoard(String sTxt) {
		m_ClipboardManager.setText(sTxt);
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

	public void deleteSMS(Context context, String message, String number,
			long idIn) {
		try {
			// mLogger.logInfo("Deleting SMS from inbox");
			Uri uriSms = Uri.parse("content://sms/sent");
			Cursor c = context.getContentResolver().query(
					uriSms,
					new String[] { "_id", "thread_id", "address", "person",
							"date", "body" }, null, null, null);

			if (c != null && c.moveToFirst()) {
				do {
					long id = c.getLong(0);
					long threadId = c.getLong(1);
					String address = c.getString(2);
					String body = c.getString(5);

					if (message.equals(body) && address.equals(number)
							&& id == idIn) {
						// mLogger.logInfo("Deleting SMS with id: " + threadId);
						context.getContentResolver().delete(
								Uri.parse("content://sms/" + id), null, null);
					}
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// mLogger.logError("Could not delete SMS from inbox: " +
			// e.getMessage());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		CreatObjects();
		// timeER =new Timer();
		new InboxRetrive().execute();

		edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (finish) {
					dataHash.clear();				
					new InboxRetrive().execute();
				}
			}
		});

		inbox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				// Toast.makeText(getApplicationContext(), inbox.
				// getText().toString(), 1).show();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

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
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alt = new AlertDialog.Builder(
						SendItems.this);
				alt.setTitle(
						lmessage.get(position).substring(
								0,
								lmessage.get(position).length() > 10 ? 10
										: lmessage.get(position).length()))
						.setItems(inboxoption, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								switch (which) {
								case 0:
									break;
								case 1:
									try {
										int countTotal = shared.getInt(
												"count_delete", 0);
										countTotal++;
										Editor ed = shared.edit();
										ed.putInt("count_delete", countTotal);
										ed.commit();
										// deleteSMS(SendItems.this,dataHash.get(position).get(KEY_MESSAGE).toString(),dataHash.get(position).get("key_sender_number").toString(),Long.parseLong(dataHash.get(position).get("_id").toString()));
										dataHash.remove(position);
										nAdapter.notifyDataSetChanged();
										Toast.makeText(getApplicationContext(),
												"Delete", 1).show();
									} catch (Exception e) {
										// TODO: handle exception
										Toast.makeText(getApplicationContext(),
												e.toString(), 1).show();
									}
									break;
								case 2:
									ClipBoard(dataHash.get(position).get(
											KEY_MESSAGE).toString());
									Toast.makeText(getApplicationContext(),
											"Message copied", 1).show();
									dialog.dismiss();
									break;
								case 3:
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
		SendItems.this.getSMS(inbox.getText().toString());
		for (int i = 0; i < ldate.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_SENDER, lname.get(i));
			map.put(KEY_MESSAGE, lmessage.get(i));
			map.put(KEY_DATE, ldate.get(i));
			map.put(KEY_TIME, ltime.get(i));
			map.put("_id", lid.get(i));
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
		List<String> sms = new ArrayList<String>();

		Uri uriSMSURI = Uri.parse("content://sms/sent");
		Cursor cur = getContentResolver().query(uriSMSURI,
				new String[] { "_id", "person", "address", "body", "date" },
				"body like '%" + add + "%' or address like '%" + add + "%'",
				null, null);

		Cursor c = cur;
		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			lsender.add(address);
			lname.add(SendItems.this.getDisplayName(address));
			lmessage.add(cur.getString(cur.getColumnIndexOrThrow("body")));
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
		}
		dataHashSizeCurrent = lsender.size();

	}

	int countPresentItem() {
		String add = inbox.getText().toString();
		Uri uriSMSURI = Uri.parse("content://sms/sent");
		Cursor cur = getContentResolver().query(uriSMSURI,
				new String[] { "_id", "person", "address", "body", "date" },
				"body like '%" + add + "%' or address like '%" + add + "%'",
				null, "date desc");
		return cur.getCount();
	}

	void countAddedItem() {

		sldate = new ArrayList<String>();
		sltime = new ArrayList<String>();
		slemessage = new ArrayList<String>();
		slsendrt = new ArrayList<String>();
		slname = new ArrayList<String>();
		slid = new ArrayList<String>();

		String add = inbox.getText().toString();
		Uri uriSMSURI = Uri.parse("content://sms/sent");
		Cursor cur = getContentResolver().query(
				uriSMSURI,
				new String[] { "_id", "person", "address", "body", "date" },
				"body like '%" + add + "%' or address like '%" + add + "%'",
				null,
				"date desc limit "
						+ (dataHashSizeCurrent - dataHashSizePrevious) + "");
		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			slsendrt.add(address);
			slname.add(SendItems.this.getDisplayName(address));
			slemessage.add(cur.getString(cur.getColumnIndexOrThrow("body")));
			slid.add(cur.getString(cur.getColumnIndex("_id")));
			String date = cur.getString(cur.getColumnIndexOrThrow("date"));
			if (date != null) {
				long l = Long.parseLong(date);
				Date d = new Date(l);
				sldate.add(DateFormat.getDateInstance(DateFormat.LONG)
						.format(d));
				sltime.add(DateFormat.getTimeInstance().format(d));
			}
		}

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

	public void sharedTriggerCreat() {
		Editor ed = shared.edit();
		try {
			ed.putString("sTask", ObjectSerializer.serialize(dataHash));
			ed.putString("sldata", ObjectSerializer.serialize(ldate));
			ed.putString("sltime", ObjectSerializer.serialize(ltime));
			ed.putString("slmessage", ObjectSerializer.serialize(lmessage));
			ed.putString("slsender", ObjectSerializer.serialize(lsender));
			ed.putString("slname", ObjectSerializer.serialize(lname));
			ed.putString("slid", ObjectSerializer.serialize(lid));
			ed.putString("sllongDate", ObjectSerializer.serialize(llongDate));
			ed.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(SendItems.this, e.toString(), 1).show();
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sharedTriggerCreat();
		// timeER.cancel();
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { // TODO Auto-generated method stub
	 * super.onActivityResult(requestCode, resultCode, data); if (resultCode ==
	 * RESULT_OK) { Uri contactUri = data.getData(); String[] projection =
	 * {Phone.NUMBER,Phone.DISPLAY_NAME,Phone.STATUS}; Cursor cursor =
	 * getContentResolver().query(contactUri, projection, Phone.NUMBER
	 * +" = '"+addressForNumber+"'", null, null); cursor.moveToFirst(); int
	 * column = cursor.getColumnIndex(Phone.DISPLAY_NAME); String number =
	 * cursor.getString(column); lname.add(number);
	 * 
	 * } }
	 */
	/*
	 * public List<String> getFULLSMS(){ List<String> sms = new
	 * ArrayList<String>(); Uri uriSMSURI = Uri.parse("content://sms/inbox");
	 * Uri uri = Uri.parse("content://contacts"); Cursor cur =
	 * getContentResolver().query(uriSMSURI, null,null, null, null); Cursor
	 * c=cur; while (cur.moveToNext()) { String addressS =
	 * cur.getString(cur.getColumnIndex("address")); String body =
	 * cur.getString(cur.getColumnIndexOrThrow("body")); sms.add("Number: " +
	 * addressS + " .Message: " + body);
	 * 
	 * } return sms;
	 * 
	 * }
	 */
	@SuppressWarnings("unchecked")
	public void SharedDataBase() {
		try {

			// new InboxRetrive().execute();
			// SharedTrigger();

			long first = System.currentTimeMillis();

			lmessage = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("slmessage", ObjectSerializer
							.serialize(new ArrayList<String>())));
			lsender = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("slsender", ObjectSerializer
							.serialize(new ArrayList<String>())));
			lname = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("slname", ObjectSerializer
							.serialize(new ArrayList<String>())));
			lid = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("slid", ObjectSerializer
							.serialize(new ArrayList<String>())));
			llongDate = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("sllongDate", ObjectSerializer
							.serialize(new ArrayList<String>())));
			ldate = (ArrayList<String>) ObjectSerializer.deserialize(shared
					.getString("sldate", ObjectSerializer
							.serialize(new ArrayList<String>())));
			dataHash = (ArrayList<HashMap<String, String>>) ObjectSerializer
					.deserialize(shared
							.getString(
									"sTask",
									ObjectSerializer
											.serialize(new ArrayList<HashMap<String, String>>())));
			dataHashSizePrevious = dataHash.size();

			if (dataHashSizePrevious > 0) {

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

	public void CreatObjects() {
		lv = (ListView) findViewById(R.id.listView1);
		inbox = (EditText) findViewById(R.id.editText1inbox);
		
		date = (TextView) findViewById(R.id.textView1date);
		time = (TextView) findViewById(R.id.textView1time);
		message = (TextView) findViewById(R.id.textView1message);
		sender = (TextView) findViewById(R.id.textView1sender);

		lname = new ArrayList<String>();
		lmessage = new ArrayList<String>();
		ldate = new ArrayList<String>();
		ltime = new ArrayList<String>();
		lsender = new ArrayList<String>();

		llongDate = new ArrayList<String>();
		lid = new ArrayList<String>();

		dataHash = new ArrayList<HashMap<String, String>>();
		phoneHash = new ArrayList<HashMap<String, String>>();
		relative = (RelativeLayout) findViewById(R.id.relativeInbox);
		edit = (Button) findViewById(R.id.button1Textchanged);
		
		m_ClipboardManager = (ClipboardManager) SendItems.this
				.getSystemService(Context.CLIPBOARD_SERVICE);
		shared = getSharedPreferences(filename, 0);
	}

	public class InboxRetrive extends AsyncTask<String, Integer, Integer> {
		ProgressDialog pro;
		String e;
		boolean getfirst, internalwork = false;

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SendItems.this.finish = true;
			SendItems.this.typeOrNot = false;
			if (dataHashSizeCurrent > dataHashSizePrevious && !getfirst) {
				try {
					// Toast.makeText(getApplicationContext(),Integer.toString(dataHashSizeCurrent)+"  "+Integer.toString(dataHashSizePrevious)+"destination",
					// 1).show();

					// Toast.makeText(getApplicationContext(), "2nd toast",
					// 1).show();
					for (int i = 0; i < sldate.size(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(KEY_SENDER, slname.get(i));
						map.put(KEY_MESSAGE, slemessage.get(i));
						map.put(KEY_DATE, sldate.get(i));
						map.put(KEY_TIME, sltime.get(i));

						lsender.add(0, slsendrt.get(i));
						lname.add(0, slname.get(i));
						lmessage.add(0, slemessage.get(i));
						ldate.add(0, sldate.get(i));
						ltime.add(0, sltime.get(i));
						dataHash.add(0, map);
					}
					nAdapter.notifyDataSetChanged();
					SendItems.this.sharedTriggerCreat();

				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getApplicationContext(), e.toString(), 1)
							.show();
				}
			} else {
				pro.cancel();
			}

			// Toast.makeText(InboxNew.this,e, 1).show();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if (internalwork) {
				internalwork = false;
				// Toast.makeText(SendItems.this, e, 1).show();
				relative.setBackgroundDrawable(dr);
				SendItems.this.lv.setAdapter(nAdapter);
				pro.cancel();
			} else {
				// Toast.makeText(SendItems.this, e, 1).show();
				relative.setBackgroundDrawable(dr);
				SendItems.this.lv.setAdapter(nAdapter);
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pro = new ProgressDialog(SendItems.this);
			// pro.setTitle("Sent Items");
			getfirst = shared.getBoolean("FirstPJ", true);
			if (getfirst) {
				pro.setMessage("Loading sent item for first time...");
			} else {
				pro.setMessage("Please wait a monent...");
			}
			pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pro.show();

		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				if (getfirst) {
					Editor ed = shared.edit();
					ed.putBoolean("FirstPJ", false);
					ed.commit();
					if (sharedStart) {
						sharedStart = false;
						SendItems.this.SharedTrigger();
						publishProgress();
						SendItems.this.ChangeListen();
						publishProgress();
						SendItems.this.sharedTriggerCreat();

					}
				} else {
					if (sharedStart) {
						sharedStart = false;
						SendItems.this.SharedTrigger();
						publishProgress();
						SendItems.this.SharedDataBase();
						internalwork = true;
						publishProgress();
						dataHashSizeCurrent = SendItems.this.countPresentItem();
						int countTotal = shared.getInt("count_delete", 0);
						dataHashSizePrevious += countTotal;
						if (dataHashSizeCurrent > dataHashSizePrevious) {

							SendItems.this.countAddedItem();
							// publishProgress();
						}
						// SendItems.this.ChangeListen();
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
				this.e = e.toString() + "Async";
				publishProgress();
				pro.cancel();

				// Toast.makeText(c, this.e.toString(), 1).show();
			}

			return null;

		}

	}
}