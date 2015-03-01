package com.jtronlabs.crash_boom_bop_free;

import activities.GameActivityInLibrary;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class GameActivity extends GameActivityInLibrary
{
    private AdView adView;
    final int adID = 237418231;//some random number...
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	
    	//create the adview
		adView = new AdView(this);
		adView.setAdSize(AdSize.LARGE_BANNER);
		adView.setAdUnitId("ca-app-pub-1314947069846070/4430001944");
		
		adView.setId(adID);
		
		//position the adView onscreen
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
			    RelativeLayout.LayoutParams.WRAP_CONTENT, 
			    RelativeLayout.LayoutParams.WRAP_CONTENT); 
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adView.setLayoutParams(params);
		
		//add to layout
		gameWindow.addView(adView);
	    
		//load the ad
	    AdRequest adRequest = new AdRequest.Builder()
	    	//.addTestDevice("FFAA2B4ECD49CBF2A0AB7F9D447410D7")//found from logcat when running an ad. filter "addTestDevice"
	    	.build();
	    adView.loadAd(adRequest);
	    
	    //in order for the ad to persist when the screen clears between cases, it must be added to the array of View ID's that are
	    //exempt from that clearing. So, copy the old values to a new array, insert the adView's ID, and then assign the old array to the new array
	    int[] stayVisibleWhenScreenClears = new int[viewsThatPersistOnClearScreen.length+1];
	    for(int i=0;i<viewsThatPersistOnClearScreen.length;i++){
	    	stayVisibleWhenScreenClears[i] = viewsThatPersistOnClearScreen[i];
	    }
	    stayVisibleWhenScreenClears[stayVisibleWhenScreenClears.length-1] = adID;
	    viewsThatPersistOnClearScreen = stayVisibleWhenScreenClears;
    }
    
    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }
	@Override
	protected void onResume(){
		adView.resume();
		super.onResume();
	}
	@Override
	protected void onPause() {
		adView.pause();
	    super.onPause();
	}
	
	
	@Override
	protected void gameOver(){
		adView.pause();		
		super.gameOver();
	}
	
}

