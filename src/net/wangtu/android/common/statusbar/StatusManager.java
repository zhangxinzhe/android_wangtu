package net.wangtu.android.common.statusbar;

import net.wangtu.android.common.statusbar.StatusForegroundColor.FlymeUtils;
import net.wangtu.android.common.statusbar.StatusForegroundColor.MIUIUtils;
import net.wangtu.android.common.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


/**
 * 状态栏控制器
 * @author zhangxz
 *
 */
public class StatusManager {
	
	/**
	 * 设置状态栏背景状态
	 */
	@SuppressLint("InlinedApi")
	public static void initStatusBar(Activity context,boolean showStatus,View backgroundStatusView,boolean blackWord,boolean keyBorder){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			//bar改变颜色
			Window win = context.getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			win.setAttributes(winParams);
			
			SystemStatusManager tintManager = new SystemStatusManager(context);
			tintManager.setStatusBarTintEnabled(true);
			if(blackWord){
				if(MIUIUtils.isMIUI() || FlymeUtils.isFlyme()){
					tintManager.setStatusBarTintResource(0);//状态栏无背景
					StatusForegroundColor.setForegroundColor(win, blackWord);//将字体改成黑色
				}else{
					tintManager.setStatusBarTintColor(0x88a0a0a0);//将背景加深
				}
			}else{
			}
			tintManager.setStatusBarTintResource(0);//状态栏无背景
			
			if(showStatus){
				ViewGroup container = (ViewGroup)context.findViewById(android.R.id.content);
				View contentView = container.getChildAt(0);
				contentView.setPadding(0, Util.getStatusHeight(context), 0, 0);

				if(backgroundStatusView != null){
					ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Util.getStatusHeight(context));
					backgroundStatusView.setLayoutParams(layoutParams);
					container.addView(backgroundStatusView);
				}
			}
			
			//解决输入框遮住问题
			if(keyBorder){
				AndroidBug5497Workaround.assistActivity(context);
			}
		}
	}
}
