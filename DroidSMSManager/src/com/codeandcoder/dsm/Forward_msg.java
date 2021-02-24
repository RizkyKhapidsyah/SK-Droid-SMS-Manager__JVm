package com.codeandcoder.dsm;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Forward_msg extends Activity {
	String[] numberlist = new String[20];
	int numberlist_index = 0;
	EditText edWrite_no, edWrite_text;
	Button bSend, bFetchContact;
	String myEnterNumber = "";
	boolean isNumericEntered = false;

	static Boolean control = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_msg_layout);
		edWrite_no = (EditText) findViewById(R.id.edSendNo);
		edWrite_text = (EditText) findViewById(R.id.edSendText);

		edWrite_no.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (control == false) {
					control = true;
				} else {
					control = false;
					char pressedKey = (char) event.getUnicodeChar();
					if (pressedKey == '0' || pressedKey == '1'
							|| pressedKey == '2' || pressedKey == '3'
							|| pressedKey == '4' || pressedKey == '5'
							|| pressedKey == '6' || pressedKey == '7'
							|| pressedKey == '8' || pressedKey == '9') {
						myEnterNumber = myEnterNumber + pressedKey;
						isNumericEntered = true;
					}
				}

				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Intent i = getIntent();
		edWrite_text.setText(i.getStringExtra("message"));
	}

	protected void onStart() {
		super.onStart();

		bSend = (Button) findViewById(R.id.btSend);
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
				String s = edWrite_no.getText().toString();
				Check_addmyNumber();
				try {
					if (numberlist_index <= 0) {
						Toast
								.makeText(
										Forward_msg.this,
										"Please, Either enter number or select number from contact.",
										Toast.LENGTH_SHORT).show();
					} else
						while (numberlist_index > 0) {
							Toast
									.makeText(
											Forward_msg.this,
											"Sender index number : "
													+ numberlist_index,
											Toast.LENGTH_SHORT).show();
							numberlist_index -= 1;
							String number = numberlist[numberlist_index];
							sendLongSMS(number);
							sendSMSRecordToSendItem(number);
							Toast.makeText(Forward_msg.this,
									"Sender number : " + number,
									Toast.LENGTH_SHORT).show();
							numberlist[numberlist_index] = null;
							Toast.makeText(
									Forward_msg.this,
									"Sender number (After operation): "
											+ numberlist[numberlist_index],
									Toast.LENGTH_SHORT).show();
							if (numberlist_index == 0) {

								finish();
							}

						}

				} catch (ArrayIndexOutOfBoundsException e) {
					Toast.makeText(Forward_msg.this,
							"You can not send it more than 20 person.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

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
						+ number2);

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
}
