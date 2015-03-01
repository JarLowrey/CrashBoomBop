package activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.jtronlabs.cbblibrary.R;

public abstract class IntroActivityInLibrary extends Activity{

	public abstract void openMainActivity();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_intro);
		
		new CountDownTimer(800,200){
	        @Override 
	        public void onTick(long millisUntilFinished){
	        } 

	        @Override
	        public void onFinish(){
	        	openMainActivity();
	        }
	   }.start();		
	}	
}
