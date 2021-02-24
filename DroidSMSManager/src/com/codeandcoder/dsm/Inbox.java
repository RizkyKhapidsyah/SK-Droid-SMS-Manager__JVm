package com.codeandcoder.dsm;

import java.text.DateFormat;
import java.util.Date;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Inbox extends ListActivity {
	String[] Inbox_name = new String[100], Inbox_number = new String[100],
			Inbox_date = new String[100], Inbox_type = new String[100],
			Inbox_msg = new String[100];

	int pos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Inbox_Read();
		setListAdapter(new InboxArrayAdapter(this, Inbox_name, Inbox_number,
				Inbox_date, Inbox_type, Inbox_msg));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Toast.makeText(Inbox.this, "Number : " + Inbox_number[position],
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ViewMesg.class);
		intent.putExtra("name", Inbox_name[position]);
		intent.putExtra("no", Inbox_number[position]);
		intent.putExtra("date", Inbox_date[position]);
		intent.putExtra("time", Inbox_type[position]);
		intent.putExtra("msg", Inbox_msg[position]);
		startActivity(intent);
	}

	void Inbox_Read() {
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/sent");
		Cursor cursor1 = getContentResolver().query(
				mSmsinboxQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, null);
		startManagingCursor(cursor1);
		String[] columns = new String[] { "address", "person", "date", "body",
				"type" };
		if (cursor1.getCount() > 0) {
			String count = Integer.toString(cursor1.getCount());

			while (cursor1.moveToNext()) {

				String number = cursor1.getString(cursor1
						.getColumnIndex(columns[0]));
				String name = cursor1.getString(cursor1
						.getColumnIndex(columns[1]));
				String date = cursor1.getString(cursor1
						.getColumnIndex(columns[2]));
				String msg = cursor1.getString(cursor1
						.getColumnIndex(columns[3]));
				String type = cursor1.getString(cursor1
						.getColumnIndex(columns[4]));

				Inbox_name[pos] = name;
				Inbox_number[pos] = number;

				if (date != null) {
					long l = Long.parseLong(date);
					Date d = new Date(l);
					Inbox_date[pos] = DateFormat.getDateInstance(
							DateFormat.LONG).format(d);
					Inbox_type[pos] = DateFormat.getTimeInstance().format(d);
				} else {
					Inbox_date[pos] = date;
					Inbox_type[pos] = type;
				}

				Inbox_msg[pos] = msg;
				pos += 1;

			}
		}
	}

}