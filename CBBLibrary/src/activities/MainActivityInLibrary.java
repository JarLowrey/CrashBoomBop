package activities;

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

import com.jtronlabs.cbblibrary.R;

//http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html

public abstract class MainActivityInLibrary extends Activity implements OnClickListener{ 

	protected boolean appIsFree;
	
	/**
	 * Set the mainGraphic ImageView to the desired png file
	 */
	public abstract void setMainGraphic();
	
	/**
	 * Open a new activity that is the profile
	 */
	public abstract void openProfileActivity();
	/**
	 * Open a new activity that is the Game to be played
	 */
	public abstract void openGameActivity();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		setMainGraphic();
		
		Button shoppingCart = (Button)findViewById(R.id.purchase);
		if(appIsFree){
			shoppingCart.setVisibility(View.GONE);
		}else{
			shoppingCart.setOnClickListener(this);
		}
		
		RelativeLayout playBtn = (RelativeLayout)findViewById(R.id.playBtn);
		playBtn.setOnClickListener(this);
		RelativeLayout profileBtn= (RelativeLayout)findViewById(R.id.profileBtn);
		profileBtn.setOnClickListener(this);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.playBtn){
			openGameActivity();
		}
		else if(v.getId() == R.id.profileBtn){
			openProfileActivity();
		}
		else if(v.getId() == R.id.purchase){
			//http://stackoverflow.com/questions/10816757/rate-this-app-link-in-google-play-store-app-on-the-phone
			Uri uri = Uri.parse("market://details?id=com.jtronlabs.crash_boom_bop");
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK); 

			try {
			  startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
			  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.jtronlabs.crash_boom_bop")));
			}
		}
	}
		
}
