package com.codeandcoder.dsm;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;



public class SplashScreenActivity extends Activity {
	/** Called when the activity is first created. */
	private final Handler mHandler = new Handler();
	private static final int duration = 3000;
	
	
	

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashscreen);

		mHandler.postDelayed(mPendingLauncherRunnable,
				SplashScreenActivity.duration);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mPendingLauncherRunnable);
	}

	private final Runnable mPendingLauncherRunnable = new Runnable() {

		public void run() {
			final Intent intent = new Intent(SplashScreenActivity.this,
					MainActivity.class);
			
			startActivity(intent);
			finish();
		}
	};
	
	
	
}
