package com.codeandcoder.dsm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Sent_itemArrayAdapter extends ArrayAdapter {

	private Context context;
	private String[] Send_item_name, Send_item_number, Send_item_date,
			Send_item_type, Send_item_msg;

	Sent_itemArrayAdapter(Context context, String[] Send_item_name,
			String[] Send_item_number, String[] Send_item_date,
			String[] Send_item_type, String[] Send_item_msg) {
		super(context, R.layout.sent_item, Send_item_number);
		this.context = context;
		this.Send_item_name = Send_item_name;
		this.Send_item_number = Send_item_number;
		this.Send_item_date = Send_item_date;
		this.Send_item_type = Send_item_type;
		this.Send_item_msg = Send_item_msg;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.sent_item, parent, false);
		// ImageView imageView = (ImageView)
		// rowView.findViewById(R.id.imageView1);

		TextView name = (TextView) rowView.findViewById(R.id.tvName);
		TextView date = (TextView) rowView.findViewById(R.id.tvDate);
		TextView msg = (TextView) rowView.findViewById(R.id.tvSmallMsgView);
		TextView type = (TextView) rowView.findViewById(R.id.tvTime);

		/*
		 * String s = Send_item_name[position]; if(s==null)
		 * name.setText(Send_item_number[position]); else
		 * name.setText(Send_item_name[position]);
		 */
		name.setText(Send_item_number[position]);
		date.setText(Send_item_date[position]);
		msg.setText(Send_item_msg[position]);
		type.setText(Send_item_type[position]);

		/*
		 * System.out.println(s);
		 * 
		 * if (s.equals("Create Message")) {
		 * imageView.setImageResource(R.drawable.writemessage); } else if
		 * (s.equals("Send_item")) {
		 * imageView.setImageResource(R.drawable.Send_item); } else if
		 * (s.equals("Send Item")) {
		 * imageView.setImageResource(R.drawable.send); } else {
		 * imageView.setImageResource(R.drawable.ic_launcher); }
		 */

		return rowView;
	}

}