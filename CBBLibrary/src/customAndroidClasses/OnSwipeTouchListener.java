package customAndroidClasses;


import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

//http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private Context ctx;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        ctx=context;
    }
    public Context getContext(){
    	return ctx;
    }

    //abstract
    public void onSwipeLeft() {}

    public void onSwipeRight() {}
    
    public void onSwipeTop() {}

    public void onSwipeBottom() {}
    public void onClick(){}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY)){
            	if(Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
		            if (distanceX > 0){
		                onSwipeRight();
		            }else{
		                onSwipeLeft();
		            }
		            return true;
            	}
            }else{
            	if (Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    return true;
                }
            }
            return false;
        }
        
        //signal that these events were not handled by the OnSwipeTouchListener
        @Override 
        public boolean onSingleTapUp(MotionEvent e){
        	onClick(); // my method
            return true;
        }
        
    }
}