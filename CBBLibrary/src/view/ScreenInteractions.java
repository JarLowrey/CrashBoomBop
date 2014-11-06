package view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jtronlabs.cbblibrary.R;

public class ScreenInteractions {
	private final int[] btnBackgrounds = {R.drawable.btn_blue,R.drawable.btn_green,R.drawable.btn_orange,
    		R.drawable.btn_purple,R.drawable.btn_red};
	
	public void moveBtn(Context ctx,RelativeLayout gameWindow,Button movingBtn){
		//Set up ability to view device specific screen attributes
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
	    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
	    int widthPixels = displayMetrics.widthPixels;
	    int heightPixels = displayMetrics.heightPixels;
	    
		//px/dens=dp------------dp*dens=px
		//remove btn from layout so that position can be changed and btn can be added in with new position values
		gameWindow.removeView(movingBtn);
        		
		//button is a SQUARE
		int btnSideLength = (int)ctx.getResources().getDimension(R.dimen.game_moving_btn_side_length);//there is padding on the 
		float distanceFromTop = ctx.getResources().getDimension(R.dimen.game_moving_margin_top);
		
		//change get max btn position & set btn size using LayoutParams
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(btnSideLength, btnSideLength);
		double marginLeft=widthPixels-btnSideLength;//adjust for button size
		double marginTop=heightPixels-(ctx.getResources().getDimension(R.dimen.ad_banner_margin)+btnSideLength+distanceFromTop);//adjust for ad height @ bottom of screen,btnlength@ top of screen, and the acion bar height at top of screen
		
		//randomize btn position
		params.leftMargin = (int)(Math.random()*marginLeft);
		params.topMargin = (int) ((Math.random()*marginTop)+distanceFromTop);
		
		//set btn size, position, background resource. Add btn into the layout and make it visible
		movingBtn.setLayoutParams(params);
		movingBtn.setBackgroundResource(btnBackgrounds[(int)(Math.random()*btnBackgrounds.length)]);
		gameWindow.addView(movingBtn);
		movingBtn.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide everything on screen and cancel all animations (except for pauseGame animation)
	 */
	public void clearGameScreen(RelativeLayout gameWindow,int[] dontClearTheseViews){
		clearAllAnimations(gameWindow);
		for ( int i = 0; i < gameWindow.getChildCount();  i++ ){
		    View view = gameWindow.getChildAt(i);
		    
		    int id= view.getId();//check if current view should be cleared
		    boolean clearView = true;
		    for(int viewId : dontClearTheseViews){
		    	clearView = clearView && id!=viewId;
		    }
		    
		    if(clearView){
			    view.clearAnimation();
			    view.setVisibility(View.GONE);
		    }
		}
	}
	
	public void clearAllAnimations(RelativeLayout gameWindow){
		for ( int i = 0; i < gameWindow.getChildCount();  i++ ){
		    View view = gameWindow.getChildAt(i);
		    view.clearAnimation();
		}
	}
	
	public int chooseMedalId(Context ctx,int score){
		int[] cutoffs = ctx.getResources().getIntArray(R.array.medal_cutoff_scores);
		if(score<cutoffs[0]){
			return R.drawable.medal_bronze;
		}else if(score<cutoffs[1]){
			return R.drawable.medal_silver;
		}else if(score<cutoffs[2]){
			return R.drawable.medal_gold;
		}else{
			return R.drawable.medal_platinum;
		}		
	}
	

	
	//http://stackoverflow.com/questions/13286358/sharing-to-facebook-twitter-via-share-intent-android
	/**
	 * Find a ShareIntent from the available ones on device.
	 * @param type	name of intent to share 
	 * @param subject	header of shared intent
	 * @param text	message displayed in sharedintent
	 * @return	the sharedintent. Can be used by intent.chooser and possibly more
	 */
	private Intent getShareIntent(Context ctx,String type,int score) 
	{
	    boolean found = false;
	    Intent share = new Intent(android.content.Intent.ACTION_SEND);
	    share.setType("text/plain");

	    // gets the list of intents that can be loaded.
	    List<ResolveInfo> resInfo = ctx.getPackageManager().queryIntentActivities(share, 0);
	    System.out.println("resinfo: " + resInfo);
	    if (!resInfo.isEmpty()){
	        for (ResolveInfo info : resInfo) {
	            if (info.activityInfo.packageName.toLowerCase().contains(type) || 
	                    info.activityInfo.name.toLowerCase().contains(type) ) {
	            	if(!type.equals("mms")){
	            		share.putExtra(Intent.EXTRA_SUBJECT,  "Crash Boom Bop");
	            	}
	            	share.putExtra(Intent.EXTRA_TEXT,     "Check out my "+score+" points! Beat that!! #CrashBoomBop");
	                found = true;
	                share.setPackage(info.activityInfo.packageName);
	                break;
	            }
	        }
	        if (!found)
	            return null;

	        return share;
	    }
	    return null;
	}
	
	/**
	 * shows a group of shareintents to the screen
	 * @param intents	string array of the names of share intents that will be shown
	 */
	public void findAndDisplayTargetedShareIntentChooser(Context ctx,int score, String[] intents,String subject,String message){
		List<Intent> targetedShareIntents = new ArrayList<Intent>();

		for(int i=0;i<intents.length;i++){
			Intent thisShareIntent = getShareIntent(ctx,intents[i],score);
			if(thisShareIntent != null){targetedShareIntents.add(thisShareIntent);}
		}

		Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "Share via");
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		ctx.startActivity(chooser);
	}
	
	public void showHeartsOnScreen(Context ctx,RelativeLayout allHearts,int howMuchLife){
		allHearts.removeAllViews();
		
		//set up small heart sizes (in density pixels=dp)
        int heartLen = (int) ctx.getResources().getDimension(R.dimen.game_heart_length);
	    
		//add a heart to the screen for each life the user has
		for(int i =0;i<howMuchLife;i++){
			ImageView image = new ImageView(ctx);
			image.setBackgroundResource(R.drawable.heart);
			
			//position the hearts with the wrapping 'allHearts' RelativeLayout
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(heartLen,heartLen);
	        params.leftMargin = heartLen*(i%3)+(int) ctx.getResources().getDimension(R.dimen.game_heart_length);//only 5 hearts in a row. heartWidth*(i%5) prevents them from overlapping. (int)(5*dens) gives space between
	        params.topMargin = heartLen*(i/3);
	        image.setLayoutParams(params);
	        
	        allHearts.addView(image);
		}
	}
}
