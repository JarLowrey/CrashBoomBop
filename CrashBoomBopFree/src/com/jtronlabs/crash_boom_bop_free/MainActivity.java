package com.jtronlabs.crash_boom_bop_free;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

//http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html

public class MainActivity extends Activity implements OnClickListener{ 

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		MainActivity.this.setContentView(R.layout.activity_main);
		
		RelativeLayout playBtn = (RelativeLayout)findViewById(R.id.playBtn);
		playBtn.setOnClickListener(this);
		RelativeLayout profileBtn= (RelativeLayout)findViewById(R.id.profileBtn);
		profileBtn.setOnClickListener(this);
		Button purchaseBtn = (Button)findViewById(R.id.purchase);
		purchaseBtn.setOnClickListener(this);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
		case R.id.playBtn:
			i= new Intent(this, GameActivity.class);
			startActivity(i);
			break;
		case R.id.profileBtn:
			i= new Intent(this, ProfileFreeActivity.class);
			startActivity(i);
			break;
		case R.id.purchase:
			//http://stackoverflow.com/questions/10816757/rate-this-app-link-in-google-play-store-app-on-the-phone
			Uri uri = Uri.parse("market://details?id=com.jtronlabs.crash_boom_bop");
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK); 

			try {
			  startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
			  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.jtronlabs.crash_boom_bop")));
			}
			break;
		default:
			break;
		}
	}
	
	@Override 
	public void onBackPressed(){  
		finish();
	}
	
}
