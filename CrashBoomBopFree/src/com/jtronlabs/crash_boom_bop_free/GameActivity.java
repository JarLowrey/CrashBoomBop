package com.jtronlabs.crash_boom_bop_free;

import java.util.Locale;

import support.CaseChecks;
import support.CaseChecks.CaseResult;
import support.GameModel;
import support.GameModel.Case;
import support.MediaManager;
import support.MyStopWatch;
import view.DrawTextPanel;
import view.ScreenInteractions;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import customAndroidClasses.CustomToastCreator;
import database.GameStatsAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

//import com.mopub.mobileads.MoPubView;

//import com.mopub.mobileads.MoPubView.BannerAdListener;



/*
 * Feature List: 
 * 
 * Background color, welcome+pause images, sounds
 * 
 * Case=Dont move it at all
 * BONUS:
 * 			Raise/lower sensitivity 
 * 			
 * 
 * unlockables:
 * 			after playing x number games
 * 			after playing x amount of time
 * 			after reaching x score
 * 			very important to keep user interested. Cold unlock flair or extra 'Case'
 * 
 */

/*
 * TODO
 * publish...
 * backgrond png??
 * Iap
 * ask for a rating?
 * different ad networks?
 */

/*
 * Release 2:
 * ManyX, ManyY, ManyZ----"Countdown! Side to side"--animate the same as respective single shakes
 * different way to implement ratings
 * colors-share btn, splat birghtness,
 * Game over screen focused on medal
 * medals in profile page
 * overdraw optimization
 * NO BANNER ADS IN GAME
 * sensitivity change events
 * median,avg, mode stats
 * on touch listener/gesture detectors for a swipe case?
 */

/*
 * Not Implementing
 * Any gyroscope usage for Twist/Flip animation & Case
 * startup screen
 * pause animation on pause game
 * 4 separate time trials
 * Each bonus type (taps,movingtaps,manyshakes)has their respective points logged in the DB
 */

/* Questionable
 * game descripion (tutorial images explaining shakes w/ simple phone & arrow?
 * Remove SPLAT image on bonus cases? it's kind of distracting...
 * change color of rating & shopping cart icons
 * PAUSED txt background?
 * Moving Btn hidden behind ads?
 * OnSensorAccuracyChanged()-unimplemented?
 */


/* Accelerometer
 * http://www.techrepublic.com/blog/software-engineer/a-quick-tutorial-on-coding-androids-accelerometer/
 *http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
 */

//http://stackoverflow.com/questions/19446231/admob-banners-cause-high-cpu-usage

public class GameActivity extends Activity implements OnClickListener, SensorEventListener
{
	//Resource ID arrays
    private final int[] splats = {R.drawable.splat_0_0,R.drawable.splat_0_1,R.drawable.splat_0_2,
    		R.drawable.splat_1_0,R.drawable.splat_1_1,//splat_1_2 currently removed
    		R.drawable.splat_2_0,R.drawable.splat_2_1,
    		R.drawable.splat_3_0,R.drawable.splat_3_1,R.drawable.splat_3_2};
    private final int[] splatAnim = {R.anim.splat0,R.anim.splat1,R.anim.splat2,
    		R.anim.splat3,R.anim.splat4,R.anim.splat5,R.anim.splat6};
    private final int[] viewsThatPersistOnClearScreen = {R.id.heartGroup,R.id.score,R.id.pause_or_play};
	
	//sensor variable
	private float lastX, lastY, lastZ;
	private boolean accelInitialized=false;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
            
    //Time variables
    private MyStopWatch watch = new MyStopWatch();
    private boolean screenIsCleared=true;
    
    //view variables
    private RelativeLayout gameWindow,detectTapArea,heartGroup;
    private TextView pauseText,bonusTimeRemaining;
    private Button movingBtn,pauseBtn;
    private ImageView splat,good_bad,phone;
    private AdView adView;
    
