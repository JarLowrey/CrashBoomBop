package customAndroidClasses;
//package com.lowreyjamesr.projx.customAndroidClasses;
//
//import android.content.Context;
//import android.util.DisplayMetrics;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.lowreyjamesr.projx.R;
//
//public class GameOverScreen extends RelativeLayout implements OnClickListener{
//
//	private TextView titleView, messageView;
//	private ImageView iconView;
//	private ImageButton backBtn,shareBtn;
//	
//	public GameOverScreen(Context context) {
//		super(context);
//		DisplayMetrics displayMetrics = new DisplayMetrics();
//	    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//	    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//	    float dens = displayMetrics.density;
//	    
//	    int length = (int)(300*dens);
//		this.setLayoutParams(new RelativeLayout.LayoutParams(length, length));
//		this.setBackgroundResource(R.drawable.gameover_background);
//		
//		titleView=new TextView(context);
//		messageView = new TextView(context);
//		iconView = new ImageView(context);
//		backBtn = new ImageButton(context);
//		backBtn.setBackgroundResource(R.drawable.ic_action_btn_back);
//		shareBtn = new ImageButton(context);
//		backBtn.setBackgroundResource(R.drawable.ic_action_btn_back);
//	}
//	
//	public void setTitle(String title){
//		titleView.setText(title);
//	}
//	
//	public void setMessage(String message){
//		messageView.setText(message);
//	}
//
//	public void setIconImage(int iconId){
//		iconView.setBackgroundResource(iconId);
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//}
