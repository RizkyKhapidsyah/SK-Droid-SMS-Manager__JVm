package com.codeandcoder.dsm;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsArrayAdapter extends ArrayAdapter {

	private final Context context;
	private final String[] item;

	SmsArrayAdapter(Context context, String[] item) {
		super(context, R.layout.list_sms_layout, item);
		this.context = context;
		this.item = item;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("4", "fdfdf");
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater
				.inflate(R.layout.list_sms_layout, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
		TextView textView = (TextView) rowView.findViewById(R.id.textView1);
		textView.setText(item[position]);

		String s = item[position];

		System.out.println(s);

		if (s.equals("Create Message")) {
			imageView.setImageResource(R.drawable.write_e_mail);
		} else if (s.equals("Inbox")) {
			imageView.setImageResource(R.drawable.inbox);
		} else if (s.equals("Send Item")) {
			imageView.setImageResource(R.drawable.sent_item);
		} else if (s.equals("Add Label")) {
			imageView.setImageResource(R.drawable.plus_edittext);
		} else if (s.equals("Settings")) {
			imageView.setImageResource(R.drawable.settings);
		} else if (s.equals("Draft")) {
			imageView.setImageResource(R.drawable.draft);
		} else if (s.equals("Trash")) {
			imageView.setImageResource(R.drawable.trash);
		}

		else
			imageView.setImageResource(R.drawable.ic_launcher);

		return rowView;
	}

}
