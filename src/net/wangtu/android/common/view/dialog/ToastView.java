package net.wangtu.android.common.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

public class ToastView{
	@SuppressLint("ShowToast")
	public static android.widget.Toast makeText(Context context, CharSequence text) {
		android.widget.Toast toast= android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_LONG);
//		View view = toast.getView();
//		view.setPadding(10, 10, 10, 10);
//		view.findViewById(android.R.id.message).setBackgroundResource(android.R.color.transparent);
//		view.setBackgroundColor(0x88444444);
		return toast;
	}
}
