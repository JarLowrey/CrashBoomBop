package com.jtronlabs.crash_boom_bop_free;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import database.GameStatsAdapter;

public class ProfileFreeActivity extends Activity implements OnClickListener{
	float screenDens;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_free);
		
		//set up click listeners
		Button back = (Button)findViewById(R.id.back_btn_profile);
		back.setOnClickListener(this);
		
		//setup data
		GameStatsAdapter gameStatsDb = new GameStatsAdapter(this);
		gameStatsDb.open();
		
		//find screen density
		DisplayMetrics displayMetrics = new DisplayMetrics();
	    WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
	    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
	    screenDens = displayMetrics.density;
	    		
	    //setup medals & highscores
  		TextView numBronze = (TextView)findViewById(R.id.num_bronze);
  		TextView numSilver = (TextView)findViewById(R.id.num_silver);
  		TextView numGold = (TextView)findViewById(R.id.num_gold);
  		TextView numPlatinum = (TextView)findViewById(R.id.num_platinum);
  		TextView highscores = (TextView)findViewById(R.id.highscores);		

  		int[] numMedals = gameStatsDb.getNumMedals();
  		numBronze.setText(": "+numMedals[0]);
  		numSilver.setText(": "+numMedals[1]);
  		numGold.setText(": "+numMedals[2]);
  		numPlatinum.setText(": "+numMedals[3]);
  		highscores.setText(""+formatTopTenString(gameStatsDb.getTopTenScores()));
  		
  		//setup average response
  		TextView avgResponseTime = (TextView)findViewById(R.id.totalAverageResponseTime);
  		String avgTimeString = String.format(Locale.getDefault(),"%.3f", gameStatsDb.getAverageResponseTime())+" Seconds";				
		avgResponseTime.setText("Avg Response: "+avgTimeString);
		
		//clean up
		gameStatsDb.close();
	}
	
	@Override 
	public void onBackPressed(){
		finish();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_btn_profile:
			finish();
			break;
		default:
			break;
		}
		
	}
	
	private String formatTopTenString(int[] topTen){
		int onlyShowEightScores=8;
		String retVal="";
		for(int i=0;i<onlyShowEightScores;i++){
			int score = topTen[i];
			if(score==0){break;}
			retVal+=score+"\n";
		}
		return retVal;
	}
}
