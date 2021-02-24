package com.codeandcoder.dsm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InboxArrayAdapter extends ArrayAdapter {

	private Context context;
	private String[] Inbox_name, Inbox_number, Inbox_date, Inbox_type,
			Inbox_msg;

	InboxArrayAdapter(Context context, String[] Inbox_name,
			String[] Inbox_number, String[] Inbox_date, String[] Inbox_type,
			String[] Inbox_msg) {
		super(context, R.layout.inbox, Inbox_number);
		this.context = context;
		this.Inbox_name = Inbox_name;
		this.Inbox_number = Inbox_number;
		this.Inbox_date = Inbox_date;
		this.Inbox_type = Inbox_type;
		this.Inbox_msg = Inbox_msg;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.inbox, parent, false);
		// ImageView imageView = (ImageView)
		// rowView.findViewById(R.id.imageView1);

		TextView name = (TextView) rowView.findViewById(R.id.tvName);
		TextView date = (TextView) rowView.findViewById(R.id.tvDate);
		TextView msg = (TextView) rowView.findViewById(R.id.tvSmallMsgView);
		TextView type = (TextView) rowView.findViewById(R.id.tvTime);

		/*
		 * String s = Inbox_name[position]; if(s==null)
		 * name.setText(Inbox_number[position]); else
		 * name.setText(Inbox_name[position]);
		 */
		name.setText(Inbox_number[position]);
		date.setText(Inbox_date[position]);
		msg.setText(Inbox_msg[position]);
		type.setText(Inbox_type[position]);

		/*
		 * System.out.println(s);
		 * 
		 * if (s.equals("Create Message")) {
		 * imageView.setImageResource(R.drawable.writemessage); } else if
		 * (s.equals("Inbox")) { imageView.setImageResource(R.drawable.inbox); }
		 * else if (s.equals("Send Item")) {
		 * imageView.setImageResource(R.drawable.send); } else {
		 * imageView.setImageResource(R.drawable.ic_launcher); }
		 */

		return rowView;
	}

}