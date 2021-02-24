package com.codeandcoder.dsm;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
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
import android.view.WindowManager.LayoutParams;
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

public class SI extends Activity {
	/** Called when the activity is first created. */
	ListView lv;
	EditText inbox;
	Button edit;
	TextView date, time, message, sender;
	ArrayList<String> ldate, ltime, lmessage, lsender, lname, lid,ldateLong;
	ArrayList<String> Dldate, Dltime, Dlmessage, Dlsender, Dlname, Dlid;
	SimpleAdapter nAdapter,dialogAdapter;
	ArrayList<HashMap<String, String>> DdataHash,dataHash, phoneHash;
	String addressForNumber;
	private String KEY_DATE = "iDate", KEY_TIME = "iTime",
			KEY_MESSAGE = "iMessage", KEY_SENDER = "iSender";
	private boolean finish = false;
	private boolean typeOrNot = true;
	private boolean firstTime = true;
	boolean sharedStart = true;
	boolean pressedSearch=true;
	// Handler mHandalar;
	// Timer timeER;
	CharSequence inboxoption[] = { "Forward", "Delete", "Copy Message",
			"Cancle" };
	CharSequence Dialoginboxoption[] = { "Forward", "Copy Message",
	"Cancle" };

	RelativeLayout relative;
	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	SharedPreferences shared;
	Drawable dr;
	ClipboardManager m_ClipboardManager;
	public void deleteSMS(Context context, String message,String datePass) {
		try {
			// mLogger.logInfo("Deleting SMS from inbox");
			Uri uriSms = Uri.parse("content://sms/sent");
			Cursor c = context.getContentResolver().query(uriSms,new String[] { "_id", "thread_id", "address", "person","date", "body" }, null, null, null);

			if (c != null && c.moveToFirst()) {
				do {
					long id = c.getLong(0);
					long threadId = c.getLong(1);
					String address = c.getString(2);
					String body = c.getString(5);
					String date = c.getString(4);

					if (message.equals(body) 
							&& date.equals(datePass)) {
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
	public void DialogSave()
	{
		
		final Dialog info=new Dialog(SI.this);
		info.getWindow();
		info.setTitle(Integer.toString(DdataHash.size())+" Matched Found for "+"'"+inbox.getText().toString()+"'"); 
		info.setContentView(R.layout.dialog_main); 
		// info.setTitle("Info");
		info.setCancelable(true);
		
		info.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);		
		ListView lv = (ListView)info. findViewById(R.id.listView1);
		
		lv.setAdapter(dialogAdapter);		
		pressedSearch=false;
		if(DdataHash.size()==0)
		{
			Toast.makeText(getApplicationContext(), "No Matched Found", Toast.LENGTH_LONG).show();
		}
		else
		{
		info.show();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SI.this, Create_msg.class);
				i.putExtra("forward", DdataHash.get(position).get(KEY_MESSAGE)
						.toString());
				startActivity(i);

				// Toast.makeText(getApplicationContext(),dataHash.get(position).get(KEY_MESSAGE).toString(),
				// 1).show();
				/* Intent i=new Intent(Draft.this,Create_msg.class);
				 * i.putExtra("reply", lsender.get(position)); startActivity(i);
				 */
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alt = new AlertDialog.Builder(SI.this);
				alt.setTitle(
						Dlmessage.get(position).substring(
								0,
								Dlmessage.get(position).length() > 10 ? 10
										: Dlmessage.get(position).length()))
						.setItems(Dialoginboxoption, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								switch (which) {
								case 0:
									Intent i = new Intent(SI.this,
											Create_msg.class);
									i.putExtra("forward", Dlmessage
											.get(position));
									startActivity(i);
									break;
								
								case 1:
									ClipBoard(Dlmessage.get(position));
									Toast.makeText(getApplicationContext(),
											"Message copied", 1).show();
									dialog.dismiss();
									break;
								case 2:
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
		
		
	}
	public void ClipBoard(String sTxt) {
		m_ClipboardManager.setText(sTxt);
	}

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
				if( inbox.getText().toString().length()!=0)
				{
				if (finish ) {
					pressedSearch=false;					
					new InboxRetrive().execute();
				}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Empty Search Field", Toast.LENGTH_SHORT).show();
				}
			}
		});

		inbox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				/*
				 * if(finish) { dataHash.clear(); ldate.clear();
				 * lmessage.clear(); ltime.clear(); lsender.clear(); new
				 * InboxRetrive().execute(); }
				 */
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
				Intent i = new Intent(SI.this, Create_msg.class);
				i.putExtra("forward", dataHash.get(position).get(KEY_MESSAGE)
						.toString());
				startActivity(i);

				// Toast.makeText(getApplicationContext(),dataHash.get(position).get(KEY_MESSAGE).toString(),
				// 1).show();
				/* Intent i=new Intent(Draft.this,Create_msg.class);
				 * i.putExtra("reply", lsender.get(position)); startActivity(i);
				 */
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alt = new AlertDialog.Builder(SI.this);
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
									Intent i = new Intent(SI.this,
											Create_msg.class);
									i.putExtra("forward", lmessage
											.get(position));
									startActivity(i);
									break;
								case 1:
								/*	deleteSMS(Draft.this, lmessage
											.get(position), lsender
											.get(position), Long.parseLong(lid
											.get(position)));*/

									try
									{
									deleteSMS(SI.this,dataHash.get(
											position).get(KEY_MESSAGE)
											.toString(),
									dataHash.get(
											position).get("date_long")
											.toString() );
										/*Toast.makeText(getApplicationContext(),
												dataHash.get(
														position).get(KEY_MESSAGE)
														.toString()+
												dataHash.get(
														position).get("date_long")
														.toString()
														
														, Toast.LENGTH_SHORT).show();*/
									}catch (Exception e) {
										// TODO: handle exception
										Toast.makeText(getApplicationContext(),e.toString() , Toast.LENGTH_SHORT).show();
									}
									
									dataHash.remove(position);
									nAdapter.notifyDataSetChanged();
									Toast.makeText(getApplicationContext(),
											"Delete", 1).show();
									break;
								case 2:
									ClipBoard(lmessage.get(position));
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
		SI.this.getSMS(inbox.getText().toString());
		for (int i = 0; i < ldate.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_SENDER, lname.get(i));
			map.put(KEY_MESSAGE, lmessage.get(i));
			map.put(KEY_DATE, ldate.get(i));
			map.put(KEY_TIME, ltime.get(i));
			map.put("date_long", ldateLong.get(i));
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

	public void ChangeListenDialog() {

		DdataHash.clear();
		Dldate.clear();
		Dlmessage.clear();
		Dltime.clear();
		Dlsender.clear();
		SI.this.getSMSDialog(inbox.getText().toString());
		for (int i = 0; i < Dldate.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_SENDER, Dlname.get(i));
			map.put(KEY_MESSAGE, Dlmessage.get(i));
			map.put(KEY_DATE, Dldate.get(i));
			map.put(KEY_TIME, Dltime.get(i));
			
			DdataHash.add(map);
		}
		String[] from = { KEY_DATE, KEY_TIME, KEY_MESSAGE, KEY_SENDER };
		int[] to = { R.id.textView1date, R.id.textView1time,
				R.id.textView1message, R.id.textView1sender };		
		dialogAdapter = new SimpleAdapter(getApplicationContext(), DdataHash,
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
				"body like '%" + add + "%' or address like '%" + add + "%'", null, "date desc");		
		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			lsender.add(address);
			lname.add(SI.this.getDisplayName(address));
			lmessage.add(cur.getString(cur.getColumnIndexOrThrow("body")));
			lid.add(cur.getString(cur.getColumnIndex("_id")));
			String date = cur.getString(cur.getColumnIndexOrThrow("date"));
			ldateLong.add(date);
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

	}
	public void getSMSDialog(String add) {
		List<String> sms = new ArrayList<String>();

		Uri uriSMSURI = Uri.parse("content://sms/sent");
		Cursor cur = getContentResolver().query(uriSMSURI,
				new String[] { "_id", "person", "address", "body", "date" },
				"body like '%" + add + "%' or address like '%" + add + "%'", null, "date desc");		
		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			Dlsender.add(address);
			Dlname.add(SI.this.getDisplayName(address));
			Dlmessage.add(cur.getString(cur.getColumnIndexOrThrow("body")));
			Dlid.add(cur.getString(cur.getColumnIndex("_id")));
			String date = cur.getString(cur.getColumnIndexOrThrow("date"));
			if (date != null) {
				long l = Long.parseLong(date);
				Date d = new Date(l);
				Dldate
						.add(DateFormat.getDateInstance(DateFormat.LONG)
								.format(d));
				Dltime.add(DateFormat.getTimeInstance().format(d));
			}

			// sms.add("Number: " + addressS + " .Message: " +
			// body+" date "+date);

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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// timeER.cancel();
	}

	
	public void CreatObjects() {
		lv = (ListView) findViewById(R.id.listView1);
		inbox = (EditText) findViewById(R.id.editText1inbox);
		date = (TextView) findViewById(R.id.textView1date);
		time = (TextView) findViewById(R.id.textView1time);
		message = (TextView) findViewById(R.id.textView1message);
		sender = (TextView) findViewById(R.id.textView1sender);
		edit = (Button) findViewById(R.id.button1Textchanged);
		ldateLong= new ArrayList<String>();
		ldate = new ArrayList<String>();
		ltime = new ArrayList<String>();
		lmessage = new ArrayList<String>();
		lsender = new ArrayList<String>();
		lname = new ArrayList<String>();
		lid = new ArrayList<String>();
		dataHash = new ArrayList<HashMap<String, String>>();
		
		Dldate = new ArrayList<String>();
		Dltime = new ArrayList<String>();
		Dlmessage = new ArrayList<String>();
		Dlsender = new ArrayList<String>();
		Dlname = new ArrayList<String>();
		Dlid = new ArrayList<String>();
		DdataHash = new ArrayList<HashMap<String, String>>();
		
		phoneHash = new ArrayList<HashMap<String, String>>();
		relative = (RelativeLayout) findViewById(R.id.relativeInbox);
		m_ClipboardManager = (ClipboardManager) SI.this
				.getSystemService(Context.CLIPBOARD_SERVICE);
	}

	public class InboxRetrive extends AsyncTask<String, Integer, Integer> {
		ProgressDialog pro;
		String e;

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SI.this.finish = true;
			SI.this.typeOrNot = false;
			pro.cancel();
			if(pressedSearch)
				//Toast.makeText(Draft.this,inbox.getText().toString(), 1).show();
				SI.this.lv.setAdapter(nAdapter);
			else
				DialogSave();
			
			

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pro = new ProgressDialog(SI.this);
			pro.setTitle("Send Item");
			pro.setMessage("Please wait a moment ..");
			pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pro.show();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			relative.setBackgroundDrawable(dr);
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try

			{
				if (sharedStart) {
					SI.this.SharedTrigger();
					sharedStart = false;
					publishProgress();
					}
				if(pressedSearch)
					SI.this.ChangeListen();
				else
					SI.this.ChangeListenDialog();

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