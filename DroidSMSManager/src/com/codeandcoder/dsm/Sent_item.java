package com.codeandcoder.dsm;

import java.text.DateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class Sent_item extends ListActivity {

	String[] Send_item_name = new String[100],
			Send_item_number = new String[100],
			Send_item_date = new String[100], Send_item_type = new String[100],
			Send_item_msg = new String[100];

	int pos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Send_item_Read();
		setListAdapter(new Sent_itemArrayAdapter(this, Send_item_name,
				Send_item_number, Send_item_date, Send_item_type, Send_item_msg));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Toast.makeText(Sent_item.this,
				"Number : " + Send_item_number[position], Toast.LENGTH_SHORT)
				.show();

		Intent intent = new Intent(this, ViewMesg.class);
		intent.putExtra("name", Send_item_name[position]);
		intent.putExtra("no", Send_item_number[position]);
		intent.putExtra("date", Send_item_date[position]);
		intent.putExtra("time", Send_item_type[position]);
		intent.putExtra("msg", Send_item_msg[position]);
		startActivity(intent);
	}

	void Send_item_Read() {
		Uri mSmsSend_itemQueryUri = Uri.parse("content://sms/sent");
		Cursor cursor1 = getContentResolver().query(
				mSmsSend_itemQueryUri,
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

				Send_item_name[pos] = name;
				Send_item_number[pos] = number;

				if (date != null) {
					long l = Long.parseLong(date);
					Date d = new Date(l);
					Send_item_date[pos] = DateFormat.getDateInstance(
							DateFormat.LONG).format(d);
					Send_item_type[pos] = DateFormat.getTimeInstance()
							.format(d);
				} else {
					Send_item_date[pos] = date;
					Send_item_type[pos] = type;
				}

				Send_item_msg[pos] = msg;
				pos += 1;

			}
		}
	}

}