package net.wangtu.android.common.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘工具类
 * @author kuanghf
 */
public class SoftInputUtil {
	
	/**
	 * 呼出键盘
	 * @param context
	 * @param view
	 */
	public static void showSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}
	
	/**
	 * 收回键盘
	 * @param context
	 * @param view
	 */
	public static void hideSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}