package com.jtronlabs.crash_boom_bop;

import activities.IntroActivityInLibrary;
import android.content.Intent;

public class IntroActivity extends IntroActivityInLibrary{

	@Override
	public void openMainActivity(){
    	Intent i= new Intent(this, MainActivity.class);
		startActivity(i);
		finish();
	}
}
