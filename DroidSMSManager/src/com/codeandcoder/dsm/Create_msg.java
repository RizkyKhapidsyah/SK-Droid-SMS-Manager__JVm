package com.codeandcoder.dsm;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class Create_msg extends Activity {
	String[] numberlist = new String[20];
	int numberlist_index = 0;
	EditText edWrite_text;
	AutoCompleteTextView edWrite_no;
	Button bSend, bFetchContact;
	String myEnterNumber = "";
	boolean isNumericEntered = false;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> phoneContact;
	private boolean first = false;
	public boolean finish = false;
	boolean permissionUndo;
	int timeCount = 3;
	long y;
	RelativeLayout relativelayout;
	Drawable dr;

	static Boolean control = false;
	CountDownTimer timer;
	String filename = "shared_file_name";
	String KEY_CHK_WAT = "undo";
	String KEY_CHK_WAT_TIME = "time";
	String KEY_IMG = "images_file_64bit";
	String KEY_IMG_CHK = "img_file";
	SharedPreferences shared;

	public void CreatDraft() {
		// Store the message in the draft folder so that it shows in Messaging
		// apps.
		ContentValues values = new ContentValues();
		// Message address.
		values.put("address", edWrite_no.getText().toString());
		// Message body.
		values.put("body", edWrite_text.getText().toString());
		// Date of the draft message.
		values.put("date", String.valueOf(System.currentTimeMillis()));
		values.put("type", "3"); // Put the actual thread id here. 0 if there is
									// no thread yet.

		getContentResolver().insert(Uri.parse("content://sms/draft"), values);
	}

	public void SharedTrigger() {
		permissionUndo = shared.getBoolean(KEY_CHK_WAT, false);
		if (permissionUndo) {
			timeCount = shared.getInt(KEY_CHK_WAT_TIME, 3);
			timeCount = timeCount == 3 ? 3 : timeCount / 10;
		}
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

	public void TimerNew() {
		timer = new CountDownTimer(y, 1000) {

			public void onTick(long millisUntilFinished) {
				bSend.setText("Undo ( " + (millisUntilFinished / 1000)
						+ " Sec )");
			}

			public void onFinish() {

				// Toast.makeText(getApplicationContext(),
				// Integer.toString(timeCount), 1).show();
				// Toast.makeText(getApplicationContext(), "Finish", 1).show();
				sendSMSTimer();
				// timeAlrt.show();
				// time.setText("done!");
			}

		};

	}

	public void sendSMSTimer() {
		String s = edWrite_text.getText().toString();
		Check_addmyNumber();
		try {
			if (numberlist_index <= 0) {
				edWrite_no.setText("");
				bSend.setText("Send");
				Toast
						.makeText(
								Create_msg.this,
								"Please, Either enter number or select number from contact.",
								Toast.LENGTH_SHORT).show();
			} else {
				while (numberlist_index > 0) {
					// Toast.makeText(Create_msg.this, "Sender index number : "
					// + numberlist_index, Toast.LENGTH_SHORT).show();
					numberlist_index -= 1;
					String number = numberlist[numberlist_index];

					try {
						// sendLongSMS(number);
						sendSMSRecordToSendItem(number);
						sendSMS(number, s);
					} catch (Exception e) {
						// TODO: handle exception
						Toast
								.makeText(getApplicationContext(),
										e.toString(), 1).show();
					}
					Toast.makeText(Create_msg.this,
							"Sender number : " + number, Toast.LENGTH_SHORT)
							.show();
					numberlist[numberlist_index] = null;
					// mistake // Toast.makeText(Create_msg.this,
					// "Sender number (After operation): " +
					// numberlist[numberlist_index], Toast.LENGTH_SHORT).show();
					if (numberlist_index == 0) {

						finish();

					}

				}
				bSend.setText("SMS Sent...");
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			Toast.makeText(Create_msg.this,
					"You can not send it more than 20 person.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_msg_layout);
		edWrite_no = (AutoCompleteTextView) findViewById(R.id.edSendNo);
		edWrite_text = (EditText) findViewById(R.id.edSendText);
		bSend = (Button) findViewById(R.id.btSend);
		phoneContact = new ArrayList<String>();
		shared = getSharedPreferences(filename, 0);
		relativelayout = (RelativeLayout) findViewById(R.id.creatBackground);

		Intent i = getIntent();
		String forward = i.getStringExtra("forward");
		String reply = i.getStringExtra("reply");
		if (forward != null) {
			edWrite_text.setText(forward);
			// Toast.makeText(Create_msg.this, reply, 1).show();
		} else if (reply != null) {
			numberlist[0] = reply;
			edWrite_no.setText(reply);
			numberlist_index++;
		}

		new ContactRead().execute();

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, phoneContact) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);

				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				/* YOUR CHOICE OF COLOR */
				textView.setTextColor(Color.BLACK);

				return view;
			}

		};
		edWrite_no.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ... your stuff
				Object ob = parent.getItemAtPosition(position);

				String number = ob.toString().substring(
						ob.toString().indexOf("<") + 1,
						ob.toString().indexOf(">"));
				edWrite_no.setText(number);
				isNumericEntered = true;
				myEnterNumber = number;
				first = true;
				
				numberlist[0]=number;
				numberlist_index=1;
				 Toast.makeText(getApplicationContext(),
				 "Pressed"+"  "+number, 1).show();
			}
		});

		edWrite_no.setThreshold(1);
		edWrite_no.setAdapter(adapter);

		edWrite_text
				.setOnEditorActionListener(new DoneOnEditorActionListener());
		edWrite_no.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			//	Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			//	Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				numberlist[0]=arg0.toString();
				numberlist_index=1;
			//	Toast.makeText(getApplicationContext(), Character.toString(arg0.toString().charAt(arg0.toString().length()-1)), Toast.LENGTH_SHORT).show();
				
			}
		});

		
	}

	public void getContact() {
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer
						.parseInt(cur
								.getString(cur
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// Query phone here. Covered next
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						// Do something with phones
						String phone = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneContact.add(name + "<" + phone + ">");
						// Toast.makeText(Create_msg.this,
						// name+" "+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
						// 1).show();
					}
					pCur.close();

				}
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}

	protected void onStart() {
		super.onStart();

		bFetchContact = (Button) findViewById(R.id.btContactFtech);
		bFetchContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("content://contacts");
				Intent pickContactIntent = new Intent(Intent.ACTION_PICK, uri);
				pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only
																// contacts w/
																// phone numbers
				startActivityForResult(pickContactIntent, 1);
			}
		});

		bSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bSend.getText().toString().length() != 0
						&& edWrite_text.getText().toString().length() != 0) {
					if (permissionUndo) {
						if (bSend.getText().toString().equals("Send")) {
							timer.start();
							// Toast.makeText(getApplicationContext(),
							// "Started", 1).show();
						} else {
							Toast.makeText(getApplicationContext(), "Stopped",
									1).show();
							timer.cancel();
							bSend.setText("Send");
						}
					}

					else {
						sendSMSTimer();
						// Toast.makeText(getApplicationContext(), "Fast send",
						// 1).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Please put all field currectly", 1).show();
				}
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitByBackKey();
			// moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void exitByBackKey() {

		AlertDialog alertbox = new AlertDialog.Builder(this).setMessage(
				"Do you want to exit editor?").setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							if (edWrite_no.getText().toString().length() != 0
									&& edWrite_text.getText().toString()
											.length() != 0) {
								// CreatDraft();
								// Toast.makeText(getApplicationContext(),
								// "Draft start0", 1).show();
								AddData add = new AddData(Create_msg.this);
								// Toast.makeText(getApplicationContext(),
								// "Draft start1", 1).show();
								add
										.addsms(
												edWrite_no.getText().toString(),
												edWrite_text.getText()
														.toString(),
												String.valueOf(System
														.currentTimeMillis()),
												"3", "0");
								Toast.makeText(getApplicationContext(),
										"Draft Saved", 1).show();
								finish();
							} else {
								finish();
							}
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(getApplicationContext(),
									e.toString(), 1).show();
						}

						// close();

					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).show();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		SharedTrigger();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {

				Check_addmyNumber();

				Uri contactUri = data.getData();
				String[] projection = { Phone.NUMBER, Phone.DISPLAY_NAME,
						Phone.STATUS };
				Cursor cursor = getContentResolver().query(contactUri,
						projection, null, null, null);
				cursor.moveToFirst();
				int column = cursor.getColumnIndex(Phone.NUMBER);
				String number = cursor.getString(column);
				// edWrite_no.setText(number);

				numberlist[numberlist_index] = number;

				numberlist_index += 1;

				int column2 = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				String number2 = cursor.getString(column2);
				edWrite_no.setText(edWrite_no.getText().toString() + " "
						+ number);

			}
		}
	}

	public void Check_addmyNumber() {
		if (isNumericEntered == true) {
			numberlist[numberlist_index] = myEnterNumber;
			numberlist_index += 1;
			myEnterNumber = "";
			isNumericEntered = false;
		}
	}

	public void sendLongSMS(String number) {

		String phoneNumber = number;// .getText().toString();
		String message = edWrite_text.getText().toString();
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> parts = smsManager.divideMessage(message);
		smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null,
				null);
	}

	public void sendSMSRecordToSendItem(String number) {

		ContentValues values = new ContentValues();
		values.put("address", number);
		values.put("body", edWrite_text.getText().toString());
		getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}

	public class ContactRead extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			Create_msg.this.SharedTrigger();
			publishProgress();
			Create_msg.this.getContact();

			// Toast.makeText(Create_msg.this, "Background", 1).show();
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			relativelayout.setBackgroundDrawable(dr);
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// Create_msg.this.SharedTrigger();
			Create_msg.this.finish = true;

			switch (timeCount) {

			case 3:
				// Toast.makeText(getApplicationContext(),
				// "Preference:"+Integer.toString(3), 1).show();
				y = 4000;
				break;
			case 4:
				y = 5000;
				break;
			case 5:
				y = 6000;
				break;
			case 6:
				y = 7000;
				break;
			case 7:
				y = 8000;
				break;
			case 8:
				y = 9000;
				break;
			case 9:
				y = 10000;
				break;
			case 10:
				y = 11000;
				break;
			default:
				break;
			}
			Create_msg.this.TimerNew();

			// if(permissionUndo)
			// Toast.makeText(getApplicationContext(),
			// Long.toString((int)timeCount*1000), 1).show();
			// Toast.makeText(Create_msg.this, "Finish", 1).show();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	}

	class DoneOnEditorActionListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION) {
				Toast.makeText(getApplicationContext(), "Done Pressed", 1)
						.show();
				edWrite_text.clearFocus();
				return true;
			}
			return false;
		}
	}
}
