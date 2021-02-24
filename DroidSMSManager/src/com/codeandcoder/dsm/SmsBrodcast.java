package com.codeandcoder.dsm;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsBrodcast extends BroadcastReceiver {

	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String BOOT_COMPLETED="android.intent.action.BOOT_COMPLETED";
	private static final String TAG = "smsfwd";
	public static boolean isReceived = false;
	SharedPreferences shared;
	String fie="shared_file_name";
	public static int count = 0;
	public static ArrayList<String> ldate, ltime, lmessage, lsender, lname,
			lid, llongDate;
	Context c;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			// TODO Auto-generated method stub
			c=context;
			Bundle extras = intent.getExtras();
			// a notification message
			CreatObject();
			if(intent.getAction().equals(BOOT_COMPLETED))
			{
				SharedPreferences.Editor editor = shared.edit();
				editor.putBoolean("boot", true);
				editor.commit();
			}
			String messages = "";
			if (extras != null) {
				// get array data from SMS
				Object[] smsExtra = (Object[]) extras.get("pdus"); // "pdus" is
																	// the key

				for (int i = 0; i < smsExtra.length; ++i) {
					// get sms message
					SmsMessage sms = SmsMessage
							.createFromPdu((byte[]) smsExtra[i]);
					// get content and number

					String address = sms.getOriginatingAddress();
					lsender.add(address);
					String body = sms.getMessageBody();
					lmessage.add(body);
					long date = sms.getTimestampMillis();
					llongDate.add(Long.toString(date));
					lid.add(Integer.toString(sms.getIndexOnIcc()));

					if (date > 0) {
						Date d = new Date(date);
						ldate.add(DateFormat.getDateInstance(DateFormat.LONG)
								.format(d));
						ltime.add(DateFormat.getTimeInstance().format(d));
					}
					// create display message
					// store in the list
				}

				// better check size before continue

			}

			if (intent.getAction().equals(SMS_RECEIVED)) {

				isReceived = true;
				count++;
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, e.toString(), 1).show();
		}
	}

	public void CreatObject() {		
		shared=c.getSharedPreferences(fie, 0);
		ldate = new ArrayList<String>();
		ltime = new ArrayList<String>();
		lmessage = new ArrayList<String>();
		lsender = new ArrayList<String>();
		lname = new ArrayList<String>();
		lid = new ArrayList<String>();
		llongDate = new ArrayList<String>();
	}

}
