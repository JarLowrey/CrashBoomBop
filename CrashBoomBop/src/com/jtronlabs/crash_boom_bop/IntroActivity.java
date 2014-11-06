package com.jtronlabs.crash_boom_bop;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;

public class IntroActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		IntroActivity.this.setContentView(R.layout.activity_intro);
		
		new CountDownTimer(800,200){
	        @Override
	        public void onTick(long millisUntilFinished){
	        } 

	        @Override
	        public void onFinish(){
	        	Intent i= new Intent(IntroActivity.this, MainActivity.class);
				startActivity(i);
				finish();
	        }
	   }.start();		
	}	
}
