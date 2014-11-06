package com.jtronlabs.crash_boom_bop;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
		RelativeLayout profileBtn = (RelativeLayout)findViewById(R.id.profileBtn);
		profileBtn.setOnClickListener(this);
		
		//music
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
			i= new Intent(this, ProfilePaidActivity.class);
			startActivity(i);
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
