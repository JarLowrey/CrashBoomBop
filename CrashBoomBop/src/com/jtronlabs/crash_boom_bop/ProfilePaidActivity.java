package com.jtronlabs.crash_boom_bop;

import java.util.Locale;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import charts.BarChartViewBuilder;
import charts.PieChartViewBuilder;
import database.GameStatsAdapter;

public class ProfilePaidActivity extends Activity implements OnClickListener{
	Toast toast;
	
    float screenDens;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_paid);
		
		//find screen density
		DisplayMetrics displayMetrics = new DisplayMetrics();
	    WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
	    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
	    screenDens = displayMetrics.density;
		
		//Toast
		toast = new Toast(getApplicationContext());
		
		Button backBtn = (Button)findViewById(R.id.back_btn_profile);
		backBtn.setOnClickListener(this);
//		Button rateBtn = (Button)findViewById(R.id.rate);
//		rateBtn.setOnClickListener(this);
		
		//setup data
		GameStatsAdapter gameStatsDb = new GameStatsAdapter(this);
		gameStatsDb.open();
		int numGames = gameStatsDb.getNumGames();
		int[] streakValues = gameStatsDb.getRecentStreaks(numGames);
		int[] scoreValues = gameStatsDb.getRecentScores(numGames);
		long[] timeValues = gameStatsDb.getRecentTimes(numGames);
		
		//change text
		TextView totalScore = (TextView)findViewById(R.id.totalScore);
		TextView highestScore = (TextView)findViewById(R.id.highestScore);
		TextView highestStreak = (TextView)findViewById(R.id.highestStreak);
		TextView totalTime = (TextView)findViewById(R.id.totalTime);
		TextView longestTime = (TextView)findViewById(R.id.longestTime);
		TextView avgResponseTime = (TextView)findViewById(R.id.totalAverageResponseTime);
		TextView totalBonusPoints = (TextView)findViewById(R.id.totalBonusPoints);
		highestScore.setText("Score: "+gameStatsDb.getHighestScore());
		longestTime.setText("Longest Game: "+ gameStatsDb.getLongestGame()+ " Seconds");
		totalScore.setText("Score: "+gameStatsDb.getTotalScore());
		highestStreak.setText("Streak: "+gameStatsDb.getHighestStreak());
		totalTime.setText("Time: "+convertMillisToString(gameStatsDb.getTotalTime()));
		totalBonusPoints.setText("Bonus Points: "+gameStatsDb.getTotalBonusPoints());		
		String avgTimeString = String.format(Locale.getDefault(),"%.3f", gameStatsDb.getAverageResponseTime())+" Seconds";				
		avgResponseTime.setText("Avg Response: "+avgTimeString);
		
		//setup medals & highscores
		TextView numBronze = (TextView)findViewById(R.id.num_bronze);
		TextView numSilver = (TextView)findViewById(R.id.num_silver);
		TextView numGold = (TextView)findViewById(R.id.num_gold);
		TextView numPlatinum = (TextView)findViewById(R.id.num_platinum);
		TextView highscores = (TextView)findViewById(R.id.highscores);		
