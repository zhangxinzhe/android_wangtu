package net.wangtu.android.common.util;

import android.view.MotionEvent;
import android.view.View;


/**
 * 
 * @author zhangxz
 *
 */
public class ViewUtil {
	/**
	 * 手指触摸区域是否在此view上
	 * @param view
	 * @param ev
	 * @return
	 */
	public static boolean inRangeOfView(View view, MotionEvent ev){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if(ev.getRawX() < x || ev.getRawX() > (x + view.getWidth()) || ev.getRawY() < y || ev.getRawY() > (y + view.getHeight())){
			return false;
		}
		return true;
	}
}
