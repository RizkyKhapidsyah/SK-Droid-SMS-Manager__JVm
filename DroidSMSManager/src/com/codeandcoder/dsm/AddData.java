package com.codeandcoder.dsm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AddData {
	Activity act;
	Context ctx, context;
	ContentResolver cr;

	public AddData(Activity act) {
		cr = act.getContentResolver();
		this.act = act;
	}

	public void addsms(String address, String body, String date, String type, String read) {
		String[] addr = address.split(" ");
		String thread_id = save_draft(addr);
		ContentValues values = new ContentValues();
		values.put("body", body);
		values.put("date", date);
		values.put("type", type);
		
		if (type.equals("3")) {
			values.put("thread_id", thread_id);
		} else {
			values.put("address", address);
		}

		Uri uri = cr.insert(Uri.parse("content://sms/"), values);
		cr.notifyChange(uri, null);

	}

	protected String save_draft(String[] recipients) {
		Uri threadIdUri = Uri.parse("content://mms-sms/threadID");
		Uri.Builder builder = threadIdUri.buildUpon();
		for (String recipient : recipients) {
			builder.appendQueryParameter("recipient", recipient);
		}
		Uri uri = builder.build();
		String thread_id = get_thread_id(uri).toString();
		Log.d("thread_id", thread_id + " ");

		// ^tried "content://sms/" as well, but got the same result
		return thread_id;
	}

	private Long get_thread_id(Uri uri) {
		long threadId = 0;
		Cursor cursor = act.getContentResolver().query(uri,
				new String[] { "_id" }, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					threadId = cursor.getLong(0);
				}
			} finally {
				cursor.close();
			}
		}
		return threadId;
	}

}
