package customAndroidClasses;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jtronlabs.cbblibrary.R;

public class CustomToastCreator {
	
	public void createToast(String toastText,Activity context,Toast toast){
		createCustomToast(toastText,context,toast);
	}
	
	private void createCustomToast(String toastText,Activity context,Toast toast){
		LayoutInflater inflater = context.getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast,(ViewGroup) context.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.toastText);
		text.setBackgroundResource(R.drawable.toast_exciting);
		text.setText(toastText);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
//	public void createCenteredToast(String toastText,Activity context,Toast toast){
//		LayoutInflater inflater = context.getLayoutInflater();
//		View layout = inflater.inflate(R.layout.custom_toast,(ViewGroup) context.findViewById(R.id.toast_layout_root));
//
//		TextView text = (TextView) layout.findViewById(R.id.toastText);
//		text.setBackgroundResource(R.drawable.toast_exciting);
////		text.setTextAppearance(context, R.style.VeryLargeText);
//		text.setText(toastText);
////		text.setTextColor(context.getResources().getColor(R.color.red));
//		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);//set Toast to be centered a little ways above the bottom of the screen
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.setView(layout);
//		toast.show();
//	}
}
