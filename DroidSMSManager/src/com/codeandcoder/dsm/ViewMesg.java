package com.codeandcoder.dsm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ViewMesg extends Activity {
	TextView number, date, time, msg;
	Button frd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_msg_layout);
		number = (TextView) findViewById(R.id.tvNumber);
		date = (TextView) findViewById(R.id.tvDate);
		time = (TextView) findViewById(R.id.tvTime);
		msg = (TextView) findViewById(R.id.tvMsg);
		frd = (Button) findViewById(R.id.btFrd);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent i = getIntent();
		number.setText(i.getStringExtra("no"));
		date.setText(i.getStringExtra("date"));
		time.setText(i.getStringExtra("time"));
		msg.setText(i.getStringExtra("msg"));
		// startActivity(i);

		frd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent click = new Intent(ViewMesg.this, Forward_msg.class);
				click.putExtra("message", msg.getText());
				startActivity(click);

			}
		});

	}

}