//		numBronze.setCompoundDrawables(getResources().getDrawable(R.drawable.medal_copper), null, null, null);
//		numSilver.setCompoundDrawables(getResources().getDrawable(R.drawable.medal_silver), null, null, null);
//		numGold.setCompoundDrawables(getResources().getDrawable(R.drawable.medal_gold), null, null, null);
//		numPlatinum.setCompoundDrawables(getResources().getDrawable(R.drawable.medal_platinum), null, null, null);
		int[] numMedals = gameStatsDb.getNumMedals();
		numBronze.setText(": "+numMedals[0]);
		numSilver.setText(": "+numMedals[1]);
		numGold.setText(": "+numMedals[2]);
		numPlatinum.setText(": "+numMedals[3]);
		highscores.setText(""+formatTopTenString(gameStatsDb.getTopTenScores()));
				
		//Create bar Charts
		BarChartViewBuilder bar = new BarChartViewBuilder(this,screenDens);
		GraphicalView scores = bar.getBarChart("Game Scores","Game","Score", scoreValues);
		GraphicalView streaks = bar.getBarChart("Highest Streak","Game","Streak", streakValues);
		GraphicalView times = bar.getBarChart("Game Times","Game","Seconds", timeValues);
		
		//Create Pie Charts
		PieChartViewBuilder pie = new PieChartViewBuilder(this,screenDens);
		GraphicalView pieTotalCases = pie.getPieChart("Total Cases", gameStatsDb.getNumCasesPassed(), gameStatsDb.getNumCasesFailed());
		GraphicalView pieTotalCombos = pie.getPieChart("Total Combos", gameStatsDb.getNumCombosPassed(), gameStatsDb.getNumCombosFailed());

		//add charts to layout
		LinearLayout pastScoresChart = (LinearLayout) findViewById(R.id.pastScoresChart);
		LinearLayout pastStreaksChart = (LinearLayout) findViewById(R.id.streakChart);
		LinearLayout pastTimesChart = (LinearLayout) findViewById(R.id.pastTimesChart);
		LinearLayout casesPieChart = (LinearLayout) findViewById(R.id.casesPieChart);
		LinearLayout comboPieChart = (LinearLayout) findViewById(R.id.comboPieChart);
		pastStreaksChart.addView(streaks);
		pastScoresChart.addView(scores);
		pastTimesChart.addView(times);	
		casesPieChart.addView(pieTotalCases);		
		comboPieChart.addView(pieTotalCombos);
		
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
	private String convertMillisToString(long milli){
		String timeStr="";
		long x = milli / 1000;
		int seconds = (int)(x % 60);
		x /= 60;
		int minutes =(int) (x % 60);
		x /= 60;
		int hours = (int)(x % 24);
		x /= 24;
		int days = (int)x;
		
		timeStr+=(days>0) ? (days+" Days "):"";
		timeStr+=(hours>0) ? (hours+" Hours "):"";
		timeStr+=(minutes>0) ? (minutes+" Minutes "):"";
		timeStr+=seconds+" Seconds";
		
		return timeStr;
	}
	
	//http://androidresearch.wordpress.com/2013/01/06/taking-a-screenshot-of-current-activity-in-android/
			//http://stackoverflow.com/questions/14492354/create-bitmap-from-view-makes-view-disappear-how-to-get-view-canvas
			//http://stackoverflow.com/questions/13194118/android-screenshot-of-activity-with-actionbar
//		public Bitmap takeScreenshot(View view) {
//			Log.d("why","took");
////			   View rootView = findViewById(R.id.gameOverRoot).getRootView();
////			   rootView.setDrawingCacheEnabled(true);
////			   return rootView.getDrawingCache();
//			   
//				// Prepping.
//			   boolean oldWillNotCacheDrawing = view.willNotCacheDrawing();
//			   view.setWillNotCacheDrawing(false); 
//			   view.setDrawingCacheEnabled(true);
//			   
//			   // Getting the bitmap. must copy as destroying the cache destroys the bitmap
//			   Bitmap copy = view.getDrawingCache().copy(Config.RGB_565, false);//ALPHA_8 , ARGB_8888 , RGB_565 
//
////			   And make sure to reset the view back to its old self.
//
//			   view.destroyDrawingCache();
//			   view.setDrawingCacheEnabled(false);
//			   view.setWillNotCacheDrawing(oldWillNotCacheDrawing);    
//
//			   return copy; 
//			}
//		
//		public void saveBitmap(Bitmap bitmap) {
//			Log.d("why","save");
//		    File imagePath = new File(Environment.getExternalStorageDirectory() + "/sdfsdf_screenshot.png");
//		    FileOutputStream fos;
//		    try {
//		        fos = new FileOutputStream(imagePath);
//		        bitmap.compress(CompressFormat.JPEG, 100, fos);
//		        fos.flush();
//		        fos.close();
//		    } catch (FileNotFoundException e) {
//		        Log.e("GREC", e.getMessage(), e);
//		    } catch (IOException e) {
//		        Log.e("GREC", e.getMessage(), e);
//		    }
//		}
}
