package com.jtronlabs.crash_boom_bop;

import activities.MainActivityInLibrary;
import android.content.Intent;
import android.widget.ImageView;

public class MainActivity extends MainActivityInLibrary{ 

	public MainActivity(){
		super();
		
		this.appIsFree=true;
	}
	
	@Override
	public void openProfileActivity(){
		Intent i = new Intent(this,ProfilePaidActivity.class);
		startActivity(i);
	}

	@Override
	public void openGameActivity() {
		Intent i = new Intent(this,GameActivity.class);
		startActivity(i);
	}

	@Override
	public void setMainGraphic() {
		ImageView mainGraphic = (ImageView)findViewById(R.id.mainGraphic);
		mainGraphic.setBackgroundResource(R.drawable.paid_main_graphic);
	}
	
}