    //screen vars
    private Animation myAnim;
    private Toast streak,combo,bonus,score;
    boolean alertMsgIsShowing=false;
    private DrawTextPanel scorePanel;
        
    //Custom Class Variables
    private GameModel myModel;
    private CustomToastCreator toastCreator = new CustomToastCreator();
    private CaseChecks checkCase = new CaseChecks();
	private MediaManager mp;
	private ScreenInteractions changeScreen = new ScreenInteractions();
    
    //Game Variables
	private boolean gameOver=false,gameStarted;
    private long totalResponseTimeThisGame=0,totalTimeThisGame=0;
    private boolean  gameIsPaused=false;
    
    //data access
	private GameStatsAdapter gameStatsDb;
	
	//thread vars
	Handler handler;
	private boolean handlerIsRunning;
    
	/*------------------------------------------------------------OVERRIDE ANDROID METHODS------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//set Orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				
		//set up data structures
	    gameStatsDb = new GameStatsAdapter(this);
	    gameStatsDb.open();
	    
	    //Toasts
	    streak=new Toast(this);
	    combo=new Toast(this);
	    bonus=new Toast(this);
	    score=new Toast(this);
	    
	    //music
  		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	    
	    //setup sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(GameActivity.this, mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        
	    //Set up game screen and media
//	    wrong_and_right_toast = new Toast(this);//should this be getApplicationContext?
	    mp=new MediaManager();	    

		handler = new Handler();
		
	    //setup game
	    setupGame();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(adView!=null){adView.destroy();}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		if(adView!=null){adView.resume();}
	}
	@Override
	protected void onPause() {
	    turnOffSensor();
	    resetAdView();

	    if(gameStarted && !gameOver){
	    	pauseGame();
	    }
	    combo.cancel();
	    super.onPause();
	}
	
	@Override
    public void onBackPressed()
    {
		if(gameStarted && !gameOver){
			if(gameIsPaused){resumeGame();}
			else{pauseGame();}
		}else{
			leaveGame();
		}
    }
		
	/*------------------------------------------------------------SENSOR METHODS------------------------------------------------------------*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { //I have not touched this method
		switch(accuracy){
		case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
			//high
			break;
		case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
			//med
			break;
		case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
			//low
			break;
		case SensorManager.SENSOR_STATUS_UNRELIABLE:
			//unreliable
			break;
		default:
		    break;
		}
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) { //Algorithm to check sensors: current coord-previous coord. That's it.
		if(!gameIsPaused && gameStarted && !gameOver){//avoid uneccessary processing. wait until setup complete (flagged by gameStarted)
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				float x = event.values[0];
		        float y = event.values[1];
		        float z = event.values[2];
		        
		        initializeLastVarsifNecessary(x,y,z);//if sensor is just opened then the LAST vars must be initialized
			
				float[] diffs={(x-lastX),(y-lastY),(z-lastZ)};
				
				if(screenIsCleared){
					if(watch.getElapsedTime()>myModel.clearScreenTimeLength){
			        	screenIsCleared=false;
			        	showNewCase();
			        	
						watch.reset();
						watch.start();
						accelInitialized = false;
					}					
				}else{
					//User does not respond in time
					if(myModel.currentCaseIsBonus()){
						int endTime = myModel.getBonusEndTime(); 
						if(watch.getElapsedTime()>endTime){
							updateGameAfterCase("",CaseResult.PASS);
						}else {
							bonusTimeRemaining.setText((endTime-watch.getElapsedTime())/1000+"");
							if(myModel.currentCase==Case.MANYSHAKESX /*|| myModel.currentCase==Case.MANYSHAKESY || myModel.currentCase==Case.MANYSHAKESZ*/){
								if(checkCase.checkManyShakes(myModel.currentCase, diffs)){
									myModel.numBonusShakesOrTaps++;
									if(mp.donePlayingSoundClip){mp.playSoundClip(this, R.raw.swoosh);}
								}
							}
						}
					}else {
						if(watch.getElapsedTime()>myModel.getNonBonusFailTime()){
							Log.d("why","sensor "+watch.getElapsedTime());
							updateGameAfterCase("Too slow",CaseResult.FAIL);
						}else{
							CaseResult result = checkCase.checkAllCasesExceptManyTapAndMovingBtn(myModel.currentCase, diffs);
							if(result!=CaseResult.NONE){updateGameAfterCase("",result);}
						}
					}
				}
			lastX = x;
		    lastY = y;
		    lastZ = z;
			}	
		}
	}

	/*------------------------------------------------------------CLICK METHODS------------------------------------------------------------*/
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.gameOverAccept:
			leaveGame();
			break;
		case R.id.gameOverShare:
			String[] sharingTargets = {"face","twit","whats","chat","sms","mms","plus","email","gmail"};
			changeScreen.findAndDisplayTargetedShareIntentChooser(this,myModel.score,sharingTargets,
					"Crash Boom Bop","Check out my "+myModel.score+" points! Beat that!! #CrashBoomBop");
			break;
		case R.id.pause_or_play:
			if(gameIsPaused){resumeGame();}
			else{pauseGame();}
			break;
		default:
			if( !gameIsPaused && !screenIsCleared && !handlerIsRunning){
				switch(myModel.currentCase){
				case TAP:	
					mp.playSoundClip(this,R.raw.tap); 
					//if user too slow, then fail him. otherwise he passes.
					CaseResult result = (watch.getElapsedTime()>myModel.getNonBonusFailTime()) ? CaseResult.FAIL : CaseResult.PASS;
					updateGameAfterCase("Too slow",result);
					break;
				case MANYTAPS:
					mp.playSoundClip(this,R.raw.tap); 
					//increase the number of bonus recorded, result is NONE so bonus count down will continue
					myModel.numBonusShakesOrTaps++;
					break;
				case MOVINGBTN:
					//increase the number of bonus recorded if user hit a button, result is NONE so bonus count down will continue
					//do not play sound if user misses btn
					if(id==R.id.movingBtn){
						mp.playSoundClip(this,R.raw.tap); 
						myModel.numBonusShakesOrTaps++;
						changeScreen.moveBtn(this,gameWindow,movingBtn);
					}
					break;
				default:
					mp.playSoundClip(this,R.raw.tap); 
					if(id==R.id.detectTapArea){updateGameAfterCase("",CaseResult.FAIL);}
					break;
				}
			}
		}
	}
	
	/*------------------------------------------------------------SCREEN METHODS------------------------------------------------------------*/
	/**
	 * Setup game to be updated:clear screen, add watch.getElapsedTime() to total, check the CaseResult to decrease/increase myModel.lif, modify streak and score
	 */
	private void updateGameAfterCase(String failedCaseMessage,CaseResult result){
		long timeToCompletePreviousCase = watch.getElapsedTime();
		
		//clear screen & set timer to run for its duration. This needs to be completed before you check for life increase/decrease, as those methods change the screen
		screenIsCleared=true;
		changeScreen.clearGameScreen(gameWindow,viewsThatPersistOnClearScreen);
		watch.reset();
		watch.start();
		
		myModel.highestStreakThisGame = (myModel.streak>myModel.highestStreakThisGame) ? myModel.streak : myModel.highestStreakThisGame;
		
		if(!myModel.currentCaseIsBonus()){
			totalResponseTimeThisGame+=timeToCompletePreviousCase;
		}else{
			//response time is not affected by how well user completes Bonus cases
			mp.playSoundClip(this, R.raw.bonus_passed);
		}
		totalTimeThisGame+=timeToCompletePreviousCase;
		
		switch(result){
			case FAIL:
				//update Model variables
				if(myModel.isCombo){myModel.stopCombo();}				
				myModel.numCasesFailed++;
				myModel.streak=0;
				myModel.life--;
				
				//alert user of failure
				if(myModel.life<=0){
					gameOver();
				}else{
					mp.playSoundClip(this,R.raw.fail);
					showAlertMsg(failedCaseMessage,500,score);
//					toastCreator.createToast(failedCaseMessage, this, wrong_and_right_toast);
					good_bad.setBackgroundResource(R.drawable.game_fail);
					good_bad.setVisibility(View.VISIBLE);
				}
				break;
			case PASS:
				//play a pleasant ding when passing a combo
				if(myModel.isCombo && myModel.currentPosInCombo==3){
					mp.playSoundClip(this, R.raw.bonus_passed);					
				}
				//play a swoosh noise when passing a non bonus case that required the accelerometer
				else if(!myModel.currentCaseIsBonus() && myModel.currentCase!=Case.TAP && myModel.currentCase!=Case.MANYTAPS && myModel.currentCase!=Case.MOVINGBTN ){
					mp.playSoundClip(this,R.raw.swoosh);
				}
				
				myModel.numCasesPassed++;
				myModel.streak++;
				if(myModel.currentCaseIsBonus()){
					myModel.numBonusCases++;
					int inc =myModel.increaseScore(myModel.numBonusShakesOrTaps);
					showAlertMsg(inc +" Bonus!",500,score);
				}else{
					myModel.increaseScore(timeToCompletePreviousCase);
				}
				break;
			default:
				break;
		}
		if(!gameOver){
			//make model check for increased level. if level increased and level is even, increase score or life
			if(myModel.nextLevelIfNeccessary() && myModel.level!=0 && myModel.level%2==0){
				lifeOrScoreBonus();
			}
			
			myModel.setNextCase();
			
			//update score and hearts
	    	changeScreen.showHeartsOnScreen(this,heartGroup,myModel.life);
			scorePanel.drawText(myModel.score+"");
		}
	}
	
	
	public void showNewCase(){
		//show everything, then hide what is not needed
		showAllViewsOnScreen();
		pauseText.setVisibility(View.GONE);
		if(!myModel.currentCaseIsBonus()){bonusTimeRemaining.setVisibility(View.GONE);}
		movingBtn.setVisibility(View.GONE);
		good_bad.setVisibility(View.GONE);
    	
		// create splat animation
		setSplatAnim();
		
		if(myModel.isCombo){
			//@ beginning of combo show the cases that comprise the combo
			if(myModel.currentPosInCombo==1){
				showComboAnimations();
			}
			//If combo has started then hide the case text. User needs to remember.
			else{
				phone.setVisibility(View.GONE);
			}
		}else{
			//set up animations/movements on screen
			if(myModel.currentCaseIsBonus()){
				showAlertMsg(myModel.getTextForCase(),1000,bonus);
				switch(myModel.currentCase){
				case MOVINGBTN:
					phone.setVisibility(View.GONE);
					changeScreen.moveBtn(this,gameWindow,movingBtn);
					break;
				case MANYTAPS:
					setAnimationAndPhoneImage(myModel.currentCase);
					break;
				case MANYSHAKESX:
					setAnimationAndPhoneImage(myModel.currentCase);
					break;
				default:
					break;
				}
			}else{
				setAnimationAndPhoneImage(myModel.currentCase);
			}
		}
				
		if(myModel.passStreakMileStone() && !alertMsgIsShowing){
			showAlertMsg("Streak: "+myModel.streak,500,streak);
		}
	}
	
	/**
	 * Reset model and inform user of score & gameOver status
	 */
	private void gameOver(){
		//put game into game over state
		gameOver=true;
		turnOffSensor();
		changeScreen.clearGameScreen(gameWindow,viewsThatPersistOnClearScreen);
		
		//play game over sound
		mp.playSoundClip(this,R.raw.endgame);
		
		//update gameStatsDb 
		gameStatsDb.addRow(myModel.score, totalResponseTimeThisGame, totalTimeThisGame, 
    			myModel.numTotalNonBonusCases(), myModel.numCasesPassed, 
    			myModel.numBonusCases,myModel.totalNumBonusPoints,
    			myModel.numCombosOccurred, myModel.numCombosPassed,
    			myModel.highestStreakThisGame,-1);//the -1 is for gamemode, it is legacy code. -1 now signifies user has no choice
		
		//clean up ad before setting new content view
				resetAdView();
		setContentView(R.layout.custom_gameover);
		
		//set up game over screen
		Button acceptBtn = (Button)findViewById(R.id.gameOverAccept);//on click listeners for buttons on lower portion of screen
		acceptBtn.setOnClickListener(this);
		Button share = (Button)findViewById(R.id.gameOverShare);
		share.setOnClickListener(this);
		ImageView medal = (ImageView)findViewById(R.id.gameoverMedal);
		medal.setBackgroundResource(changeScreen.chooseMedalId(this,myModel.score));
		
		//display game specific data in views
		//scores
		TextView score = (TextView)findViewById(R.id.gameOverCurrentScoreValue);
		score.setText(""+myModel.score);
		TextView best = (TextView)findViewById(R.id.gameOverBestScoreValue);
		best.setText(""+gameStatsDb.getHighestScore());
		//times
		TextView avg = (TextView)findViewById(R.id.gameOverAvgTimeValue);
		double avgResponse = (totalResponseTimeThisGame/myModel.numTotalNonBonusCases())/1000.0;
		avgResponse = (avgResponse>5) ? 5.0001:avgResponse; //for UX sake, the random errors in determining fail time should be hidden from the user on gameover screen
		String avgResponseString = String.format(Locale.getDefault(),"%.2f", avgResponse)+" sec";
		avg.setText(avgResponseString);
		
		//reset time vars
		totalResponseTimeThisGame=0;
		totalTimeThisGame=0;
	}
	
	/*
	 * The user has increased his myModel.life. Update the game model and inform user of change.
	 */
	private void lifeOrScoreBonus(){
		//update game variables and set alertMsg to show a Toast to user
		String alertMsg="";
//		if(myModel.life<6){
//			myModel.life++; 
//			alertMsg="Life Increase";
//		}else{
			int scoreIncrease = myModel.level*5;
			myModel.increaseScore(scoreIncrease);
			alertMsg="Score Increase!";
//		}
		
		//alert user of bonus
//		toastCreator.createToast(alertMsg, this, wrong_and_right_toast);
			showAlertMsg(alertMsg,500,score);
		mp.playSoundClip(this,R.raw.clap);
		good_bad.setBackgroundResource(R.drawable.game_good);
		good_bad.setVisibility(View.VISIBLE);
	}
	
	/*------------------------------------------------------------PAUSE METHODS------------------------------------------------------------*/
	
	private void pauseGame(){
		//don't pause multiple times in a row (example: already paused and user hits back btn)
		if(!gameIsPaused){			 
			//set game to paused state
			turnOffSensor();
			gameIsPaused=true;
			

			//hide all toasts
//			if(wrong_and_right_toast!=null){wrong_and_right_toast.cancel();}
			
			//cancel combo explanation if necessary
			if(handler!=null){
				handlerIsRunning=false;
				handler.removeCallbacksAndMessages(null);
			}
			
			//change actionbar pause icon
			pauseBtn.setBackgroundResource(R.drawable.ic_action_btn_play);
			
			//show user game paused by clearing the screen, setting screen alpha to 50%, and showing pauseText
			changeScreen.clearAllAnimations(gameWindow);
			AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.25F);
			alpha.setDuration(0); // Make animation instant
			alpha.setFillAfter(true); // Tell it to persist after the animation ends
			gameWindow.startAnimation(alpha);
			pauseText.setVisibility(View.VISIBLE);
			
			//cant figure out how to alpha this. just hide it
			scorePanel.setVisibility(View.GONE);
			
			watch.pause();
		}
	}
	
	private void resumeGame(){		
		gameIsPaused=false;
		//change actionbar pause icon
		pauseBtn.setBackgroundResource(R.drawable.ic_action_btn_pause);
		
		//show user game has resumed by showing the screen, clearing alpha, playing the currentCase animation, and hiding pauseText
		AlphaAnimation alpha = new AlphaAnimation(0.25F, 1.0F);
		alpha.setDuration(0); // Make animation instant
		alpha.setFillAfter(true); // Tell it to persist after the animation ends
		gameWindow.startAnimation(alpha);
		
		scorePanel.setVisibility(View.VISIBLE);		
		pauseText.setVisibility(View.GONE);
		showNewCase();
		if(!(myModel.isCombo && myModel.currentPosInCombo==1)){
			mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			watch.start();			
		}
	}
	
	/*------------------------------------------------------------SUPPORT METHODS------------------------------------------------------------*/
	
	/**
	 * show all views in the Layout on screen
	 */
	private void showAllViewsOnScreen(){
		for ( int i = 0; i < gameWindow.getChildCount();  i++ ){
		    View view = gameWindow.getChildAt(i);
		    view.setVisibility(View.VISIBLE);
		}
	}
	
	private void initializeLastVarsifNecessary(float x,float y,float z){
		if(!accelInitialized){
        	lastX=x;
        	lastY=y;
        	lastZ=z;
        	accelInitialized=true;
        }
	}	
	
	private void setupGame(){
		resetAdView();
		setContentView(R.layout.activity_game);//required to access views in this layout
		mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //reset all game variables
  		gameOver=false;
  		myModel = new GameModel();        
        gameStarted=true;
		
		//set views @ top of screen
		pauseBtn = (Button)findViewById(R.id.pause_or_play);
		pauseBtn.setOnClickListener(this);
		heartGroup = (RelativeLayout)findViewById(R.id.heartGroup);
    	changeScreen.showHeartsOnScreen(this,heartGroup,myModel.life);
		scorePanel = (DrawTextPanel)findViewById(R.id.score);
		scorePanel.drawText("0");
        
        //Set up views
		pauseBtn = (Button)findViewById(R.id.pause_or_play);
		pauseBtn.setOnClickListener(this);
	    pauseText = (TextView)this.findViewById(R.id.pauseText);
        pauseText.setVisibility(View.INVISIBLE);
	    gameWindow = (RelativeLayout)this.findViewById(R.id.gameWindow);
	    bonusTimeRemaining = (TextView)findViewById(R.id.bonusTimeRemaining);
//        currentCase = (TextView)this.findViewById(R.id.currentCase);
        phone = (ImageView)findViewById(R.id.phoneImage);
        splat = (ImageView)this.findViewById(R.id.splat);
        good_bad = (ImageView)this.findViewById(R.id.fail_or_reward_image);
        detectTapArea=(RelativeLayout)this.findViewById(R.id.detectTapArea);
        detectTapArea.setOnClickListener(this);
        movingBtn = (Button) this.findViewById(R.id.movingBtn);
        movingBtn.setOnClickListener(this);
        movingBtn.setVisibility(View.GONE);

        //start the game
        watch.reset();
        watch.start();
        
      //Ads--device id--FFAA2B4ECD49CBF2A0AB7F9D447410D7 (found from logcat when running an ad. filter "addTestDevice")
        if(adView==null){
	        adView = (AdView) this.findViewById(R.id.adViewGameMain);
	  	    AdRequest adRequest = new AdRequest.Builder()
//	  	    	.addTestDevice("FFAA2B4ECD49CBF2A0AB7F9D447410D7")
	  	    	.build();
	  	    adView.loadAd(adRequest);
        }else{
        	adView.resume();
        }      
	}
	/**
	 * Clean up before exiting Activity to prevent Memory Leaks
	 */
	private void leaveGame(){ 
		gameStatsDb.close();
		finish();
	} 
	
	private void turnOffSensor(){
		mSensorManager.unregisterListener(this);
		accelInitialized=false;
	}
	
	private void showComboAnimations(){
		turnOffSensor();
		watch.reset();
		handlerIsRunning=true;		

		showAlertMsg("COMBO\nMEMORIZE THE ORDER!",1800,combo);
		phone.setVisibility(View.INVISIBLE);

		/*
		 * Threading Notes:::
		 * if you wanted to make one runnable, when you had handler.postDelayed(this,time); it would call the same runnable and re-execute commands
		 * The if(handlerIsRunning) is a required check because handler.removeCallbacksAndMessage only removes pending calls. If something has started it won't be stopped
		 * by this when it is called in pauseGame()*/
		 
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
       	    if(handlerIsRunning){
           		phone.setVisibility(View.VISIBLE);
				setAnimationAndPhoneImage(myModel.casesInCombo[0],View.INVISIBLE);
       	    }
           }
        },2000);
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        	    if(handlerIsRunning){
	            	phone.setVisibility(View.VISIBLE);
	 				setAnimationAndPhoneImage(myModel.casesInCombo[1],View.INVISIBLE);
        	    }
            }
         },3000);
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        	    if(handlerIsRunning){
	            	phone.setVisibility(View.VISIBLE);
	 				setAnimationAndPhoneImage(myModel.casesInCombo[2],View.INVISIBLE);
        	    }
            }
         },4000);
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        	    if(handlerIsRunning){
	        		phone.setVisibility(View.INVISIBLE);
        	    }
            }
         },4500);
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        	    if(handlerIsRunning){
					setSplatAnim();
	            	handlerIsRunning=false;
					mSensorManager.registerListener(GameActivity.this,mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
					watch.start();
        	    }        		
            }
         },5200);
	}
	
	private void setAnimationAndPhoneImage(Case whichCase){
		if(whichCase==Case.TAP || whichCase==Case.MANYTAPS){
			phone.setBackgroundResource(R.drawable.phone_tap);
		}else{
			phone.setBackgroundResource(R.drawable.phone);
		}
		myAnim=AnimationUtils.loadAnimation(this,myModel.getAnimationId(whichCase));
		phone.startAnimation(myAnim);
	}
	
	private void setAnimationAndPhoneImage(Case whichCase, final int visibilityAfterAnim){
		if(whichCase==Case.TAP || whichCase==Case.MANYTAPS){
			phone.setBackgroundResource(R.drawable.phone_tap);
		}else{
			phone.setBackgroundResource(R.drawable.phone);
		}

		myAnim=AnimationUtils.loadAnimation(this,myModel.getAnimationId(whichCase));
		myAnim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {phone.setVisibility(visibilityAfterAnim);}

			@Override
			public void onAnimationRepeat(Animation arg0) {}

			@Override
			public void onAnimationStart(Animation arg0) {}				
		});
		phone.startAnimation(myAnim); 
	}
	
	private void setSplatAnim(){
		splat.setBackgroundResource(splats[(int)(Math.random()*splats.length)]);//splat = background image to current case
		myAnim=AnimationUtils.loadAnimation(this,splatAnim[(int)(Math.random()*splatAnim.length)]);
		splat.startAnimation(myAnim);
	}
	
	private void showAlertMsg(String msg,long timeToDisplay,final Toast toast){
		if(!msg.equals("")){
			
			toastCreator.createToast(msg, this, toast);
			
			handler.postDelayed(new Runnable() {
		           @Override
		           public void run() {
		        	   toast.cancel();
		           }
		        },timeToDisplay);
		}
	}
	/**
	 * I've been having major issues with ads. This pauses the ad and sets it to null, so that it will be regenerated on setupGame() and the processes won't leak.
	 * EDIT: The process leaks. it is unavoidable and consistent across applications
	 */
	private void resetAdView(){
		if(adView!=null){adView.pause();}
		adView=null;
	}
}

