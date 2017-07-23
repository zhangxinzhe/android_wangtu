package net.wangtu.android.common.statusbar;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;


/**
 * 状态栏前景色修改
 */
public class StatusForegroundColor {
	/**
	 * 将背景色设置成黑色
	 * @param window
	 * @param dark
	 * @return
	 */
	public static boolean setForegroundColor(Window window, boolean dark){
		if(MIUIUtils.isMIUI()){
			return MIUISetStatusBarLightMode(window,dark);
		}
		if(FlymeUtils.isFlyme()){
			return FlymeSetStatusBarLightMode(window,dark);
		}
		
		return false;
	}

	/**
	 * 设置状态栏图标为深色和魅族特定的文字风格
	 * 可以用来判断是否为Flyme用户
	 * @param window 需要设置的窗口
	 * @param dark 是否把状态栏字体及图标颜色设置为深色
	 * @return  boolean 成功执行返回true
	 *
	 */
	public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
	    boolean result = false;
	    if (window != null) {
	        try {
	            WindowManager.LayoutParams lp = window.getAttributes();
	            Field darkFlag = WindowManager.LayoutParams.class
	                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
	            Field meizuFlags = WindowManager.LayoutParams.class
	                    .getDeclaredField("meizuFlags");
	            darkFlag.setAccessible(true);
	            meizuFlags.setAccessible(true);
	            int bit = darkFlag.getInt(null);
	            int value = meizuFlags.getInt(lp);
	            if (dark) {
	                value |= bit;
	            } else {
	                value &= ~bit;
	            }
	            meizuFlags.setInt(lp, value);
	            window.setAttributes(lp);
	            result = true;
	        } catch (Exception e) {

	        }
	    }
	    return result;
	}

	/**
	 * 设置状态栏字体图标为深色，需要MIUIV6以上
	 * @param window 需要设置的窗口
	 * @param dark 是否把状态栏字体及图标颜色设置为深色
	 * @return  boolean 成功执行返回true
	 *
	 */
	public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
	    boolean result = false;
	    if (window != null) {
	        Class clazz = window.getClass();
	        try {
	            int darkModeFlag = 0;
	            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
	            Field  field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
	            darkModeFlag = field.getInt(layoutParams);
	            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
	            if(dark){
	                extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
	            }else{
	                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
	            }
	            result=true;
	        }catch (Exception e){
	
	        }
	    }
	    return result;
	}
	
	public static boolean Android60SetStatusBarLightMode(Window window, boolean dark) {
		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}*/
		
		return false;
	}

	/**
	 * 验证是否是小米
	 * @author Administrator
	 *
	 */
	public static final class MIUIUtils {
		private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
		private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
		private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

		public static boolean isMIUI() {
			try {
				final BuildProperties prop = BuildProperties.newInstance();
				return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
						|| prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
						|| prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
			} catch (final IOException e) {
				return false;
			}
		}
	}
	
	/**
	 * 验证是否是魅族
	 * @author Administrator
	 *
	 */
	public static final class FlymeUtils {
		public static boolean isFlyme() {
			try {
				// Invoke Build.hasSmartBar()
				final Method method = Build.class.getMethod("hasSmartBar");
				return method != null;
			} catch (final Exception e) {
				return false;
			}
		}

	}
 
}
